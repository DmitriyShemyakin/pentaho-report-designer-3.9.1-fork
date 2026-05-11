package org.pentaho.plugin.jfreereport.reportcharts.collectors;

import java.util.ArrayList;
import java.util.Arrays;

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.pentaho.reporting.engine.classic.core.function.Expression;

/**
 * Todo: Document me!
 * <p/>
 * Date: 01.03.2010
 * Time: 14:08:15
 *
 * @author Thomas Morgner.
 */
public class IntervalXYSeriesCollector extends AbstractCollectorFunction
{
  private ArrayList xMinValueColumns;
  private ArrayList yMinValueColumns;
  private ArrayList xMaxValueColumns;
  private ArrayList yMaxValueColumns;

  public IntervalXYSeriesCollector()
  {
    xMinValueColumns = new ArrayList();
    yMinValueColumns = new ArrayList();
    xMaxValueColumns = new ArrayList();
    yMaxValueColumns = new ArrayList();
  }

  protected Dataset createNewDataset()
  {
    return new XYIntervalSeriesCollection();
  }

  public void setxMinValueColumn(final int index, final String field)
  {
    if (xMinValueColumns.size() == index)
    {
      xMinValueColumns.add(field);
    }
    else
    {
      xMinValueColumns.set(index, field);
    }
  }

  public String getxMinValueColumn(final int index)
  {
    return (String) xMinValueColumns.get(index);
  }

  public int getxMinValueColumnCount()
  {
    return xMinValueColumns.size();
  }

  public String[] getxMinValueColumn()
  {
    return (String[]) xMinValueColumns.toArray(new String[xMinValueColumns.size()]);
  }

  public void setxMinValueColumn(final String[] fields)
  {
    this.xMinValueColumns.clear();
    this.xMinValueColumns.addAll(Arrays.asList(fields));
  }

  public void setyMinValueColumn(final int index, final String field)
  {
    if (yMinValueColumns.size() == index)
    {
      yMinValueColumns.add(field);
    }
    else
    {
      yMinValueColumns.set(index, field);
    }
  }


  public String getyMinValueColumn(final int index)
  {
    return (String) yMinValueColumns.get(index);
  }

  public int getyMinValueColumnCount()
  {
    return yMinValueColumns.size();
  }

  public String[] getyMinValueColumn()
  {
    return (String[]) yMinValueColumns.toArray(new String[yMinValueColumns.size()]);
  }

  public void setyMinValueColumn(final String[] fields)
  {
    this.yMinValueColumns.clear();
    this.yMinValueColumns.addAll(Arrays.asList(fields));
  }

  public void setxMaxValueColumn(final int index, final String field)
  {
    if (xMaxValueColumns.size() == index)
    {
      xMaxValueColumns.add(field);
    }
    else
    {
      xMaxValueColumns.set(index, field);
    }
  }


  public String getxMaxValueColumn(final int index)
  {
    return (String) xMaxValueColumns.get(index);
  }

  public int getxMaxValueColumnCount()
  {
    return xMaxValueColumns.size();
  }

  public String[] getxMaxValueColumn()
  {
    return (String[]) xMaxValueColumns.toArray(new String[xMaxValueColumns.size()]);
  }

  public void setxMaxValueColumn(final String[] fields)
  {
    this.xMaxValueColumns.clear();
    this.xMaxValueColumns.addAll(Arrays.asList(fields));
  }

  public String getyMaxValueColumn(final int index)
  {
    return (String) yMaxValueColumns.get(index);
  }

  public int getyMaxValueColumnCount()
  {
    return yMaxValueColumns.size();
  }

  public String[] getyMaxValueColumn()
  {
    return (String[]) yMaxValueColumns.toArray(new String[yMaxValueColumns.size()]);
  }

  public void setyMaxValueColumn(final String[] fields)
  {
    this.yMaxValueColumns.clear();
    this.yMaxValueColumns.addAll(Arrays.asList(fields));
  }

  /**
   * Return a completly separated copy of this function. The copy does no longer share any changeable objects with the
   * original function.
   *
   * @return a copy of this function.
   */
  public Expression getInstance()
  {
    final IntervalXYSeriesCollector expression = (IntervalXYSeriesCollector) super.getInstance();
    expression.xMinValueColumns = (ArrayList) xMinValueColumns.clone();
    expression.yMinValueColumns = (ArrayList) yMinValueColumns.clone();
    expression.xMaxValueColumns = (ArrayList) xMaxValueColumns.clone();
    expression.yMaxValueColumns = (ArrayList) yMaxValueColumns.clone();
    return expression;
  }


  protected void buildDataset()
  {
    final XYIntervalSeriesCollection xyIntervalxySeriesDataset = (XYIntervalSeriesCollection) getDataSet();

    final int maxIndex = Math.min(this.yMaxValueColumns.size(),
        Math.min(this.xMinValueColumns.size(),
        Math.min(this.yMinValueColumns.size(), this.xMaxValueColumns.size())));
    for (int i = 0; i < maxIndex; i++)
    {
      final Comparable seriesName = querySeriesValue(i);
      final Object xValueObject = getDataRow().get((String) xMinValueColumns.get(i));
      final Object yValueObject = getDataRow().get((String) yMinValueColumns.get(i));
      final Object xMaxValueObject = getDataRow().get((String) xMaxValueColumns.get(i));
      final Object yMaxValueObject = getDataRow().get((String) yMaxValueColumns.get(i));
      final Number xValue = (xValueObject instanceof Number) ? (Number) xValueObject : null;
      final Number yValue = (yValueObject instanceof Number) ? (Number) yValueObject : null;
      final Number xMaxValue = (xMaxValueObject instanceof Number) ? (Number) xMaxValueObject : null;
      final Number yMaxValue = (yMaxValueObject instanceof Number) ? (Number) yMaxValueObject : null;

      if (xValue == null || yValue == null || xMaxValue == null || yMaxValue == null)
      {
        continue;
      }


      //find series
      final XYIntervalSeries l_xyIntervalSeries;
      final int index = xyIntervalxySeriesDataset.indexOf(seriesName);
      if (index == -1)
      {
        l_xyIntervalSeries = new XYIntervalSeries(seriesName);
        xyIntervalxySeriesDataset.addSeries(l_xyIntervalSeries);
      }
      else
      {
        l_xyIntervalSeries = xyIntervalxySeriesDataset.getSeries(index);
      }


      l_xyIntervalSeries.add(xValue.doubleValue(), xValue.doubleValue(), xMaxValue.doubleValue(),
          yValue.doubleValue(), yValue.doubleValue(), yMaxValue.doubleValue());


    }
  }

}
