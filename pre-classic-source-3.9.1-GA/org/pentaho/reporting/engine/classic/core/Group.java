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

/**
 * A report group.  Reports can contain any number of (nested) groups. The order of the fields is not important. If the
 * group does not contain any fields, the group spans the whole report from the first to the last row (such a group is
 * called the default group).
 * <p/>
 * The group's field list should not be modified after the group was added to the group list, or the results are
 * undefined.
 * <p/>
 * Groups of the same GroupList must have a subgroup relation. The designated child group must contain all fields of the
 * direct parent plus at least one new field. There is no requirement, that the referenced field actually exists, if it
 * doesn't, null is assumed as field value.
 * <p/>
 * It is recommended that the name of the group is unique within the report. The name will not be used internally to
 * identify the group, but most functions depend on a recognizable group name to identify the group to be processed.
 *
 * @author David Gilbert
 * @author Thomas Morgner
 * @see GroupList
 */
public abstract class Group extends Section
{
  /**
   * A unique identifier for long term persistance.
   */
  private static final long serialVersionUID = 8309478419800349694L;

  /**
   * The group header (optional).
   */
  private GroupHeader header;

  /**
   * The group footer (optional).
   */
  private GroupFooter footer;

  private GroupBody body;

  /**
   * The internal constant to mark anonymous group names.
   */
  public static final String ANONYMOUS_GROUP_PREFIX = "anonymousGroup@";

  /**
   * Constructs a group with no fields, and an empty header and footer.
   */
  protected Group()
  {
    this.footer = new GroupFooter();
    this.header = new GroupHeader();
    this.body = createDefaultBody();

    registerAsChild(footer);
    registerAsChild(header);
    registerAsChild(body);
  }

  /**
   * Returns the group header. <P> The group header is a report band that contains elements that should be printed at
   * the start of a group.
   *
   * @return the group header.
   */
  public GroupHeader getHeader()
  {
    return header;
  }

  /**
   * Sets the header for the group.
   *
   * @param header the header (null not permitted).
   * @throws NullPointerException if the given header is null
   */
  public void setHeader(final GroupHeader header)
  {
    if (header == null)
    {
      throw new NullPointerException("Header must not be null");
    }
    validateLooping(header);
    if (unregisterParent(header))
    {
      return;
    }

    final Element element = this.header;
    this.header.setParent(null);
    this.header = header;
    this.header.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.header);
  }

  public GroupBody getBody()
  {
    return body;
  }

  public void setBody(final GroupBody body)
  {
    if (body == null)
    {
      throw new NullPointerException("The body must not be null");
    }
    validateLooping(body);
    if (unregisterParent(body))
    {
      return;
    }

    final Element element = this.body;
    this.body.setParent(null);
    this.body = body;
    this.body.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.body);
  }

  /**
   * Returns the group footer.
   *
   * @return the footer.
   */
  public GroupFooter getFooter()
  {
    return footer;
  }

  /**
   * Sets the footer for the group.
   *
   * @param footer the footer (null not permitted).
   * @throws NullPointerException if the given footer is null.
   */
  public void setFooter(final GroupFooter footer)
  {
    if (footer == null)
    {
      throw new NullPointerException("The footer must not be null");
    }
    validateLooping(footer);
    if (unregisterParent(footer))
    {
      return;
    }

    final Element element = this.footer;
    this.footer.setParent(null);
    this.footer = footer;
    this.footer.setParent(this);

    notifyNodeChildRemoved(element);
    notifyNodeChildAdded(this.footer);
  }

  /**
   * Clones this Element.
   *
   * @return a clone of this element.
   * @throws CloneNotSupportedException should never be thrown.
   */
  public Object clone()
      throws CloneNotSupportedException
  {
    final Group g = (Group) super.clone();
    g.footer = (GroupFooter) footer.clone();
    g.header = (GroupHeader) header.clone();
    g.body = (GroupBody) body.clone();

    g.registerAsChild(g.footer);
    g.registerAsChild(g.header);
    g.registerAsChild(g.body);
    return g;
  }

  public Element derive(final boolean preserveElementInstanceIds)
      throws CloneNotSupportedException
  {
    final Group g = (Group) super.derive(preserveElementInstanceIds);
    g.footer = (GroupFooter) footer.derive(preserveElementInstanceIds);
    g.header = (GroupHeader) header.derive(preserveElementInstanceIds);
    g.body = (GroupBody) body.derive(preserveElementInstanceIds);

    g.registerAsChild(g.footer);
    g.registerAsChild(g.header);
    g.registerAsChild(g.body);
    return g;
  }

  public abstract boolean isGroupChange(final DataRow dataRow);

  protected void removeElement(final Element element)
  {
    if (element == null)
    {
      throw new NullPointerException();
    }

    if (footer == element)
    {
      this.footer.setParent(null);
      this.footer = new GroupFooter();
      this.footer.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.footer);

    }
    else if (header == element)
    {
      this.header.setParent(null);
      this.header = new GroupHeader();
      this.header.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.header);
    }
    else if (body == element)
    {
      this.body.setParent(null);
      this.body = createDefaultBody();
      this.body.setParent(this);

      notifyNodeChildRemoved(element);
      notifyNodeChildAdded(this.body);
    }
    // Else: Ignore the request, none of my childs.
  }

  protected abstract GroupBody createDefaultBody();

  public int getElementCount()
  {
    return 3;
  }

  public ReportElement getElement(final int index)
  {
    switch (index)
    {
      case 0:
        return header;
      case 1:
        return body;
      case 2:
        return footer;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

  public void setElementAt(final int index, final Element element)
  {
    switch (index)
    {
      case 0:
        setHeader((GroupHeader) element);
        break;
      case 1:
        setBody((GroupBody) element);
        break;
      case 2:
        setFooter((GroupFooter) element);
        break;
      default:
        throw new IndexOutOfBoundsException();
    }
  }

}
