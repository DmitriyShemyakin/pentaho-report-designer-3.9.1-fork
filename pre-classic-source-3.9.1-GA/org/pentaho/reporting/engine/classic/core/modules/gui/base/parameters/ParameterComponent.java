package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import javax.swing.JComponent;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

/**
 * Todo: Document me!
 * <p/>
 * Date: 12.02.11
 * Time: 12:46
 *
 * @author Thomas Morgner.
 */
public interface ParameterComponent
{
  public JComponent getUIComponent();
  public void initialize() throws ReportDataFactoryException;
}
