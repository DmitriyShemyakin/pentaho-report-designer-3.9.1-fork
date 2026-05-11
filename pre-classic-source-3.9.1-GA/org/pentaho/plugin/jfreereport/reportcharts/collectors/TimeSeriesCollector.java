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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.TimeSeriesDataItem;
import org.jfree.data.time.Day;
import org.jfree.data.xy.XYSeries;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * The number of entries in the series, value, and timeValueColumns properties must be the same. The function
 * will collect from tuples of {series, value (y), timeValue (x)}. 
 *
 * @author Thomas Morgner.
 */
public class TimeSeriesCollector extends AbstractCollectorFunction
{
  /**
   * @noinspection EqualsAndHashcode
   */
  private static class FastTimeSeriesCollection extends TimeSeriesCollection
  {
    private static final long serialVersionUID = 2096209400748561882L;

    /**
     * Superclass hashcode is WAY slow.
     *
     * @see TimeSeriesCollection#hashCode()
     */
    public int hashCode()
    {
      return this.getSeriesCount();
    }
  }

  private Class timePeriod;
  private ArrayList valueColumns;
  private ArrayList timeValueColumns;
  private HashMap seriesMap;

  public TimeSeriesCollector()
  {
    this.valueColumns = new ArrayList();
    this.timeValueColumns = new ArrayList();
    this.timePeriod = Day.class;
  }

  protected Dataset createNewDataset()
  {
    seriesMap = new HashMap();
    return new FastTimeSeriesCollection();
  }

  public Class getTimePeriod()
  {
    return timePeriod;
  }

  public void setTimePeriod(final Class timePeriod)
  {
    this.timePeriod = timePeriod;
  }


  public void setValueColumn(final int index, final String field)
  {
    if (valueColumns.size() == index)
    {
      valueColumns.add(field);
    }
    else
    {
      valueColumns.set(index, field);
    }
  }

  public void setTimeValueColumn(final int index, final String field)
  {
    if (timeValueColumns.size() == index)
    {
      timeValueColumns.add(field);
    }
    else
    {
      timeValueColumns.set(index, field);
    }
  }

  public String getValueColumn(final int index)
  {
    return (String) valueColumns.get(index);
  }

  public int getValueColumnCount()
  {
    return valueColumns.size();
  }

  public String[] getValueColumn()
  {
    return (String[]) valueColumns.toArray(new String[valueColumns.size()]);
  }

  public void setValueColumn(final String[] fields)
  {
    this.valueColumns.clear();
    this.valueColumns.addAll(Arrays.asList(fields));
  }

  public String getTimeValueColumn(final int index)
  {
    return (String) timeValueColumns.get(index);
  }

  public int getTimeValueColumnCount()
  {
    return timeValueColumns.size();
  }

  public String[] getTimeValueColumn()
  {
    return (String[]) timeValueColumns.toArray(new String[timeValueColumns.size()]);
  }

  public void setTimeValueColumn(final String[] fields)
  {
    this.timeValueColumns.clear();
    this.timeValueColumns.addAll(Arrays.asList(fields));
  }

  protected void buildDataset()
  {
    final Object o = getDataSet();
    if (o instanceof TimeSeriesCollection == false)
    {
      return;
    }

    final TimeSeriesCollection timeSeriesDataset = (TimeSeriesCollection) o;
    final List seriesList = timeSeriesDataset.getSeries();
    if (seriesMap == null)
    {
      final HashMap seriesMap = new HashMap();
      for (int i = 0; i < seriesList.size(); i++)
      {
        final XYSeries series = (XYSeries) seriesList.get(i);
        seriesMap.put(series.getKey(), series);
      }
      this.seriesMap = seriesMap;
    }

    final int maxIndex = this.valueColumns.size();
    for (int i = 0; i < maxIndex; i++)
    {
      final Comparable seriesName = querySeriesValue(i);
      final Object valueObject = getDataRow().get((String) valueColumns.get(i));
      final Object timeValueObject = getDataRow().get((String) timeValueColumns.get(i));

      final Number value = (valueObject instanceof Number) ? (Number) valueObject : null;
      final Date timeValue = convertToDate(timeValueObject);
      if (timeValue == null)
      {
        continue;
      }
      
      TimeSeries series = (TimeSeries) seriesMap.get(seriesName);
      if (series == null)
      {
        series = new TimeSeries(seriesName, getTimePeriod());
        timeSeriesDataset.addSeries(series);
        seriesMap.put(seriesName, series);
      }

      final RegularTimePeriod regularTimePeriod =
          RegularTimePeriod.createInstance(getTimePeriod(), timeValue, TimeZone.getDefault());

      final TimeSeriesDataItem timeSeriesDataItem = new TimeSeriesDataItem(regularTimePeriod, value);
      series.add(timeSeriesDataItem);
    }
  }

  private Date convertToDate(final Object value)
  {
    if (value instanceof Date)
    {
      return (Date) value;
    }
    if (value instanceof Number)
    {
      final Number n = (Number) value;
      return new Date(n.longValue());
    }
    return null;

  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event The event.
   */
  public void reportDone(final ReportEvent event)
  {
    seriesMap = null;
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    final TimeSeriesCollector expression = (TimeSeriesCollector) super.getInstance();
    expression.valueColumns = (ArrayList) valueColumns.clone();
    expression.timeValueColumns = (ArrayList) timeValueColumns.clone();
    expression.seriesMap = null;
    return expression;
  }
}
