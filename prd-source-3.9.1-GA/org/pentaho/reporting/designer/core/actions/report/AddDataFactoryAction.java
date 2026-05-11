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

package org.pentaho.reporting.designer.core.actions.report;

import java.awt.Cursor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.util.Locale;
import javax.swing.Action;
import javax.swing.table.TableModel;

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.DataSourcePlugin;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AddDataFactoryAction extends AbstractReportContextAction
{
  private DataFactoryMetaData dataSourcePlugin;

  public AddDataFactoryAction(final DataFactoryMetaData dataSourcePlugin)
  {
    this.dataSourcePlugin = dataSourcePlugin;
    putValue(Action.NAME, dataSourcePlugin.getDisplayName(Locale.getDefault()));
    putValue(Action.SHORT_DESCRIPTION, dataSourcePlugin.getDescription(Locale.getDefault()));
    putValue(Action.SMALL_ICON, dataSourcePlugin.getIcon(Locale.getDefault(), BeanInfo.ICON_COLOR_32x32));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportRenderContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }

    final DataSourcePlugin editor = dataSourcePlugin.createEditor();
    if (editor == null)
    {
      return;
    }

    final ReportDesignerDesignTimeContext theDesignTimeContext = new ReportDesignerDesignTimeContext(getReportDesignerContext());
    final DataFactory dataFactory = editor.performEdit(theDesignTimeContext, null, null);
    if (dataFactory == null)
    {
      return;
    }

    try
    {
      final Window theParentWindow = theDesignTimeContext.getParentWindow();
      theParentWindow.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

      final AbstractReportDefinition element = activeContext.getReportDefinition();
      final DataFactory originalDataFactory = element.getDataFactory();

      final String queryAttribute = (String) element.getAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY);
      if (StringUtils.isEmpty(queryAttribute) && dataFactory.getQueryNames().length > 0)
      {
        element.setAttribute(AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, dataFactory.getQueryNames()[0]);
      }
      if (originalDataFactory == null || isEmpty(dataFactory))
      {
        element.setDataFactory(CompoundDataFactory.normalize(dataFactory));
      }
      else
      {
        final CompoundDataFactory reportDf = CompoundDataFactory.normalize(originalDataFactory);
        final int position = reportDf.size();
        reportDf.add(dataFactory);
        activeContext.getUndo().addChange(ActionMessages.getString("AddDataFactoryAction.UndoText"),
            new DataSourceEditUndoEntry(position, null, dataFactory.derive()));
        element.setDataFactory(reportDf);
      }
    }
    finally
    {
      if(activeContext.getReportDataSchemaModel() != null &&
          activeContext.getReportDataSchemaModel().getDataFactoryException() != null) {
        UncaughtExceptionsModel.getInstance().addException(activeContext.getReportDataSchemaModel().getDataFactoryException());
      }
      
      final Window theParentWindow = theDesignTimeContext.getParentWindow();
      theParentWindow.setCursor(Cursor.getDefaultCursor());
    }
  }

  private boolean isEmpty(final DataFactory dataFactory)
  {
    if (dataFactory == null)
    {
      return true;
    }
    if (dataFactory instanceof TableDataFactory)
    {
      final String[] queryNames = dataFactory.getQueryNames();
      if (queryNames.length == 0)
      {
        return true;
      }

      try
      {
        // check for legacy-built-in defaults and selectively ignore them ..
        final TableModel tableModel = dataFactory.queryData("default", null);
        if (tableModel.getRowCount() == 0 && tableModel.getColumnCount() == 0)
        {
          return true;
        }
      }
      catch (final Exception e)
      {
        return false;
      }
    }
    return false;
  }
}
