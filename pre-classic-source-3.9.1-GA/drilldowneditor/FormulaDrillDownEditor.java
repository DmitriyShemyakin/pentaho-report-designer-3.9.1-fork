package drilldowneditor;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.pentaho.reporting.engine.classic.extensions.drilldown.DrillDownProfile;
import org.pentaho.reporting.engine.classic.extensions.drilldown.FormulaLinkCustomizer;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Todo: Document me!
 * <p/>
 * Date: 17.08.2010
 * Time: 13:28:46
 *
 * @author Thomas Morgner.
 */
public class FormulaDrillDownEditor extends AbstractDrillDownEditor
{
  private JTextArea formulaField;
  private JTextField extensionField;

  public FormulaDrillDownEditor(final Dialog owner)
  {
    super(owner);
    setModal(true);
  }

  protected void init()
  {
    formulaField = new JTextArea();
    extensionField = new JTextField();
    super.init();
  }

  protected Component createDetailPane()
  {
    final JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    panel.add(new JLabel("Formula"), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    panel.add(new JScrollPane(formulaField), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    panel.add(new JLabel("Extension"), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1;
    panel.add(extensionField, gbc);

    return panel;
  }

  protected Class getType()
  {
    return FormulaLinkCustomizer.class;
  }

  public void updateUi(final DrillDownProfile profile)
  {
    if (profile == null)
    {
      formulaField.setText(null);
      extensionField.setText(null);
    }
    else
    {
      formulaField.setText(profile.getAttribute("formula"));
      extensionField.setText(profile.getAttribute("extension"));
    }
    super.updateUi(profile);
  }

  protected Map<String, String> getAttributes()
  {
    final HashMap<String, String> map = new HashMap<String, String>();
    if (StringUtils.isEmpty(formulaField.getText()) == false)
    {
      map.put("formula", formulaField.getText());
    }

    if (StringUtils.isEmpty(extensionField.getText()) == false)
    {
      map.put("extension", extensionField.getText());
    }
    return map;
  }
}