package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.types;

import java.util.Locale;

import org.olap4j.metadata.Member;

/**
 * Todo: Document me!
 * <p/>
 * Date: 08.09.2010
 * Time: 19:36:56
 *
 * @author Thomas Morgner.
 */
public class LocalizedString
{
  private Member member;
  private boolean description;

  public LocalizedString(final Member member, final boolean description)
  {
    this.member = member;
    this.description = description;
  }

  public String getValue(final Locale locale)
  {
    if (description)
    {
      return member.getDescription();
    }
    return member.getCaption();
  }
}
