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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.pentaho.reporting.libraries.base.util.LinkedMap;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class GlobalMetaDefinition implements Cloneable
{
  private HashMap styleGroups;
  private HashMap attributeGroups;

  public GlobalMetaDefinition()
  {
    styleGroups = new HashMap();
    attributeGroups = new HashMap();
  }

  public void addAttributeGroup(final AttributeGroup group)
  {
    if (group == null)
    {
      throw new NullPointerException();
    }

    attributeGroups.put(group.getName(), group);
  }

  public void addStyleGroup(final StyleGroup group)
  {
    if (group == null)
    {
      throw new NullPointerException();
    }
    styleGroups.put(group.getName(), group);
  }

  public StyleGroup getStyleGroup(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    return (StyleGroup) styleGroups.get(name);
  }

  public AttributeGroup getAttributeGroup(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    return (AttributeGroup) attributeGroups.get(name);
  }

  public Object clone() throws CloneNotSupportedException
  {
    final GlobalMetaDefinition definition = (GlobalMetaDefinition) super.clone();
    definition.styleGroups = (HashMap) styleGroups.clone();
    definition.attributeGroups = (HashMap) attributeGroups.clone();
    return definition;
  }

  public void merge(final GlobalMetaDefinition definition)
  {
    if (definition == null)
    {
      throw new NullPointerException();
    }
    mergeStyles(definition);

    final Iterator iterator = definition.attributeGroups.entrySet().iterator();
    while (iterator.hasNext())
    {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final AttributeGroup styleGroup = (AttributeGroup) this.attributeGroups.get(entry.getKey());
      if (styleGroup == null)
      {
        addAttributeGroup((AttributeGroup) entry.getValue());
        continue;
      }
      final AttributeGroup entryGroup = (AttributeGroup) entry.getValue();
      final String name = styleGroup.getName();
      final LinkedMap styles = new LinkedMap();
      final AttributeDefinition[] data = styleGroup.getMetaData();
      for (int i = 0; i < data.length; i++)
      {
        final AttributeDefinition handler = data[i];
        styles.put(handler.getName(), handler);
      }

      final AttributeDefinition[] entryData = entryGroup.getMetaData();
      for (int i = 0; i < entryData.length; i++)
      {
        final AttributeDefinition handler = entryData[i];
        styles.put(handler.getName(), handler);
      }

      addAttributeGroup(new AttributeGroup(name,
          (AttributeDefinition[]) styles.values(new AttributeDefinition[styles.size()])));
    }

  }

  private void mergeStyles(final GlobalMetaDefinition definition)
  {
    final Iterator iterator = definition.styleGroups.entrySet().iterator();
    while (iterator.hasNext())
    {
      final Map.Entry entry = (Map.Entry) iterator.next();
      final StyleGroup styleGroup = (StyleGroup) this.styleGroups.get(entry.getKey());
      if (styleGroup == null)
      {
        addStyleGroup((StyleGroup) entry.getValue());
        continue;
      }
      final StyleGroup entryGroup = (StyleGroup) entry.getValue();
      final String name = styleGroup.getName();
      final LinkedMap styles = new LinkedMap();
      final StyleReadHandler[] data = styleGroup.getMetaData();
      for (int i = 0; i < data.length; i++)
      {
        final StyleReadHandler handler = data[i];
        styles.put(handler.getName(), handler);
      }

      final StyleReadHandler[] entryData = entryGroup.getMetaData();
      for (int i = 0; i < entryData.length; i++)
      {
        final StyleReadHandler handler = entryData[i];
        styles.put(handler.getName(), handler);
      }

      addStyleGroup(new StyleGroup(name, (StyleReadHandler[]) styles.values(new StyleReadHandler[styles.size()])));
    }
  }
}
