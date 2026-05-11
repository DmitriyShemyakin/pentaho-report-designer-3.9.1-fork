package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ValidationResult;
import org.pentaho.reporting.engine.classic.core.util.ReportParameterValues;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Todo: Document me!
 * <p/>
 * Date: 27.10.2010
 * Time: 18:25:01
 *
 * @author Thomas Morgner.
 */
public class Prd3029Test extends TestCase
{
  public Prd3029Test()
  {
  }

  public Prd3029Test(final String name)
  {
    super(name);
  }

  public void setUp()
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParametrization() throws Exception
  {
    if (executeReport(new Integer[]{103, 112}, true) == false)
    {
      fail();
    }

    if (executeReport(new Integer[]{103, 112}, false) == false)
    {
      fail();
    }
  }

  public void testParametrizationEmpty() throws Exception
  {
    if (executeReport(new Integer[]{}, true))
    {
      fail();
    }
    if (executeReport(new Integer[]{}, false) == false)
    {
      fail();
    }
  }

  public void testParametrizationNull() throws Exception
  {
    final Object pvalue = null;
    if (executeReport(pvalue, false) == false)
    {
      fail();
    }

    if (executeReport(pvalue, true))
    {
      fail();
    }

  }

  private boolean executeReport(final Object pvalue, final boolean mandatory)
      throws ReportDataFactoryException
  {
    final DriverConnectionProvider driverConnectionProvider = new DriverConnectionProvider();
    driverConnectionProvider.setDriver("org.hsqldb.jdbcDriver");
    driverConnectionProvider.setUrl("jdbc:hsqldb:mem:SampleData");
    driverConnectionProvider.setProperty("user", "sa");
    driverConnectionProvider.setProperty("password", "");
    final SQLReportDataFactory sdf = new SQLReportDataFactory(driverConnectionProvider);
    sdf.setQuery("default", "SELECT * FROM CUSTOMERS WHERE CUSTOMERNUMBER IN (${parameter})");
    sdf.setQuery("params", "SELECT DISTINCT CUSTOMERNUMBER FROM CUSTOMERS");
    sdf.open();

    final ReportParameterValues parameterValues = new ReportParameterValues();
    parameterValues.put("parameter", pvalue);
    sdf.queryData("default", parameterValues);
    sdf.close();

    final MasterReport report = new MasterReport();
    report.setDataFactory(sdf);

    final DefaultListParameter listParameter =
        new DefaultListParameter("params", "CUSTOMERNUMBER", "CUSTOMERNUMBER", "parameter", true, true, Integer[].class);
    listParameter.setMandatory(mandatory);

    final ModifiableReportParameterDefinition definition =
        (ModifiableReportParameterDefinition) report.getParameterDefinition();
    definition.addParameterDefinition(listParameter);
    report.getParameterValues().put("parameter", pvalue);


    return (execGraphics2D(report));
  }


  public static boolean execGraphics2D(final MasterReport report)
  {
    try
    {
      final PrintReportProcessor proc = new PrintReportProcessor(report);
      final int nop = proc.getNumberOfPages();
      if (proc.isError())
      {
        throw proc.getErrorReason();
      }
      if (nop == 0)
      {
        return false;
      }
      for (int i = 0; i < nop; i++)
      {
        if (proc.getPageDrawable(i) == null)
        {
          return false;
        }
      }
      proc.close();
      return true;
    }
    catch (ReportParameterValidationException ve)
    {
      final ValidationResult validationResult = ve.getValidationResult();
      final List<String> strings = Arrays.asList(validationResult.toMessageList());
      DebugLog.log(strings, ve);
      return false;
    }
    catch (Throwable e)
    {
      DebugLog.log("Generating Graphics2D failed.", e);
      return false;
    }
  }
}
