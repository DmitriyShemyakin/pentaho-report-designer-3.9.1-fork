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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.awt.Component;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JFileChooser;

import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * A common class that manages all file-chooser dialogs and keeps references
 * to them.
 *
 * @author Thomas Morgner.
 * @deprecated Unused; will be removed in 4.0
 */
public class FileChooserService
{
  private static class FilterKey
  {
    private FilesystemFilter[] filters;

    private FilterKey(final FilesystemFilter[] filters)
    {
      if (filters != null)
      {
        this.filters = (FilesystemFilter[]) filters.clone();
      }
      else
      {
        this.filters = new FilesystemFilter[0];
      }
    }

    public FilesystemFilter[] getFilters()
    {
      return (FilesystemFilter[]) filters.clone();
    }

    public boolean equals(final Object o)
    {
      if (this == o)
      {
        return true;
      }
      if (o == null || getClass() != o.getClass())
      {
        return false;
      }

      final FilterKey filterKey = (FilterKey) o;

      if (!ObjectUtilities.equalArray(filters, filterKey.filters))
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      return ObjectUtilities.hashCode(filters);
    }
  }

  private static FileChooserService service;
  private HashMap instances;

  public static synchronized FileChooserService getInstance()
  {
    if (service == null)
    {
      service = new FileChooserService();
    }
    return service;
  }

  public FileChooserService()
  {
    instances = new HashMap();
  }

  public void flush()
  {
    instances.clear();
  }

  public File performOpen(final Component parent,
                          final String title,
                          final FilesystemFilter[] rawFilter)
  {
    final FilterKey key = new FilterKey(rawFilter);
    final JFileChooser fileChooser = new JFileChooser();
    final FilesystemFilter[] filters = key.getFilters();
    for (int i = 0; i < filters.length; i++)
    {
      final FilesystemFilter filesystemFilter = filters[i];
      fileChooser.addChoosableFileFilter(filesystemFilter);
    }
    final File o = (File) instances.get(key);
    if (o != null)
    {
      final File parentDir = o.getParentFile();
      if (parentDir != null)
      {
        fileChooser.setCurrentDirectory(parentDir);
      }
    }
    final int result = fileChooser.showOpenDialog(parent);
    if (result == JFileChooser.APPROVE_OPTION)
    {
      final File resultFile = fileChooser.getSelectedFile();
      instances.put(key, resultFile);
      return resultFile;
    }

    return null;
  }


  public File performSave(final Component parent,
                          final String title,
                          final FilesystemFilter[] rawFilter)
  {
    final FilterKey key = new FilterKey(rawFilter);

    final JFileChooser fileChooser = new JFileChooser();
    final FilesystemFilter[] filters = key.getFilters();
    for (int i = 0; i < filters.length; i++)
    {
      final FilesystemFilter filesystemFilter = filters[i];
      fileChooser.addChoosableFileFilter(filesystemFilter);
    }
    final File o = (File) instances.get(key);
    if (o != null)
    {
      final File parentDir = o.getParentFile();
      if (parentDir != null)
      {
        fileChooser.setCurrentDirectory(parentDir);
      }
    }
    final int result = fileChooser.showSaveDialog(parent);
    if (result == JFileChooser.APPROVE_OPTION)
    {
      final File resultFile = fileChooser.getSelectedFile();
      instances.put(key, resultFile);
      return resultFile;
    }

    return null;
  }
}
