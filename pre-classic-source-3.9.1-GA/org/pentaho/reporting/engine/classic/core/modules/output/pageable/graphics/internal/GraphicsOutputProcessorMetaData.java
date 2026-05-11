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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.internal;

import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfiguration;
import org.pentaho.reporting.libraries.base.config.ExtendedConfigurationWrapper;
import org.pentaho.reporting.libraries.fonts.awt.AWTFontRegistry;
import org.pentaho.reporting.libraries.fonts.registry.DefaultFontStorage;
import org.pentaho.reporting.libraries.fonts.registry.FontStorage;

/**
 * Creation-Date: 02.01.2006, 19:57:08
 *
 * @author Thomas Morgner
 */
public class GraphicsOutputProcessorMetaData extends AbstractOutputProcessorMetaData
{
  public GraphicsOutputProcessorMetaData(final Configuration configuration)
  {
    this(configuration, new DefaultFontStorage(new AWTFontRegistry()));
  }

  public GraphicsOutputProcessorMetaData(final Configuration configuration,
                                         final FontStorage storage)
  {
    super(configuration, storage);
    addFeature(OutputProcessorFeature.FAST_FONTRENDERING);
    addFeature(OutputProcessorFeature.BACKGROUND_IMAGE);
    addFeature(OutputProcessorFeature.PAGE_SECTIONS);
    addFeature(OutputProcessorFeature.PAGEBREAKS);
    addFeature(OutputProcessorFeature.SPACING_SUPPORTED);

    if ("true".equals(configuration.getConfigProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.WatermarkPrinted")))
    {
      addFeature(OutputProcessorFeature.WATERMARK_SECTION);
    }
    final ExtendedConfiguration extendedConfig = new ExtendedConfigurationWrapper(configuration);
    final double deviceResolution = extendedConfig.getIntProperty
        ("org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.DeviceResolution", 0);
    if (deviceResolution > 0)
    {
      setNumericFeatureValue(OutputProcessorFeature.DEVICE_RESOLUTION, deviceResolution);
    }

    if ("true".equals(configuration.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.AssumeOverflowX")))
    {
      addFeature(OutputProcessorFeature.ASSUME_OVERFLOW_X);
    }
    if ("true".equals(configuration.getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.AssumeOverflowY")))
    {
      addFeature(OutputProcessorFeature.ASSUME_OVERFLOW_Y);
    }

  }

  public String getExportDescriptor()
  {
    return "pageable/X-AWT-Graphics";
  }
}
