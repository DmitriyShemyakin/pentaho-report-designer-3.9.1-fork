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

package org.pentaho.reporting.engine.classic.core.states;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;

import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterValidator;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.engine.classic.core.util.ReportProperties;
import org.pentaho.reporting.engine.classic.core.util.beans.DateValueConverter;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Creation-Date: Dec 14, 2006, 7:59:39 PM
 *
 * @author Thomas Morgner
 */
public class StateUtilities
{
  /**
   * A comparator for levels in descending order.
   */
  public static final class DescendingComparator implements Comparator, Serializable
  {
    /**
     * Default constructor.
     */
    public DescendingComparator()
    {
    }

    /**
     * Compares its two arguments for order.  Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.<p>
     *
     * @param o1 the first object to be compared.
     * @param o2 the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     * @throws ClassCastException if the arguments' types prevent them from being compared by this Comparator.
     */
    public int compare(final Object o1, final Object o2)
    {
      if ((o1 instanceof Comparable) == false)
      {
        throw new ClassCastException("Need comparable Elements");
      }
      if ((o2 instanceof Comparable) == false)
      {
        throw new ClassCastException("Need comparable Elements");
      }
      final Comparable c1 = (Comparable) o1;
      final Comparable c2 = (Comparable) o2;
      return -1 * c1.compareTo(c2);
    }
  }

  private StateUtilities()
  {
  }

  public static void computeLevels(final DefaultFlowController report,
                                   final LayoutProcess lp,
                                   final HashSet<Integer> levels)
  {
    if (report == null)
    {
      throw new NullPointerException();
    }
    if (lp == null)
    {
      throw new NullPointerException();
    }
    if (levels == null)
    {
      throw new NullPointerException();
    }
    final StructureFunction[] collectionFunctions = lp.getCollectionFunctions();
    for (int i = 0; i < collectionFunctions.length; i++)
    {
      final StructureFunction function = collectionFunctions[i];
      if (function.getDependencyLevel() == Integer.MAX_VALUE)
      {
        // this indicates a structural-preprocessor function, like the CrosstabNormalizer. They do not
        // take part in the ordinary processing.
        continue;
      }

      levels.add(IntegerCache.getInteger(function.getDependencyLevel()));
    }
    levels.add(IntegerCache.getInteger(LayoutProcess.LEVEL_PAGINATE));

    final Expression[] expressions = report.getMasterRow().getExpressionDataRow().getExpressions();
    for (int i = 0; i < expressions.length; i++)
    {
      final Expression expression = expressions[i];
      levels.add(IntegerCache.getInteger(expression.getDependencyLevel()));
    }
  }

  public static ValidationResult validate(final MasterReport report,
                                          final ValidationResult result) throws ReportProcessingException
  {

    final ReportParameterDefinition parameters = report.getParameterDefinition();
    final DefaultParameterContext parameterContext = new DefaultParameterContext(report);
    parameterContext.open();
    try
    {
      final ReportParameterValidator reportParameterValidator = parameters.getValidator();
      return reportParameterValidator.validate(result, parameters, parameterContext);
    }
    finally
    {
      parameterContext.close();
    }
  }

  /**
   * Computes the parameter value set for a given report. Note that this method ignores the validation result,
   * so if the specified parameter values are wrong you may end up with a bunch of default values.
   *
   * @param report
   * @return
   * @throws ReportProcessingException
   */
  public static ReportParameterValues computeParameterValueSet(final MasterReport report) throws ReportProcessingException
  {

    final ReportParameterDefinition parameters = report.getParameterDefinition();
    final DefaultParameterContext parameterContext = new DefaultParameterContext(report);
    parameterContext.open();
    final ReportParameterValues parameterValues;
    try
    {
      final ReportParameterValidator reportParameterValidator = parameters.getValidator();
      final ValidationResult validationResult =
          reportParameterValidator.validate(new ValidationResult(), parameters, parameterContext);
      parameterValues = validationResult.getParameterValues();
      return computeParameterValueSet(report, parameterValues);
    }
    finally
    {
      parameterContext.close();
    }
  }

  public static ReportParameterValues computeParameterValueSet(final MasterReport report,
                                                               final ReportParameterValues parameterValues)
      throws ReportProcessingException
  {
    final ReportParameterValues retval = new ReportParameterValues();
    // for the sake of backward compatiblity ..
    final ReportProperties reportProperties = report.getProperties();
    final String[] propertyKeys = reportProperties.keyArray();
    for (int i = 0; i < propertyKeys.length; i++)
    {
      final String string = propertyKeys[i];
      retval.put(string, reportProperties.get(string));
    }
    retval.putAll(parameterValues);

    final ReportEnvironment reportEnvironment = report.getReportEnvironment();
    final Object property = tryParse(reportEnvironment.getEnvironmentProperty("::internal::report.date"));
    if (property == null)
    {
      retval.put(MasterReport.REPORT_DATE_PROPERTY, new Date());
    }
    else
    {
      retval.put(MasterReport.REPORT_DATE_PROPERTY, property);
    }
    return retval;
  }

  private static Date tryParse(final String value)
  {
    try
    {
      if (value == null)
      {
        return null;
      }
      return (Date) new DateValueConverter().toPropertyValue(value);
    }
    catch (Exception e)
    {
      // ignore ..
    }
    return null;
  }

  public static ReportParameterValues computeParameterValueSet(final SubReport report)
  {
    // todo: Grab parent reports and compute the dataschema for them, so that the parameters here
    // get a meaning.

    final ReportParameterValues retval = new ReportParameterValues();
    // for the sake of backward compatiblity ..
    retval.put(MasterReport.REPORT_DATE_PROPERTY, new Date());

    final ParameterMapping[] reportParameterValues = report.getInputMappings();
    for (int i = 0; i < reportParameterValues.length; i++)
    {
      final ParameterMapping mapping = reportParameterValues[i];
      if ("*".equals(mapping.getName()))
      {
        continue;
      }
      retval.put(mapping.getName(), null);
    }
    return retval;
  }

  /**
   * @deprecated will be removed in the next release
   */
  public static int findGroupByName(final ReportDefinition definition, final String groupName)
  {
    final int count = definition.getGroupCount();
    for (int i = 0; i < count; i++)
    {
      final Group group = definition.getGroup(i);
      if (ObjectUtilities.equal(groupName, group.getName()))
      {
        return i;
      }
    }
    return -1;
  }
}
