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

package org.pentaho.reporting.engine.classic.core;

/**
 * A datafactory that can have a named connection. This is a design-time support interface to allow designer-UIs
 * a better presentation of datafactories.
 *
 * @author Ezequiel Cuellar
 * @deprecated This is handled by the datafactory-metadata.
 */
public interface NamedDataFactory extends DataFactory
{
  /**
   * Returns the presentation level name for this datafactory.
   *
   * @return the data factory presentation text.
   */
  public String getConnectionName();
}
