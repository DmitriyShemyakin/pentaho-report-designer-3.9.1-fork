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

package org.pentaho.reporting.designer.extensions.pentaho.repository.model;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.provider.UriParser;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.pensol.vfs.WebSolutionFileSystem;

public class RepositoryConnection
{
  private static final Log logger = LogFactory.getLog(RepositoryConnection.class);

  private static final String WEB_SOLUTION_PREFIX = "web-solution:";

  private String baseURL;
  private boolean showFoldersOnly;
  private String[] filters;
  private FileObject repositoryRoot;

  public RepositoryConnection(final String baseURL,
                              final String serverUserId,
                              final String serverPassword,
                              final String[] filters,
                              final boolean showFoldersOnly) throws IOException
  {
    this.baseURL = baseURL;
    if (filters != null)
    {
      this.filters = filters.clone();
    }
    this.showFoldersOnly = showFoldersOnly;
    this.repositoryRoot = VFS.getManager().resolveFile(normalizeURL(serverUserId, serverPassword));
  }


  protected RepositoryConnection(final FileObject root,
                                 final String baseURL,
                                 final String[] filters,
                                 final boolean showFoldersOnly) throws IOException
  {
    this.baseURL = baseURL;
    if (filters != null)
    {
      this.filters = filters.clone();
    }
    this.showFoldersOnly = showFoldersOnly;
    this.repositoryRoot = root;
  }

  public String getBaseURL()
  {
    return baseURL;
  }

  protected String normalizeURL(final String user, final String password)
  {
    final StringBuilder prefix = new StringBuilder(100);
    final String url2;
    if (baseURL.toLowerCase().startsWith("http://"))
    {
      url2 = baseURL.substring("http://".length());
      prefix.append(WEB_SOLUTION_PREFIX);
      prefix.append("http://");
    }
    else if (baseURL.toLowerCase().startsWith("https://"))
    {
      url2 = baseURL.substring("https://".length());
      prefix.append(WEB_SOLUTION_PREFIX);
      prefix.append("https://");
    }
    else
    {
      throw new IllegalArgumentException("Not a expected URL");
    }

    if (StringUtils.isEmpty(user) == false)
    {
      final char[] reserved = new char[]{'/', ':', '%', '@'};
      prefix.append(UriParser.encode(user, reserved));
      if (StringUtils.isEmpty(password) == false)
      {
        prefix.append(':');
        prefix.append(UriParser.encode(password, reserved));
      }
      prefix.append('@');
    }

    final int maxlen = url2.length();
    for (int pos = 0; pos < maxlen; pos++)
    {
      final char ch = url2.charAt(pos);
      if (ch == '@')
      {
        // Found the end of the user info
        return prefix.append(url2.substring(0, pos + 1)).toString();
      }
      if (ch == '/' || ch == '?')
      {
        // Not allowed in user info
        break;
      }
    }
    return prefix.append(url2).toString();

  }

  public RepositoryConnection derive(final boolean showFoldersOnly) throws IOException
  {
    return new RepositoryConnection(this.repositoryRoot, this.baseURL, this.filters, showFoldersOnly);
  }

  public void refresh() throws IOException
  {
    final WebSolutionFileSystem fileSystem = (WebSolutionFileSystem) this.repositoryRoot.getFileSystem();
    fileSystem.getLocalFileModel().refresh();
  }

  public FileObject getRepositoryRoot()
  {
    return repositoryRoot;
  }

  public void createNewFolder(final String solution,
                              final String path,
                              final String name,
                              final String description) throws IOException
  {
    final FileObject fileObject = repositoryRoot.getFileSystem().resolveFile(solution + '/' + path + '/' + name);
    fileObject.createFolder();
  }

  public boolean exists(final String path)
  {
    // todo:
    if (path == null)
    {
      return false;
    }
    try
    {
      final StringTokenizer st = new StringTokenizer(path, "/");
      FileObject node = repositoryRoot;
      while (st.hasMoreTokens())
      {
        final String token = st.nextToken();
        final FileObject element = RepositoryTreeModel.findNodeByName(node, token);
        if (element == null)
        {
          return false;
        }

        node = element;
      }
      return true;
    }
    catch (FileSystemException fse)
    {
      logger.debug("Failed", fse);
      return false;
    }
  }

  public boolean isShowFoldersOnly()
  {
    return showFoldersOnly;
  }

  public String[] getFilters()
  {
    if (filters == null)
    {
      return null;
    }
    return filters.clone();
  }
}
