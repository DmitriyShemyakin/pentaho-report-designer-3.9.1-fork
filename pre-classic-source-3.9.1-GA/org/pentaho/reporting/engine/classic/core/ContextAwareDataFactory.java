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

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * A datafactory that is able to receive context information from the report-processor.
 *
 * @author Thomas Morgner
 * @deprecated Merged with DataFactory interface
 */
public interface ContextAwareDataFactory extends DataFactory
{
  /**
   * Initializes the data factory and provides new context information. Initialize is always called before the
   * datafactory has been opened by calling DataFactory#open.
   *
   * @param configuration the current report configuration.
   * @param resourceManager the report's resource manager.
   * @param contextKey the report's context key to access resources relative to the report location.
   * @param resourceBundleFactory the report's resource-bundle factory to access localization information.
   */
  public void initialize(Configuration configuration,
                         ResourceManager resourceManager,
                         ResourceKey contextKey,
                         ResourceBundleFactory resourceBundleFactory);
}
