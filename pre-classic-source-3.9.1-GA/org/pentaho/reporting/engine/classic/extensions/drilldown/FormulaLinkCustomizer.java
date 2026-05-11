package org.pentaho.reporting.engine.classic.extensions.drilldown;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.parameters.CompoundDataRow;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.libraries.formula.ContextEvaluationException;
import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 09.02.2010
 * Time: 15:39:24
 *
 * @author Thomas Morgner.
 */
public class FormulaLinkCustomizer implements LinkCustomizer
{
  private static final String TAB_ACTIVE_PARAMETER = "::TabActive";
  private static final String TAB_NAME_PARAMETER = "::TabName";

  public FormulaLinkCustomizer()
  {
  }

  private String computeMantleTabActive(final FormulaContext formulaContext,
                                        final ParameterEntry[] entries) throws ContextEvaluationException
  {
    for (int i = 0; i < entries.length; i++)
    {
      final ParameterEntry parameterEntry = entries[i];
      final String parameterName = parameterEntry.getParameterName();
      if (TAB_ACTIVE_PARAMETER.equals(parameterName))
      {
        final Object o = parameterEntry.getParameterValue();
        if (o != null)
        {
          return String.valueOf(o);
        }
      }
    }

    final Object o = formulaContext.resolveReference(TAB_ACTIVE_PARAMETER);
    if (o != null)
    {
      return String.valueOf(o);
    }
    return null;
  }

  private String computeMantleTabName(final FormulaContext formulaContext,
                                      final ParameterEntry[] entries) throws ContextEvaluationException
  {
    for (int i = 0; i < entries.length; i++)
    {
      final ParameterEntry parameterEntry = entries[i];
      final String parameterName = parameterEntry.getParameterName();
      if (TAB_NAME_PARAMETER.equals(parameterName))
      {
        final Object o = parameterEntry.getParameterValue();
        if (o != null)
        {
          return String.valueOf(o);
        }
      }
    }

    final Object o = formulaContext.resolveReference(TAB_NAME_PARAMETER);
    if (o != null)
    {
      return String.valueOf(o);
    }
    return null;
  }

  private ParameterEntry[] filterEntries(final ParameterEntry[] entries)
  {
    final ArrayList<ParameterEntry> list = new ArrayList<ParameterEntry>();
    for (int i = 0; i < entries.length; i++)
    {
      final ParameterEntry entry = entries[i];
      if (TAB_NAME_PARAMETER.equals(entry.getParameterName()))
      {
        continue;
      }
      if (TAB_ACTIVE_PARAMETER.equals(entry.getParameterName()))
      {
        continue;
      }

      list.add(entry);
    }
    return list.toArray(new ParameterEntry[list.size()]);
  }

  private Object[][] createEntryTable(final ParameterEntry[] entries)
  {
    final Object[][] values = new Object[entries.length][2];
    for (int i = 0; i < entries.length; i++)
    {
      final ParameterEntry entry = entries[i];
      values[i][0] = (entry.getParameterName());
      values[i][1] = (entry.getParameterValue());
    }
    return values;
  }

  public String format(final FormulaContext formulaContext,
                       final String configIndicator,
                       final String reportPath,
                       final ParameterEntry[] entries) throws EvaluationException
  {
    try
    {
      final String parameter = PatternLinkCustomizer.computeParameter(formulaContext, filterEntries(entries));
      final StaticDataRow staticDataRow = new StaticDataRow
          (new String[]{"::path", "::parameter", "::config", "::entries",
              TAB_NAME_PARAMETER, TAB_ACTIVE_PARAMETER},
              new Object[]{reportPath, parameter, configIndicator, createEntryTable(entries),
                  computeMantleTabName(formulaContext, entries), computeMantleTabActive(formulaContext, entries)});

      final ExpressionRuntime expressionRuntime;
      if (formulaContext instanceof ReportFormulaContext)
      {
        final ReportFormulaContext rfc = (ReportFormulaContext) formulaContext;
        expressionRuntime = new WrapperExpressionRuntime(staticDataRow, rfc.getRuntime());
      }
      else
      {
        expressionRuntime = new GenericExpressionRuntime
            (new CompoundDataRow(staticDataRow, createDataRow(entries)),
                new DefaultTableModel(), -1, new DefaultProcessingContext());
      }


      final String formula = computeFormula(configIndicator);
      final Formula compiledFormula = new Formula(formula);
      compiledFormula.initialize(new ReportFormulaContext(formulaContext, expressionRuntime));
      final Object o = compiledFormula.evaluate();
      if (o instanceof ErrorValue)
      {
        throw EvaluationException.getInstance((ErrorValue) o);
      }
      if (o == null)
      {
        throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_NA_VALUE);
      }
      return String.valueOf(o);
    }
    catch (final UnsupportedEncodingException e)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE);
    }
    catch (final BeanException e)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE);
    }
    catch (ParseException e)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE);
    }
    catch(EvaluationException e)
    {
      throw e;
    }
    catch (Exception e)
    {
      throw EvaluationException.getInstance(LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE);
    }
  }

  private String computeFormula(final String configIndicator) throws EvaluationException
  {
    final DrillDownProfile downProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile(configIndicator);
    return downProfile.getAttribute("formula");
  }

  private DataRow createDataRow(final ParameterEntry[] parameterEntries)
  {
    final String[] parameterNames = new String[parameterEntries.length];
    final Object[] parameterValues = new Object[parameterEntries.length];
    for (int i = 0; i < parameterEntries.length; i++)
    {
      final ParameterEntry entry = parameterEntries[i];
      parameterNames[i] = entry.getParameterName();
      parameterValues[i] = entry.getParameterValue();
    }
    return new StaticDataRow(parameterNames, parameterValues);
  }

}
