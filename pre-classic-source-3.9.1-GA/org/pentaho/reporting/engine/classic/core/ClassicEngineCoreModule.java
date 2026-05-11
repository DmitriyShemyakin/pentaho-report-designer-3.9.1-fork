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

package org.pentaho.reporting.engine.classic.core;

import java.io.InputStream;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;


/**
 * The CoreModule is used to represent the base classes of JFreeReport in a PackageManager-compatible way. Modules may
 * request a certain core-version to be present by referencing to this module.
 * <p/>
 * This module is used to initialize the image and drawable factories. If the Pixie library is available, support for
 * WMF-files is added to the factories.
 *
 * @author Thomas Morgner
 */
public class ClassicEngineCoreModule extends AbstractModule
{
  /**
   * The 'no-printer-available' property key.
   */
  public static final String NO_PRINTER_AVAILABLE_KEY
      = "org.pentaho.reporting.engine.classic.core.NoPrinterAvailable";

  /**
   * The G2 fontrenderer bug override configuration key.
   */
  public static final String FONTRENDERER_ISBUGGY_FRC_KEY
      = "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.IsBuggyFRC";

  /**
   * The text aliasing configuration key.
   */
  public static final String FONTRENDERER_USEALIASING_KEY
      = "org.pentaho.reporting.engine.classic.core.layout.fontrenderer.UseAliasing";

  /**
   * A configuration key that defines, whether errors will abort the report processing. This defaults to true.
   */
  public static final String STRICT_ERROR_HANDLING_KEY
      = "org.pentaho.reporting.engine.classic.core.StrictErrorHandling";

  /**
   * Creates a new module definition based on the 'coremodule.properties' file of this package.
   *
   * @throws ModuleInitializeException if the file could not be loaded.
   */
  public ClassicEngineCoreModule()
      throws ModuleInitializeException
  {
    final InputStream in = ObjectUtilities.getResourceRelativeAsStream
        ("coremodule.properties", ClassicEngineCoreModule.class);
    if (in == null)
    {
      throw new ModuleInitializeException
          ("File 'coremodule.properties' not found in JFreeReport package.");
    }
    loadModuleInfo(in);
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error ocurred while initializing the module.
   */
  public void initialize(final SubSystem subSystem)
      throws ModuleInitializeException
  {
    StyleKey.registerDefaults();
    ElementMetaDataParser.initializeElementMetaData();
  }
}
