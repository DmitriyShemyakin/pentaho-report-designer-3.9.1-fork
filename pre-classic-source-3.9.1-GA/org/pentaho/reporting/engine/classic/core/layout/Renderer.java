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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

/**
 * Creation-Date: 08.04.2007, 16:35:29
 *
 * @author Thomas Morgner
 */
public interface Renderer extends Cloneable
{
  public static enum LayoutResult
  {
    LAYOUT_UNVALIDATABLE, LAYOUT_NO_PAGEBREAK, LAYOUT_PAGEBREAK
  }

  public static final int TYPE_NORMALFLOW = 0;
  public static final int TYPE_HEADER = 1;
  public static final int TYPE_FOOTER = 2;
  public static final int TYPE_REPEATED_FOOTER = 3;
  public static final int TYPE_WATERMARK = 4;

  public OutputProcessor getOutputProcessor();

  public void startReport(final ReportDefinition pageDefinition);

  public void startSubReport(final ReportDefinition report, final InstanceID insertationPoint);

  public void startGroup(final Group group);

  public void startGroupBody(final GroupBody groupBody);

  public void startSection(int type);

  public InlineSubreportMarker[] endSection();

  public void addEmptyRootLevelBand(final ReportStateKey stateKey)
      throws ReportProcessingException;

  public void add(Band band, ExpressionRuntime runtime, final ReportStateKey stateKey)
      throws ReportProcessingException;

  public void add(RenderBox box);

  public void endGroupBody();

  public void endGroup();

  public void endSubReport();

  public void endReport();

  public LayoutResult validatePages()
      throws ContentProcessingException;

  public boolean processPage(final LayoutPagebreakHandler handler,
                             final Object commitMarker,
                             final boolean performOutput) throws ContentProcessingException;

  public void processIncrementalUpdate(final boolean performOutput) throws ContentProcessingException;

  public int getPagebreaks();

  public boolean isOpen();

  public Object clone() throws CloneNotSupportedException;

  public ReportStateKey getLastStateKey();

  public void addPagebreak(final ReportStateKey stateKey);

  public boolean clearPendingPageStart(final LayoutPagebreakHandler layoutPagebreakHandler);

  public boolean isPageStartPending();

  public boolean isCurrentPageEmpty();

  public Renderer deriveForStorage();

  public Renderer deriveForPagebreak();

  public boolean isValid();

  public void createRollbackInformation();

  public void applyRollbackInformation();

  public void rollback();

  public void setStateKey(Object stateKey);

  public void applyAutoCommit();

  public LayoutBuilder createBufferedLayoutBuilder();

  public boolean isPendingPageHack();

  public boolean isSafeToStore();

  void print();

  void newPageStarted();

  public int getPageCount();

}
