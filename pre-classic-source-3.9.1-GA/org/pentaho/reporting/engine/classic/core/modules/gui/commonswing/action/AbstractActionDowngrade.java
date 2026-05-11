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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.action;

import javax.swing.AbstractAction;

/**
 * A class that allows Action features introduced in JDK 1.3 to be used with JDK 1.2.2, by defining the two new
 * constants introduced by Sun in JDK 1.3.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractActionDowngrade extends AbstractAction implements ActionDowngrade
{
  // kills a compile error for JDK's >= 1.3
  // ambiguous reference error ...
  /**
   * The key used for storing a <code>KeyStroke</code> to be used as the accelerator for the action.
   */
  public static final String ACCELERATOR_KEY = ActionDowngrade.ACCELERATOR_KEY;

  /**
   * The key used for storing an int key code to be used as the mnemonic for the action.
   */
  public static final String MNEMONIC_KEY = ActionDowngrade.MNEMONIC_KEY;

  /**
   * Creates a new action with a default (transparent) icon.
   */
  protected AbstractActionDowngrade()
  {
    // nothing required
  }

}
