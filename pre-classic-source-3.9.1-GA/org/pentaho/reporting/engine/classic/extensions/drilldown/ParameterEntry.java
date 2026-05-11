package org.pentaho.reporting.engine.classic.extensions.drilldown;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.02.2010
 * Time: 15:38:32
 *
 * @author Thomas Morgner.
 */
public class ParameterEntry
{
  private String parameterName;
  private Object parameterValue;

  public ParameterEntry(final String parameterName, final Object parameterValue)
  {
    this.parameterName = parameterName;
    this.parameterValue = parameterValue;
  }

  public String getParameterName()
  {
    return parameterName;
  }

  public Object getParameterValue()
  {
    return parameterValue;
  }
}
