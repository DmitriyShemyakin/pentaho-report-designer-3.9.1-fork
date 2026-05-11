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

package org.pentaho.plugin.jfreereport.reportcharts.collectors;

import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.data.general.Dataset;
import org.pentaho.plugin.jfreereport.reportcharts.CollectorFunctionResult;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.FunctionUtilities;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * A base class for collector functions. The series name can be given as either an static text or a column name.
 * If given and not empty, a column name takes precedence over a static series name.
 *
 * @author Thomas Morgner.
 */
public abstract class AbstractCollectorFunction extends AbstractFunction
{
  private static class CacheKey
  {
    private int index;
    private ReportStateKey stateKey;

    private CacheKey(final ReportStateKey stateKey, final int index)
    {
      this.stateKey = stateKey;
      this.index = index;
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

      final CacheKey cacheKey = (CacheKey) o;

      if (index != cacheKey.index)
      {
        return false;
      }
      if (stateKey != null ? !stateKey.equals(cacheKey.stateKey) : cacheKey.stateKey != null)
      {
        return false;
      }

      return true;
    }

    public int hashCode()
    {
      int result = index;
      result = 31 * result + (stateKey != null ? stateKey.hashCode() : 0);
      return result;
    }
  }

  private static class StaticCollectorFunctionResult implements CollectorFunctionResult
  {
    private Dataset dataSet;
    private CacheKey cacheKey;

    private StaticCollectorFunctionResult(final Dataset dataSet, final CacheKey cacheKey)
    {
      this.dataSet = dataSet;
      this.cacheKey = cacheKey;
    }

    public Dataset getDataSet()
    {
      return dataSet;
    }

    public Object getCacheKey()
    {
      return cacheKey;
    }
  }

  private Dataset dataSet;
  private ArrayList seriesNames;
  private ArrayList seriesColumns;
  private ArrayList results;
  private int currentIndex;
  private String summaryGroup;
  private String resetGroup;
  private ReportStateKey processKey;
  private Boolean autoGenerateMissingSeriesNames;

  public AbstractCollectorFunction()
  {
    results = new ArrayList();
    seriesColumns = new ArrayList();
    seriesNames = new ArrayList();
  }

  public boolean isSummaryDataSet()
  {
    return summaryGroup != null;
  }

  public void setSeriesName(final int index, final String field)
  {
    if (seriesNames.size() == index)
    {
      seriesNames.add(field);
    }
    else
    {
      seriesNames.set(index, field);
    }
  }

  public String getSeriesName(final int index)
  {
    return (String) seriesNames.get(index);
  }

  public int getSeriesNameCount()
  {
    return seriesNames.size();
  }

  public String[] getSeriesName()
  {
    return (String[]) seriesNames.toArray(new String[seriesNames.size()]);
  }

  public void setSeriesName(final String[] fields)
  {
    this.seriesNames.clear();
    this.seriesNames.addAll(Arrays.asList(fields));
  }

  public void setSeriesColumn(final int index, final String field)
  {
    if (seriesColumns.size() == index)
    {
      seriesColumns.add(field);
    }
    else
    {
      seriesColumns.set(index, field);
    }
  }

  public String getSeriesColumn(final int index)
  {
    return (String) seriesColumns.get(index);
  }

  public int getSeriesColumnCount()
  {
    return seriesColumns.size();
  }

  public String[] getSeriesColumn()
  {
    return (String[]) seriesColumns.toArray(new String[seriesColumns.size()]);
  }

  public void setSeriesColumn(final String[] fields)
  {
    this.seriesColumns.clear();
    this.seriesColumns.addAll(Arrays.asList(fields));
  }

  public String getResetGroup()
  {
    return resetGroup;
  }

  public void setResetGroup(final String resetGroup)
  {
    this.resetGroup = resetGroup;
  }

  public String getSummaryGroup()
  {
    return summaryGroup;
  }

  public void setSummaryGroup(final String summaryGroup)
  {
    this.summaryGroup = summaryGroup;
  }

  /**
   * Return the current expression value.
   * <p/>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue()
  {
    return new StaticCollectorFunctionResult(dataSet, new CacheKey(processKey, currentIndex));
  }


  public void reportInitialized(final ReportEvent event)
  {
    currentIndex = -1;
    if (processKey == null)
    {
      processKey = event.getState().getProcessKey();
    }
    
    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event))
    {
      dataSet = null;
      results.clear();
      if (getResetGroup() == null)
      {

        dataSet = createNewDataset();
        results.add(dataSet);

      }
    }
    else
    {
      // Activate the current group, which was filled in the prepare run.
      if (getResetGroup() == null && results.isEmpty() == false)
      {
        dataSet = (Dataset) results.get(0);
      }
    }
  }

  public void groupStarted(final ReportEvent event)
  {
    if (!FunctionUtilities.isDefinedGroup(getResetGroup(), event))
    {
      return;
    }

    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event))
    {
      dataSet = createNewDataset();
      results.add(dataSet);
    }
    else if (FunctionUtilities.isLayoutLevel(event))
    {
      // Activate the current group, which was filled in the prepare run.
      currentIndex += 1;
      dataSet = (Dataset) results.get(currentIndex);
    }
  }

  protected Dataset getDataSet()
  {
    return dataSet;
  }

  /**
   * Receives notification that a row of data is being processed.
   *
   * @param event the event.
   */
  public void itemsAdvanced(final ReportEvent event)
  {
    if (isSummaryDataSet())
    {
      return;
    }
    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event) == false)
    {
      return;
    }

    buildDataset();
  }

  /**
   * Receives notification that a group has finished.
   *
   * @param event the event.
   */
  public void groupFinished(final ReportEvent event)
  {
    if (isSummaryDataSet() == false)
    {
      return;
    }

    if (FunctionUtilities.isDefinedGroup(getSummaryGroup(), event) == false)
    {
      return;
    }

    if (FunctionUtilities.isDefinedPrepareRunLevel(this, event) == false)
    {
      return;
    }

    buildDataset();
  }

  protected void buildDataset()
  {
  }

  protected abstract Dataset createNewDataset();

  protected int getMaximumSeriesIndex()
  {
    return Math.max(seriesColumns.size(), seriesNames.size());
  }

  public Boolean getAutoGenerateMissingSeriesNames()
  {
    return autoGenerateMissingSeriesNames;
  }

  public void setAutoGenerateMissingSeriesNames(final Boolean autoGenerateMissingSeriesNames)
  {
    this.autoGenerateMissingSeriesNames = autoGenerateMissingSeriesNames;
  }

  protected String generateName(final int index)
  {
    return "Series " + (index + 1);
  }

  protected Comparable querySeriesValue(final int index)
  {
    if (index < getSeriesColumnCount())
    {
      final String seriesColumn = getSeriesColumn(index);
      if (StringUtils.isEmpty(seriesColumn) == false)
      {
        final Object o = getDataRow().get(seriesColumn);
        if (o instanceof Comparable)
        {
          return (Comparable) o;
        }
        if (Boolean.FALSE.equals(autoGenerateMissingSeriesNames))
        {
          return null;
        }
        return generateName(index);
      }
    }

    if (index < getSeriesNameCount())
    {
      final String retval = getSeriesName(index);
      if (retval != null)
      {
        return retval;
      }
    }
    if (Boolean.FALSE.equals(autoGenerateMissingSeriesNames))
    {
      return null;
    }
    return generateName(index);
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    final AbstractCollectorFunction expression = (AbstractCollectorFunction) super.getInstance();
    expression.dataSet = null;
    expression.processKey = null;
    expression.currentIndex = -1;
    expression.results = (ArrayList) results.clone();
    expression.results.clear();
    expression.seriesColumns = (ArrayList) seriesColumns.clone();
    expression.seriesNames = (ArrayList) seriesNames.clone();
    return expression;
  }


}
