package org.pentaho.reporting.engine.classic.core.metadata;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public interface ExpressionPropertyCore
{
  public String[] getReferencedFields(ExpressionPropertyMetaData metaData, Expression expression, Object attributeValue);

  public String[] getReferencedGroups(ExpressionPropertyMetaData metaData, Expression expression, Object attributeValue);

  public String[] getReferencedElements(ExpressionPropertyMetaData metaData, Expression expression, Object attributeValue);

  public ResourceReference[] getReferencedResources(ExpressionPropertyMetaData metaData,
                                                    Expression expression,
                                                    Object attributeValue,
                                                    Element reportElement,
                                                    ResourceManager resourceManager);

  public String[] getExtraCalculationFields(ExpressionPropertyMetaData metaData);

}
