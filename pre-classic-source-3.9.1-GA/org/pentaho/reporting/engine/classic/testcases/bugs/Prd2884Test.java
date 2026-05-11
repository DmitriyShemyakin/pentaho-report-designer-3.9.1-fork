package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.net.URL;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.EventMonitorFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PageDrawable;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.graphics.PrintReportProcessor;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document me!
 * <p/>
 * Date: 11.10.2010
 * Time: 15:50:00
 *
 * @author Thomas Morgner.
 */
public class Prd2884Test extends TestCase
{
  public Prd2884Test()
  {
  }

  protected void setUp() throws Exception
  {
    ClassicEngineBoot.getInstance().start();
  }

  public void testRunner() throws ResourceException, ReportProcessingException
  {
    final URL url = getClass().getResource("prd-2884.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();
    final EventMonitorFunction eventMonitorFunction = new EventMonitorFunction("Name");
    eventMonitorFunction.setDeepTraversing(true);
    report.addExpression(eventMonitorFunction);

    final PrintReportProcessor prc = new PrintReportProcessor(report);
    assertEquals("18 pages", 18, prc.getNumberOfPages());
    final PageDrawable pageDrawable = prc.getPageDrawable(2);
    assertNotNull(pageDrawable);
    final RenderNode[] renderNodes = pageDrawable.getNodesAt(1, 1, null, null);
    for (int i = 0; i < renderNodes.length; i++)
    {
      RenderNode renderNode = renderNodes[i];
      System.out.println(renderNode);
    }
  }

  public void testRunner2() throws ResourceException, ReportProcessingException
  {
    final URL url = getClass().getResource("prd-2884-2.prpt");
    assertNotNull(url);
    final ResourceManager resourceManager = new ResourceManager();
    resourceManager.registerDefaults();
    final Resource directly = resourceManager.createDirectly(url, MasterReport.class);
    final MasterReport report = (MasterReport) directly.getResource();

    final PrintReportProcessor prc = new PrintReportProcessor(report);
    if (prc.isError())
    {
      prc.getErrorReason().printStackTrace();
      fail();
    }
    final PageDrawable pageDrawable = prc.getPageDrawable(20);
    assertNotNull(pageDrawable);
    for (int i = 0; i < prc.getNumberOfPages(); i++)
    {
      System.out.println("I = " + i);
      if (i == 6)
      {
        DebugLog.logHere();
      }
      System.out.println(prc.getPageDrawable(i));

    }
  }
}
