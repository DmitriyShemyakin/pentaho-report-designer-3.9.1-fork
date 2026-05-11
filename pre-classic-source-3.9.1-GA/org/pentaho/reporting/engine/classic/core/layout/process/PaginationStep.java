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

import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.BreakMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.PageBreakPositionList;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.context.StaticBoxLayoutProperties;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;

/**
 * Creation-Date: 11.04.2007, 14:23:34
 *
 * @author Thomas Morgner
 */
public final class PaginationStep extends IterateVisualProcessStep
{
  private boolean breakPending;
  private PageBreakPositionList breakUtility;
  private long pageHeight;
  private long pageEnd;
  private ReportStateKey visualState;
  private BreakMarkerRenderBox breakIndicatorEncountered;

  public PaginationStep()
  {
    breakUtility = new PageBreakPositionList();
  }

  public PaginationResult performPagebreak(final LogicalPageBox pageBox)
  {
    final RenderNode lastChild = pageBox.getLastChild();
    if (lastChild != null)
    {
      final long lastChildY2 = lastChild.getY() + lastChild.getHeight();
      if (lastChildY2 < pageBox.getHeight())
      {
        //ModelPrinter.print(pageBox);
        throw new IllegalStateException
            ("Assertation failed: Block layouting did not proceed: " + lastChildY2 + " < " + pageBox.getHeight());
      }
    }

    this.pageHeight = pageBox.getPageHeight();
    this.breakIndicatorEncountered = null;

    try
    {
      final long[] allCurrentBreaks = pageBox.getPhysicalBreaks(RenderNode.VERTICAL_AXIS);
      final long pageOffset = pageBox.getPageOffset();

      if (allCurrentBreaks.length == 0)
      {
        // No maximum height.
        throw new IllegalStateException("No page given. This is really bad.");
      }

      // Note: For now, we limit both the header and footer to a single physical
      // page. This safes me a lot of trouble for now.

      final BlockRenderBox headerArea = pageBox.getHeaderArea();
      final long headerHeight = Math.min(headerArea.getHeight(), allCurrentBreaks[0]);
      headerArea.setHeight(headerHeight);

      final long lastBreakLocal = allCurrentBreaks[allCurrentBreaks.length - 1];
      final BlockRenderBox footerArea = pageBox.getFooterArea();
      final BlockRenderBox repeatFooterArea = pageBox.getRepeatFooterArea();
      long footerHeight = footerArea.getHeight();
      long repeatFooterHeight = repeatFooterArea.getHeight();
      if (allCurrentBreaks.length > 1)
      {
        final long lastPageHeight = lastBreakLocal - allCurrentBreaks[allCurrentBreaks.length - 2];
        footerHeight = Math.min(footerHeight, lastPageHeight);
        footerArea.setHeight(footerHeight);

        repeatFooterHeight = Math.min(footerHeight, lastPageHeight);
        repeatFooterArea.setHeight(repeatFooterHeight);
      }

      // Assertation: Make sure that we do not run into a infinite loop..
      if (headerHeight + footerHeight + repeatFooterHeight >= lastBreakLocal)
      {
        // This is also bad. There will be no space left to print a single element.
        throw new IllegalStateException("Header and footer consume the whole page. No space left for normal-flow.");
      }

      final PageBreakPositionList allPreviousBreak = pageBox.getAllVerticalBreaks();
      breakUtility.copyFrom(allPreviousBreak);

      // Then add all new breaks (but take the header and footer-size into account) ..
      if (allCurrentBreaks.length == 1)
      {
        breakUtility.addMajorBreak(pageOffset, headerHeight);
        breakUtility.addMajorBreak((lastBreakLocal - repeatFooterHeight - footerHeight - headerHeight) + pageOffset, headerHeight);
      }
      else // more than one physical page; therefore header and footer are each on a separate canvas ..
      {
        breakUtility.addMajorBreak(pageOffset, headerHeight);
        final int breakCount = allCurrentBreaks.length - 1;
        for (int i = 1; i < breakCount; i++)
        {
          final long aBreak = allCurrentBreaks[i];
          breakUtility.addMinorBreak(pageOffset + (aBreak - headerHeight));
        }
        breakUtility.addMajorBreak(pageOffset + (lastBreakLocal - headerHeight - repeatFooterHeight - footerHeight), headerHeight);
      }

      pageEnd = breakUtility.getLastMasterBreak();
      visualState = null;

      // now process all the other content (excluding the header and footer area)
      if (startBlockLevelBox(pageBox))
      {
        processBoxChilds(pageBox);
      }
      finishBlockLevelBox(pageBox);

      if (lastChild != null)
      {
        final long lastChildY2 = lastChild.getY() + lastChild.getHeight();
        if (lastChildY2 < pageBox.getHeight())
        {
          throw new IllegalStateException
              ("Assertation failed: Pagination violated block-constraints: " + lastChildY2 + " < " + pageBox.getHeight());
        }
      }

      final PageableBreakContext context = (PageableBreakContext) pageBox.getBreakContext();
      if (context == null)
      {
        throw new IllegalStateException("After pagination, we have no break context. Why?");
      }

      final long masterBreak = breakUtility.getLastMasterBreak();
      final boolean overflow = breakIndicatorEncountered != null || pageBox.getHeight() > masterBreak;
      final boolean nextPageContainsContent = (pageBox.getHeight() > masterBreak);
      return new PaginationResult(breakUtility, overflow, nextPageContainsContent, visualState);
    }
    finally
    {
      visualState = null;
    }
  }

  protected void processParagraphChilds(final ParagraphRenderBox box)
  {
    processBoxChilds(box);
  }

  private PageableBreakContext getBreakContext(final RenderBox box,
                                               final boolean createBoxIfNeeded,
                                               final boolean useInitialShift)
  {
    final Object boxContext = box.getBreakContext();
    final RenderBox parentBox = box.getParent();
    if (boxContext instanceof PageableBreakContext)
    {
      final PageableBreakContext context = (PageableBreakContext) boxContext;
      if (createBoxIfNeeded)
      {
        if (parentBox != null)
        {
          final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
          context.updateFromParent(parentContext, useInitialShift);
        }
        else
        {
          // reset ...
          context.setShift(0);
          context.setAppliedShift(0);
        }
      }
      return context;
    }

    if (createBoxIfNeeded == false)
    {
      throw new IllegalStateException("How can i have a finish without a start?");
    }

    if (parentBox == null)
    {
      final PageableBreakContext context = new PageableBreakContext();
      box.setBreakContext(context);
      return context;
    }

    final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
    final PageableBreakContext context = new PageableBreakContext(parentContext, useInitialShift);
    box.setBreakContext(context);
    return context;
  }

  protected boolean startBlockLevelBox(final RenderBox box)
  {
    final PageableBreakContext boxContext = getBreakContext(box, true, false);
    final long shift = boxContext.getShift();
    if (boxContext.isBreakSuspended() == false &&
        breakIndicatorEncountered == null && box.getNodeType() == LayoutNodeTypes.TYPE_BOX_BREAKMARK)
    {
      // pin the parent ...
      box.markPinned(pageEnd);
      breakIndicatorEncountered = (BreakMarkerRenderBox) box;
    }

    if (box.isFinishedPaginate() == false)
    {
      if (box.isCommited())
      {
        box.setFinishedPaginate(true);
      }
      else
      {
        final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
        if (sblp.isAvoidPagebreakInside() || sblp.getWidows() > 0 || sblp.getOrphans() > 0)
        {
          // Check, whether this box sits on a break-position. In that case, we can call that box finished as well.
          final long boxY = box.getY();
          final long nextMinorBreak = breakUtility.findNextBreakPosition(boxY + shift);
          final long spaceAvailable = nextMinorBreak - (boxY + shift);

          // This box sits directly on a pagebreak. No matter how much content we fill in the box, it will not move.
          // This makes this box a finished box.
          if (spaceAvailable == 0 || box.isPinned())
          {
            box.setFinishedPaginate(true);
          }
        }
        else
        {
          // This box defines no constraints that would cause a shift of it later in the process. We can treat it as
          // if it is finished already ..
          box.setFinishedPaginate(true);
        }
      }
    }

    final int breakIndicator = box.getManualBreakIndicator();

    // First check the simple cases:
    // If the box wants to break, then there's no point in waiting: Shift the box and continue.
    final RenderLength fixedPosition = box.getBoxDefinition().getFixedPosition();

    final long fixedPositionResolved = fixedPosition.resolve(pageHeight, 0);
    if (breakIndicator == RenderBox.DIRECT_MANUAL_BREAK || breakPending)
    {
      // find the next major break and shift the box to this position.
      // update the 'shift' to reflect this new change. Process the contents of this box as well, as the box may
      // have additional breaks inside (or may overflow, or whatever ..).
      final long boxY = box.getY();
      final long shiftedBoxY = boxY + shift;
      final long nextNonShiftedMajorBreak = breakUtility.findNextMajorBreakPosition(shiftedBoxY);
      final long fixedPositionOnNextPage = breakUtility.computeFixedPositionInFlow(nextNonShiftedMajorBreak,
          fixedPositionResolved);
      final long nextMajorBreak = Math.max(nextNonShiftedMajorBreak, fixedPositionOnNextPage);
//      final long nextMajorBreak = nextNonShiftedMajorBreak + fixedPositionResolved;
      if (nextMajorBreak < shiftedBoxY)
      {
        // This band will be outside the last pagebreak. We can only shift it normally, but there is no way
        // that we could shift it to the final position yet.
        box.setY(boxY + shift);
      }
      else
      {
        final long nextShift = nextMajorBreak - boxY;
        final long shiftDelta = nextShift - shift;
        box.setY(boxY + nextShift);
        BoxShifter.extendHeight(box.getParent(), shiftDelta);
        boxContext.setShift(nextShift);
        boxContext.setAppliedShift(nextShift);
      }
      updateStateKey(box);
      box.markPinned(pageEnd);
      breakPending = (false);
      return true;
    }

    // If this box does not cross any (major or minor) break, it may need no additional shifting at all.
    if (RenderLength.AUTO.equals(fixedPosition))
    {
      if (breakUtility.isCrossingPagebreak(box, shift) == false)
      {
        // The whole box fits on the current page. No need to do anything fancy.
        if (breakIndicator == RenderBox.NO_MANUAL_BREAK)
        {
          // As neither this box nor any of the children will cause a pagebreak, we can shift them and skip the processing
          // from here.
          BoxShifter.shiftBox(box, shift);
          updateStateKeyDeep(box);
          return false;
        }
        if (breakIndicator == RenderBox.INDIRECT_MANUAL_BREAK)
        {
          // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
          // for now, we will only apply the ordinary shift.
          final long boxY = box.getY();
          box.setY(boxY + shift);
          updateStateKey(box);
          return true;
        }
        throw new IllegalStateException("The box contains an invalid BreakIndicator.");
      }

      // At this point we know, that the box may cause some shifting. It crosses at least one minor or major pagebreak.
      // Right now, we are just evaluating the next break. In a future version, we could search all possible break
      // positions up to the next major break.
      final long boxY = box.getY();

      final long nextMinorBreak = breakUtility.findNextBreakPosition(boxY + shift);
      final long spaceAvailable = nextMinorBreak - (boxY + shift);

      // This box sits directly on a pagebreak. This means, the page is empty, and there is no need for additional
      // shifting.
      if (spaceAvailable == 0)
      {
        box.setY(boxY + shift);
        updateStateKey(box);
        box.markPinned(pageEnd);
        return true;
      }

      final long spaceConsumed = computeNonBreakableBoxHeight(box);
      if (spaceAvailable < spaceConsumed)
      {
        // So we have not enough space to fullfill the layout-constraints. Be it so. Lets shift the box to the next
        // break.
        final long nextShift = nextMinorBreak - boxY;
        final long shiftDelta = nextShift - shift;
        box.setY(boxY + nextShift);
        BoxShifter.extendHeight(box.getParent(), shiftDelta);
        boxContext.setShift(nextShift);
        boxContext.setAppliedShift(nextShift);
        updateStateKey(box);
        box.markPinned(pageEnd);
        return true;
      }

      // OK, there *is* enough space available. Start the normal processing
      box.setY(boxY + shift);
      updateStateKey(box);
      return true;
    }

    // If you've come this far, this means, that your box has a fixed position defined.

    final long boxY = box.getY();
    final long shiftedBoxPosition = boxY + shift;
    final long fixedPositionInFlow = breakUtility.computeFixedPositionInFlow(shiftedBoxPosition, fixedPositionResolved);
    if (fixedPositionInFlow < shiftedBoxPosition)
    {
      // This is a invalid result, create a ordinary pagebreak and try again the next time.
      final long nextMinorBreak = breakUtility.findNextBreakPosition(shiftedBoxPosition);
      final long spaceAvailable = nextMinorBreak - (shiftedBoxPosition);

      // This box sits directly on a pagebreak. This means, the page is empty, and there is no need for additional
      // shifting.
      if (spaceAvailable == 0)
      {
        box.setY(boxY + shift);
        updateStateKey(box);
        box.markPinned(pageEnd);
        return true;
      }

      // Perform an ordinary pagebreak here ..
      final long nextShift = nextMinorBreak - boxY;
      final long shiftDelta = nextShift - shift;
      box.setY(boxY + nextShift);
      BoxShifter.extendHeight(box.getParent(), shiftDelta);
      boxContext.setShift(nextShift);
      boxContext.setAppliedShift(nextShift);
      updateStateKey(box);
      box.markPinned(pageEnd);
      return true;
    }

    //breakUtility.computeFixedPositionInFlow(shiftedBoxPosition, fixedPositionResolved);
    // The computed break seems to be valid.
    final long fixedPositionDelta = fixedPositionInFlow - shiftedBoxPosition;
    if (breakUtility.isCrossingPagebreakWithFixedPosition
        (shiftedBoxPosition, box.getHeight(), fixedPositionResolved) == false)
    {
      // The whole box fits on the current page. However, we have to apply the shifting to move the box
      // to its defined fixed-position.
      if (breakIndicator == RenderBox.NO_MANUAL_BREAK)
      {
        // As neither this box nor any of the children will cause a pagebreak, we can shift them and skip the processing
        // from here.
        BoxShifter.shiftBox(box, fixedPositionDelta);
        BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
        updateStateKeyDeep(box);
        return false;
      }
      if (breakIndicator == RenderBox.INDIRECT_MANUAL_BREAK)
      {
        // One of the children of this box will cause a manual pagebreak. We have to dive deeper into this child.
        // for now, we will only apply the ordinary shift.
        box.setY(fixedPositionInFlow);
        boxContext.setShift(shift + fixedPositionDelta);
        boxContext.setAppliedShift(shift + fixedPositionDelta);
        BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
        updateStateKey(box);
        return true;
      }
      throw new IllegalStateException("The box contains an invalid BreakIndicator.");
    }

    // A box with a fixed position will always be printed at this position, even if it does not seem
    // to fit there. If we move the box, we would break the explict layout constraint 'fixed-position' in
    // favour of an implict one ('page-break: avoid').

    final long nextMinorBreak = breakUtility.findNextBreakPosition(fixedPositionInFlow);
    final long spaceAvailable = nextMinorBreak - fixedPositionInFlow;

    // This box sits directly on a pagebreak. This means, the current page is empty, and there is no need for additional
    // shifting.
    if (spaceAvailable == 0)
    {
      boxContext.setShift(shift + fixedPositionDelta);
      boxContext.setAppliedShift(shift + fixedPositionDelta);
      box.setY(fixedPositionInFlow);
      BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
      updateStateKey(box);
      box.markPinned(pageEnd);
      return true;
    }

    final long spaceConsumed = computeNonBreakableBoxHeight(box);
    if (spaceAvailable < spaceConsumed)
    {
      // So we have not enough space to fullfill the layout-constraints. Be it so. Lets shift the box to the next
      // break.
      final long nextShift = nextMinorBreak - boxY;
      final long shiftDelta = nextShift - shift;
      box.setY(boxY + nextShift);
      BoxShifter.extendHeight(box.getParent(), shiftDelta);
      boxContext.setShift(nextShift);
      boxContext.setAppliedShift(nextShift);
      updateStateKey(box);
      box.markPinned(pageEnd);
      return true;
    }

    // OK, there *is* enough space available. Start the normal processing
    boxContext.setShift(shift + fixedPositionDelta);
    boxContext.setAppliedShift(shift + fixedPositionDelta);
    box.setY(fixedPositionInFlow);
    BoxShifter.extendHeight(box.getParent(), fixedPositionDelta);
    updateStateKey(box);
    return true;
  }

  private void updateStateKey(final RenderBox box)
  {
    final long y = box.getY();
    if (y < (pageEnd))
    {
      final ReportStateKey stateKey = box.getStateKey();
      if (stateKey != null)
      {
        this.visualState = stateKey;
      }
    }
  }

  private boolean updateStateKeyDeep(final RenderBox box)
  {
    final long y = box.getY();
    if (y < (pageEnd))
    {
      final ReportStateKey stateKey = box.getStateKey();
      if (stateKey != null)
      {
//        Log.debug ("Deep: Updating state key: " + stateKey);
        this.visualState = stateKey;
        return true;
      }
      else
      {
        RenderNode lastChild = box.getLastChild();
        while (lastChild != null)
        {
          if ((lastChild.getNodeType() & LayoutNodeTypes.MASK_BOX) != LayoutNodeTypes.MASK_BOX)
          {
            lastChild = lastChild.getPrev();
            continue;
          }
          final RenderBox lastBox = (RenderBox) lastChild;
          if (updateStateKeyDeep(lastBox))
          {
            return true;
          }
          lastChild = lastBox.getPrev();
        }
        return false;
      }
    }
    else
    {
//      Log.debug ("Deep: Not in Range: " + y + " <= " + (pageOffset + pageHeight));
      return false;
    }
  }

  protected void processBlockLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
    if (breakPending == false && node.isBreakAfter())
    {
      breakPending = (true);
    }
  }

  protected void finishBlockLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, false, false);
    if (breakPending == false && box.isBreakAfter())
    {
      breakPending = (true);
    }
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
    parentContext.setShift(context.getShift());
  }

  protected boolean startInlineLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, true, true);
    context.suspendBreaks();
    BoxShifter.shiftBox(box, context.getShift());
    return false;
  }

  protected void processInlineLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected void finishInlineLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, false, false);
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
    parentContext.setShift(context.getShift());
  }

  // At a later point, we have to do some real page-breaking here. We should check, whether the box fits, and should
  // shift the box if it doesnt.

  protected boolean startCanvasLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, true, true);
    context.suspendBreaks();
    box.setY(box.getY() + context.getShift());
    return true;
  }

  protected void finishCanvasLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, false, false);
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
    parentContext.setShift(context.getShift());
  }

  protected void processCanvasLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  protected boolean startRowLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, true, true);
    context.suspendBreaks();
    box.setY(box.getY() + context.getShift());
    return true;
  }

  protected void finishRowLevelBox(final RenderBox box)
  {
    final PageableBreakContext context = getBreakContext(box, false, false);
    final RenderBox parentBox = box.getParent();
    if (parentBox == null)
    {
      return;
    }
    final PageableBreakContext parentContext = getBreakContext(parentBox, false, false);
    parentContext.setShift(context.getShift());
  }


  protected void processRowLevelNode(final RenderNode node)
  {
    final PageableBreakContext context = getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }

  /**
   * Computes the height that will be required on this page to display at least some parts of the box.
   *
   * @param box the box for which the height is computed
   * @return the height in micro-points.
   */
  private long computeNonBreakableBoxHeight(final RenderBox box)
  {
    final StaticBoxLayoutProperties sblp = box.getStaticBoxLayoutProperties();
    if (sblp.isAvoidPagebreakInside() && box.isPinned() == false)
    {
      return box.getHeight();
    }

    if (box.isPinned())
    {
      return 0;
    }

    final int nodeType = box.getNodeType();
    if ((nodeType == LayoutNodeTypes.TYPE_BOX_CONTENT && sblp.isAvoidPagebreakInside()) ||
        (nodeType & LayoutNodeTypes.MASK_BOX_INLINE) == LayoutNodeTypes.MASK_BOX_INLINE)
    {
      // inline boxes are never broken down (at least we avoid it as if the breakinside is set.
      // same for renderable replaced content
      return box.getHeight();
    }

    if ((nodeType & LayoutNodeTypes.MASK_BOX_BLOCK) != LayoutNodeTypes.MASK_BOX_BLOCK)
    {
      // Canvas boxes have no notion of lines, and therefore they cannot have orphans and widows.
      return 0;
    }

    final int orphans = sblp.getOrphans();
    final int widows = sblp.getWidows();
    if (orphans == 0 && widows == 0)
    {
      // Widows and orphans will be ignored if both of them are zero.
      return 0;
    }

    int counter = 0;
    RenderNode child = box.getFirstChild();
    while (child != null && counter < orphans)
    {
      counter += 1;
      child = child.getNext();
    }

    final long orphanHeight;
    if (child == null)
    {
      orphanHeight = 0;
    }
    else
    {
      orphanHeight = box.getY() - (child.getY() + child.getHeight());
    }

    counter = 0;
    child = box.getLastChild();
    while (child != null && counter < orphans)
    {
      counter += 1;
      child = child.getPrev();
    }

    final long widowHeight;
    if (child == null)
    {
      widowHeight = 0;
    }
    else
    {
      widowHeight = (box.getY() + box.getHeight()) - (child.getY());
    }

    // todo: Compute the height the orphans and widows consume.
    return Math.max(orphanHeight, widowHeight);
  }

  protected void processOtherLevelChild(final RenderNode node)
  {
    final PageableBreakContext context = getBreakContext(node.getParent(), false, false);
    node.setY(node.getY() + context.getShift());
  }
}
