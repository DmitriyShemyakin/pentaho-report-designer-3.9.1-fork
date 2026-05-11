package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.02.2010
 * Time: 15:37:33
 *
 * @author Thomas Morgner.
 */
public interface LinkCustomizer
{
  public String format(final FormulaContext formulaContext,
                       final String configIndicator,
                       final String hostName,
                       final ParameterEntry[] entries) throws EvaluationException;
}
