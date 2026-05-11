package org.pentaho.reporting.engine.classic.extensions.toc;

import java.util.Locale;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class IndexElementType implements ElementType
{
  public IndexElementType()
  {
  }

  public ElementMetaData getMetaData()
  {
    return ElementTypeRegistry.getInstance().getElementType("index");
  }

  public Object getDesignValue(final ExpressionRuntime runtime, final Element element)
  {
    return "Index";
  }

  public void configureDesignTimeDefaults(final Element element, final Locale locale)
  {

  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element the element for which the data is computed.
   * @return the value.
   */
  public Object getValue(final ExpressionRuntime runtime, final Element element)
  {
    return null;
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }
}