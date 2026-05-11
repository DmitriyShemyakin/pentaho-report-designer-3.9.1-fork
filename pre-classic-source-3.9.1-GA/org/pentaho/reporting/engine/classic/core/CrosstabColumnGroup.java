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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabColumnGroupType;

/**
 * Can have either a column body or a details body.
 *
 * @author Thomas Morgner
 */
public class CrosstabColumnGroup extends Group
{
  private CrosstabTitleHeader titleHeader;
  private CrosstabTitleFooter titleFooter;

  public CrosstabColumnGroup()
  {
    setElementType(new CrosstabColumnGroupType());
    titleHeader = new CrosstabTitleHeader();
    titleFooter = new CrosstabTitleFooter();
    registerAsChild(titleHeader);
    registerAsChild(titleFooter);
  }

  public CrosstabTitleHeader getTitleHeader()
  {
    return titleHeader;
  }

  public void setTitleHeader(final CrosstabTitleHeader titleHeader)
  {
    if (titleHeader == null)
    {
      throw new NullPointerException("titleHeader must not be null");
    }
    validateLooping(titleHeader);
    if (unregisterParent(titleHeader))
    {
      return;
    }

    final Element element = this.titleHeader;
    this.titleHeader.setParent(null);
    this.titleHeader = titleHeader;
    this.titleHeader.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.titleHeader);
  }

  public CrosstabTitleFooter getTitleFooter()
  {
    return titleFooter;
  }

  public void setTitleFooter(final CrosstabTitleFooter titleFooter)
  {
    if (titleFooter == null)
    {
      throw new NullPointerException("titleFooter must not be null");
    }
    validateLooping(titleFooter);
    if (unregisterParent(titleFooter))
    {
      return;
    }

    final Element element = this.titleFooter;
    this.titleFooter.setParent(null);
    this.titleFooter = titleFooter;
    this.titleFooter.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.titleFooter);
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
    return new GroupDataBody();
  }

  public void setBody(final GroupBody body)
  {
    if (body instanceof GroupDataBody == false &&
        body instanceof CrosstabColumnGroupBody == false)
    {
      throw new IllegalArgumentException();
    }
    super.setBody(body);
  }

  public boolean isGroupChange(final DataRow dataRow)
  {
    final String field = getField();
    if (field == null)
    {
      return false;
    }
    if (dataRow.isChanged(field))
    {
      return true;
    }
    return false;
  }

  public Object clone() throws CloneNotSupportedException
  {
    final CrosstabColumnGroup element = (CrosstabColumnGroup) super.clone();
    element.titleHeader = (CrosstabTitleHeader) titleHeader.clone();
    element.titleFooter = (CrosstabTitleFooter) titleFooter.clone();
    element.registerAsChild(element.titleHeader);
    element.registerAsChild(element.titleFooter);
    return element;
  }

  public Element derive(final boolean preserveElementInstanceIds)
      throws CloneNotSupportedException
  {
    final CrosstabColumnGroup element = (CrosstabColumnGroup) super.derive(preserveElementInstanceIds);
    element.titleHeader = (CrosstabTitleHeader) titleHeader.derive(preserveElementInstanceIds);
    element.titleFooter = (CrosstabTitleFooter) titleFooter.derive(preserveElementInstanceIds);
    element.registerAsChild(element.titleHeader);
    element.registerAsChild(element.titleFooter);
    return element;
  }

  public int getElementCount()
  {
    return 5;
  }

  public ReportElement getElement(final int index)
  {
    switch (index)
    {
      case 0:
        return getTitleHeader();
      case 1:
        return getHeader();
      case 2:
        return getBody();
      case 3:
        return getTitleFooter();
      case 4:
        return getFooter();
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setElementAt(final int index, final Element element)
  {
    switch (index)
    {
      case 0:
        setTitleHeader((CrosstabTitleHeader) element);
        break;
      case 1:
        setHeader((GroupHeader) element);
        break;
      case 2:
        setBody((GroupBody) element);
        break;
      case 3:
        setTitleFooter((CrosstabTitleFooter) element);
        break;
      case 4:
        setFooter((GroupFooter) element);
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  protected void removeElement(final Element element)
  {
    if (element == null)
    {
      throw new NullPointerException();
    }

    if (titleHeader == element)
    {
      this.titleHeader.setParent(null);
      this.titleHeader = new CrosstabTitleHeader();
      this.titleHeader.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.titleHeader);
    }
    else if (titleFooter == element)
    {
      this.titleFooter.setParent(null);
      this.titleFooter = new CrosstabTitleFooter();
      this.titleFooter.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.titleFooter);
    }
    else
    {
      super.removeElement(element);
    }
  }
}
