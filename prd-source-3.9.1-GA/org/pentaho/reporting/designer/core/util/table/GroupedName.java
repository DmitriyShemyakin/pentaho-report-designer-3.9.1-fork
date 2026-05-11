package org.pentaho.reporting.designer.core.util.table;

import java.io.Serializable;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.12.2009
 * Time: 16:57:31
 *
 * @author Thomas Morgner.
 */
public class GroupedName implements Serializable, Comparable
{
  private String name;
  private String groupName;

  public GroupedName(final String name, final String groupName)
  {
    if (groupName == null)
    {
      throw new NullPointerException();
    }
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
    this.groupName = groupName;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name = name;
  }

  public String getGroupName()
  {
    return groupName;
  }

  public int compareTo(final Object o)
  {
    final GroupedName other = (GroupedName) o;
    if (other == null)
    {
      return 1;
    }
    final int nameResult = name.compareTo(other.name);
    if (nameResult != 0)
    {
      return nameResult;
    }

    return groupName.compareTo(other.groupName);
  }
}
