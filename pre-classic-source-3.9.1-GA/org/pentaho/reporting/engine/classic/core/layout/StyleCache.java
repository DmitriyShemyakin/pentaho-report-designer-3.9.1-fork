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

package org.pentaho.reporting.engine.classic.core.layout;

import org.pentaho.reporting.engine.classic.core.layout.style.NonPaddingWrapperStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.LFUMap;

/**
 * Creation-Date: 26.04.2007, 20:47:23
 *
 * @author Thomas Morgner
 */
public class StyleCache
{
  private static class CacheKey
  {
    private Object instanceId;
    private String styleClass;

    protected CacheKey()
    {
    }

    protected CacheKey(final Object instanceId, final String styleClass)
    {
      if (instanceId == null)
      {
        throw new NullPointerException();
      }
      if (styleClass == null)
      {
        throw new NullPointerException();
      }
      this.instanceId = instanceId;
      this.styleClass = styleClass;
    }

    public Object getInstanceId()
    {
      return instanceId;
    }

    public void setInstanceId(final Object instanceId)
    {
      this.instanceId = instanceId;
    }

    public String getStyleClass()
    {
      return styleClass;
    }

    public void setStyleClass(final String styleClass)
    {
      this.styleClass = styleClass;
    }

    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if (!instanceId.equals(cacheKey.instanceId))
      {
        return false;
      }
      if (!styleClass.equals(cacheKey.styleClass))
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      int result = instanceId.hashCode();
      result = 31 * result + styleClass.hashCode();
      return result;
    }

    public String toString()
    {
      return "CacheKey{" +
          "instanceId=" + instanceId +
          ", styleClass='" + styleClass + '\'' +
          '}';
    }
  }

  private static class CacheCarrier
  {
    private long changeTracker;
    private SimpleStyleSheet styleSheet;

    protected CacheCarrier(final long changeTracker, final SimpleStyleSheet styleSheet)
    {
      this.changeTracker = changeTracker;
      this.styleSheet = styleSheet;
    }

    public long getChangeTracker()
    {
      return changeTracker;
    }

    public SimpleStyleSheet getStyleSheet()
    {
      return styleSheet;
    }
  }

  private LFUMap<CacheKey, CacheCarrier> cache;
  private boolean omitPadding;
  private NonPaddingWrapperStyleSheet nonPaddingWrapperStyleSheet;
  private CacheKey lookupKey;

  public StyleCache(final boolean omitPadding)
  {
    this.lookupKey = new CacheKey();
    this.omitPadding = omitPadding;
    this.cache = new LFUMap<CacheKey, CacheCarrier>(100);
    this.nonPaddingWrapperStyleSheet = new NonPaddingWrapperStyleSheet();
  }

  public SimpleStyleSheet getStyleSheet(final StyleSheet parent)
  {
    if (omitPadding)
    {
      // this only works, because we know that the created SimpleStyleSheet will not hold any references
      // to this wrapper-stylesheet.
      nonPaddingWrapperStyleSheet.setParent(parent);

      try
      {
        final InstanceID id = nonPaddingWrapperStyleSheet.getId();
        final String styleClass = NonPaddingWrapperStyleSheet.class.getName() + "|" + parent.getClass().getName();
        lookupKey.setStyleClass(styleClass);
        lookupKey.setInstanceId(id);
        final CacheCarrier cc = cache.get(lookupKey);
        if (cc == null)
        {
          final CacheKey key = new CacheKey(id, NonPaddingWrapperStyleSheet.class.getName());
          final SimpleStyleSheet styleSheet = new SimpleStyleSheet(nonPaddingWrapperStyleSheet);
          cache.put(key, new CacheCarrier(nonPaddingWrapperStyleSheet.getChangeTracker(), styleSheet));
          return styleSheet;
        }

        if (cc.getChangeTracker() != nonPaddingWrapperStyleSheet.getChangeTracker())
        {
          final CacheKey key = new CacheKey(id, styleClass);
          final SimpleStyleSheet styleSheet = new SimpleStyleSheet(nonPaddingWrapperStyleSheet);
          cache.put(key, new CacheCarrier(nonPaddingWrapperStyleSheet.getChangeTracker(), styleSheet));
          return styleSheet;
        }

        return cc.getStyleSheet();
      }
      finally
      {
        nonPaddingWrapperStyleSheet.setParent(null);
      }
    }

    final InstanceID id = parent.getId();
    final String styleClass = parent.getClass().getName();
    lookupKey.setStyleClass(styleClass);
    lookupKey.setInstanceId(id);
    final CacheCarrier cc = cache.get(lookupKey);
    if (cc == null)
    {
      final CacheKey key = new CacheKey(id, styleClass);
      final SimpleStyleSheet styleSheet = new SimpleStyleSheet(parent);
      cache.put(key, new CacheCarrier(parent.getChangeTracker(), styleSheet));
      return styleSheet;
    }

    if (cc.getChangeTracker() != parent.getChangeTracker())
    {
      final CacheKey key = new CacheKey(id, styleClass);
      final SimpleStyleSheet styleSheet = new SimpleStyleSheet(parent);
      cache.put(key, new CacheCarrier(parent.getChangeTracker(), styleSheet));
      return styleSheet;
    }

    return cc.getStyleSheet();
  }
}
