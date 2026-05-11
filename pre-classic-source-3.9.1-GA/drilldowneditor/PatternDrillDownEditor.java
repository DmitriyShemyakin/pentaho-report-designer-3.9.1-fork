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
import org.pentaho.reporting.engine.classic.extensions.drilldown.PatternLinkCustomizer;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Todo: Document me!
 * <p/>
 * Date: 17.08.2010
 * Time: 13:28:46
 *
 * @author Thomas Morgner.
 */
public class PatternDrillDownEditor extends AbstractDrillDownEditor
{
  private JTextArea patternField;
  private JTextField extensionField;

  public PatternDrillDownEditor(final Dialog owner)
  {
    super(owner);
    setModal(true);
  }

  protected void init()
  {
    patternField = new JTextArea();
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
    panel.add(new JLabel("Pattern"), gbc);

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    panel.add(new JScrollPane(patternField), gbc);

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
    return PatternLinkCustomizer.class;
  }

  public void updateUi(final DrillDownProfile profile)
  {
    if (profile == null)
    {
      patternField.setText(null);
      extensionField.setText(null);
    }
    else
    {
      patternField.setText(profile.getAttribute("pattern"));
      extensionField.setText(profile.getAttribute("extension"));
    }
    super.updateUi(profile);
  }

  protected Map<String, String> getAttributes()
  {
    final HashMap<String, String> map = new HashMap<String, String>();
    if (StringUtils.isEmpty(patternField.getText()) == false)
    {
      map.put("pattern", patternField.getText());
    }

    if (StringUtils.isEmpty(extensionField.getText()) == false)
    {
      map.put("extension", extensionField.getText());
    }
    return map;
  }
}
