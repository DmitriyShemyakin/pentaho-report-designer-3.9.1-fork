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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.ReportFooter;
import org.pentaho.reporting.engine.classic.core.ReportHeader;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class RootLevelContentReadHandler extends BandReadHandler
{
  public RootLevelContentReadHandler(final String elementType)
      throws ParseException
  {
    super(elementType);
  }

  protected Element createElement(final String elementType) throws ParseException
  {
    if ("report-header".equals(elementType))
    {
      return new ReportHeader();
    }
    if ("report-footer".equals(elementType))
    {
      return new ReportFooter();
    }
    if ("group-header".equals(elementType))
    {
      return new GroupHeader();
    }
    if ("group-footer".equals(elementType))
    {
      return new GroupFooter();
    }
    if ("itemband".equals(elementType))
    {
      return new ItemBand();
    }
    if ("no-data-band".equals(elementType))
    {
      return new NoDataBand();
    }
    throw new ParseException("Unregognized root level type");
  }
}