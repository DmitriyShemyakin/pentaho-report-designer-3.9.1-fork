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

import java.util.Comparator;

import org.pentaho.reporting.engine.classic.core.function.StructureFunction;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class StructureFunctionComparator implements Comparator
{
  public StructureFunctionComparator()
  {
  }

  public int compare(final Object o1, final Object o2)
  {
    final StructureFunction s1 = (StructureFunction) o1;
    final StructureFunction s2 = (StructureFunction) o2;

    final int dL1 = s1.getDependencyLevel();
    final int dL2 = s2.getDependencyLevel();
    if (dL1 > dL2)
    {
      return -1;
    }
    if (dL1 < dL2)
    {
      return 1;
    }

    final int priority1 = s1.getProcessingPriority();
    final int priority2 = s2.getProcessingPriority();
    if (priority1 < priority2)
    {
      return -1;
    }
    if (priority1 > priority2)
    {
      return 1;
    }
    return 0;
  }
}
