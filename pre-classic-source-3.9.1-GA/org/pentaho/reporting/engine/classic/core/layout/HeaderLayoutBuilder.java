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

package org.pentaho.reporting.engine.classic.core.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

public class HeaderLayoutBuilder implements LayoutBuilder
{
  private static final Log logger = LogFactory.getLog(HeaderLayoutBuilder.class);

  private DefaultLayoutBuilder backend;

  public HeaderLayoutBuilder(final OutputProcessorMetaData metaData)
  {
    this.backend = new DefaultLayoutBuilder(metaData);
    this.backend.setCollapseProgressMarker(false);
  }

  public void startSection(final RenderBox pageArea, final boolean limitedSubReports)
  {
    backend.startSection(pageArea, limitedSubReports);
  }

  public void add(final RenderBox parent,
                  final Band band,
                  final ExpressionRuntime runtime,
                  final ReportStateKey stateKey) throws ReportProcessingException
  {
    logger.debug("Added Band to Header ( ): " + stateKey);
    logger.debug("Added Band to Header (1): " + band);

    backend.add(parent, band, runtime, stateKey);
  }

  public void addEmptyRootLevelBand(final RenderBox parent,
                                    final ReportStateKey stateKey) throws ReportProcessingException
  {
    backend.addEmptyRootLevelBand(parent, stateKey);
  }

  public InlineSubreportMarker[] endSection(final RenderBox pageArea, final RenderBox sectionBox)
  {
    final InlineSubreportMarker[] retval = backend.endSection(pageArea, sectionBox);

    pageArea.clear();
    pageArea.addChild(sectionBox);
    return retval;
  }
}
