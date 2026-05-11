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

package org.pentaho.reporting.engine.classic.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.RelationalGroupType;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A group that accepts fields.
 *
 * @author Thomas Morgner
 */
public class RelationalGroup extends Group
{
  private static final String[] EMPTY_FIELDS = new String[0];

  public RelationalGroup()
  {
    setElementType(new RelationalGroupType());
  }

  /**
   * Sets the fields for this group. The given list must contain Strings defining the needed fields from the DataRow.
   * Don't reference Function-Fields here, functions are not supported in th groupfield definition.
   *
   * @param c the list containing strings.
   * @throws NullPointerException if the given list is null or the list contains null-values.
   */
  public void setFields(final List c)
  {
    if (c == null)
    {
      throw new NullPointerException();
    }
    final String[] fields = (String[]) c.toArray(new String[c.size()]);
    setFieldsArray(fields);
  }

  public void clearFields()
  {
    setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, EMPTY_FIELDS);
  }

  protected GroupBody createDefaultBody()
  {
    return new GroupDataBody();
  }

  /**
   * Adds a field to the group.  The field names must correspond to the column names in the report's TableModel.
   *
   * @param name the field name (null not permitted).
   * @throws NullPointerException if the name is null
   */
  public void addField(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException("Group.addField(...): name is null.");
    }
    final ArrayList fieldsList = new ArrayList(getFields());
    fieldsList.add(name);
    Collections.sort(fieldsList);
    setFieldsArray((String[]) fieldsList.toArray(new String[fieldsList.size()]));
  }

  /**
   * Returns the list of fields for this group.
   *
   * @return a list (unmodifiable) of fields for the group.
   */
  public List getFields()
  {
    return Collections.unmodifiableList(Arrays.asList(getFieldsArray()));
  }

  public void setFieldsArray(final String[] fields)
  {
    if (fields == null)
    {
      throw new NullPointerException();
    }
    setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS, fields.clone());
  }

  /**
   * Returns the group fields as array.
   *
   * @return the fields as string array.
   */
  public String[] getFieldsArray()
  {
    final Object o = getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS);
    if (o instanceof String[])
    {
      final String[] fields = (String[]) o;
      return (String[]) fields.clone();
    }
    return EMPTY_FIELDS;
  }

  /**
   * Returns a string representation of the group (useful for debugging).
   *
   * @return A string.
   */
  public String toString()
  {
    final StringBuilder b = new StringBuilder(120);
    b.append("DefaultGroup={Name='");
    b.append(getName());
    b.append("', fields=");
    b.append(getFields());
    b.append("} ");
    return b.toString();
  }

  public void setBody(final GroupBody body)
  {
    if (body instanceof GroupDataBody == false &&
        body instanceof SubGroupBody == false)
    {
      throw new IllegalArgumentException();
    }

    super.setBody(body);
  }

  /**
   * Checks whether the group is equal. A group is considered equal to another group, if it defines the same fields as
   * the other group.
   *
   * @param obj the object to be checked
   * @return true, if the object is a group instance with the same fields, false otherwise.
   */
  public boolean equals(final Object obj)
  {
    if (this == obj)
    {
      return true;
    }
    if (!(obj instanceof RelationalGroup))
    {
      return false;
    }

    final RelationalGroup group = (RelationalGroup) obj;
    final String[] otherFields = group.getFieldsArray();
    final String[] myFields = getFieldsArray();
    if (ObjectUtilities.equalArray(otherFields, myFields) == false)
    {
      return false;
    }
    return true;
  }

  /**
   * Computes a hashcode for this group.
   *
   * @return the hashcode.
   */
  public int hashCode()
  {
    final String[] fields = getFieldsArray();

    int hashCode = 0;
    final int length = fields.length;
    for (int i = 0; i < length; i++)
    {
      final String field = fields[i];
      if (field == null)
      {
        hashCode = 29 * hashCode;
      }
      else
      {
        hashCode = 29 * hashCode + field.hashCode();
      }
    }
    return hashCode;
  }

  public boolean isGroupChange(final DataRow dataRow)
  {
    // compare item and item+1, if any field differs, then item==last in group
    final Object o = getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.GROUP_FIELDS);
    if (o instanceof String[])
    {
      final String[] fields = (String[]) o;
      for (int i = 0; i < fields.length; i++)
      {
        final String field = fields[i];
        if (field != null && dataRow.isChanged(field))
        {
          return true;
        }
      }
    }

    return false;
  }
}
