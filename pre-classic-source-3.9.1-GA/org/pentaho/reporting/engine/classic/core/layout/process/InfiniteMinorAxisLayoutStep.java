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

package org.pentaho.reporting.engine.classic.core.layout.process;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.FinishedRenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageGrid;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphPoolBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.CenterAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.JustifyAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.LeftAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.RightAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.alignment.TextAlignmentProcessor;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.EndSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineBoxSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.InlineNodeSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.ReplacedContentSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SequenceList;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.SpacerSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.StartSequenceElement;
import org.pentaho.reporting.engine.classic.core.layout.process.layoutrules.TextSequenceElement;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;


/**
 * This process-step computes the effective layout, but it does not take horizontal pagebreaks into account. (It has to
 * deal with vertical breaks, as they affect the text layout.)
 * <p/>
 * This processing step does not ajust anything on the vertical axis. Vertical alignment is handled in a second step.
 * <p/>
 * Please note: This layout model (unlike the default CSS model) uses the BOX-WIDTH as computed with. This means, the
 * defined width specifies the sum of all borders, paddings and the content area width.
 *
 * @author Thomas Morgner
 */
public final class InfiniteMinorAxisLayoutStep extends IterateVisualProcessStep
{
  private static final Log logger = LogFactory.getLog(InfiniteMinorAxisLayoutStep.class);
  private static final long OVERFLOW_DUMMY_WIDTH = StrictGeomUtility.toInternalValue(20000);

  private MinorAxisParagraphBreakState breakState;
  private PageGrid pageGrid;
  private RenderBox continuedElement;

  private TextAlignmentProcessor centerProcessor;
  private TextAlignmentProcessor rightProcessor;
  private TextAlignmentProcessor leftProcessor;
  private TextAlignmentProcessor justifyProcessor;
  private OutputProcessorMetaData metaData;

  public InfiniteMinorAxisLayoutStep(final OutputProcessorMetaData metaData)
  {
    this.metaData = metaData;
    breakState = new MinorAxisParagraphBreakState();
  }

  public void compute(final LogicalPageBox root)
  {
    try
    {
      continuedElement = null;
      pageGrid = root.getPageGrid();
      startProcessing(root);
    }
    finally
    {
      continuedElement = null;
      pageGrid = null;
      breakState.deinit();
    }
  }

  /**
   * Continues processing. The renderbox must have a valid x-layout (that is: X, content-X1, content-X2 and Width)
   *
   * @param pageGrid
   * @param box
   */
  public void continueComputation(final PageGrid pageGrid,
                                  final RenderBox box)
  {
    if (box.getContentAreaX2() == 0 || box.getCachedWidth() == 0)
    {
      throw new IllegalStateException("Box must be layouted a bit ..");
    }

    try
    {
      this.pageGrid = pageGrid;
      this.breakState.deinit();
      this.continuedElement = box;
      startProcessing(box);
    }
    finally
    {
      this.continuedElement = null;
      this.pageGrid = null;
      this.breakState.deinit();
    }
  }

  /**
   * The whole computation is only done for exactly one nesting level of paragraphs. If we encounter an inline-block or
   * inline-table, we handle them as a single element.
   *
   * @param box
   * @return
   */
  protected boolean startBlockLevelBox(final RenderBox box)
  {
    // first, compute the position. The position is global, not relative to a
    // parent or so. Therefore a child has no connection to the parent's
    // effective position, when it is painted.

    if (breakState.isActive() == false)
    {
      if (box.isCacheValid())
      {
        return false;
      }

      computeContentArea(box, computeBlockPosition(box));

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {

        final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;

        if (continuedElement == null)
        {
          final long lineBoxChangeTracker = paragraphBox.getEffectiveLineboxContainer().getChangeTracker();
          final boolean unchanged = lineBoxChangeTracker == paragraphBox.getMinorLayoutAge();
          if (unchanged)
          {
            return false;
          }
        }

        paragraphBox.clearLayout();
        breakState.init(paragraphBox);
      }
      return true;
    }


    if (breakState.isSuspended() == false)
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

    // this way or another - we are suspended now. So there is no need to look
    // at the children anymore ..
    return false;
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
    if (box.isCacheValid())
    {
      return;
    }

    applyComputedWidth(box);

    // find the maximum width of all childs and adjust this box's width to match that.
    // take the maximum and preferred size into account as well. If a preferred size
    // is set, then ignore the childs ..

    if (breakState.isActive())
    {
      final Object suspender = breakState.getSuspendItem();
      if (box.getInstanceId() == suspender)
      {
        breakState.setSuspendItem(null);
        return;
      }
      if (suspender != null)
      {
        return;
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        // finally update the change tracker ..
        final ParagraphRenderBox paraBox = (ParagraphRenderBox) box;
        paraBox.setMinorLayoutAge(paraBox.getEffectiveLineboxContainer().getChangeTracker());

        breakState.deinit();
      }
    }

  }

  /**
   * Computes the effective content area. The content area is the space that can be used by any of the childs of the
   * given box.
   * <p/>
   * InlineBoxes get computed in the alignment processor.
   *
   * @param box the block render box for which we compute the content area
   */
  private void computeContentArea(final RenderBox box, final long x)
  {
    if (box == continuedElement)
    {
      return;
    }

    final BoxDefinition bdef = box.getBoxDefinition();
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    box.setCachedX(x);
    // next, compute the width ...

    final long leftPadding = blp.getBorderLeft() + bdef.getPaddingLeft();
    final long rightPadding = blp.getBorderRight() + bdef.getPaddingRight();

    // computed width is box-size, so it contains the paddings already
    final long computedWidth = box.getComputedWidth();

    final long bcw = ProcessUtility.computeBlockContextWidth(box);
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength minLength = boxDef.getMinimumWidth();
    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();
    final long min = minLength.resolve(bcw, 0);
    final long max = maxLength.resolve(bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO);

    final long contentAreaX1 = x + leftPadding;
    box.setContentAreaX1(contentAreaX1);

    if (box.getBoxDefinition().isSizeSpecifiesBorderBox())
    {
      final long minChunkWidth;
      if (box.getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.USE_MIN_CHUNKWIDTH))
      {
        minChunkWidth = box.getMinimumChunkWidth() + leftPadding + rightPadding;
      }
      else
      {
        minChunkWidth = leftPadding + rightPadding;
      }
      final long pref = prefLength.resolve(bcw, Math.max(computedWidth, minChunkWidth));

      final long width = Math.max(0, ProcessUtility.computeLength(min, max, pref));
      final long contentAreaX2 = x + width - rightPadding;
      if (contentAreaX1 < contentAreaX2)
      {
        box.setContentAreaX2(contentAreaX2);
      }
      else
      {
        // autocorrect to a zero-width box.
        box.setContentAreaX2(contentAreaX1);
      }
    }
    else
    {
      final long minChunkWidth;
      if (box.getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.USE_MIN_CHUNKWIDTH))
      {
        minChunkWidth = box.getMinimumChunkWidth();
      }
      else
      {
        minChunkWidth = 0;
      }
      final long computedContentWidth = Math.max(0, computedWidth - leftPadding - rightPadding);
      final long pref = prefLength.resolve(bcw, Math.max(computedContentWidth, minChunkWidth));
      final long width = Math.max(0, ProcessUtility.computeLength(min, max, pref));
      box.setContentAreaX2(contentAreaX1 + width);
    }
  }

  private void computeNodeWidth(final RenderNode box)
  {
    if (box == continuedElement)
    {
      return;
    }

    // next, compute the width ...
    box.setCachedWidth(box.getComputedWidth());
  }

  /**
   * Ensures that the width of the box does not exceed the computed width. For canvas level boxes, we have to stick with
   * the minimum width as defined by the user or the layout would become unpredictable.
   *
   * @param box
   */
  private void applyStrictComputedWidth(final RenderBox box)
  {
    final BoxDefinition bdef = box.getBoxDefinition();
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    // next, compute the width ...

    final long leftPadding = blp.getBorderLeft() + bdef.getPaddingLeft();
    final long rightPadding = blp.getBorderRight() + bdef.getPaddingRight();

    // computed width is box-size, so it contains the paddings already
    final long computedWidth = box.getComputedWidth();
    final long contentAreaX1 = box.getContentAreaX1();
    long usedX2 = box.getContentAreaX2();
    if (box.getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.USE_MIN_CHUNKWIDTH))
    {
      RenderNode node = box.getFirstChild();
      while (node != null)
      {
        final long x2 = node.getCachedX() + node.getCachedWidth();
        if (x2 > usedX2)
        {
          usedX2 = x2;
        }
        node = node.getNext();
      }
    }
    final long usedContentWidth = (usedX2 - contentAreaX1);

    final long bcw = ProcessUtility.computeBlockContextWidth(box);
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength minLength = boxDef.getMinimumWidth();
    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long min = minLength.resolve(bcw, 0);
    final long max = maxLength.resolve(bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO);

    if (box.getBoxDefinition().isSizeSpecifiesBorderBox())
    {
      final long minChunkWidth = usedContentWidth + leftPadding + rightPadding;
      final long pref = prefLength.resolve(bcw, Math.max(computedWidth, minChunkWidth));
      final long width = Math.max(0, ProcessUtility.computeLength(min, max, pref));
      final long contentAreaX2 = box.getCachedX() + width - rightPadding;
      if (contentAreaX1 < contentAreaX2)
      {
        box.setContentAreaX2(contentAreaX2);
      }
      else
      {
        // autocorrect to a zero-width box.
        box.setContentAreaX2(contentAreaX1);
      }
    }
    else
    {
      final long computedContentWidth = Math.max(0, computedWidth - leftPadding - rightPadding);
      final long pref = prefLength.resolve(bcw, Math.max(computedContentWidth, usedContentWidth));
      final long width = Math.max(0, ProcessUtility.computeLength(min, max, pref));
      box.setContentAreaX2(contentAreaX1 + width);
    }
    box.setCachedWidth((box.getContentAreaX2() + rightPadding) - box.getCachedX());
  }

  /**
   * Verifies the content width and produces the effective box width. This expands the box as necessary, and is a
   * requirement for row-layout and block-layout.
   *
   * @param box
   */
  private void applyComputedWidth(final RenderBox box)
  {
    if (RenderLength.AUTO.equals(box.getBoxDefinition().getPreferredWidth()) == false)
    {
      // if an explicit preferred width was set, we accept it unchallenged.

      final long x = box.getCachedX();
      final long contentEnd = box.getContentAreaX2();
      final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
      final BoxDefinition bdef = box.getBoxDefinition();
      final long boxEnd = contentEnd + blp.getBorderRight() + bdef.getPaddingRight();
      box.setCachedWidth(boxEnd - x);
      return;
    }


    long nodeX2 = box.getContentAreaX2();
    if (box.getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.USE_MIN_CHUNKWIDTH))
    {
      RenderNode node = box.getFirstChild();
      while (node != null)
      {
        final long nodeWidth = node.getCachedWidth() + node.getCachedX();
        if (nodeWidth > nodeX2)
        {
          nodeX2 = nodeWidth;
        }
        node = node.getNext();
      }
    }

    // now apply the maximum X2 we just computed ..

    final long x = box.getCachedX();
    final StaticBoxLayoutProperties blp = box.getStaticBoxLayoutProperties();
    final BoxDefinition bdef = box.getBoxDefinition();
    final long boxEnd = nodeX2 + blp.getBorderRight() + bdef.getPaddingRight();
    box.setCachedWidth(boxEnd - x);
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    if (breakState.isActive() == false)
    {
      // ignore .. should not happen anyway ..
      if (box.isCacheValid())
      {
        return false;
      }
      return true;
    }

    if (breakState.isSuspended())
    {
      return false;
    }

    final int nodeType = box.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT)
    {
      breakState.add(ReplacedContentSequenceElement.INSTANCE, box);
      return false;
    }

    if ((nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      breakState.add(StartSequenceElement.INSTANCE, box);
      return true;
    }

    computeContentArea(box, 0);

    breakState.add(InlineBoxSequenceElement.INSTANCE, box);
    breakState.setSuspendItem(box.getInstanceId());
    return false;
  }

  protected void finishInlineLevelBox(final RenderBox box)
  {
    if (breakState.isActive() == false)
    {
      return;
    }
    if (breakState.getSuspendItem() == box.getInstanceId())
    {
      // stop being suspended.
      breakState.setSuspendItem(null);
      return;
    }

    final int nodeType = box.getNodeType();
    if ((nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      breakState.add(EndSequenceElement.INSTANCE, box);
      return;
    }

    final Object suspender = breakState.getSuspendItem();
    if (box.getInstanceId() == suspender)
    {
      breakState.setSuspendItem(null);
      return;
    }

    if (suspender != null)
    {
      return;
    }

    if (nodeType == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      throw new IllegalStateException("This cannot be: Why is there a paragrah inside a inline-box?");
    }

  }

  protected void processInlineLevelNode(final RenderNode node)
  {
    if (breakState.isActive() == false || breakState.isSuspended())
    {
      return;
    }

    final int nodeType = node.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      node.setCachedWidth(finNode.getLayoutedWidth());
      return;
    }

    if (nodeType == LayoutNodeTypes.TYPE_NODE_TEXT)
    {
      breakState.add(TextSequenceElement.INSTANCE, node);
    }
    else if (nodeType == LayoutNodeTypes.TYPE_NODE_SPACER)
    {
      final StyleSheet styleSheet = node.getStyleSheet();
      if (WhitespaceCollapse.PRESERVE.equals(styleSheet.getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE)) &&
          styleSheet.getBooleanStyleProperty(TextStyleKeys.TRIM_TEXT_CONTENT) == false)
      {
        breakState.add(SpacerSequenceElement.INSTANCE, node);
      }
      else if (breakState.isContainsContent())
      {
        breakState.add(SpacerSequenceElement.INSTANCE, node);
      }
    }
    else
    {
      breakState.add(InlineNodeSequenceElement.INSTANCE, node);
    }
  }

  protected void processBlockLevelNode(final RenderNode node)
  {
    // This could be anything, text, or an image.
    node.setCachedX(computeBlockPosition(node));
    if (node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      node.setCachedWidth(finNode.getLayoutedWidth());
    }
    else
    {
      computeNodeWidth(node);
    }
  }

  protected void processCanvasLevelNode(final RenderNode node)
  {
    // next, compute the width ...
    node.setCachedX(computeCanvasPosition(node));

    if (node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      node.setCachedWidth(finNode.getLayoutedWidth());
    }
    else
    {
      computeNodeWidth(node);
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    if (box.isComplexParagraph())
    {
      final RenderBox lineboxContainer = box.getLineboxContainer();
      RenderNode node = lineboxContainer.getFirstChild();
      while (node != null)
      {
        // all childs of the linebox container must be inline boxes. They
        // represent the lines in the paragraph. Any other element here is
        // a error that must be reported
        if (node.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX)
        {
          throw new IllegalStateException("Expected ParagraphPoolBox elements.");
        }

        final ParagraphPoolBox inlineRenderBox = (ParagraphPoolBox) node;
        if (startLine(inlineRenderBox))
        {
          processBoxChilds(inlineRenderBox);
          finishLine(inlineRenderBox);
        }

        node = node.getNext();
      }
    }
    else
    {
      final ParagraphPoolBox node = box.getPool();
      // all childs of the linebox container must be inline boxes. They
      // represent the lines in the paragraph. Any other element here is
      // a error that must be reported
      if (startLine(node))
      {
        processBoxChilds(node);
        finishLine(node);
      }
    }
  }

  private boolean startLine(final RenderBox inlineRenderBox)
  {
    if (breakState.isActive() == false)
    {
      return false;
    }

    if (breakState.isSuspended())
    {
      return false;
    }

    breakState.clear();
    breakState.add(StartSequenceElement.INSTANCE, inlineRenderBox);
    return true;
  }

  private void finishLine(final RenderBox inlineRenderBox)
  {
    if (breakState.isActive() == false || breakState.isSuspended())
    {
      throw new IllegalStateException("No active breakstate, finish-line cannot continue.");
    }

    breakState.add(EndSequenceElement.INSTANCE, inlineRenderBox);

    final ParagraphRenderBox paragraph = breakState.getParagraph();

    final ElementAlignment textAlignment = paragraph.getTextAlignment();
    final long textIndent = paragraph.getTextIndent();
    final long firstLineIndent = paragraph.getFirstLineIndent();
    // This aligns all direct childs. Once that is finished, we have to
    // check, whether possibly existing inner-paragraphs are still valid
    // or whether moving them violated any of the inner-pagebreak constraints.
    final TextAlignmentProcessor processor = create(textAlignment);

    final SequenceList sequence = breakState.getSequence();

    final long x2;
    final boolean overflowX = paragraph.getStaticBoxLayoutProperties().isOverflowX();
    if (overflowX)
    {
      x2 = OVERFLOW_DUMMY_WIDTH;
    }
    else
    {
      x2 = paragraph.getContentAreaX2();
    }
    final long x1 = paragraph.getContentAreaX1();
    final long lineStart = Math.min(x2, x1 + firstLineIndent);
    final long lineEnd = x2;
    if (lineEnd - lineStart <= 0)
    {
      final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
      processor.initialize(metaData, sequence, lineStart, lineStart + minimumChunkWidth, pageGrid, overflowX);
      InfiniteMinorAxisLayoutStep.logger.warn("Auto-Corrected zero-width first-line on paragraph " + paragraph.getName());
    }
    else
    {
      processor.initialize(metaData, sequence, lineStart, lineEnd, pageGrid, overflowX);
    }

    while (processor.hasNext())
    {
      final RenderNode linebox = processor.next();
      if (linebox.getNodeType() != LayoutNodeTypes.TYPE_BOX_LINEBOX)
      {
        throw new IllegalStateException("Line must not be null");
      }

      paragraph.addGeneratedChild(linebox);

      if (processor.hasNext())
      {
        final long innerLineStart = Math.min(x2, x1 + textIndent);
        final long innerLineEnd = x2;
        if (innerLineEnd - innerLineStart <= 0)
        {
          final long minimumChunkWidth = paragraph.getPool().getMinimumChunkWidth();
          processor.updateLineSize(innerLineStart, innerLineStart + minimumChunkWidth);
          InfiniteMinorAxisLayoutStep.logger.warn("Auto-Corrected zero-width text-line on paragraph " + paragraph.getName());
        }
        else
        {
          processor.updateLineSize(innerLineStart, innerLineEnd);
        }
      }
    }

    processor.deinitialize();
  }

  /**
   * Reuse the processors ..
   *
   * @param alignment
   * @return
   */
  private TextAlignmentProcessor create(final ElementAlignment alignment)
  {
    if (ElementAlignment.CENTER.equals(alignment))
    {
      if (centerProcessor == null)
      {
        centerProcessor = new CenterAlignmentProcessor();
      }
      return centerProcessor;
    }
    else if (ElementAlignment.RIGHT.equals(alignment))
    {
      if (rightProcessor == null)
      {
        rightProcessor = new RightAlignmentProcessor();
      }
      return rightProcessor;
    }
    else if (ElementAlignment.JUSTIFY.equals(alignment))
    {
      if (justifyProcessor == null)
      {
        justifyProcessor = new JustifyAlignmentProcessor();
      }
      return justifyProcessor;
    }

    if (leftProcessor == null)
    {
      leftProcessor = new LeftAlignmentProcessor();
    }
    return leftProcessor;
  }

  private long computeBlockPosition(final RenderNode node)
  {
    final RenderBox parent = node.getParent();
    if (parent == null)
    {
      return 0;
    }
    return parent.getContentAreaX1();
  }

  private long computeCanvasPosition(final RenderNode node)
  {
    final RenderBox parent = node.getParent();
    if (parent == null)
    {
      return 0;
    }

    final long contentAreaX1 = parent.getContentAreaX1();
    final long bcw = ProcessUtility.computeBlockContextWidth(node);
    final double posX = node.getNodeLayoutProperties().getPosX();
    final long position = RenderLength.resolveLength(bcw, posX);
    return (contentAreaX1 + position);
  }

  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    // first, compute the position. The position is global, not relative to a
    // parent or so. Therefore a child has no connection to the parent's
    // effective position, when it is painted.

    if (breakState.isActive() == false)
    {
      if (box.isCacheValid())
      {
        return false;
      }

      if (box != continuedElement)
      {
        computeContentArea(box, computeCanvasPosition(box));
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
        if (continuedElement == null)
        {
          final long lineBoxChangeTracker = paragraphBox.getEffectiveLineboxContainer().getChangeTracker();
          final boolean unchanged = lineBoxChangeTracker == paragraphBox.getMinorLayoutAge();
          if (unchanged)
          {
            return false;
          }
        }

        paragraphBox.clearLayout();
        breakState.init(paragraphBox);
      }
      return true;
    }

    if (breakState.isSuspended() == false)
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

    // this way or another - we are suspended now. So there is no need to look
    // at the children anymore ..
    return false;
  }

  protected void finishCanvasLevelBox(final RenderBox box)
  {
    if (box.isCacheValid())
    {
      return;
    }

    // make sure that the width takes all the borders and paddings into account.
    applyStrictComputedWidth(box);

    if (breakState.isActive())
    {
      final Object suspender = breakState.getSuspendItem();
      if (box.getInstanceId() == suspender)
      {
        breakState.setSuspendItem(null);
        return;
      }
      if (suspender != null)
      {
        return;
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        // finally update the change tracker ..
        final ParagraphRenderBox paraBox = (ParagraphRenderBox) box;
        paraBox.setMinorLayoutAge(paraBox.getEffectiveLineboxContainer().getChangeTracker());

        breakState.deinit();
      }
    }
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    if (breakState.isActive() == false)
    {
      if (box.isCacheValid())
      {
        return false;
      }

      if (box != continuedElement)
      {
        computeContentArea(box, computeRowPosition(box));
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        final ParagraphRenderBox paragraphBox = (ParagraphRenderBox) box;
        if (continuedElement == null)
        {
          final long lineBoxChangeTracker = paragraphBox.getEffectiveLineboxContainer().getChangeTracker();
          final boolean unchanged = lineBoxChangeTracker == paragraphBox.getMinorLayoutAge();
          if (unchanged)
          {
            return false;
          }
        }

        paragraphBox.clearLayout();
        breakState.init(paragraphBox);
      }
      return true;
    }

    if (breakState.isSuspended() == false)
    {
      // The break-state exists only while we are inside of an paragraph
      // and suspend can only happen on inline elements.
      // A block-element inside a paragraph cannot be (and if it does, it is
      // a bug)
      throw new IllegalStateException("This cannot be.");
    }

    // this way or another - we are suspended now. So there is no need to look
    // at the children anymore ..
    return false;
  }

  protected void processRowLevelNode(final RenderNode node)
  {

    // This could be anything, text, or an image.
    node.setCachedX(computeRowPosition(node));

    if (node.getNodeType() == LayoutNodeTypes.TYPE_NODE_FINISHEDNODE)
    {
      final FinishedRenderNode finNode = (FinishedRenderNode) node;
      node.setCachedWidth(finNode.getLayoutedWidth());
    }
    else
    {
      computeNodeWidth(node);
    }
  }

  protected void finishRowLevelBox(final RenderBox box)
  {
    if (box.isCacheValid())
    {
      return;
    }

    applyComputedWidth(box);

    if (breakState.isActive())
    {
      final Object suspender = breakState.getSuspendItem();
      if (box.getInstanceId() == suspender)
      {
        breakState.setSuspendItem(null);
        return;
      }
      if (suspender != null)
      {
        return;
      }

      if (box.getNodeType() == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
      {
        // finally update the change tracker ..
        final ParagraphRenderBox paraBox = (ParagraphRenderBox) box;
        paraBox.setMinorLayoutAge(paraBox.getEffectiveLineboxContainer().getChangeTracker());
        breakState.deinit();
      }
    }
  }


  private long computeRowPosition(final RenderNode node)
  {
    // we have no margins yet ..
    final long marginLeft = 0;

    // The y-position of a box depends on the parent.
    final RenderBox parent = node.getParent();

    // A table row is something special. Although it is a block box,
    // it layouts its children from left to right
    if (parent != null)
    {
      final RenderNode prev = node.getPrev();
      if (prev != null)
      {
        // we have a silbling. Position yourself directly below your silbling ..
        return (marginLeft + prev.getCachedX() + prev.getCachedWidth());
      }
      else
      {
        final StaticBoxLayoutProperties blp = parent.getStaticBoxLayoutProperties();
        final BoxDefinition bdef = parent.getBoxDefinition();
        final long insetLeft = (blp.getBorderLeft() + bdef.getPaddingLeft());

        return (marginLeft + insetLeft + parent.getCachedX());
      }
    }
    else
    {
      // there's no parent ..
      return (marginLeft);
    }
  }
}
