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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

/**
 * This function is used to generate sheet names into table exports. Sheet names are generated on page breaks and have
 * different representations depending the export type.<br/> To use this functionnality report configuration must set
 * the property {@link #DECALRED_SHEETNAME_FUNCTION_KEY} to point to an existing function or property accessible within
 * the report.
 * <p/>
 * As for example using simple report definition:<br/>
 * <pre>
 * &lt;report&gt;
 *   &lt;configuration&gt;
 *     &lt;!-- where sheetNameExpression is pointing to a valid function declared in this report --&gt;
 *     &lt;property name="org.pentaho.reporting.engine.classic.core.targets.table.TableWriter.SheetNameFunction"&gtsheetNameExpression&lt;/property&gt;
 *   &lt;/configuration&gt;
 *   ...
 * &lt;/report&gt;
 * </pre>
 * This way of defining a sheetname for elements is deprecated. Sheetnames should be declared or computed directly on
 * the bands by specifiing a sheetname using the "computed-sheetname" style-property.
 *
 * @author Cedric Pronzato
 */
public class SheetNameFunction extends AbstractElementFormatFunction implements StructureFunction
{
  private static final Log logger = LogFactory.getLog(SheetNameFunction.class);

  /**
   * The configuration property declaring the function name to call in order to generate sheet names.<br/>
   */
  private static final String DECALRED_SHEETNAME_FUNCTION_KEY =
      "org.pentaho.reporting.engine.classic.core.targets.table.TableWriter.SheetNameFunction";

  /**
   * A property that holds the last computed value of the sheetname function.
   */
  private transient String lastValue;
  /**
   * A property that holds the name of the column from where to receive the sheetname.
   */
  private transient String functionToCall;

  /**
   * Default constructor.
   */
  public SheetNameFunction()
  {
  }


  public void reportInitialized(final ReportEvent event)
  {
    functionToCall = this.getReportConfiguration().getConfigProperty(SheetNameFunction.DECALRED_SHEETNAME_FUNCTION_KEY);
    super.reportInitialized(event);
  }

  /**
   * Overrides the dependency level to only execute this function on the pagination and content-generation level.
   *
   * @return LayoutProcess.LEVEL_PAGINATE.
   */
  public int getDependencyLevel()
  {
    return LayoutProcess.LEVEL_PAGINATE;
  }

  public int getProcessingPriority()
  {
    return 3000;
  }

  /**
   * Sets the sheet name value to the current <code>Band</code> {@link org.pentaho.reporting.engine.classic.core.style.BandStyleKeys#COMPUTED_SHEETNAME}
   * style key.
   *
   * @param b The current band element.
   */
  protected void processRootBand(final Band b)
  {
    processSheetName(b);
  }

  private void processSheetName(final Element b)
  {
    lastValue = null;
    // if exporting to a table/* export
    if (functionToCall == null)
    {
      return;
    }

    if (!getRuntime().getExportDescriptor().startsWith("table/"))
    {
      return;
    }
    final Object value = this.getDataRow().get(functionToCall);
    if (value == null)
    {
      SheetNameFunction.logger.debug(
          "Cannot find the sheetname function/property referenced by '" + functionToCall + '\'');
    }
    else
    {
      // setting the value as a style property
      lastValue = value.toString();
      b.getStyle().setStyleProperty(BandStyleKeys.COMPUTED_SHEETNAME, lastValue);
    }
  }

  protected void processGroup(final Group group)
  {
    super.processGroup(group);
    processSheetName(group);
    processSheetName(group.getBody());

  }

  /**
   * Structure functions do not care of the result so this method should never be called.
   *
   * @return <code>null</code>
   */
  public Object getValue()
  {
    return lastValue;
  }
}
