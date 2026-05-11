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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.types;

import org.pentaho.metadata.model.concept.types.Font;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class FontBoldConceptMapper implements ConceptQueryMapper
{
  public FontBoldConceptMapper()
  {
  }

  /**
   * @param value
   * @param type
   * @return
   */
  public Object getValue(final Object value, final Class type, final DataAttributeContext context)
  {
    if (value == null)
    {
      return null;
    }

    if (value instanceof Font == false)
    {
      return null;
    }

    if (type == null || Font.class.equals(type))
    {
      return value;
    }

    final Font fontSettings = (Font) value;
    if (String.class.isAssignableFrom(type))
    {
      return String.valueOf(fontSettings.isBold());
    }
    if (Boolean.class.isAssignableFrom(type))
    {
      return fontSettings.isBold();
    }
    return null;
  }
}