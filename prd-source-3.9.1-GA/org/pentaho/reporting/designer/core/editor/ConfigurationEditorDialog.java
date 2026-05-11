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

package org.pentaho.reporting.designer.core.editor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashSet;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingUtil;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.base.config.ModifiableConfiguration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.tools.configeditor.ConfigEditorPane;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ConfigurationEditorDialog extends CommonDialog
{
  private ConfigEditorPane editorPane;

  public ConfigurationEditorDialog()
  {
    init();
  }

  public ConfigurationEditorDialog(final Frame owner)
  {
    super(owner);
    init();
  }

  public ConfigurationEditorDialog(final Dialog owner)
  {
    super(owner);
    init();
  }

  protected Component createContentPane()
  {
    setTitle(Messages.getString("ConfigurationEditorDialog.Title"));

    editorPane = new ConfigEditorPane(ClassicEngineBoot.getInstance().getPackageManager(), false);
    final InputStream in = ObjectUtilities.getResourceAsStream
        ("org/pentaho/reporting/engine/classic/core/config-description.xml", ClassicEngineBoot.class); // NON-NLS
    if (in != null)
    {
      try
      {
        editorPane.loadModel(in, false);
      }
      catch (final IOException ioe)
      {
        UncaughtExceptionsModel.getInstance().addException(ioe);
      }
      finally
      {
        try
        {
          in.close();
        }
        catch (IOException e)
        {
          // ignore ..
        }
      }
    }

    return editorPane;
  }

  public boolean performEdit(final ModifiableConfiguration config)
  {
    final HashSet<String> existingKeys = new HashSet<String>();
    final HierarchicalConfiguration hconf = new HierarchicalConfiguration(config);
    if (config instanceof HierarchicalConfiguration)
    {
      final HierarchicalConfiguration oconf = (HierarchicalConfiguration) config;
      final Enumeration configProperties = oconf.getConfigProperties();
      while (configProperties.hasMoreElements())
      {
        // mark all manually set properties as defined ..
        final String key = (String) configProperties.nextElement();
        hconf.setConfigProperty(key, oconf.getConfigProperty(key));
        existingKeys.add(key);
      }
    }
    editorPane.updateConfiguration(hconf);
    pack();
    SwingUtil.centerDialogInParent(this);

    if (performEdit() == false)
    {
      return false;
    }
    
    editorPane.commit();

    final Enumeration configProperties = hconf.getConfigProperties();
    while (configProperties.hasMoreElements())
    {
      final String key = (String) configProperties.nextElement();
      config.setConfigProperty(key, hconf.getConfigProperty(key));
      existingKeys.remove(key);
    }

    final String[] keys = existingKeys.toArray(new String[existingKeys.size()]);
    for (int i = 0; i < keys.length; i++)
    {
      final String key = keys[i];
      config.setConfigProperty(key, null);
    }
    return true;
  }

  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    final ConfigurationEditorDialog dialog = new ConfigurationEditorDialog();
    dialog.performEdit(ClassicEngineBoot.getInstance().getEditableConfig());
  }


}
