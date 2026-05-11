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

package org.pentaho.reporting.engine.classic.core.metadata;

import java.util.HashMap;

import org.pentaho.reporting.engine.classic.core.ReportProcessTask;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportProcessTaskRegistry
{
  private HashMap exportTypes;
  private static ReportProcessTaskRegistry processTaskRegistry;

  public static synchronized ReportProcessTaskRegistry getInstance()
  {
    if (processTaskRegistry == null)
    {
      processTaskRegistry = new ReportProcessTaskRegistry();
    }
    return processTaskRegistry;
  }

  private ReportProcessTaskRegistry()
  {
    this.exportTypes = new HashMap();
  }

  public void registerExportType(final String exportType, final Class exportTask)
  {
    if (exportType == null)
    {
      throw new NullPointerException();
    }
    if (exportTask == null)
    {
      throw new NullPointerException();
    }

    if (ReportProcessTask.class.isAssignableFrom(exportTask) == false)
    {
      throw new IllegalArgumentException("Invalid Implementation: " + exportTask);
    }
    this.exportTypes.put(exportType, exportTask.getName());
  }

  public String[] getExportTypes()
  {
    return (String[]) exportTypes.keySet().toArray(new String[exportTypes.size()]);
  }

  public boolean isExportTypeRegistered(final String exportType)
  {
    return exportTypes.containsKey(exportType);
  }

  public ReportProcessTask createProcessTask(final String exportType)
  {
    final String c = (String) exportTypes.get(exportType);
    final ReportProcessTask o = (ReportProcessTask) ObjectUtilities.loadAndInstantiate(c,
        ReportProcessTaskRegistry.class, ReportProcessTask.class);
    if (o == null)
    {
      throw new IllegalArgumentException();
    }
    return o;
  }
}
