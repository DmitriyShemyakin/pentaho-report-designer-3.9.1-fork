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

package org.pentaho.reporting.engine.classic.core.wizard;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class DefaultDataSchemaDefinition implements DataSchemaDefinition
{
  private ArrayList backend;

  public DefaultDataSchemaDefinition()
  {
    backend = new ArrayList();
  }

  public void addRule(final DataSchemaRule rule)
  {
    if (rule == null)
    {
      throw new NullPointerException();
    }
    backend.add(rule);
  }

  public DataSchemaRule getRule(final int index)
  {
    return (DataSchemaRule) backend.get(index);
  }

  public int getRuleCount()
  {
    return backend.size();
  }

  /**
   * Returns all known rules.
   *
   * @return
   */
  public GlobalRule[] getGlobalRules()
  {
    final ArrayList retval = new ArrayList();
    for (int i = 0; i < backend.size(); i++)
    {
      final DataSchemaRule rule = (DataSchemaRule) backend.get(i);
      if (rule instanceof GlobalRule)
      {
        retval.add(rule);
      }
    }
    return (GlobalRule[]) retval.toArray(new GlobalRule[retval.size()]);
  }

  public MetaSelectorRule[] getIndirectRules()
  {
    final ArrayList retval = new ArrayList();
    for (int i = 0; i < backend.size(); i++)
    {
      final DataSchemaRule rule = (DataSchemaRule) backend.get(i);
      if (rule instanceof MetaSelectorRule)
      {
        retval.add(rule);
      }
    }
    return (MetaSelectorRule[]) retval.toArray(new MetaSelectorRule[retval.size()]);
  }

  public DirectFieldSelectorRule[] getDirectRules()
  {
    final ArrayList retval = new ArrayList();
    for (int i = 0; i < backend.size(); i++)
    {
      final DataSchemaRule rule = (DataSchemaRule) backend.get(i);
      if (rule instanceof DirectFieldSelectorRule)
      {
        retval.add(rule);
      }
    }
    return (DirectFieldSelectorRule[]) retval.toArray(new DirectFieldSelectorRule[retval.size()]);
  }

  /**
   * Returns all known rules.
   *
   * @return
   */
  public DataSchemaRule[] getRules()
  {
    return (DataSchemaRule[]) backend.toArray(new DataSchemaRule[backend.size()]);
  }

  public void merge(final DataSchemaDefinition schemaDefinition)
  {
    if (schemaDefinition == null)
    {
      throw new NullPointerException();
    }
    final GlobalRule[] globalRules = schemaDefinition.getGlobalRules();
    final MetaSelectorRule[] indirectRules = schemaDefinition.getIndirectRules();
    final DirectFieldSelectorRule[] directRules = schemaDefinition.getDirectRules();
    backend.ensureCapacity(backend.size() + globalRules.length + indirectRules.length + directRules.length);
    backend.addAll(Arrays.asList(globalRules));
    backend.addAll(Arrays.asList(indirectRules));
    backend.addAll(Arrays.asList(directRules));
  }

  public Object clone() throws CloneNotSupportedException
  {
    final DefaultDataSchemaDefinition def = (DefaultDataSchemaDefinition) super.clone();
    def.backend = (ArrayList) backend.clone();
    return def;
  }
}
