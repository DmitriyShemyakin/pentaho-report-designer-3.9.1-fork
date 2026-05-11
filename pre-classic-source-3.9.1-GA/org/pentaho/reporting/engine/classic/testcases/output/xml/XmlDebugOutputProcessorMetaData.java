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
 * Copyright (c) 2007 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.output.xml;

import org.pentaho.reporting.libraries.fonts.FontMappingUtility;
import org.pentaho.reporting.libraries.fonts.itext.ITextFontStorage;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;

/**
 * Creation-Date: 20.10.2007, 16:33:56
 *
 * @author Thomas Morgner
 */
public class XmlDebugOutputProcessorMetaData extends AbstractOutputProcessorMetaData
{
  private boolean pageableMode;

  public XmlDebugOutputProcessorMetaData(final Configuration configuration,
                                         final ITextFontStorage fontStorage,
                                         final boolean pageableMode)
  {
    super(configuration, fontStorage);
    this.pageableMode = pageableMode;
    setFamilyMapping(null, "Helvetica");
    addFeature(OutputProcessorFeature.FAST_FONTRENDERING);
    addFeature(OutputProcessorFeature.BACKGROUND_IMAGE);
    addFeature(OutputProcessorFeature.PAGE_SECTIONS);
    addFeature(OutputProcessorFeature.SPACING_SUPPORTED);
    if (pageableMode)
    {
      addFeature(OutputProcessorFeature.PAGEBREAKS);
      addFeature(OutputProcessorFeature.WATERMARK_SECTION);
    }
  }

  public String getNormalizedFontFamilyName(final String name)
  {
    final String mappedName = super.getNormalizedFontFamilyName(name);
    if (FontMappingUtility.isSerif(mappedName))
    {
      return "Times";
    }
    if (FontMappingUtility.isSansSerif(mappedName))
    {
      return "Helvetica";
    }
    if (FontMappingUtility.isCourier(mappedName))
    {
      return "Courier";
    }
    if (FontMappingUtility.isSymbol(mappedName))
    {
      return "Symbol";
    }
    return mappedName;
  }

  public String getExportDescriptor()
  {
    if (pageableMode)
    {
      return "pageable/xml-debug";
    }
    return "table/xml-debug";
  }
}
