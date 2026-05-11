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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple;

import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The Module specification for the simple parser module. This module handles the simple report definition format.
 *
 * @author Thomas Morgner
 */
public class SimpleParserModule extends AbstractModule
{
  public static final String NAMESPACE =
      "http://jfreereport.sourceforge.net/namespaces/reports/legacy/simple";

  /**
   * Loads the module information from the module.properties file.
   *
   * @throws ModuleInitializeException if loading the specifications failed.
   */
  public SimpleParserModule()
      throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  /**
   * Initializes the module.
   *
   * @param subSystem the subsystem which this module belongs to.
   * @throws ModuleInitializeException if initialisation fails.
   */
  public void initialize(final SubSystem subSystem)
      throws ModuleInitializeException
  {
    performExternalInitialize(SimpleParserModuleInit.class.getName(), SimpleParserModule.class);
  }
}
