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
 * Copyright (c) 2005 - 2009 Pentaho Corporation, Object Refinery Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases;

import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.FocusManager;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.action.ActionMenuItem;

/**
 * Creation-Date: 26.10.2006, 13:49:53
 *
 * @author Thomas Morgner
 */
public class Accelerators extends JDialog
{
  public static class MyAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default description string and
     * default icon.
     */
    public MyAction()
    {
      putValue(Action.NAME, "Blah");
     // putValue(Action.MNEMONIC_KEY, new Integer('b'));
      final KeyStroke keyStroke =
          KeyStroke.getKeyStroke(KeyEvent.VK_D, getMenuKeyMask());
      putValue(Action.ACCELERATOR_KEY, keyStroke);
    }

    private int getMenuKeyMask() {
        try {
          final int menuShortcutKeyMask =
              Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
          return menuShortcutKeyMask;
        }
        catch (UnsupportedOperationException he) {
            // headless exception extends UnsupportedOperation exception,
            // but the HeadlessException is not defined in older JDKs...
          System.out.println ("FAILED");
            return InputEvent.CTRL_MASK;
        }
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e)
    {
      System.out.println ("Execute!");
      final Window activeWindow =
          FocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
      final Component focusOwner =
          FocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
      final Component pfocusOwner =
          FocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();

      System.out.println ("Execute! 1 = " + activeWindow);
      System.out.println ("Execute! 2 = " + focusOwner);
      System.out.println ("Execute! 3 = " + pfocusOwner);
    }
  }

  /**
   * Constructs a new frame that is initially invisible.
   * <p/>
   * This constructor sets the component's locale property to the value returned
   * by <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless()
   *                                    returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see java.awt.Component#setSize
   * @see java.awt.Component#setVisible
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public Accelerators()
      throws HeadlessException
  {
    JMenuBar menuBar = new JMenuBar();
    final JMenu c = new JMenu("Fha");
    c.add(new ActionMenuItem (new MyAction()));
    c.add(new ActionMenuItem (new MyAction()));
    menuBar.add(c);

    setJMenuBar(menuBar);
  }

  public static void main(String[] args)
      throws InvocationTargetException, InterruptedException
  {
    Accelerators a = new Accelerators();
    a.setSize(300, 300);
    a.setLocation(300, 300);
    a.setVisible(true);

    Thread.sleep(2000);
    
    SwingUtilities.invokeAndWait(new Runnable(){
      public void run() {
          final Window activeWindow =
              FocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
          final Component focusOwner =
              FocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
          final Component pfocusOwner =
              FocusManager.getCurrentKeyboardFocusManager().getPermanentFocusOwner();
          System.out.println ("Execute! 1 = " + activeWindow);
          System.out.println ("Execute! 2 = " + focusOwner);
          System.out.println ("Execute! 3 = " + pfocusOwner);
      }
    });
  }
}
