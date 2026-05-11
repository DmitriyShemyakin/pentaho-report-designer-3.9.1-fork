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

package org.pentaho.reporting.engine.classic.core.wizard;

import java.util.HashSet;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.AggregationFunction;
import org.pentaho.reporting.engine.classic.core.function.FieldAggregationFunction;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AggregateFieldPreProcessor implements ReportPreProcessor
{
  private HashSet generatedExpressionNames;

  public AggregateFieldPreProcessor()
  {
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public MasterReport performPreProcessing(final MasterReport definition,
                                           final DefaultFlowController flowController)
      throws ReportProcessingException
  {
    try
    {
      generatedExpressionNames = new HashSet();

      final DataSchema schema = flowController.getDataSchema();
      processSection(schema, definition, definition);
      return definition;
    }
    finally
    {
      generatedExpressionNames = null;
    }
  }

  public SubReport performPreProcessing(final SubReport definition,
                                        final DefaultFlowController flowController)
      throws ReportProcessingException
  {
    try
    {
      generatedExpressionNames = new HashSet();

      final DataSchema schema = flowController.getDataSchema();
      processSection(schema, definition, definition);
      return definition;
    }
    finally
    {
      generatedExpressionNames = null;
    }
  }

  private void processSection(final DataSchema schema,
                              final AbstractReportDefinition definition,
                              final Section section) throws ReportProcessingException
  {
    final int count = section.getElementCount();
    for (int i = 0; i < count; i++)
    {
      final ReportElement element = section.getElement(i);
      if (element instanceof SubReport)
      {
        continue;
      }

      if (element instanceof Section)
      {
        processSection(schema, definition, (Section) element);
        continue;
      }

      final Object attribute =
          element.getAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE);
      if (attribute instanceof Class == false)
      {
        continue;
      }

      final Class aggType = (Class) attribute;
      if (AggregationFunction.class.isAssignableFrom(aggType) == false)
      {
        continue;
      }

      try
      {
        final AggregationFunction o = (AggregationFunction) aggType.newInstance();
        final String group = (String) element.getAttribute
            (AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_GROUP);
        if (group != null)
        {
          o.setGroup(group);
        }
        else
        {
          final Group g = findGroup(element);
          if (g != null)
          {
            if (g.getName() == null)
            {
              g.setName("::wizard:group:" + g.getClass().getName() + ':' + System.identityHashCode(g));
            }

            o.setGroup(g.getName());
          }
        }

        final String fieldName = (String) element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD);
        if (o instanceof FieldAggregationFunction)
        {
          final FieldAggregationFunction fo = (FieldAggregationFunction) o;
          fo.setField(fieldName);
        }

        final Object labelFor =
            element.getAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR);
        if (labelFor == null)
        {
          element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR, fieldName);
        }

        final String name = AutoGeneratorUtility.generateUniqueExpressionName
            (schema, "::wizard:aggregation:{0}",
                (String[]) generatedExpressionNames.toArray(new String[generatedExpressionNames.size()]));
        o.setName(name);
        generatedExpressionNames.add(name);

        element.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, name);
        // finally clean up 
        element.setAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.AGGREGATION_TYPE, null);
        definition.addExpression(o);
      }
      catch (Exception e)
      {
        throw new ReportProcessingException("Failed to pre-process the report", e);
      }
    }
  }

  private Group findGroup(final ReportElement element)
  {
    Section parentSection = element.getParentSection();
    while (parentSection != null)
    {
      if (parentSection instanceof Group)
      {
        return (Group) parentSection;
      }
      parentSection = parentSection.getParentSection();
    }
    return null;
  }
}
