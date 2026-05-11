/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.DefaultLayoutBuilder;
import org.pentaho.reporting.engine.classic.core.layout.StyleCache;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.NonDynamicHeightWrapperStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

public class DesignerLayoutBuilder extends DefaultLayoutBuilder
{
  public DesignerLayoutBuilder(final OutputProcessorMetaData metaData)
  {
    super(metaData);
  }

  protected Object computeValue(final ExpressionRuntime runtime, final Element element)
  {
    final DesignerExpressionRuntime designerExpressionRuntime = (DesignerExpressionRuntime) runtime;
    designerExpressionRuntime.clear();

    if (element.getElementType() instanceof MessageType)
    {
      final Object message = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE);
      if (message == null || String.valueOf(message).length() == 0)
      {
        return element.getElementTypeName();
      }
      return message;
    }

    if (element.getElementType() instanceof LabelType)
    {
      if (Boolean.TRUE.equals(element.getAttribute
          (AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES)))
      {
        final Object labelFor = element.getAttribute
            (AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR);
        if (labelFor instanceof String)
        {
          final String labelForText = (String) labelFor;
          final DataAttributes attributes = runtime.getDataSchema().getAttributes(labelForText);
          if (attributes != null)
          {
            final DefaultDataAttributeContext context = new DefaultDataAttributeContext
                (runtime.getProcessingContext().getOutputProcessorMetaData(),
                    runtime.getResourceBundleFactory().getLocale());
            final Object o = attributes.getMetaAttribute
                (MetaAttributeNames.Formatting.NAMESPACE, MetaAttributeNames.Formatting.LABEL, String.class, context);
            if (o != null)
            {
              return o;
            }
          }
        }
      }
    }

    final Object value = element.getElementType().getDesignValue(designerExpressionRuntime, element);
    final String[] strings = designerExpressionRuntime.getFields();
    if (strings.length == 0)
    {
      // should be ok for most cases ..
      if (value != null)
      {
        return value;
      }
    }

    return element.getElementTypeName();
  }

  protected void performAddInlineSubReport(final ExpressionRuntime runtime,
                                           final ReportStateKey stateKey,
                                           final RenderBox box,
                                           final SubReport element)
      throws ReportProcessingException
  {
    if (isLimitedSubReports())
    {
      return;
    }

    final int parentNodeType = box.getNodeType();
    final boolean parentIsInlineContainer =
        ((parentNodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE ||
            (parentNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH));
    if (parentIsInlineContainer)
    {
      return;
    }

    final DesignerExpressionRuntime designerExpressionRuntime = (DesignerExpressionRuntime) runtime;
    designerExpressionRuntime.clear();
    final Object value = element.getElementType().getDesignValue(runtime, element);
    processSubreportDrawableContent(new SubreportDrawable(value), box, element, stateKey);
  }

  public void add(final RenderBox box,
                  final SubReport element,
                  final ExpressionRuntime runtime,
                  final ReportStateKey stateKey) throws ReportProcessingException
  {

    final DesignerExpressionRuntime designerExpressionRuntime = (DesignerExpressionRuntime) runtime;
    designerExpressionRuntime.clear();
    final Object value = element.getElementType().getDesignValue(runtime, element);
    processSubreportDrawableContent(new SubreportDrawable(value), box, element, stateKey);
  }

  protected void processSubreportDrawableContent(final SubreportDrawable reportDrawable,
                                                 final RenderBox box,
                                                 final Element element,
                                                 final ReportStateKey stateKey)
  {
    final StyleCache textStyleCache = getTextStyleCache();
    final StyleCache styleCache = getStyleCache();
    final SimpleStyleSheet elementStyle;
    final int nodeType = box.getNodeType();
    if (((nodeType & LayoutNodeTypes.MASK_BOX_BLOCK) == LayoutNodeTypes.MASK_BOX_BLOCK))
    {
      elementStyle = textStyleCache.getStyleSheet
          (new FullWidthWrapperStyleSheet(new NonDynamicHeightWrapperStyleSheet(element.getStyle())));
    }
    else if ((nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      elementStyle = styleCache.getStyleSheet(new FullWidthWrapperStyleSheet(element.getStyle()));
    }
    else
    {
      elementStyle = textStyleCache.getStyleSheet
          (new NonDynamicHeightWrapperStyleSheet(element.getStyle()));
    }


    reportDrawable.setStyleSheet(elementStyle);
    final DrawableWrapper wrapper = new DrawableWrapper(reportDrawable);

    final RenderableReplacedContent content = new RenderableReplacedContent(elementStyle, wrapper, null, getMetaData());
    final BoxDefinition boxDefinition = getBoxDefinitionFactory().getBoxDefinition(elementStyle);
    final RenderableReplacedContentBox child =
        new RenderableReplacedContentBox(elementStyle, element.getObjectID(), boxDefinition,
            element.getElementType(), element.getAttributes(), stateKey, content);
    child.setName(element.getName());
    box.addChild(child);
  }
}
