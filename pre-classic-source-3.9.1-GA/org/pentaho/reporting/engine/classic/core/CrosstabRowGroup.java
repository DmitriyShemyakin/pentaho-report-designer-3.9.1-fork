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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabRowGroupType;

/**
 * Can have either a row- or a column body.
 *
 * @author Thomas Morgner
 */
public class CrosstabRowGroup extends Group
{
  private CrosstabTitleHeader titleHeader;
  private CrosstabTitleFooter titleFooter;
  private CrosstabSummaryHeader summaryHeader;
  private CrosstabSummaryFooter summaryFooter;

  public CrosstabRowGroup()
  {
    setElementType(new CrosstabRowGroupType());
    titleHeader = new CrosstabTitleHeader();
    titleFooter = new CrosstabTitleFooter();
    registerAsChild(titleHeader);
    registerAsChild(titleFooter);
    summaryHeader = new CrosstabSummaryHeader();
    summaryFooter = new CrosstabSummaryFooter();
    registerAsChild(summaryHeader);
    registerAsChild(summaryFooter);
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


  public CrosstabSummaryHeader getSummaryHeader()
  {
    return summaryHeader;
  }

  public void setSummaryHeader(final CrosstabSummaryHeader summaryHeader)
  {
    if (summaryHeader == null)
    {
      throw new NullPointerException("summaryHeader must not be null");
    }
    validateLooping(summaryHeader);
    if (unregisterParent(summaryHeader))
    {
      return;
    }

    final Element element = this.summaryHeader;
    this.summaryHeader.setParent(null);
    this.summaryHeader = summaryHeader;
    this.summaryHeader.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.summaryHeader);
  }

  public CrosstabSummaryFooter getSummaryFooter()
  {
    return summaryFooter;
  }

  public void setSummaryFooter(final CrosstabSummaryFooter summaryFooter)
  {
    if (summaryFooter == null)
    {
      throw new NullPointerException("summaryFooter must not be null");
    }
    validateLooping(summaryFooter);
    if (unregisterParent(summaryFooter))
    {
      return;
    }

    final Element element = this.summaryFooter;
    this.summaryFooter.setParent(null);
    this.summaryFooter = summaryFooter;
    this.summaryFooter.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.summaryFooter);
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
    return new CrosstabColumnGroupBody();
  }

  public void setBody(final GroupBody body)
  {
    if (body instanceof CrosstabRowGroupBody == false &&
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
    final CrosstabRowGroup element = (CrosstabRowGroup) super.clone();
    element.titleHeader = (CrosstabTitleHeader) titleHeader.clone();
    element.titleFooter = (CrosstabTitleFooter) titleFooter.clone();
    element.registerAsChild(element.titleHeader);
    element.registerAsChild(element.titleFooter);
    element.summaryHeader = (CrosstabSummaryHeader) summaryHeader.clone();
    element.summaryFooter = (CrosstabSummaryFooter) summaryFooter.clone();
    element.registerAsChild(element.summaryHeader);
    element.registerAsChild(element.summaryFooter);
    return element;
  }

  public Element derive(final boolean preserveElementInstanceIds)
      throws CloneNotSupportedException
  {
    final CrosstabRowGroup element = (CrosstabRowGroup) super.derive(preserveElementInstanceIds);
    element.titleHeader = (CrosstabTitleHeader) titleHeader.derive(preserveElementInstanceIds);
    element.titleFooter = (CrosstabTitleFooter) titleFooter.derive(preserveElementInstanceIds);
    element.registerAsChild(element.titleHeader);
    element.registerAsChild(element.titleFooter);
    element.summaryHeader = (CrosstabSummaryHeader) summaryHeader.derive(preserveElementInstanceIds);
    element.summaryFooter = (CrosstabSummaryFooter) summaryFooter.derive(preserveElementInstanceIds);
    element.registerAsChild(element.summaryHeader);
    element.registerAsChild(element.summaryFooter);
    return element;
  }

  public int getElementCount()
  {
    return 7;
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
        return getSummaryHeader();
      case 3:
        return getBody();
      case 4:
        return getTitleFooter();
      case 5:
        return getFooter();
      case 6:
        return getSummaryFooter();
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
        setSummaryHeader((CrosstabSummaryHeader) element);
        break;
      case 3:
        setBody((GroupBody) element);
        break;
      case 4:
        setTitleFooter((CrosstabTitleFooter) element);
        break;
      case 5:
        setFooter((GroupFooter) element);
        break;
      case 6:
        setSummaryFooter((CrosstabSummaryFooter) element);
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
    else if (summaryHeader == element)
    {
      this.summaryHeader.setParent(null);
      this.summaryHeader = new CrosstabSummaryHeader();
      this.summaryHeader.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.summaryHeader);
    }
    else if (summaryFooter == element)
    {
      this.summaryFooter.setParent(null);
      this.summaryFooter = new CrosstabSummaryFooter();
      this.summaryFooter.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.summaryFooter);
    }
    else
    {
      super.removeElement(element);
    }
  }
}
