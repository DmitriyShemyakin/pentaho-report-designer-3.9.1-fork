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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout;

import java.awt.Shape;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Anchor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.RawDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.CanvasRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ContentPlaceholderRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContent;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderableReplacedContentBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RowRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinitionFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverter;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverterRegistry;
import org.pentaho.reporting.engine.classic.core.layout.richtext.RichTextConverterUtilities;
import org.pentaho.reporting.engine.classic.core.layout.style.AnchorStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.CanvasMinWidthStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.DynamicHeightWrapperStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.NonDynamicHeightWrapperStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.ParagraphPoolboxStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.text.DefaultRenderableTextFactory;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.ReportDrawable;
import org.pentaho.reporting.engine.classic.core.util.ShapeDrawable;
import org.pentaho.reporting.libraries.fonts.encoding.CodePointBuffer;
import org.pentaho.reporting.libraries.fonts.encoding.manual.Utf16LE;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.factory.drawable.DrawableWrapper;

/**
 * A layout builder is responsible for translating a single band into a layoutable chunk. The builder also collects the
 * Instance-IDs of all subreports it encounters.
 *
 * @author Thomas Morgner
 */
public class DefaultLayoutBuilder implements Cloneable, BufferedLayoutBuilder
{
  private static final InlineSubreportMarker[] EMPTY_ARRAY = new InlineSubreportMarker[0];
  private static final Log logger = LogFactory.getLog(DefaultLayoutBuilder.class);
  private static final String STRING_CLASSNAME = "java.lang.String";

  private OutputProcessorMetaData metaData;
  private CodePointBuffer buffer;
  private DefaultRenderableTextFactory textFactory;
  private TextCache textCache;
  private StyleCache bandCache;
  private StyleCache styleCache;
  private StyleCache textStyleCache;
  private BoxDefinitionFactory boxDefinitionFactory;
  private boolean limitedSubReports;
  private ArrayList<InlineSubreportMarker> collectedReports;
  private int[] bufferArray;
  private StyleKey[] definedStyleKeys;
  private boolean collapseProgressMarker;

  public DefaultLayoutBuilder(final OutputProcessorMetaData metaData)
  {
    this.collectedReports = new ArrayList<InlineSubreportMarker>();
    this.metaData = metaData;
    this.textFactory = new DefaultRenderableTextFactory(metaData);
    this.textCache = new TextCache(500);
    this.bufferArray = new int[500];
    this.definedStyleKeys = StyleKey.getDefinedStyleKeys();
    final boolean paddingsDisabled = metaData.isFeatureSupported(OutputProcessorFeature.DISABLE_PADDING);
    this.bandCache = new StyleCache(paddingsDisabled);
    this.styleCache = new StyleCache(paddingsDisabled);
    this.textStyleCache = new StyleCache(paddingsDisabled);
    this.boxDefinitionFactory = new BoxDefinitionFactory();
    this.collapseProgressMarker = true;
  }

  protected StyleCache getTextStyleCache()
  {
    return textStyleCache;
  }

  protected StyleCache getStyleCache()
  {
    return styleCache;
  }

  protected OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  protected BoxDefinitionFactory getBoxDefinitionFactory()
  {
    return boxDefinitionFactory;
  }

  public InlineSubreportMarker[] endSection(final RenderBox pageArea, final RenderBox sectionBox)
  {
    // ignored
    if (collectedReports.isEmpty())
    {
      return EMPTY_ARRAY;
    }
    return collectedReports.toArray(new InlineSubreportMarker[collectedReports.size()]);
  }

  public void startSection(final RenderBox pageArea, final boolean limitedSubReports)
  {
    this.limitedSubReports = limitedSubReports;
    this.collectedReports.clear();
  }

  private String getStyleFromLayoutManager(final Band band)
  {
    return "canvas";
  }

  public void addEmptyRootLevelBand(final RenderBox parent,
                                    final ReportStateKey stateKey) throws ReportProcessingException
  {
    final RenderNode child = parent.getLastChild();
    if (isCollapseProgressMarker() && child != null &&
        child.getNodeType() == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER)
    {
      final ProgressMarkerRenderBox markerRenderBox = (ProgressMarkerRenderBox) child;
      markerRenderBox.setStateKey(stateKey);
    }
    else
    {
      final ProgressMarkerRenderBox markerBox = new ProgressMarkerRenderBox();
      markerBox.setStateKey(stateKey);
      parent.addChild(markerBox);
      markerBox.close();
    }
  }

  public void setCollapseProgressMarker(final boolean collapseProgressMarker)
  {
    this.collapseProgressMarker = collapseProgressMarker;
  }

  public boolean isCollapseProgressMarker()
  {
    return collapseProgressMarker;
  }

  public void add(final RenderBox parent,
                  final Band band,
                  final ExpressionRuntime runtime,
                  final ReportStateKey stateKey) throws ReportProcessingException
  {
    if (isEmpty(band))
    {
      final boolean invConsSpace = parent.getStyleSheet().getBooleanStyleProperty
          (ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, parent.getNodeType() == LayoutNodeTypes.TYPE_BOX_ROWBOX);
      if (invConsSpace == false)
      {
        if (isControlBand(band))
        {
          final int parentNodeType = parent.getNodeType();
          final boolean parentIsInlineContainer =
              ((parentNodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE ||
                  (parentNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH));
          final RenderBox box = produceBox(band, stateKey, parentIsInlineContainer);
          parent.addChild(box);
          box.getStaticBoxLayoutProperties().setPlaceholderBox(true);
          box.close();
        }
        else if (band instanceof RootLevelBand)
        {
          addEmptyRootLevelBand(parent, stateKey);
        }
        else
        {
          // if parent is row, then add empty band.
          ensureEmptyChildIsAdded(parent, band, stateKey);
        }
        return;
      }
    }

    final int parentNodeType = parent.getNodeType();
    final boolean parentIsInlineContainer =
        ((parentNodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE ||
            (parentNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH));
    final RenderBox box = produceBox(band, stateKey, parentIsInlineContainer);
    ParagraphRenderBox paragraphBox = null;
    if (((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE) &&
        parentIsInlineContainer == false)
    {
      // Normalize the rendering-model. Inline-Boxes must always be contained in Paragraph-Boxes ..
      final ElementStyleSheet bandStyle = band.getStyle();
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(bandStyle);
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      paragraphBox = new ParagraphRenderBox
          (styleSheet, band.getObjectID(), boxDefinition, box.getElementType(), box.getAttributes(), stateKey);
      paragraphBox.setName(band.getName());
      paragraphBox.getBoxDefinition().setPreferredWidth(RenderLength.AUTO);
      paragraphBox.addChild(box);

      parent.addChild(paragraphBox);
    }
    else
    {
      parent.addChild(box);
    }

    final boolean invConsSpace = box.getStyleSheet().getBooleanStyleProperty
        (ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, box.getNodeType() == LayoutNodeTypes.TYPE_BOX_ROWBOX);

    final Element[] elementBuffer = band.unsafeGetElementArray();
    final int elementCount = band.getElementCount();
    for (int i = 0; i < elementCount; i++)
    {
      final Element element = elementBuffer[i];
      if (element.isVisible() == false && invConsSpace == false)
      {
        continue;
      }

      if (element instanceof Band)
      {
        final Band childBand = (Band) element;
        add(box, childBand, runtime, stateKey);
        continue;
      }

      if (element instanceof SubReport)
      {
        performAddInlineSubReport(runtime, stateKey, box, (SubReport) element);
        continue;
      }

      final Object value = computeValue(runtime, element);
      if (value instanceof Element)
      {
        final Band b = RichTextConverterUtilities.convertToBand(definedStyleKeys, element, (Element) value);
        add(box, b, runtime, stateKey);
      }
      else
      {
        performRenderValue(runtime, stateKey, box, element, value);
      }

      // if value instanceof element, then treat the element as band, and the value as sub-element to the band.
    }

    box.close();

    if (paragraphBox != null)
    {
      paragraphBox.close();
    }
  }

  private void ensureEmptyChildIsAdded(final RenderBox parent, final Element element, final ReportStateKey stateKey)
  {
    final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(element.getStyle());
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);

    final int parentNodeType = parent.getNodeType();
    final boolean parentIsInlineContainer =
        ((parentNodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE ||
            (parentNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH));
    final RenderBox box;
    if (parentIsInlineContainer)
    {
      box = new InlineRenderBox(styleSheet, element.getObjectID(), boxDefinition, element.getElementType(),
          element.getAttributes(), stateKey);
    }
    else
    {
      box = new BlockRenderBox(styleSheet, element.getObjectID(), boxDefinition, element.getElementType(),
          element.getAttributes(), stateKey);
    }
    box.getStaticBoxLayoutProperties().setPlaceholderBox(true);
    final String name = element.getName();
    if (name != null &&
        name.length() != 0 && name.startsWith(Band.ANONYMOUS_BAND_PREFIX) == false)
    {
      box.setName(name);
    }
    
    box.close();
    parent.addChild(box);
  }

  protected void performRenderValue(final ExpressionRuntime runtime,
                                    final ReportStateKey stateKey,
                                    final RenderBox parentRenderBox,
                                    final Element element,
                                    final Object initialValue) throws ReportProcessingException
  {
    if (initialValue == null || metaData.isContentSupported(initialValue) == false)
    {
      if ((parentRenderBox.getNodeType() & LayoutNodeTypes.MASK_BOX_ROW) == LayoutNodeTypes.MASK_BOX_ROW ||
          metaData.isExtraContentElement(element.getStyle(), element.getAttributes()))
      {
        ensureEmptyChildIsAdded(parentRenderBox, element, stateKey);
      }
      return;
    }

    final Object value;
    final Object richTextType = element.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.RICH_TEXT_TYPE);
    if (richTextType != null)
    {
      final RichTextConverterRegistry registry = RichTextConverterRegistry.getRegistry();
      final RichTextConverter converter = registry.getConverter(String.valueOf(richTextType));
      if (converter != null)
      {
        final Object b = converter.convert(element, initialValue);
        if (b instanceof Band)
        {
          add(parentRenderBox, (Band) b, runtime, stateKey);
          return;
        }
        value = b;
      }
      else
      {
        value = initialValue;
      }
    }
    else
    {
      value = initialValue;
    }

    if (value instanceof ReportDrawable)
    {
      // A report drawable element receives some context information as well.

      final ReportDrawable reportDrawable = (ReportDrawable) value;
      final ProcessingContext processingContext = runtime.getProcessingContext();
      reportDrawable.setConfiguration(processingContext.getConfiguration());
      reportDrawable.setResourceBundleFactory(processingContext.getResourceBundleFactory());
      processReportDrawableContent(reportDrawable, parentRenderBox, element, stateKey);
    }
    else if (value instanceof Anchor)
    {
      DefaultLayoutBuilder.logger.warn
          ("The use of anchor-objects is deprecated and will be removed from future reports. " +
              "Update your report definition.");
      processAnchor((Anchor) value, parentRenderBox, element, stateKey);
    }
    else
    {
      final DataSource dataSource = element.getElementType();
      final Object rawValue;
      if (dataSource instanceof RawDataSource)
      {
        final RawDataSource rds = (RawDataSource) dataSource;
        rawValue = rds.getRawValue(runtime, element);
      }
      else
      {
        rawValue = null;
      }
      // String is final, so it is safe to do this ...
      if (DefaultLayoutBuilder.STRING_CLASSNAME.equals(value.getClass().getName()))
      {
        processText(value, rawValue, parentRenderBox, element, stateKey);
      }
      else if (value instanceof Shape)
      {
        final Shape shape = (Shape) value;
        final ReportDrawable reportDrawable = new ShapeDrawable
            (shape, element.getStyle().getBooleanStyleProperty(ElementStyleKeys.KEEP_ASPECT_RATIO));
        final ProcessingContext processingContext = runtime.getProcessingContext();
        reportDrawable.setConfiguration(processingContext.getConfiguration());
        reportDrawable.setResourceBundleFactory(processingContext.getResourceBundleFactory());
        processReportDrawableContent(reportDrawable, parentRenderBox, element, stateKey);
      }
      else if (value instanceof ImageContainer ||
          value instanceof DrawableWrapper)
      {
        processReplacedContent(value, rawValue, parentRenderBox, element, stateKey);
      }
      else if (DrawableWrapper.isDrawable(value))
      {
        processReplacedContent(new DrawableWrapper(value), rawValue, parentRenderBox, element, stateKey);
      }
      else
      {
        processText(value, rawValue, parentRenderBox, element, stateKey);
      }
    }
  }

  protected void performAddInlineSubReport(final ExpressionRuntime runtime,
                                           final ReportStateKey stateKey,
                                           final RenderBox box,
                                           final SubReport element)
      throws ReportProcessingException
  {
    if (limitedSubReports)
    {
      logger.debug("Not adding subreport: Subreports in header or footer area are not allowed.");
      return;
    }

    final int parentNodeType = box.getNodeType();
    final boolean parentIsInlineContainer =
        ((parentNodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE ||
            (parentNodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH));
    if (parentIsInlineContainer)
    {
      logger.warn("Not adding subreport: Subreports in inline-contexts are not supported.");
      return;
    }

    final RenderBox subreportbox = produceSubreportBox(element, stateKey);
    box.addChild(subreportbox);
    // the box will be closed
    collectedReports.add(new InlineSubreportMarker(element, subreportbox.getInstanceId(), SubReportProcessType.INLINE));
  }

  public boolean isLimitedSubReports()
  {
    return limitedSubReports;
  }

  protected Object computeValue(final ExpressionRuntime runtime, final Element element)
  {
    return element.getElementType().getValue(runtime, element);
  }

  protected boolean isControlBand(final Band band)
  {
    final ElementStyleSheet style = band.getStyle();
    if (style.getStyleProperty(BandStyleKeys.COMPUTED_SHEETNAME) != null)
    {
      return true;
    }
    if (style.getStyleProperty(BandStyleKeys.BOOKMARK) != null)
    {
      return true;
    }
    if ("inline".equals(style.getStyleProperty(BandStyleKeys.LAYOUT)) == false)
    {
      if (Boolean.TRUE.equals(style.getStyleProperty(BandStyleKeys.PAGEBREAK_AFTER)))
      {
        return true;
      }
      if (Boolean.TRUE.equals(style.getStyleProperty(BandStyleKeys.PAGEBREAK_BEFORE)))
      {
        return true;
      }
    }
    return false;
  }

  protected boolean isEmpty(final Band band)
  {
    if (band.isVisible() == false)
    {
      return true;
    }
    if (band.getElementCount() > 0)
    {
      return false;
    }
    return isEmptyElement(band);
  }

  protected final boolean isLengthDefined(final StyleKey key, final ElementStyleSheet styleSheet)
  {
    if (key.isInheritable())
    {
      if (styleSheet.isLocalKey(key) == false)
      {
        return false;
      }
    }

    final Object o = styleSheet.getStyleProperty(key, null);
    if (o == null)
    {
      return false;
    }
    if (o instanceof Number == false)
    {
      return false;
    }
    final Number n = (Number) o;
    return n.doubleValue() != 0;
  }
  
  protected boolean isEmptyElement(final Element band)
  {
    final ElementStyleSheet style = band.getStyle();
    // A band is not empty, if it has a defined minimum or preferred height
    if (isLengthDefined(ElementStyleKeys.HEIGHT, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.WIDTH, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.POS_Y, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.POS_X, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.MIN_HEIGHT, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.MIN_WIDTH, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.PADDING_TOP, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.PADDING_LEFT, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.PADDING_BOTTOM, style))
    {
      return false;
    }
    if (isLengthDefined(ElementStyleKeys.PADDING_RIGHT, style))
    {
      return false;
    }
    if (BorderStyle.NONE.equals(style.getStyleProperty(ElementStyleKeys.BORDER_BOTTOM_STYLE,
        BorderStyle.NONE)) == false)
    {
      return false;
    }
    if (BorderStyle.NONE.equals(style.getStyleProperty(ElementStyleKeys.BORDER_TOP_STYLE, BorderStyle.NONE)) == false)
    {
      return false;
    }
    if (BorderStyle.NONE.equals(style.getStyleProperty(ElementStyleKeys.BORDER_LEFT_STYLE, BorderStyle.NONE)) == false)
    {
      return false;
    }
    if (BorderStyle.NONE.equals(style.getStyleProperty(ElementStyleKeys.BORDER_RIGHT_STYLE, BorderStyle.NONE)) == false)
    {
      return false;
    }
    if (style.getStyleProperty(ElementStyleKeys.BACKGROUND_COLOR) != null)
    {
      return false;
    }

    if (metaData.isExtraContentElement(band.getStyle(), band.getAttributes()))
    {
      return false;
    }
    return true;
  }

  protected void processText(final Object value,
                             final Object rawValue,
                             final RenderBox parentBox,
                             final Element element,
                             final ReportStateKey stateKey)
  {
    final String text;
    if (element.getStyle().getBooleanStyleProperty(TextStyleKeys.TRIM_TEXT_CONTENT))
    {
      text = String.valueOf(value).trim();
    }
    else
    {
      text = String.valueOf(value);
    }

    final ElementStyleSheet style = element.getStyle();
    final ReportAttributeMap attrs = element.getAttributes();
    final TextCache.Result result =
        textCache.get(style.getId(), style.getChangeTracker(), attrs.getChangeTracker(), text);
    if (result != null)
    {
      addTextNodes(element, rawValue, result.getText(), result.getFinish(),
          parentBox, result.getStyleSheet(), stateKey);
      return;
    }

    final SimpleStyleSheet elementStyle;
    final int nodeType = parentBox.getNodeType();
    if ((nodeType & LayoutNodeTypes.MASK_BOX_CANVAS) == LayoutNodeTypes.MASK_BOX_CANVAS)
    {
      if (element.isDynamicContent() == false)
      {
        elementStyle = textStyleCache.getStyleSheet(new NonDynamicHeightWrapperStyleSheet(style));
      }
      else
      {
        elementStyle = styleCache.getStyleSheet(new DynamicHeightWrapperStyleSheet(style));
      }
    }
    else
    {
      elementStyle = styleCache.getStyleSheet(style);
    }

    if (buffer != null)
    {
      buffer.setCursor(0);
    }

    buffer = Utf16LE.getInstance().decodeString(text, buffer);
    bufferArray = buffer.getBuffer(bufferArray);

    if (((nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE) == false)
    {
      textFactory.startText();
    }

    final RenderNode[] renderNodes = textFactory.createText
        (bufferArray, 0, buffer.getLength(), elementStyle, element.getElementType(), element.getObjectID(), attrs);
    final RenderNode[] finishNodes = textFactory.finishText();

    addTextNodes(element, rawValue, renderNodes, finishNodes, parentBox, elementStyle, stateKey);
    textCache.store(style.getId(), style.getChangeTracker(), attrs.getChangeTracker(),
        text, elementStyle, attrs, renderNodes, finishNodes);
  }

  protected void processReportDrawableContent(final ReportDrawable reportDrawable,
                                              final RenderBox box,
                                              final Element element,
                                              final ReportStateKey stateKey)
  {
    final SimpleStyleSheet elementStyle;
    if (((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE) == false)
    {
      if (element.isDynamicContent() == false)
      {
        elementStyle = textStyleCache.getStyleSheet(new NonDynamicHeightWrapperStyleSheet(element.getStyle()));
      }
      else
      {
        elementStyle = styleCache.getStyleSheet(new DynamicHeightWrapperStyleSheet(element.getStyle()));
      }
    }
    else
    {
      elementStyle = styleCache.getStyleSheet(element.getStyle());
    }

    reportDrawable.setStyleSheet(elementStyle);
    final DrawableWrapper wrapper;
    if (reportDrawable instanceof DrawableWrapper)
    {
      wrapper = (DrawableWrapper) (reportDrawable);
    }
    else
    {
      wrapper = new DrawableWrapper(reportDrawable);
    }

    final RenderableReplacedContent content = new RenderableReplacedContent(elementStyle, wrapper, null, metaData);
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(elementStyle);
    final RenderableReplacedContentBox child =
        new RenderableReplacedContentBox(elementStyle, element.getObjectID(), boxDefinition,
            element.getElementType(), element.getAttributes(), stateKey, content);
    child.setName(element.getName());
    box.addChild(child);
  }

  /**
   * Processes an anchor object. This is now a deprecated functionality, as anchors should be defined using the
   * element-style.
   *
   * @param anchor
   * @param box
   * @param element
   * @param stateKey
   */
  protected void processAnchor(final Anchor anchor,
                               final RenderBox box,
                               final Element element,
                               final ReportStateKey stateKey)
  {
    final String anchorName = anchor.getName();

    if ((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet
          (new NonDynamicHeightWrapperStyleSheet(new AnchorStyleSheet(anchorName, element.getStyle())));
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      final RenderBox autoParagraphBox = new InlineRenderBox(styleSheet, element.getObjectID(), boxDefinition,
          element.getElementType(), element.getAttributes(), stateKey);
      autoParagraphBox.setName(element.getName());
      autoParagraphBox.getBoxDefinition().setPreferredWidth(RenderLength.AUTO);
      autoParagraphBox.close();
      box.addChild(autoParagraphBox);
    }
    else // add the replaced content into a ordinary block box. There's no need to create a full paragraph for it
    {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet
          (new NonDynamicHeightWrapperStyleSheet(new AnchorStyleSheet(anchorName, element.getStyle())));
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      final RenderBox autoParagraphBox = new CanvasRenderBox(styleSheet, element.getObjectID(), boxDefinition,
          element.getElementType(), element.getAttributes(), stateKey);
      autoParagraphBox.setName(element.getName());
      autoParagraphBox.getBoxDefinition().setPreferredWidth(RenderLength.AUTO);
      autoParagraphBox.close();
      box.addChild(autoParagraphBox);
    }
  }

  protected void processReplacedContent(final Object value,
                                        final Object rawValue,
                                        final RenderBox box,
                                        final Element element,
                                        final ReportStateKey stateKey)
  {
    final SimpleStyleSheet elementStyle;
    if (((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE) == false)
    {
      if (element.isDynamicContent() == false)
      {
        elementStyle = textStyleCache.getStyleSheet(new NonDynamicHeightWrapperStyleSheet(element.getStyle()));
      }
      else
      {
        elementStyle = styleCache.getStyleSheet(new DynamicHeightWrapperStyleSheet(element.getStyle()));
      }
    }
    else
    {
      elementStyle = styleCache.getStyleSheet(element.getStyle());
    }

    final ResourceKey rawKey;
    if (rawValue instanceof ResourceKey)
    {
      rawKey = (ResourceKey) rawValue;
    }
    else
    {
      rawKey = null;
    }

    final RenderableReplacedContent content = new RenderableReplacedContent(elementStyle, value, rawKey, metaData);
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(elementStyle);
    final RenderableReplacedContentBox child =
        new RenderableReplacedContentBox(elementStyle, element.getObjectID(), boxDefinition,
            element.getElementType(), element.getAttributes(), stateKey, content);
    child.setName(element.getName());
    box.addChild(child);
  }

  protected void addTextNodes(final Element element,
                              final Object rawValue,
                              final RenderNode[] renderNodes,
                              final RenderNode[] finishNodes,
                              final RenderBox parentBox,
                              final StyleSheet elementStyle,
                              final ReportStateKey stateKey)
  {
    if ((parentBox.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      final StyleSheet styleSheet = bandCache.getStyleSheet(elementStyle);
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      final InlineRenderBox autoParagraphBox =
          new InlineRenderBox(styleSheet, element.getObjectID(), boxDefinition,
              element.getElementType(), element.getAttributes(), stateKey);
      autoParagraphBox.setName(element.getName());
      autoParagraphBox.getBoxDefinition().setPreferredWidth(RenderLength.AUTO);
      autoParagraphBox.addChilds(renderNodes);
      autoParagraphBox.addChilds(finishNodes);
      autoParagraphBox.close();
      parentBox.addChild(autoParagraphBox);
    }
    else
    {
      final StyleSheet styleSheet = bandCache.getStyleSheet(elementStyle);
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      final ParagraphRenderBox autoParagraphBox = new ParagraphRenderBox
          (styleSheet, element.getObjectID(), boxDefinition, element.getElementType(), element.getAttributes(),
              stateKey);
      autoParagraphBox.setRawValue(rawValue);
      autoParagraphBox.setName(element.getName());
      autoParagraphBox.getBoxDefinition().setPreferredWidth(RenderLength.AUTO);
      autoParagraphBox.addChilds(renderNodes);
      autoParagraphBox.addChilds(finishNodes);
      autoParagraphBox.close();
      parentBox.addChild(autoParagraphBox);
    }
  }

  protected RenderBox produceBox(final Band band,
                                 final ReportStateKey stateKey,
                                 final boolean parentIsInlineBox)
  {
    final ElementStyleSheet elementStyleSheet = band.getStyle();
    Object layoutType = elementStyleSheet.getStyleProperty(BandStyleKeys.LAYOUT, null);
    if (layoutType == null)
    {
      layoutType = getStyleFromLayoutManager(band);
    }
    if (parentIsInlineBox)
    {
      layoutType = "inline";
    }

    // todo: Check for cachability ..
    final RenderBox box;
    if ("block".equals(layoutType))
    {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(elementStyleSheet);
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      box = new BlockRenderBox(styleSheet, band.getObjectID(), boxDefinition, band.getElementType(),
          band.getAttributes(), stateKey);
    }
    else if ("inline".equals(layoutType))
    {
      if (parentIsInlineBox)
      {
        final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(elementStyleSheet);
        final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
        box = new InlineRenderBox(styleSheet, band.getObjectID(), boxDefinition, band.getElementType(),
            band.getAttributes(), stateKey);
      }
      else
      {
        // The non-inheritable styles will be applied to the auto-generated paragraph box. The inlinebox itself
        // only receives the inheritable styles so that it can inherit it to its next child ..
        final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(new ParagraphPoolboxStyleSheet(elementStyleSheet));
        final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
        box = new InlineRenderBox(styleSheet, band.getObjectID(), boxDefinition, band.getElementType(),
            band.getAttributes(), stateKey);
      }
    }
    else if ("row".equals(layoutType))
    {
      final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(elementStyleSheet);
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      box = new RowRenderBox(styleSheet, band.getObjectID(), boxDefinition, band.getElementType(),
          band.getAttributes(), stateKey);
    }
    else // assume 'Canvas' by default ..
    {
      final SimpleStyleSheet styleSheet;
      if (elementStyleSheet.getBooleanStyleProperty(ElementStyleKeys.USE_MIN_CHUNKWIDTH))
      {
        styleSheet = bandCache.getStyleSheet(elementStyleSheet);
      }
      else
      {
        styleSheet = bandCache.getStyleSheet(new CanvasMinWidthStyleSheet(elementStyleSheet));
      }
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
      box = new CanvasRenderBox(styleSheet, band.getObjectID(), boxDefinition, band.getElementType(),
          band.getAttributes(), stateKey);
    }

    // for the sake of debugging ..
    final String name = band.getName();
    if (name != null &&
        name.length() != 0 && name.startsWith(Band.ANONYMOUS_BAND_PREFIX) == false)
    {
      box.setName(name);
    }
    return box;
  }


  private RenderBox produceSubreportBox(final SubReport report,
                                        final ReportStateKey stateKey)
  {
    final ElementStyleSheet elementStyleSheet = report.getStyle();
    final SimpleStyleSheet styleSheet = bandCache.getStyleSheet(elementStyleSheet);
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(styleSheet);
    final RenderBox box = new ContentPlaceholderRenderBox(styleSheet, report.getObjectID(), boxDefinition,
        report.getElementType(), report.getAttributes(), stateKey, report.getObjectID());
    box.getStaticBoxLayoutProperties().setPlaceholderBox(true);

    // for the sake of debugging ..
    final String name = report.getName();
    if (name != null && name.startsWith(Band.ANONYMOUS_BAND_PREFIX) == false)
    {
      box.setName(name);
    }
    return box;
  }

  public Object clone() throws CloneNotSupportedException
  {
    final DefaultLayoutBuilder o = (DefaultLayoutBuilder) super.clone();
    o.collectedReports = (ArrayList) collectedReports.clone();
    o.collectedReports.clear();
    return o;
  }

  public LayoutBuilder createBufferedLayoutBuilder()
  {
    try
    {
      return (LayoutBuilder) clone();
    }
    catch (CloneNotSupportedException e)
    {
      throw new IllegalStateException("Clone must be supported, or I am confused");
    }
  }

  public void dispose()
  {
    // nothing needed in the current constellation
  }
}
