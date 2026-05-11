package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.style.AbstractStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class FullWidthWrapperStyleSheet extends AbstractStyleSheet
{
  private StyleSheet parent;
  private static final Float POS_X = new Float(0);
  private static final Float WIDTH = new Float(-100);

  public FullWidthWrapperStyleSheet(final StyleSheet parent)
  {
    this.parent = parent;
  }

  public StyleSheet getParent()
  {
    return parent;
  }

  public InstanceID getId()
  {
    return parent.getId();
  }

  public long getChangeTracker()
  {
    return parent.getChangeTracker();
  }


  public Object getStyleProperty(final StyleKey key, final Object defaultValue)
  {
    if (ElementStyleKeys.MIN_WIDTH.equals(key))
    {
      return WIDTH;
    }
    if (ElementStyleKeys.POS_X.equals(key))
    {
      return POS_X;
    }
    return parent.getStyleProperty(key, defaultValue);
  }

  public Object[] toArray()
  {
    final Object[] objects = parent.toArray();
    objects[ElementStyleKeys.MIN_WIDTH.getIdentifier()] = WIDTH;
    objects[ElementStyleKeys.POS_X.getIdentifier()] = POS_X;
    return objects;
  }
}
