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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabOtherGroupType;

/**
 * Can have either a row- or a column body.
 *
 * @author Thomas Morgner
 */
public class CrosstabOtherGroup extends Group
{
  public CrosstabOtherGroup()
  {
    setElementType(new CrosstabOtherGroupType());
  }

  public String getField()
  {
    final Object o = getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD);
    if (o == null)
    {
      return null;
    }
    return o.toString();
  }

  public void setField(final String field)
  {
    setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, field);
    notifyNodePropertiesChanged();
  }

  protected GroupBody createDefaultBody()
  {
    return new CrosstabRowGroupBody();
  }

  public void setBody(final GroupBody body)
  {
    if (body instanceof CrosstabRowGroupBody == false &&
        body instanceof CrosstabOtherGroupBody == false)
    {
      throw new IllegalArgumentException();
    }
    super.setBody(body);
  }

  public boolean isGroupChange(final DataRow dataRow)
  {
    final String fieldName = getField();
    if (fieldName == null)
    {
      return false;
    }
    if (dataRow.isChanged(fieldName))
    {
      return true;
    }
    return false;
  }
}
