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

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

/**
 * Creation-Date: 03.07.2007, 13:18:11
 *
 * @author Thomas Morgner
 */
public interface ReportState extends Cloneable
{
  /**
   * A row number that is 'before' the first row.
   */
  public static final int BEFORE_FIRST_ROW = -1;
  /**
   * A group number that is 'before' the first group.
   */
  public static final int BEFORE_FIRST_GROUP = -1;

  int getNumberOfRows();

  DataRow getDataRow();

  ReportDefinition getReport();

  int getCurrentDataItem();

  int getCurrentCrosstabPaddingItem();

  int getCurrentGroupIndex();
  
  int getPresentationGroupIndex();

  boolean isPrepareRun();

  boolean isFinish();

  int getLevel();

  int getProgressLevel();

  int getProgressLevelCount();

  /**
   * Returns the unique event code for this report state type.
   *
   * @return the event code for this state type.
   */
  int getEventCode();

  public Object clone() throws CloneNotSupportedException;

  public DefaultFlowController getFlowController();

  public boolean isSubReportEvent();

  public void setErrorHandler(ReportProcessingErrorHandler errorHandler);

  public InlineSubreportMarker getCurrentSubReportMarker();

  public ReportProcessingErrorHandler getErrorHandler();

  public LayoutProcess getLayoutProcess();

  public void firePageFinishedEvent(final boolean noParentPassing);

  public void firePageStartedEvent(final int eventCode);

  public ReportState getParentState();
  
  public ReportState getParentSubReportState();

  public ReportStateKey getProcessKey();

  public boolean isInItemGroup();

  public boolean isInlineProcess();

  public ResourceBundleFactory getResourceBundleFactory();

  public GroupingState createGroupingState();
}
