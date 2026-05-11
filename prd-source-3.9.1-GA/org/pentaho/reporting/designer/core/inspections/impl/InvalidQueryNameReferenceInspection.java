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

package org.pentaho.reporting.designer.core.inspections.impl;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.inspections.AttributeLocationInfo;
import org.pentaho.reporting.designer.core.inspections.Inspection;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.LocationInfo;
import org.pentaho.reporting.designer.core.model.ReportDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.states.datarow.StaticDataRow;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class InvalidQueryNameReferenceInspection implements Inspection
{
  public InvalidQueryNameReferenceInspection()
  {
  }

  public boolean isInlineInspection()
  {
    return true;
  }

  public void inspect(final ReportDesignerContext designerContext, final ReportRenderContext reportRenderContext,
                      final InspectionResultListener resultHandler)
  {
    final AbstractReportDefinition definition = reportRenderContext.getReportDefinition();
    final String query = definition.getQuery();
    if (query == null)
    {
      final AttributeLocationInfo queryLocation = new AttributeLocationInfo
          (definition, AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, false);
      resultHandler.notifyInspectionResult
          (new InspectionResult(this, InspectionResult.Severity.HINT,
              Messages.getString("InvalidQueryNameReferenceInspection.QueryUndefined"),
              queryLocation));

    }
    else
    {
      if (isQueryExecutable(definition, query) == false)
      {
        final AttributeLocationInfo queryLocation = new AttributeLocationInfo
            (definition, AttributeNames.Internal.NAMESPACE, AttributeNames.Internal.QUERY, false);
        resultHandler.notifyInspectionResult
            (new InspectionResult(this, InspectionResult.Severity.ERROR,
                Messages.getString("InvalidQueryNameReferenceInspection.QueryNotRecognized", query),
                queryLocation));
      }
    }

    final ReportDataSchemaModel dataSchemaModel = reportRenderContext.getReportDataSchemaModel();
    if (dataSchemaModel.isValid())
    {
      final Throwable throwable = dataSchemaModel.getDataFactoryException();
      if (throwable != null)
      {
        final DataFactory dataFactory = reportRenderContext.getMasterReportElement().getDataFactory();
        LocationInfo queryLocation;
        if (dataFactory instanceof CompoundDataFactory)
        {
          final CompoundDataFactory cdf = (CompoundDataFactory) dataFactory;
          final DataFactory element = cdf.getDataFactoryForQuery(query);
          if (element == null)
          {
            queryLocation = new LocationInfo(dataFactory);
          }
          else
          {
            queryLocation = new LocationInfo(element);
          }
        }
        else
        {
          queryLocation = new LocationInfo(dataFactory);
        }
        resultHandler.notifyInspectionResult
            (new InspectionResult(this, InspectionResult.Severity.ERROR,
                Messages.getString("InvalidQueryNameReferenceInspection.QueryDidNotExecute", query, throwable.toString()),
                queryLocation));
      }
    }
  }

  private boolean isQueryExecutable(AbstractReportDefinition definition,
                                    String query)
  {
    while (definition != null)
    {
      if (definition.getDataFactory().isQueryExecutable(query, new StaticDataRow()))
      {
        return true;
      }

      final Section parentSection = definition.getParentSection();
      if (parentSection == null)
      {
        definition = null;
      }
      else
      {
        definition = (AbstractReportDefinition) parentSection.getReportDefinition();
      }
    }
    return false;
  }


}
