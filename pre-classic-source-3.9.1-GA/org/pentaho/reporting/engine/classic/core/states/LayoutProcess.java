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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.states;

import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public interface LayoutProcess extends Cloneable
{
  public static final int LEVEL_PAGINATE = -2;
  public static final int LEVEL_COLLECT = -1;

  public LayoutProcess getParent();

  public boolean isPageListener();

  public OutputFunction getOutputFunction();

  public StructureFunction[] getCollectionFunctions();

  public LayoutProcess deriveForStorage();

  public LayoutProcess deriveForPagebreak();

  public Object clone() throws CloneNotSupportedException;

  public void fireReportEvent(ReportEvent originalEvent);

  public void restart(final ReportState state) throws ReportProcessingException;
}
