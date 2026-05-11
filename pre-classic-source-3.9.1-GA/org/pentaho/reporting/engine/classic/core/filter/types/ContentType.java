/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.filter.types;

import java.awt.Component;
import java.awt.Image;
import java.awt.Shape;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;
import java.sql.Blob;
import java.util.Locale;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.ComponentDrawable;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawableImage;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ContentType implements ElementType
{
  public static final ElementType INSTANCE = new ContentType();
  private transient LFUMap failureCache;
  private transient JFrame frame;

  private static final Class[] TARGETS = new Class[]{DrawableWrapper.class, Image.class};
  private static final Log logger = LogFactory.getLog(ContentType.class);
  private transient ElementMetaData elementType;

  public ContentType()
  {
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element the element for which the data is computed.
   * @return the value.
   */
  public Object getValue(final ExpressionRuntime runtime, final Element element)
  {
    if (runtime == null)
    {
      throw new NullPointerException("Runtime must never be null.");
    }
    if (element == null)
    {
      throw new NullPointerException("Element must never be null.");
    }

    URL resource = null;
    final Object value = ElementTypeUtils.queryStaticValue(element);
    if (value != null)
    {
      final Object filteredValue = filter(runtime, element, value);
      if (filteredValue != null)
      {
        return filteredValue;
      }
      else
      {
        final boolean isBrokenImageEnabled = "true".equals
            (runtime.getConfiguration().getConfigProperty("org.pentaho.reporting.engine.classic.core.EnableBrokenImage"));
        if (isBrokenImageEnabled)
        {
          resource = ContentType.class.getResource
              ("/org/pentaho/reporting/engine/classic/core/metadata/icons/image_broken_50.png");
        }
      }
    }

    final Object nullValue = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE);
    if (nullValue != null)
    {
      final Object loadedNullValue = filter(runtime, element, nullValue);
      if (loadedNullValue != null)
      {
        return loadedNullValue;
      }
    }

    try
    {
      if (resource == null)
      {
        resource = ContentType.class.getResource
            ("/org/pentaho/reporting/engine/classic/core/metadata/icons/image_50.png");
      }
      if (resource != null)
      {
        final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
        final Resource loadedResource = resManager.createDirectly(resource, Image.class);
        final Image image = (Image) loadedResource.getResource();
        return new ReportDrawableImage(image);
      }
    }
    catch (Exception e)
    {
      ContentType.logger.warn("Failed to load content." + e);
    }
    return value;
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    return getValue(runtime, element);
  }

  protected Object filter(final ExpressionRuntime runtime, final Element element, final Object value)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof Image)
    {
      try
      {
        return new DefaultImageReference((Image) value);
      }
      catch (IOException e)
      {
        ContentType.logger.warn("Failed to load content using value " + value, e);
      }
      return null;
    }
    if (value instanceof Shape)
    {
      return value;
    }
    if (value instanceof ImageContainer)
    {
      return value;
    }
    if (value instanceof Component)
    {
      final Component c = (Component) value;
      return new DrawableWrapper(createComponentDrawable(runtime, c));
    }
    if (value instanceof DrawableWrapper)
    {
      return value;
    }
    if (DrawableWrapper.isDrawable(value))
    {
      return new DrawableWrapper(value);
    }

    if (failureCache != null)
    {
      final Object o = failureCache.get(value);
      if (Boolean.TRUE.equals(o))
      {
        return null;
      }
    }
    try
    {
      final ResourceKey contentBase = runtime.getProcessingContext().getContentBase();
      final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
      final ResourceKey key;
      if (value instanceof ResourceKey)
      {
        key = (ResourceKey) value;
      }
      else if (value instanceof Blob)
      {
        final Blob b = (Blob) value;
        final byte[] data = IOUtils.getInstance().readBlob(b);
        key = resManager.createKey(data);
      }
      else if (value instanceof String)
      {
        final Object baseURL = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.CONTENT_BASE);
        if (baseURL instanceof String)
        {
          final ResourceKey baseKey = createKeyFromString(resManager, null, (String) baseURL);
          key = createKeyFromString(resManager, baseKey, (String) value);
        }
        else if (baseURL instanceof ResourceKey)
        {
          final ResourceKey baseKey = (ResourceKey) baseURL;
          key = createKeyFromString(resManager, baseKey, (String) value);
        }
        else if (baseURL != null)
        {
          // if a base-url object is given, we assume that it is indeed valid.
          final ResourceKey baseKey = resManager.createKey(baseURL);
          key = createKeyFromString(resManager, baseKey, (String) value);
        }
        else
        {
          key = createKeyFromString(resManager, contentBase, (String) value);
        }
      }
      else
      {
        key = resManager.createKey(value);
      }
      if (key == null)
      {
        return null;
      }

      final Resource resource = resManager.create(key, contentBase, ContentType.TARGETS);
      final Object resourceContent = resource.getResource();
      if (resourceContent instanceof DrawableWrapper)
      {
        return resourceContent;
      }
      else if (DrawableWrapper.isDrawable(resourceContent))
      {
        return new DrawableWrapper(resourceContent);
      }
      else if (resourceContent instanceof Image)
      {
        return new DefaultImageReference(resource);
      }
      else
      {
        return resourceContent;
      }
    }
    catch (Exception e)
    {
      if (failureCache == null)
      {
        failureCache = new LFUMap(5);
      }
      failureCache.put(value, Boolean.TRUE);
      ContentType.logger.warn("Failed to load content using value " + value, e);
    }
    return null;
  }

  protected final ComponentDrawable createComponentDrawable(final ExpressionRuntime runtime, final Component c)
  {
    final Configuration config = runtime.getConfiguration();
    final ComponentDrawable cd;
    final String drawMode = config.getConfigProperty("org.pentaho.reporting.engine.classic.core.ComponentDrawableMode",
        "shared");
    if ("private".equals(drawMode))
    {
      cd = new ComponentDrawable();
    }
    else if ("synchronized".equals(drawMode))
    {
      cd = new ComponentDrawable();
      cd.setPaintSynchronized(true);
    }
    else
    {
      if (frame == null)
      {
        frame = new JFrame();
      }
      cd = new ComponentDrawable(frame);
      cd.setPaintSynchronized(true);
    }

    final String allowOwnPeer = config.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.AllowOwnPeerForComponentDrawable");
    cd.setAllowOwnPeer("true".equals(allowOwnPeer));
    cd.setComponent(c);
    return cd;
  }

  public ElementMetaData getMetaData()
  {
    if (elementType == null)
    {
      elementType = ElementTypeRegistry.getInstance().getElementType("content");
    }
    return elementType;
  }

  /**
   * Clones this <code>DataSource</code>.
   *
   * @return the clone.
   * @throws CloneNotSupportedException this should never happen.
   */
  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {
    element.getStyle().setStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.TRUE);
    element.getStyle().setStyleProperty(ElementStyleKeys.SCALE, Boolean.TRUE);

    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_WIDTH, new Float(50));
    element.getStyle().setStyleProperty(ElementStyleKeys.MIN_HEIGHT, new Float(50));
  }

  private ResourceKey createKeyFromString (final ResourceManager resourceManager,
                                           final ResourceKey contextKey,
                                           final String file)
  {

    try
    {
      if (contextKey != null)
      {
        return resourceManager.deriveKey(contextKey, file);
      }
    }
    catch (ResourceException re)
    {
      // failed to load from context
      logger.debug("Failed to load datasource as derived path: ", re);
    }

    try
    {
      return resourceManager.createKey(new URL(file));
    }
    catch (ResourceException re)
    {
      logger.debug("Failed to load datasource as URL: ",re);
    }
    catch (MalformedURLException e)
    {
      //
    }

    try
    {
      return resourceManager.createKey(new File(file));
    }
    catch (ResourceException re)
    {
      // failed to load from context
      logger.debug("Failed to load datasource as file: ", re);
    }

    return null;
  }
}
