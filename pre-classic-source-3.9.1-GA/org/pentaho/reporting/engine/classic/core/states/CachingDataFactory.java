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

import javax.swing.table.TableModel;

import org.pentaho.reporting.engine.classic.core.DataFactory;

/**
 * Creation-Date: 19.11.2006, 13:35:45
 *
 * @author Thomas Morgner
 * @deprecated Do not use anymore.
 */
public class CachingDataFactory extends org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory
{
  public CachingDataFactory(final DataFactory backend)
  {
    super(backend, Boolean.FALSE);
  }

  /**
   * Prints a table model to standard output.
   *
   * @param mod the model.
   */
  public static void printTableModelContents(final TableModel mod)
  {
    org.pentaho.reporting.engine.classic.core.cache.CachingDataFactory.printTableModelContents(mod);
  }
}
