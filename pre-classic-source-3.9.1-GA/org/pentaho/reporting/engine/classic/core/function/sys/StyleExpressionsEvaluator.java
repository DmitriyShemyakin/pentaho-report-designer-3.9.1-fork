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

package org.pentaho.reporting.engine.classic.core.function.sys;

import java.io.IOException;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.util.beans.ValueConverter;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.formula.ErrorValue;

/**
 * Evaluates style-expressions and updates the stylesheet. This is an internal helper function. It is not meant to be
 * used by end-users and manually adding this function to a report will cause funny side-effects.
 *
 * @author Thomas Morgner
 */
public class StyleExpressionsEvaluator extends AbstractElementFormatFunction
    implements StructureFunction
{

  private static class NeedEvalResult
  {
    private boolean needToRun;
    private long changeTracker;

    private NeedEvalResult(final boolean needToRun, final long changeTracker)
    {
      this.needToRun = needToRun;
      this.changeTracker = changeTracker;
    }

    public boolean isNeedToRun()
    {
      return needToRun;
    }

    public long getChangeTracker()
    {
      return changeTracker;
    }
  }

  private LFUMap expressionsCache;

  /**
   * Default Constructor.
   */
  public StyleExpressionsEvaluator()
  {
    expressionsCache = new LFUMap(500);
  }

  /**
   * Receives notification that report generation initializes the current run. <P> The event carries a
   * ReportState.Started state.  Use this to initialize the report.
   *
   * @param event The event.
   */
  public void reportInitialized(final ReportEvent event)
  {
    if (FunctionUtilities.isLayoutLevel(event) == false)
    {
      // dont do anything if there is no printing done ...
      return;
    }

    super.reportInitialized(event);


    if (event.getState().isSubReportEvent() == false)
    {
      // only evaluate master-reports. Subreports are evaluated when their parent-band is evaluated.
      final ReportDefinition definition = event.getReport();
      evaluateElement(definition);
    }
  }

  /**
   * Evaluates all style expressions from all elements and updates the style-sheet if needed.
   *
   * @param b the band.
   */
  protected void processRootBand(final Band b)
  {
    final NeedEvalResult needToRun = (NeedEvalResult) expressionsCache.get(b.getObjectID());
    if (needToRun != null)
    {
      if (needToRun.isNeedToRun() == false)
      {
        if (b.getChangeTracker() == needToRun.getChangeTracker())
        {
          return;
        }
      }
    }

    final boolean needToRunVal = processBand(b);
    expressionsCache.put(b.getObjectID(), new NeedEvalResult(needToRunVal, b.getChangeTracker()));
  }

  private boolean processBand(final Band b)
  {
    boolean hasStyleExpressions = evaluateElement(b);

    if (b.isVisible() == false)
    {
      return hasStyleExpressions;
    }

    final Element[] elementBuffer = b.unsafeGetElementArray();
    final int length = elementBuffer.length;
    for (int i = 0; i < length; i++)
    {
      final Element element = elementBuffer[i];
      if (element instanceof Band)
      {
        if (processBand((Band) element))
        {
          hasStyleExpressions = true;
        }
      }
      else
      {
        if (evaluateElement(element))
        {
          hasStyleExpressions = true;
        }
      }
    }

    if (b instanceof RootLevelBand)
    {
      final RootLevelBand rlb = (RootLevelBand) b;
      final SubReport[] reports = rlb.getSubReports();
      for (int i = 0; i < reports.length; i++)
      {
        final SubReport subReport = reports[i];
        if (evaluateElement(subReport))
        {
          hasStyleExpressions = true;
        }
      }
    }
    return hasStyleExpressions;
  }

  public int getProcessingPriority()
  {
    return 10000;
  }

  protected void processGroup(final Group group)
  {
    evaluateElement(group);
    processRootBand(group.getHeader());
    evaluateElement(group.getBody());
  }

  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e the element that should be updated.
   * @return true, if the element has style-expressions, or false otherwise.
   */
  protected boolean evaluateElement(final ReportElement e)
  {
    final Map styleExpressions = e.getStyleExpressions();
    if (styleExpressions.isEmpty())
    {
      return false;
    }
    boolean retval = false;
    final ElementStyleSheet style = e.getStyle();
    final Iterator entries = styleExpressions.entrySet().iterator();
    while (entries.hasNext())
    {
      final Map.Entry entry = (Map.Entry) entries.next();
      final StyleKey key = (StyleKey) entry.getKey();
      final Expression ex = (Expression) entry.getValue();
      if (ex == null)
      {
        continue;
      }
      retval = true;
      ex.setRuntime(getRuntime());
      try
      {
        final Object value = evaluate(ex);
        if (value == null)
        {
          style.setStyleProperty(key, null);
        }
        else if (key.getValueType().isInstance(value))
        {
          style.setStyleProperty(key, value);
        }
        else if (value instanceof ErrorValue)
        {
          style.setStyleProperty(key, null);
        }
        else
        {
          final ValueConverter valueConverter = ConverterRegistry.getInstance().getValueConverter(key.getValueType());
          if (valueConverter != null)
          {
            // try to convert it ..
            final Object o = ConverterRegistry.toPropertyValue(String.valueOf(value), key.getValueType());
            style.setStyleProperty(key, o);
          }
          else
          {
            style.setStyleProperty(key, null);
          }
        }
      }
      catch (Exception exception)
      {
        // ignored, but we clear the style as we have no valid value anymore.
        style.setStyleProperty(key, null);
      }
      finally
      {
        ex.setRuntime(null);
      }
    }
    return retval;
  }

  private Object evaluate(final Expression ex)
  {
    final Object retval = ex.getValue();
    if (retval instanceof Clob)
    {
      try
      {
        return IOUtils.getInstance().readClob((Clob) retval);
      }
      catch (Exception e)
      {
        return null;
      }
    }
    return retval;
  }

  /**
   * Returns the dependency level for the expression (controls evaluation order for expressions and functions).
   *
   * @return the level.
   */
  public int getDependencyLevel()
  {
    return LayoutProcess.LEVEL_PAGINATE;
  }

  public Expression getInstance()
  {
    final StyleExpressionsEvaluator eval = (StyleExpressionsEvaluator) super.getInstance();
    eval.expressionsCache = new LFUMap(500);
    return eval;
  }
}
