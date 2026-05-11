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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.HtmlCharacterEntities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
@SuppressWarnings({"HardcodedFileSeparator"})
public class SystemInformationDialog extends JDialog
{
  public SystemInformationDialog()
      throws HeadlessException
  {
    setModal(true);
    init();
  }

  public SystemInformationDialog(final Frame owner)
      throws HeadlessException
  {
    super(owner, true);
    init();
  }

  public SystemInformationDialog(final Dialog owner)
      throws HeadlessException
  {
    super(owner, true);
    init();
  }

  private class OKAction extends AbstractAction
  {
    /**
     * Defines an <code>Action</code> object with a default description string and default icon.
     */
    private OKAction()
    {
      putValue(Action.NAME, UtilMessages.getInstance().getString("SystemInformationDialog.Close"));
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(final ActionEvent e)
    {
      dispose();
    }
  }

  private void init()
  {
    setTitle(UtilMessages.getInstance().getString("SystemInformationDialog.Title"));
    final JPanel buttonCarrier = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    buttonCarrier.add(new JButton(new OKAction()));

    final JEditorPane editorPane = new JEditorPane();
    editorPane.setEditable(false);
    editorPane.setContentType("text/html");//NON-NLS
    editorPane.setText(getSystemInformationAsHTML());
    final HTMLDocument htmlDocument = (HTMLDocument) editorPane.getDocument();
    htmlDocument.getStyleSheet().addRule("body { font-family:sans-serif; }");//NON-NLS
    editorPane.setCaretPosition(0);

    final JScrollPane scrollPane = new JScrollPane(editorPane);

    final JPanel panel = new JPanel(new BorderLayout());
    panel.add(scrollPane, BorderLayout.CENTER);
    panel.add(buttonCarrier, BorderLayout.SOUTH);

    setContentPane(panel);

    pack();
    GUIUtils.ensureMinimumDialogSize(this, 400, 300);
    GUIUtils.ensureMaximumDialogSize(this, 800, 600);
    SwingUtil.centerDialogInParent(this);
    setVisible(true);
  }


  @SuppressWarnings({"HardcodedLineSeparator"})
  private String getSystemInformationAsHTML()
  {
    final CharacterEntityParser cep = HtmlCharacterEntities.getEntityParser();
    final StringBuilder sb = new StringBuilder(10000);
    sb.append("<html><body>\n");//NON-NLS

    sb.append("<h1>");//NON-NLS
    sb.append(cep.encodeEntities(UtilMessages.getInstance().getString("SystemInformationDialog.SystemProperties.Title")));
    sb.append("</h1>");//NON-NLS
    sb.append("<table>\n");//NON-NLS
    final Map properties = new TreeMap(System.getProperties());
    final Set<Object> enumeration = properties.keySet();
    for (final Object key : enumeration)
    {
      String value = (String) properties.get(key.toString());
      if (value != null)
      {
        value = cep.encodeEntities(value);
        value = value.replace("\n", "\\n");//NON-NLS
        value = value.replace("\f", "\\f");//NON-NLS
        value = value.replace("\r", "\\r");//NON-NLS
        if (value.length() > 80)
        {
          value = value.replace(File.pathSeparator, File.pathSeparator + "<br>\n");//NON-NLS
        }
      }
      sb.append("<tr valign=\"top\"><td>");//NON-NLS
      sb.append(cep.encodeEntities(key.toString()));
      sb.append("</td><td>");//NON-NLS
      sb.append(value);
      sb.append("</td></tr>\n");//NON-NLS
    }
    sb.append("</table>");//NON-NLS


    sb.append("<br>");//NON-NLS

    //environment
    sb.append("<h1>");//NON-NLS
    sb.append(cep.encodeEntities(UtilMessages.getInstance().getString("SystemInformationDialog.Environment.Title")));
    sb.append("</h1>");//NON-NLS
    sb.append("<table>\n");//NON-NLS
    final Map<String, String> environmentMap = new TreeMap<String, String>(System.getenv());
    for (final String key : environmentMap.keySet())
    {
      String value = environmentMap.get(key);
      if (value != null)
      {
        value = cep.encodeEntities(value);
        value = value.replace("\n", "\\n");//NON-NLS
        value = value.replace("\f", "\\f");//NON-NLS
        value = value.replace("\r", "\\r");//NON-NLS
        if (value.length() > 80)
        {
          value = value.replace(File.pathSeparator, File.pathSeparator + "<br>\n");//NON-NLS
        }
      }
      sb.append("<tr valign=\"top\"><td>");//NON-NLS
      sb.append(cep.encodeEntities(key));
      sb.append("</td><td>");//NON-NLS
      sb.append(value);
      sb.append("</td></tr>\n");//NON-NLS
    }

    sb.append("</table>");//NON-NLS


    sb.append("<br>");//NON-NLS

    //other
    sb.append("<h1>");//NON-NLS
    sb.append(cep.encodeEntities(UtilMessages.getInstance().getString("SystemInformationDialog.Other.Title")));
    sb.append("</h1>");//NON-NLS
    sb.append("<table>\n");//NON-NLS

    final Map<String, String> otherProperties = getOtherProperties();
    for (final String key : otherProperties.keySet())
    {
      String value = otherProperties.get(key);
      if (value != null)
      {
        value = cep.encodeEntities(value);
        value = value.replace("\n", "\\n");//NON-NLS
        value = value.replace("\f", "\\f");//NON-NLS
        value = value.replace("\r", "\\r");//NON-NLS
        if (value.length() > 80)
        {
          value = value.replace(File.pathSeparator, File.pathSeparator + "<br>\n");//NON-NLS
        }
      }
      sb.append("<tr valign=\"top\"><td>");//NON-NLS
      sb.append(cep.encodeEntities(key));
      sb.append("</td><td>");//NON-NLS
      sb.append(value).append("</td></tr>\n");//NON-NLS
    }

    sb.append("</table>");//NON-NLS


    sb.append("</body></html>\n");//NON-NLS

    return sb.toString();
  }


  private Map<String, String> getOtherProperties()
  {
    final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
    map.put("UIManager.LookAndFeel", UIManager.getLookAndFeel().getClass().getName());//NON-NLS
    map.put("Toolkit", Toolkit.getDefaultToolkit().getClass().getName());//NON-NLS
    map.put("Toolkit.MenuShortcutKeyMask", //NON-NLS
        String.valueOf(Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));//NON-NLS
    map.put("Toolkit.ScreenResolution", String.valueOf(Toolkit.getDefaultToolkit().getScreenResolution()));//NON-NLS
    map.put("Toolkit.ScreenSize", String.valueOf(Toolkit.getDefaultToolkit().getScreenSize()));//NON-NLS

    addDesktopProperty(map, "awt.mouse.numButtons");//NON-NLS
    addDesktopProperty(map, "awt.multiClickInterval");//NON-NLS
    addDesktopProperty(map, "DnD.Autoscroll.cursorHysteresis");//NON-NLS
    addDesktopProperty(map, "DnD.Autoscroll.initialDelay");//NON-NLS
    addDesktopProperty(map, "DnD.Autoscroll.interval");//NON-NLS
    addDesktopProperty(map, "DnD.Cursor.CopyDrop");//NON-NLS
    addDesktopProperty(map, "DnD.Cursor.CopyNoDrop");//NON-NLS
    addDesktopProperty(map, "DnD.Cursor.LinkDrop");//NON-NLS
    addDesktopProperty(map, "DnD.Cursor.LinkNoDrop");//NON-NLS
    addDesktopProperty(map, "DnD.Cursor.MoveDrop");//NON-NLS
    addDesktopProperty(map, "DnD.Cursor.MoveNoDrop");//NON-NLS
    addDesktopProperty(map, "DnD.gestureMotionThreshold");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/CanChangeAccels");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/CursorThemeName");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/CursorThemeSize");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/FileChooserBackend");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/FontName");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/IMPreeditStyle");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/IMStatusStyle");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/KeyThemeName");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/MenuBarAccel");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/MenuImages");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/ShowInputMethodMenu");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/ShowUnicodeMenu");//NON-NLS
    addDesktopProperty(map, "gnome.Gtk/ToolbarStyle");//NON-NLS
    addDesktopProperty(map, "gnome.Net/CursorBlink");//NON-NLS
    addDesktopProperty(map, "gnome.Net/CursorBlinkTime");//NON-NLS
    addDesktopProperty(map, "gnome.Net/DndDragThreshold");//NON-NLS
    addDesktopProperty(map, "gnome.Net/DoubleClickTime");//NON-NLS
    addDesktopProperty(map, "gnome.Net/FallbackIconTheme");//NON-NLS
    addDesktopProperty(map, "gnome.Net/IconThemeName");//NON-NLS
    addDesktopProperty(map, "gnome.Net/ThemeName");//NON-NLS
    addDesktopProperty(map, "gnome.Xft/Antialias");//NON-NLS
    addDesktopProperty(map, "gnome.Xft/DPI");//NON-NLS
    addDesktopProperty(map, "gnome.Xft/Hinting");//NON-NLS
    addDesktopProperty(map, "gnome.Xft/HintStyle");//NON-NLS
    addDesktopProperty(map, "gnome.Xft/RGBA");//NON-NLS
    addDesktopProperty(map, "Shell.shellFolderManager");//NON-NLS
    addDesktopProperty(map, "win.3d.backgroundColor");//NON-NLS
    addDesktopProperty(map, "win.3d.darkShadowColor");//NON-NLS
    addDesktopProperty(map, "win.3d.highlightColor");//NON-NLS
    addDesktopProperty(map, "win.3d.lightColor");//NON-NLS
    addDesktopProperty(map, "win.3d.shadowColor");//NON-NLS
    addDesktopProperty(map, "win.ansiFixed.font");//NON-NLS
    addDesktopProperty(map, "win.button.textColor");//NON-NLS
    addDesktopProperty(map, "win.defaultGUI.font");//NON-NLS
    addDesktopProperty(map, "win.frame.backgroundColor");//NON-NLS
    addDesktopProperty(map, "win.frame.textColor");//NON-NLS
    addDesktopProperty(map, "win.item.highlightColor");//NON-NLS
    addDesktopProperty(map, "win.item.highlightTextColor");//NON-NLS
    addDesktopProperty(map, "win.menu.backgroundColor");//NON-NLS
    addDesktopProperty(map, "win.menubar.backgroundColor");//NON-NLS
    addDesktopProperty(map, "win.menu.font");//NON-NLS
    addDesktopProperty(map, "win.menu.keyboardCuesOn");//NON-NLS
    addDesktopProperty(map, "win.menu.textColor");//NON-NLS
    addDesktopProperty(map, "win.scrollbar.backgroundColor");//NON-NLS
    addDesktopProperty(map, "win.scrollbar.width");//NON-NLS
    addDesktopProperty(map, "win.text.grayedTextColor");//NON-NLS
    addDesktopProperty(map, "win.xpstyle.colorName");//NON-NLS
    addDesktopProperty(map, "win.xpstyle.dllName");//NON-NLS
    addDesktopProperty(map, "win.xpstyle.sizeName");//NON-NLS
    addDesktopProperty(map, "win.xpstyle.themeActive");//NON-NLS
    return map;
  }


  private void addDesktopProperty(final LinkedHashMap<String, String> map, final String key)
  {
    final Object value = Toolkit.getDefaultToolkit().getDesktopProperty(key);
    if (value != null)
    {
      map.put(key, value.toString());
    }
  }
}
