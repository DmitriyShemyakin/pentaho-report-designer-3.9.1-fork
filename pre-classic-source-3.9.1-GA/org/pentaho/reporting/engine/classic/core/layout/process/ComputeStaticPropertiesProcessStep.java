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

import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

/**
 * Computes the width for all elements. This uses the CSS alogorithm, percentages are resolved against the parent's
 * already known width.
 *
 * @author Thomas Morgner
 */
public final class ComputeStaticPropertiesProcessStep extends IterateVisualProcessStep
{
  // Definitions:
  //
  // BCW: Block context width = the size against which percentages are resolved
  // CW: Computed Width = the computed size of the box. This is a rough estaminate of how the box will be sized later. 
  //
  //Canvas in Block :  cw  = bcw of parent
  //                   bcw = cw - insets
  //Inline in Block :  cw  = bcw of parent
  //                   bcw = 0
  //Row in Block :     cw  = bcw of parent
  //                   bcw = cw - insets
  //Block in Block  :  cw  = bcw of parent
  //                   bcw = cw - insets
  //---------------------------------------------
  //Canvas in Inline : cw  = specified width resolved against bcw
  //                   bcw = 0
  //Inline in Inline : cw  = specified width resolved against bcw
  //                   bcw = 0
  //Row in Inline   :  cw  = specified width resolved against bcw
  //                   bcw = 0
  //Block in Inline :  cw  = specified width resolved against bcw
  //                   bcw = 0
  //---------------------------------------------
  //Canvas in Canvas : cw  = specified width resolved against bcw
  //                   bcw = cw - insets
  //Inline in Canvas : cw  = specified width resolved against bcw
  //                   bcw = 0
  //Row in Canvas    : cw  = specified width resolved against bcw
  //                   bcw = cw - insets
  //Block in Canvas  : cw  = specified width resolved against bcw
  //                   bcw = cw - insets
  //----------------------------------------------
  //Canvas in row    : cw  = specified width resolved against bcw
  //                   bcw = cw - insets
  //Inline in row    : cw  = specified width resolved against bcw
  //                   bcw = 0
  //Row in row       : cw  = specified width resolved against bcw
  //                   bcw = cw - insets
  //Block in row     : cw  = specified width resolved against bcw
  //                   bcw = cw - insets
  //


  // Set the maximum height to an incredibly high value. This is now 2^43 micropoints or more than
  // 3000 kilometers. Please call me directly at any time if you need more space for printing.
  public static final long MAX_AUTO = StrictGeomUtility.toInternalValue(0x80000000000L);

  private OutputProcessorMetaData metaData;
  private boolean overflowXSupported;
  private boolean overflowYSupported;
  public static int callCount;

  public ComputeStaticPropertiesProcessStep(final OutputProcessorMetaData metaData)
  {
    this.metaData = metaData;
    overflowXSupported = metaData.isFeatureSupported(OutputProcessorFeature.ASSUME_OVERFLOW_X);
    overflowYSupported = metaData.isFeatureSupported(OutputProcessorFeature.ASSUME_OVERFLOW_Y);
  }

  public void compute(final LogicalPageBox root)
  {
    callCount += 1;
    startProcessing(root);
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    final ExtendedBaselineInfo extendedBaselineInfo = box.getStaticBoxLayoutProperties().getNominalBaselineInfo();
    if (extendedBaselineInfo == null)
    {
      throw new IllegalStateException("Baseline info must not be null at this point");
    }
    final StyleSheet styleSheet = box.getNodeLayoutProperties().getStyleSheet();
    final double value = styleSheet.getDoubleStyleProperty(TextStyleKeys.LINEHEIGHT, 0);
    final long afterEdge = extendedBaselineInfo.getBaseline(ExtendedBaselineInfo.AFTER_EDGE);
    if (value <= 0)
    {
      box.getPool().setLineHeight(afterEdge);
    }
    else
    {
      box.getPool().setLineHeight(RenderLength.resolveLength(afterEdge, value));
    }

    startProcessing(box.getEffectiveLineboxContainer());
  }


  private void computeBreakIndicator(final RenderBox box)
  {

    final StyleSheet styleSheet = box.getStyleSheet();
    final RenderBox parent = box.getParent();
    if (parent != null)
    {
      final boolean breakBefore = styleSheet.getBooleanStyleProperty(BandStyleKeys.PAGEBREAK_BEFORE);
      final boolean breakAfter = box.isBreakAfter();
      final int parentNodeType = parent.getNodeType();
      if ((breakBefore) && (parentNodeType != LayoutNodeTypes.TYPE_BOX_PARAGRAPH))
      {
        box.setManualBreakIndicator(RenderBox.DIRECT_MANUAL_BREAK);
        applyIndirectManualBreakIndicator(parent);
        return;
      }
      if (breakAfter && (parentNodeType != LayoutNodeTypes.TYPE_BOX_PARAGRAPH))
      {
        applyIndirectManualBreakIndicator(parent);
      }
    }

    final boolean fixedPosition = RenderLength.AUTO.equals
        (styleSheet.getStyleProperty(BandStyleKeys.FIXED_POSITION, RenderLength.AUTO)) == false;
    if (fixedPosition)
    {
      applyIndirectManualBreakIndicator(box);
    }
    else
    {
      box.setManualBreakIndicator(RenderBox.NO_MANUAL_BREAK);
    }
  }

  private void applyIndirectManualBreakIndicator(RenderBox node)
  {
    while (node != null)
    {
      if (node.getManualBreakIndicator() != RenderBox.NO_MANUAL_BREAK)
      {
        return;
      }
      node.setManualBreakIndicator(RenderBox.INDIRECT_MANUAL_BREAK);
      node = node.getParent();
    }
  }

  /**
   * Collects and possibly computes the static properties according to the CSS layouting model. The classic JFreeReport
   * layout model does not know anything about margins or borders, so in that case resolving against the CSS model is
   * ok.
   *
   * @param box the box that should be processed.
   * @return true if the box is new, false otherwise
   */
  private boolean updateStaticProperties(final RenderBox box)
  {
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    if (sblp.getNominalBaselineInfo() != null)
    {
      // mark box as seen ..
//      box.setStaticBoxPropertiesAge(box.getChangeTracker());
      return false;
    }

    final BoxDefinition boxDefinition = box.getBoxDefinition();
    final long parentWidth = ProcessUtility.computeBlockContextWidth(box);
    sblp.setMarginTop(boxDefinition.getMarginTop().resolve(parentWidth));
    sblp.setMarginLeft(boxDefinition.getMarginLeft().resolve(parentWidth));
    sblp.setMarginBottom(boxDefinition.getMarginBottom().resolve(parentWidth));
    sblp.setMarginRight(boxDefinition.getMarginRight().resolve(parentWidth));

    final Border border = boxDefinition.getBorder();
    sblp.setBorderTop(border.getTop().getWidth());
    sblp.setBorderLeft(border.getLeft().getWidth());
    sblp.setBorderBottom(border.getBottom().getWidth());
    sblp.setBorderRight(border.getRight().getWidth());

    final StyleSheet style = box.getStyleSheet();
    final int nodeType = box.getNodeType();
    if (nodeType == LayoutNodeTypes.TYPE_BOX_LINEBOX)
    {
      sblp.setAvoidPagebreakInside(true);
    }
    else
    {
      sblp.setAvoidPagebreakInside(style.getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, false));
    }

    sblp.setDominantBaseline(-1);
    sblp.setOrphans(style.getIntStyleProperty(ElementStyleKeys.ORPHANS, 0));
    sblp.setWidows(style.getIntStyleProperty(ElementStyleKeys.WIDOWS, 0));

    final ExtendedBaselineInfo baselineInfo = metaData.getBaselineInfo('x', style);
    if (baselineInfo == null)
    {
      throw new IllegalStateException();
    }
    sblp.setNominalBaselineInfo(baselineInfo);
    sblp.setFontFamily(metaData.getNormalizedFontFamilyName((String) style.getStyleProperty(TextStyleKeys.FONT)));

    final Object collapse = style.getStyleProperty(TextStyleKeys.WHITE_SPACE_COLLAPSE);
    sblp.setPreserveSpace(WhitespaceCollapse.PRESERVE.equals(collapse));
    sblp.setOverflowX(style.getBooleanStyleProperty(ElementStyleKeys.OVERFLOW_X, overflowXSupported));
    sblp.setOverflowY(style.getBooleanStyleProperty(ElementStyleKeys.OVERFLOW_Y, overflowYSupported));
    sblp.setInvisibleConsumesSpace(style.getBooleanStyleProperty
        (ElementStyleKeys.INVISIBLE_CONSUMES_SPACE, nodeType == LayoutNodeTypes.TYPE_BOX_ROWBOX));
    sblp.setVisible(style.getBooleanStyleProperty(ElementStyleKeys.VISIBLE));
    //box.setStaticBoxPropertiesAge(box.getChangeTracker());
    computeBreakIndicator(box);
    return true;
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return false;
    }

    if (updateStaticProperties(box))
    {
      final long bcw = ProcessUtility.computeBlockContextWidth(box);
      final long computedWidth = resolveComputedWidth(box);
      final long insets = box.getInsets();
      box.setComputedWidth(Math.max(computedWidth, bcw));

      if ((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
      {
        box.getStaticBoxLayoutProperties().setBlockContextWidth(0);
      }
      else
      {
        final long newBcw = Math.max(bcw - insets, computedWidth - insets);
        box.getStaticBoxLayoutProperties().setBlockContextWidth(Math.max(0, newBcw));
      }
    }
    return true;
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return false;
    }

    if (updateStaticProperties(box))
    {
      box.setComputedWidth(box.getInsets());
      box.getStaticBoxLayoutProperties().setBlockContextWidth(0);
    }
    return true;
  }

  private long resolveComputedWidth(final RenderBox box)
  {
    final long bcw = ProcessUtility.computeBlockContextWidth(box);
    final BoxDefinition boxDef = box.getBoxDefinition();
    final RenderLength minLength = boxDef.getMinimumWidth();
    final RenderLength prefLength = boxDef.getPreferredWidth();
    final RenderLength maxLength = boxDef.getMaximumWidth();

    final long min = minLength.resolve(bcw, 0);
    final long pref = prefLength.resolve(bcw, 0);
    final long max = maxLength.resolve(bcw, ComputeStaticPropertiesProcessStep.MAX_AUTO);
    return ProcessUtility.computeLength(min, max, pref);
  }


  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return false;
    }

    if (updateStaticProperties(box))
    {
      final long computedWidth = resolveComputedWidth(box);
      box.setComputedWidth(computedWidth);

      if ((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
      {
        box.getStaticBoxLayoutProperties().setBlockContextWidth(0);
      }
      else
      {
        box.getStaticBoxLayoutProperties().setBlockContextWidth(Math.max(0, computedWidth - box.getInsets()));
      }
    }

    return true;
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return false;
    }

    if (updateStaticProperties(box))
    {
      final long computedWidth = resolveComputedWidth(box);
      box.setComputedWidth(computedWidth);

      if ((box.getNodeType() & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
      {
        box.getStaticBoxLayoutProperties().setBlockContextWidth(0);
      }
      else
      {
        final BoxDefinition boxDefinition = box.getBoxDefinition();
        final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
        final long insets = sblp.getBorderLeft() + sblp.getBorderRight() +
            boxDefinition.getPaddingLeft() + boxDefinition.getPaddingRight();
        box.getStaticBoxLayoutProperties().setBlockContextWidth(Math.max(0, computedWidth - insets));
      }
    }

    return true;
  }

  protected void finishInlineLevelBox(final RenderBox box)
  {
    updateMinimumChunkWidth(box);
  }

  protected void finishCanvasLevelBox(final RenderBox box)
  {
    updateMinimumChunkWidth(box);
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
    updateMinimumChunkWidth(box);
  }

  protected void finishRowLevelBox(final RenderBox box)
  {
    updateMinimumChunkWidth(box);
  }

  private void updateMinimumChunkWidth(final RenderBox box)
  {
    final long changeTracker = box.getChangeTracker();
    final long age = box.getStaticBoxPropertiesAge();
    if (changeTracker == age)
    {
      return;
    }

    box.setStaticBoxPropertiesAge(changeTracker);

    final int nodeType = box.getNodeType();
    if ((nodeType & LayoutNodeTypes.TYPE_BOX_PARAGRAPH) == LayoutNodeTypes.TYPE_BOX_PARAGRAPH)
    {
      updateParagraphChunkWidth(box);
      return;
    }

    long chunkWidth = 0;
    RenderNode node = box.getFirstChild();
    while (node != null)
    {
      final long minimumChunkWidth = node.getMinimumChunkWidth();
      if (chunkWidth < minimumChunkWidth)
      {
        chunkWidth = minimumChunkWidth;
      }
      node = node.getNext();
    }

    if ((nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      box.setMinimumChunkWidth(chunkWidth);
    }
    else
    {
      box.setMinimumChunkWidth(Math.max(chunkWidth, box.getComputedWidth()));
    }
  }

  protected void updateParagraphChunkWidth(final RenderBox box)
  {
    final ParagraphRenderBox paragraphRenderBox = (ParagraphRenderBox) box;
    final RenderBox chunkBox;
    final RenderBox lineboxContainer = paragraphRenderBox.getLineboxContainer();
    if (lineboxContainer == null)
    {
      // use pool ..
      chunkBox = paragraphRenderBox.getPool();
    }
    else
    {
      chunkBox = lineboxContainer;
    }

    long chunkWidth = 0;
    RenderNode node = chunkBox.getFirstChild();
    while (node != null)
    {
      final long minimumChunkWidth = node.getMinimumChunkWidth();
      if (chunkWidth < minimumChunkWidth)
      {
        chunkWidth = minimumChunkWidth;
      }
      node = node.getNext();
    }
    box.setMinimumChunkWidth(Math.max(chunkWidth, box.getComputedWidth()));
    if (box.getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.USE_MIN_CHUNKWIDTH))
    {
      box.setComputedWidth(Math.max(box.getComputedWidth(), chunkWidth + box.getInsets()));
    }
  }


}
