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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.SubReportType;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.BlockRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.BreakMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.InlineProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.LayoutNodeTypes;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ProgressMarkerRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderLength;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.SectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.WatermarkAreaBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinitionFactory;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.LayoutPagebreakHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.ValidateSafeToStoreStateStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyAutoCommitStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyCachedValuesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ApplyCommitStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CanvasMajorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CommitStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ComputeStaticPropertiesProcessStep;
import org.pentaho.reporting.engine.classic.core.layout.process.CountBoxesStep;
import org.pentaho.reporting.engine.classic.core.layout.process.InfiniteMajorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.InfiniteMinorAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ParagraphLineBreakStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RevalidateAllAxisLayoutStep;
import org.pentaho.reporting.engine.classic.core.layout.process.RollbackStep;
import org.pentaho.reporting.engine.classic.core.layout.process.ValidateModelStep;
import org.pentaho.reporting.engine.classic.core.layout.style.ManualBreakIndicatorStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SectionKeepTogetherStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.layout.style.SubReportStyleSheet;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.FastStack;

/**
 * The LayoutSystem is a simplified version of the LibLayout-rendering system.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractRenderer implements Renderer
{
  private static final Log logger = LogFactory.getLog(AbstractRenderer.class);

  private static class Section
  {
    private int type;
    private SectionRenderBox sectionBox;

    protected Section(final int type, final SectionRenderBox sectionBox)
    {
      this.type = type;
      this.sectionBox = sectionBox;
    }

    public int getType()
    {
      return type;
    }

    public SectionRenderBox getSectionBox()
    {
      return sectionBox;
    }
  }

  private static class GroupSection
  {
    private static final double COMMON_GROWTH = 0.5;
    private static final int INITIAL_COMMON_SIZE = 50;
    private static final int MAXIMUM_COMMON_SIZE = 5000;

    private RenderBox addBox;
    private RenderBox groupBox;
    private int childCount;
    private int nextBoxStart;
    private StyleSheet styleSheet;

    protected GroupSection(final RenderBox groupBox,
                           final StyleSheet styleSheet)
    {
      if (groupBox == null)
      {
        throw new NullPointerException();
      }
      this.styleSheet = styleSheet;
      this.groupBox = groupBox;
      this.childCount = 0;
      this.nextBoxStart = GroupSection.INITIAL_COMMON_SIZE;
      this.addBox = groupBox;
    }

    protected GroupSection(final RenderBox groupBox,
                           final RenderBox addBox,
                           final int childCount,
                           final int nextBoxStart,
                           final StyleSheet styleSheet)
    {
      if (groupBox == null)
      {
        throw new NullPointerException();
      }
      this.groupBox = groupBox;
      this.addBox = addBox;
      this.childCount = childCount;
      this.nextBoxStart = nextBoxStart;
      this.styleSheet = styleSheet;
    }


    public RenderBox getAddBox()
    {
      return addBox;
    }

    public RenderBox getGroupBox()
    {
      return groupBox;
    }

    public boolean mergeSection(final ReportStateKey stateKey)
    {
      final RenderNode lastSection = addBox.getLastChild();
      if (lastSection == null)
      {
        return false;
      }
      if ((lastSection.getNodeType() & LayoutNodeTypes.MASK_BOX) != LayoutNodeTypes.MASK_BOX)
      {
        return false;
      }

      final RenderBox lastSectionBox = (RenderBox) lastSection;
      final RenderNode maybeMarker = lastSectionBox.getLastChild();
      if (maybeMarker == null)
      {
        return false;
      }
      final int nodeType = maybeMarker.getNodeType();
      if (nodeType == LayoutNodeTypes.TYPE_BOX_INLINE_PROGRESS_MARKER)
      {
        final InlineProgressMarkerRenderBox markerRenderBox = (InlineProgressMarkerRenderBox) maybeMarker;
        markerRenderBox.setStateKey(stateKey);
        return true;
      }
      else if (nodeType == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER)
      {
        final ProgressMarkerRenderBox markerRenderBox = (ProgressMarkerRenderBox) maybeMarker;
        markerRenderBox.setStateKey(stateKey);
        return true;
      }
      return false;
    }

    public void addedSection(final RenderNode node)
    {
      childCount += 1;
      if (childCount == nextBoxStart)
      {
        if (addBox != groupBox)
        {
          addBox.close();
        }
        final BlockRenderBox commonBox = new BlockRenderBox
            (styleSheet, new InstanceID(), BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE,
                ReportAttributeMap.EMPTY_MAP, null);
        commonBox.setName("Common-Section");
        groupBox.addChild(commonBox);
        addBox = commonBox;

        nextBoxStart += (int) Math.min(MAXIMUM_COMMON_SIZE, nextBoxStart * GroupSection.COMMON_GROWTH);
      }
      addBox.addChild(node);
    }

    public void close()
    {
      if (addBox != groupBox)
      {
        addBox.close();
      }
      groupBox.close();
    }

    public int getChildCount()
    {
      return childCount;
    }


    public int getNextBoxStart()
    {
      return nextBoxStart;
    }

    public StyleSheet getStyleSheet()
    {
      return styleSheet;
    }
  }

  private static class IgnoredContentIndicator
  {
    private IgnoredContentIndicator()
    {
    }
  }

  private LogicalPageBox pageBox;
  private DefaultLayoutBuilder normalFlowLayoutBuilder;
  private LayoutBuilder watermarkLayoutBuilder;
  private HeaderLayoutBuilder headerLayoutBuilder;
  private FooterLayoutBuilder footerLayoutBuilder;
  private RepeatedFooterLayoutBuilder repeatedFooterLayoutBuilder;

  private CountBoxesStep countBoxesStep;
  private ValidateModelStep validateModelStep;
  private ComputeStaticPropertiesProcessStep staticPropertiesStep;
  private ParagraphLineBreakStep paragraphLineBreakStep;
  private InfiniteMinorAxisLayoutStep minorAxisLayoutStep;
  private InfiniteMajorAxisLayoutStep majorAxisLayoutStep;
  private CanvasMajorAxisLayoutStep canvasMajorAxisLayoutStep;
  private RevalidateAllAxisLayoutStep revalidateAllAxisLayoutStep;

  private ValidateSafeToStoreStateStep validateSafeToStoreStateStep;

  private CommitStep commitStep;
  private ApplyCommitStep applyCommitStep;
  private RollbackStep rollbackStep;
  private ApplyAutoCommitStep applyAutoCommitStep;

  private OutputProcessorMetaData metaData;
  private OutputProcessor outputProcessor;
  private Section section;
  private int pagebreaks;
  private boolean dirty;
  private ReportStateKey lastStateKey;
  private ApplyCachedValuesStep applyCachedValuesStep;
  private SimpleStyleSheet manualBreakBoxStyle;
  private StyleCache sectionStyleCache;

  private boolean readOnly;
  private FastStack groupStack;
  private Object stateKey;
  private boolean paranoidChecks;
  private BoxDefinitionFactory boxDefinitionFactory;
  private BoxDefinition watermarkBoxDefinition;

  private SimpleStyleSheet bandWithoutKeepTogetherStyle;
  private SimpleStyleSheet bandWithKeepTogetherStyle;
  private static final InlineSubreportMarker[] EMPTY_ARRAY = new InlineSubreportMarker[0];

  private SectionRenderBox[] sectionBoxes;
  private LayoutResult lastValidateResult;

  protected AbstractRenderer(final OutputProcessor outputProcessor)
  {
    final BoxDefinition boxDefinition = new BoxDefinition();
    boxDefinition.setPreferredHeight(RenderLength.createPercentage(100));
    this.watermarkBoxDefinition = boxDefinition.lock();

    this.outputProcessor = outputProcessor;
    this.metaData = outputProcessor.getMetaData();
    this.normalFlowLayoutBuilder = createNormalFlowLayoutBuilder(metaData);
    this.headerLayoutBuilder = new HeaderLayoutBuilder(metaData);
    this.watermarkLayoutBuilder = new HeaderLayoutBuilder(metaData);
    this.footerLayoutBuilder = new FooterLayoutBuilder(metaData);
    this.repeatedFooterLayoutBuilder = new RepeatedFooterLayoutBuilder(metaData);
    this.paranoidChecks = "true".equals
        (metaData.getConfiguration().getConfigProperty
            ("org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks"));
    this.validateModelStep = new ValidateModelStep();
    this.staticPropertiesStep = new ComputeStaticPropertiesProcessStep(metaData);
    this.paragraphLineBreakStep = new ParagraphLineBreakStep();
    this.minorAxisLayoutStep = new InfiniteMinorAxisLayoutStep(metaData);
    this.majorAxisLayoutStep = new InfiniteMajorAxisLayoutStep();
    this.canvasMajorAxisLayoutStep = new CanvasMajorAxisLayoutStep();
    this.revalidateAllAxisLayoutStep = new RevalidateAllAxisLayoutStep(metaData);
    this.validateSafeToStoreStateStep = new ValidateSafeToStoreStateStep();
    this.applyCachedValuesStep = new ApplyCachedValuesStep();
    this.commitStep = new CommitStep();
    this.applyAutoCommitStep = new ApplyAutoCommitStep();
    this.applyCommitStep = new ApplyCommitStep();
    this.rollbackStep = new RollbackStep();
    this.countBoxesStep = new CountBoxesStep();

    this.sectionBoxes = new SectionRenderBox[5];

    this.groupStack = new FastStack(50);

    bandWithKeepTogetherStyle = new SimpleStyleSheet(new SectionKeepTogetherStyleSheet(true));
    bandWithoutKeepTogetherStyle = new SimpleStyleSheet(new SectionKeepTogetherStyleSheet(false));

    final boolean paddingsDisabled = metaData.isFeatureSupported(OutputProcessorFeature.DISABLE_PADDING);
    this.sectionStyleCache = new StyleCache(paddingsDisabled);
    this.boxDefinitionFactory = new BoxDefinitionFactory();
  }

  public boolean isSafeToStore()
  {
    if (pageBox == null)
    {
      return true;
    }
    return validateSafeToStoreStateStep.isSafeToStore(pageBox);
  }

  protected DefaultLayoutBuilder createNormalFlowLayoutBuilder(final OutputProcessorMetaData metaData)
  {
    return new DefaultLayoutBuilder(metaData);
  }

  protected OutputProcessorMetaData getMetaData()
  {
    return metaData;
  }

  public Object getStateKey()
  {
    return stateKey;
  }

  public void setStateKey(final Object stateKey)
  {
    this.stateKey = stateKey;
  }

  public OutputProcessor getOutputProcessor()
  {
    return outputProcessor;
  }

  public void startReport(final ReportDefinition report)
  {
    if (report == null)
    {
      throw new NullPointerException();
    }

    if (readOnly)
    {
      throw new IllegalStateException();
    }

    final SimpleStyleSheet reportStyle = sectionStyleCache.getStyleSheet(report.getStyle());
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(reportStyle);
    this.pageBox = new LogicalPageBox(report, reportStyle, boxDefinition);

    if (reportStyle.getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE))
    {
      this.groupStack.push(new GroupSection(pageBox.getContentArea(), bandWithKeepTogetherStyle));
    }
    else
    {
      this.groupStack.push(new GroupSection(pageBox.getContentArea(), bandWithoutKeepTogetherStyle));
    }
    markDirty();
  }

  public void startSubReport(final ReportDefinition report, final InstanceID insertationPoint)
  {
    if (readOnly)
    {
      throw new IllegalStateException("Renderer is marked read-only");
    }

    if (isIgnoreContent())
    {
      groupStack.push(new IgnoredContentIndicator());
      return;
    }


    final RenderBox box;
    if (insertationPoint == null)
    {
      final StyleSheet styleSheet = new SubReportStyleSheet
          (report.getStyle().getBooleanStyleProperty(BandStyleKeys.PAGEBREAK_BEFORE),
          (report.getStyle().getBooleanStyleProperty(BandStyleKeys.PAGEBREAK_AFTER)));

      final SimpleStyleSheet reportStyle = new SimpleStyleSheet(report.getStyle().getId(), styleSheet);
      final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(reportStyle);
      box = new BlockRenderBox
          (reportStyle, report.getObjectID(), boxDefinition, SubReportType.INSTANCE, report.getAttributes(), null);
      if (report.getName() != null)
      {
        box.setName("Banded-SubReport-Section: name=" + report.getName());
      }
      else
      {
        box.setName("Banded-SubReport-Section");
      }

      box.getStaticBoxLayoutProperties().setPlaceholderBox(true);
      addBox(box);
    }
    else
    {
      final RenderNode maybeBox = pageBox.findNodeById(insertationPoint);
      if (maybeBox == null || (maybeBox.getNodeType() & LayoutNodeTypes.MASK_BOX) != LayoutNodeTypes.MASK_BOX)
      {
        box = null;
      }
      else
      {
        box = (RenderBox) maybeBox;
      }
    }

    if (box == null)
    {
      this.groupStack.push(new IgnoredContentIndicator());
    }
    else
    {
      this.groupStack.push(new GroupSection(box, bandWithoutKeepTogetherStyle));
    }
  }

  public void startGroup(final Group group)
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    if (isIgnoreContent())
    {
      groupStack.push(new IgnoredContentIndicator());
      return;
    }

    final SimpleStyleSheet reportStyle = sectionStyleCache.getStyleSheet(group.getStyle());
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(reportStyle);
    final BlockRenderBox groupBox = new BlockRenderBox
        (reportStyle, group.getObjectID(), boxDefinition, group.getElementType(), group.getAttributes(), null);

    groupBox.getStaticBoxLayoutProperties().setPlaceholderBox(true);
    groupBox.setName(group.getElementTypeName());
    addBox(groupBox);
    if (reportStyle.getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE))
    {
      this.groupStack.push(new GroupSection(groupBox, bandWithKeepTogetherStyle));
    }
    else
    {
      this.groupStack.push(new GroupSection(groupBox, bandWithoutKeepTogetherStyle));
    }
  }

  public void startGroupBody(final GroupBody groupBody)
  {
    if (isIgnoreContent())
    {
      groupStack.push(new IgnoredContentIndicator());
      return;
    }

    final SimpleStyleSheet reportStyle = sectionStyleCache.getStyleSheet(groupBody.getStyle());
    final BoxDefinition boxDefinition = boxDefinitionFactory.getBoxDefinition(reportStyle);
    final BlockRenderBox groupBox = new BlockRenderBox(reportStyle, groupBody.getObjectID(),
        boxDefinition, groupBody.getElementType(), groupBody.getAttributes(), null);

    // todo: PRD-3154: This is black magic, placeholder box true is evil.
    // Need to evaluate side-effects of this beast. Is it safe for keep-together boxes?
    groupBox.getStaticBoxLayoutProperties().setPlaceholderBox(true);
    groupBox.setName(groupBody.getElementTypeName());
    addBox(groupBox);
    if (reportStyle.getBooleanStyleProperty(ElementStyleKeys.AVOID_PAGEBREAK_INSIDE))
    {
      this.groupStack.push(new GroupSection(groupBox, bandWithKeepTogetherStyle));
    }
    else
    {
      this.groupStack.push(new GroupSection(groupBox, bandWithoutKeepTogetherStyle));
    }
    markDirty();
  }

  private void addBox(final RenderNode node)
  {
    final GroupSection groupSection = (GroupSection) groupStack.peek();
    groupSection.addedSection(node);
  }

  private boolean mergeSection(final RenderNode node)
  {
    final GroupSection groupSection = (GroupSection) groupStack.peek();
    return groupSection.mergeSection(node.getStateKey());
  }

  private SectionRenderBox createSectionBox(final int type, final BoxDefinition boxDefinition)
  {
    if (type == Renderer.TYPE_NORMALFLOW && sectionBoxes[type] != null)
    {
      final SectionRenderBox renderBox = sectionBoxes[type];
      sectionBoxes[type] = null;
      return renderBox;
    }

    return new SectionRenderBox
        (bandWithoutKeepTogetherStyle, new InstanceID(), boxDefinition, AutoLayoutBoxType.INSTANCE,
            ReportAttributeMap.EMPTY_MAP, null);
  }

  public void startSection(final int type)
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    if (isIgnoreContent())
    {
      return;
    }

    final SectionRenderBox sectionBox;
    // todo: The group together should be cleanly inherited from the direct parent..
    if (type == Renderer.TYPE_WATERMARK)
    {
      watermarkLayoutBuilder.startSection(pageBox.getWatermarkArea(), true);

      sectionBox = createSectionBox(type, watermarkBoxDefinition);
      sectionBox.setName("Watermark-Section");
      this.section = new Section(type, sectionBox);
    }
    else if (type == Renderer.TYPE_HEADER)
    {
      // when a header starts, we have to clear the page-footer too (to get rid of all the slotted bands).
      headerLayoutBuilder.startSection(pageBox.getHeaderArea(), true);

      sectionBox = createSectionBox(type, BoxDefinition.EMPTY);
      sectionBox.setName("Header-" + type);
      this.section = new Section(type, sectionBox);
    }
    else if (type == Renderer.TYPE_FOOTER)
    {
      footerLayoutBuilder.startSection(pageBox.getFooterArea(), true);

      sectionBox = createSectionBox(type, BoxDefinition.EMPTY);
      sectionBox.setName("Footer-" + type);
      this.section = new Section(type, sectionBox);
    }
    else if (type == Renderer.TYPE_REPEATED_FOOTER)
    {
      repeatedFooterLayoutBuilder.startSection(pageBox.getRepeatFooterArea(), true);

      sectionBox = createSectionBox(type, BoxDefinition.EMPTY);
      sectionBox.setName("Repeat-Footer-" + type);
      this.section = new Section(type, sectionBox);
    }
    else
    {
      normalFlowLayoutBuilder.startSection(pageBox, false);

      sectionBox = createSectionBox(TYPE_NORMALFLOW, BoxDefinition.EMPTY);
      sectionBox.setName("Section-" + type);
      this.section = new Section(type, sectionBox);
    }

  }

  public InlineSubreportMarker[] endSection()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    if (isIgnoreContent())
    {
      return EMPTY_ARRAY;
    }

    final Section section = this.section;
    this.section = null;
    final SectionRenderBox sectionBox = section.getSectionBox();
    sectionBox.close();

    final int sectionType = section.getType();
    switch (sectionType)
    {
      case Renderer.TYPE_NORMALFLOW:
      {
        final RenderNode firstChild = sectionBox.getFirstChild();
        if (firstChild == null)
        {
          // the whole section is empty; therefore we can ignore it.
          sectionBox.makeReusable();
          sectionBoxes[sectionType] = sectionBox;
          return normalFlowLayoutBuilder.endSection(getPageBox(), sectionBox);
        }

        final int type = firstChild.getNodeType();
        if (sectionBox.getLastChild() == firstChild &&
            type == LayoutNodeTypes.TYPE_BOX_INLINE_PROGRESS_MARKER ||
            type == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER)
        {
          if (mergeSection(sectionBox))
          {
            sectionBox.makeReusable();
            sectionBoxes[sectionType] = sectionBox;
            return normalFlowLayoutBuilder.endSection(getPageBox(), sectionBox);
          }
        }

        addBox(sectionBox);
        markDirty();
        return normalFlowLayoutBuilder.endSection(getPageBox(), sectionBox);
      }
      case Renderer.TYPE_FOOTER:
      {
        final BlockRenderBox footerArea = pageBox.getFooterArea();
        if (sectionBox.getFirstChild() == sectionBox.getLastChild() &&
            isEmptyOrMarker(footerArea.getFirstChild()) &&
            isEmptyOrMarker(sectionBox.getFirstChild()))
        {
          // both boxes are empty, so we can ignore it ...
          return footerLayoutBuilder.endSection(footerArea, sectionBox);
        }

        markDirty();
        return footerLayoutBuilder.endSection(footerArea, sectionBox);
      }
      case Renderer.TYPE_REPEATED_FOOTER:
      {
        final BlockRenderBox footerArea = pageBox.getRepeatFooterArea();
        if (sectionBox.getFirstChild() == sectionBox.getLastChild() &&
            isEmptyOrMarker(footerArea.getFirstChild()) &&
            isEmptyOrMarker(sectionBox.getFirstChild()))
        {
          // both boxes are empty, so we can ignore it ...
          return repeatedFooterLayoutBuilder.endSection(footerArea, sectionBox);
        }

        markDirty();
        return repeatedFooterLayoutBuilder.endSection(footerArea, sectionBox);
      }
      case Renderer.TYPE_HEADER:
      {
        final BlockRenderBox headerArea = pageBox.getHeaderArea();
        if (sectionBox.getFirstChild() == sectionBox.getLastChild() &&
            isEmptyOrMarker(headerArea.getFirstChild()) &&
            isEmptyOrMarker(sectionBox.getFirstChild()))
        {
          // both boxes are empty, so we can ignore it ...
          return headerLayoutBuilder.endSection(headerArea, sectionBox);
        }
        markDirty();
        return headerLayoutBuilder.endSection(headerArea, sectionBox);
      }
      case Renderer.TYPE_WATERMARK:
      {
        // ignore for now.
        final WatermarkAreaBox watermarkArea = pageBox.getWatermarkArea();
        if (sectionBox.getFirstChild() == sectionBox.getLastChild() &&
            isEmptyOrMarker(watermarkArea.getFirstChild()) &&
            isEmptyOrMarker(sectionBox.getFirstChild()))
        {
          // both boxes are empty, so we can ignore it ...
          return watermarkLayoutBuilder.endSection(watermarkArea, sectionBox);
        }
        markDirty();
        return watermarkLayoutBuilder.endSection(watermarkArea, sectionBox);
      }
      default:
        throw new IllegalStateException("Type " + sectionType + " not recognized");
    }
  }

  private boolean isEmptyOrMarker(final RenderNode box)
  {
    if (box == null)
    {
      return true;
    }
    final int type = box.getNodeType();
    if (type == LayoutNodeTypes.TYPE_BOX_INLINE_PROGRESS_MARKER ||
        type == LayoutNodeTypes.TYPE_BOX_PROGRESS_MARKER)
    {
      return true;
    }
    return false;
  }

  private boolean isIgnoreContent()
  {
    return groupStack.isEmpty() == false &&
        groupStack.peek() instanceof AbstractRenderer.IgnoredContentIndicator;
  }

  public void endGroupBody()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    final Object o = groupStack.pop();
    if (o instanceof IgnoredContentIndicator)
    {
      return;
    }

    final GroupSection groupSection = (GroupSection) o;
    if (groupSection.getChildCount() == 0)
    {
      final RenderBox groupBox = groupSection.getGroupBox();
      groupBox.getParent().remove(groupBox);
    }
    groupSection.close();
  }

  public void endGroup()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    final Object o = groupStack.pop();
    if (o instanceof IgnoredContentIndicator)
    {
      return;
    }
    final GroupSection groupSection = (GroupSection) o;
    groupSection.close();
  }

  protected LogicalPageBox getPageBox()
  {
    return pageBox;
  }

  public void endSubReport()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    final Object o = groupStack.pop();
    if (o instanceof IgnoredContentIndicator)
    {
      return;
    }

    final GroupSection groupSection = (GroupSection) o;
    groupSection.close();
  }

  public void endReport()
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    final GroupSection groupSection = (GroupSection) groupStack.pop();
    groupSection.close();

    pageBox.close();
    markDirty();
  }

  public void addEmptyRootLevelBand(final ReportStateKey stateKey)
      throws ReportProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    if (isIgnoreContent())
    {
      return;
    }

    final RenderBox sectionBox = section.getSectionBox();
    final int type = section.getType();
    if (type == TYPE_FOOTER)
    {
      footerLayoutBuilder.addEmptyRootLevelBand(sectionBox, stateKey);
    }
    else if (type == TYPE_REPEATED_FOOTER)
    {
      repeatedFooterLayoutBuilder.addEmptyRootLevelBand(sectionBox, stateKey);
    }
    else if (type == TYPE_HEADER)
    {
      headerLayoutBuilder.addEmptyRootLevelBand(sectionBox, stateKey);
    }
    else if (type == TYPE_WATERMARK)
    {
      watermarkLayoutBuilder.addEmptyRootLevelBand(sectionBox, stateKey);
    }
    else
    {
      normalFlowLayoutBuilder.addEmptyRootLevelBand(sectionBox, stateKey);
    }
  }

  public void add(final Band band, final ExpressionRuntime runtime, final ReportStateKey stateKey)
      throws ReportProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    if (isIgnoreContent())
    {
      return;
    }

    final RenderBox sectionBox = section.getSectionBox();
    final int type = section.getType();
    if (type == TYPE_FOOTER)
    {
      footerLayoutBuilder.add(sectionBox, band, runtime, stateKey);
    }
    else if (type == TYPE_REPEATED_FOOTER)
    {
      repeatedFooterLayoutBuilder.add(sectionBox, band, runtime, stateKey);
    }
    else if (type == TYPE_HEADER)
    {
      headerLayoutBuilder.add(sectionBox, band, runtime, stateKey);
    }
    else if (type == TYPE_WATERMARK)
    {
      watermarkLayoutBuilder.add(sectionBox, band, runtime, stateKey);
    }
    else
    {
      normalFlowLayoutBuilder.add(sectionBox, band, runtime, stateKey);
    }
  }

  public void add(final RenderBox box)
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    if (isIgnoreContent())
    {
      return;
    }

    if (box.isOpen())
    {
      throw new IllegalStateException();
    }
    final RenderBox sectionBox = section.getSectionBox();
    sectionBox.addChild(box);
  }

  public LayoutResult validatePages()
      throws ContentProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    // Pagination time without dirty-flag: 875067
    if (pageBox == null)
    {
      // StartReport has not been called yet ..
      lastValidateResult = LayoutResult.LAYOUT_UNVALIDATABLE;
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    if (!dirty && lastValidateResult != null)
    {
      return lastValidateResult;
    }

    setLastStateKey(null);
    setPagebreaks(0);
    if (validateModelStep.isLayoutable(pageBox) == false)
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Content-Ref# " + pageBox.getContentRefCount());
      }
      lastValidateResult = LayoutResult.LAYOUT_UNVALIDATABLE;
      return LayoutResult.LAYOUT_UNVALIDATABLE;
    }

    // These structural processors will skip old nodes. These beasts cannot be cached otherwise.
    staticPropertiesStep.compute(pageBox);
    paragraphLineBreakStep.compute(pageBox);

    minorAxisLayoutStep.compute(pageBox);
    majorAxisLayoutStep.compute(pageBox);
    canvasMajorAxisLayoutStep.compute(pageBox);
    revalidateAllAxisLayoutStep.compute(pageBox);

    applyCachedValuesStep.compute(pageBox);
    if (isPageFinished())
    {
      lastValidateResult = LayoutResult.LAYOUT_PAGEBREAK;
      return LayoutResult.LAYOUT_PAGEBREAK;
    }
    else
    {
      lastValidateResult = LayoutResult.LAYOUT_NO_PAGEBREAK;
      return LayoutResult.LAYOUT_NO_PAGEBREAK;
    }
  }

  protected void clearDirty()
  {
    dirty = false;
  }

  protected abstract boolean isPageFinished();

  public void processIncrementalUpdate(final boolean performOutput) throws ContentProcessingException
  {
//    dirty = false;
  }

  public boolean processPage(final LayoutPagebreakHandler handler,
                             final Object commitMarker,
                             final boolean performOutput) throws ContentProcessingException
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }
    // Pagination time without dirty-flag: 875067
    if (pageBox == null)
    {
      // StartReport has not been called yet ..
//      Log.debug ("PageBox null");
      return false;
    }

    if (dirty == false)
    {
//      Log.debug ("Not dirty");
      return false;
    }

    setLastStateKey(null);
    setPagebreaks(0);
    if (validateModelStep.isLayoutable(pageBox) == false)
    {
//      Log.debug ("Not layoutable");
      return false;
    }

    // processes the current page
    boolean repeat = true;
    while (repeat)
    {
      if (handler != null)
      {
        // make sure we generate an up-to-date page-footer. This also implies that there
        // are more page-finished than page-started events generated during the report processing.
        handler.pageFinished();
      }

      if (outputProcessor.getMetaData().isFeatureSupported(OutputProcessorFeature.PAGEBREAKS))
      {
        createRollbackInformation();
        applyRollbackInformation();
        performParanoidModelCheck();
      }

      staticPropertiesStep.compute(pageBox);
      paragraphLineBreakStep.compute(pageBox);

      minorAxisLayoutStep.compute(pageBox);
      majorAxisLayoutStep.compute(pageBox);
      canvasMajorAxisLayoutStep.compute(pageBox);
      revalidateAllAxisLayoutStep.compute(pageBox);

      applyCachedValuesStep.compute(pageBox);

      repeat = performPagination(handler, performOutput);
    }
    clearDirty();
    return (pagebreaks > 0);
  }

  protected abstract boolean performPagination(LayoutPagebreakHandler handler,
                                               final boolean performOutput)
      throws ContentProcessingException;

  /**
   * A hook to allow easier debugging.
   *
   * @param pageBox the current page box.
   * @noinspection NoopMethodInAbstractClass
   */
  protected void debugPrint(final LogicalPageBox pageBox)
  {

  }

  public ReportStateKey getLastStateKey()
  {
    return lastStateKey;
  }

  public void setLastStateKey(final ReportStateKey lastStateKey)
  {
    this.lastStateKey = lastStateKey;
  }

  protected void setPagebreaks(final int pagebreaks)
  {
    this.pagebreaks = pagebreaks;
  }

  public int getPagebreaks()
  {
    return pagebreaks;
  }

  public boolean isOpen()
  {
    if (pageBox == null)
    {
      return false;
    }
    return pageBox.isOpen();
  }

  public boolean isValid()
  {
    return readOnly == false;
  }

  public Renderer deriveForStorage()
  {
    try
    {
      final AbstractRenderer renderer = (AbstractRenderer) clone();
      renderer.readOnly = false;
      renderer.sectionBoxes = new SectionRenderBox[5];
      if (pageBox != null)
      {
        renderer.pageBox = (LogicalPageBox) pageBox.derive(true);
        if (section != null)
        {
          final RenderNode nodeById = renderer.pageBox.findNodeById(section.getSectionBox().getInstanceId());
          renderer.section = new Section(section.getType(), (SectionRenderBox) nodeById);
        }
      }

      final int stackSize = groupStack.size();
      final Object[] tempList = new Object[stackSize];


      renderer.groupStack = (FastStack) groupStack.clone();
      final int tempListLength = tempList.length;
      for (int i = 0; i < tempListLength; i++)
      {
        tempList[i] = renderer.groupStack.pop();
      }

      // the stack is empty now ..
      // lets fill it again ..
      for (int i = tempListLength - 1; i >= 0; i--)
      {
        if (tempList[i] instanceof IgnoredContentIndicator)
        {
          renderer.groupStack.push(tempList[i]);
          continue;
        }

        final GroupSection section = (GroupSection) tempList[i];

        final RenderBox groupBox = section.getGroupBox();
        final InstanceID groupBoxInstanceId = groupBox.getInstanceId();
        final RenderBox groupBoxClone = (RenderBox) renderer.pageBox.findNodeById(groupBoxInstanceId);
        if (groupBoxClone == null)
        {
          throw new IllegalStateException("The pagebox did no longer contain the stored node.");
        }
        if (groupBoxClone == groupBox)
        {
          throw new IllegalStateException("Thought you wanted a groupBoxClone");
        }

        final RenderBox addBox = section.getAddBox();
        final RenderBox addBoxClone;
        if (addBox == groupBox)
        {
          addBoxClone = groupBoxClone;
        }
        else
        {
          final InstanceID addBoxInstanceId = addBox.getInstanceId();
          addBoxClone = (RenderBox) renderer.pageBox.findNodeById(addBoxInstanceId);
          if (addBoxClone == null)
          {
            throw new IllegalStateException("The pagebox did no longer contain the stored node.");
          }
          if (addBoxClone == addBox)
          {
            throw new IllegalStateException("Thought you wanted a groupBoxClone");
          }
        }
        renderer.groupStack.push(new GroupSection(groupBoxClone, addBoxClone,
            section.getChildCount(), section.getNextBoxStart(), section.getStyleSheet()));
      }
      return renderer;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new InvalidReportStateException("Failed to derive Renderer", cne);
    }
  }

  public Renderer deriveForPagebreak()
  {
    try
    {
      final AbstractRenderer renderer = (AbstractRenderer) clone();
      renderer.readOnly = true;
      if (pageBox != null)
      {
        if (section != null)
        {
          renderer.section = new Section(section.getType(), section.getSectionBox());
        }
      }

      final int stackSize = groupStack.size();
      final Object[] tempList = new Object[stackSize];
      renderer.groupStack = (FastStack) groupStack.clone();
      final int tempListLength = tempList.length;
      for (int i = 0; i < tempListLength; i++)
      {
        tempList[i] = renderer.groupStack.pop();
      }

      // the stack is empty now ..
      // lets fill it again ..
      for (int i = tempListLength - 1; i >= 0; i--)
      {
        if (tempList[i] instanceof IgnoredContentIndicator)
        {
          renderer.groupStack.push(tempList[i]);
          continue;
        }

        final GroupSection section = (GroupSection) tempList[i];

        final RenderBox groupBox = section.getGroupBox();
        final RenderBox addBox = section.getAddBox();

//        validate(addBox, groupBox);
        renderer.groupStack.push(new GroupSection(groupBox, addBox,
            section.getChildCount(), section.getNextBoxStart(), section.getStyleSheet()));
      }
      return renderer;
    }
    catch (CloneNotSupportedException cne)
    {
      throw new InvalidReportStateException("Failed to derive Renderer", cne);
    }
  }

  public void performParanoidModelCheck()
  {
    if (paranoidChecks)
    {
      final int stackSize = groupStack.size();

      // the stack is empty now ..
      // lets fill it again ..
      for (int i = 0; i < stackSize; i++)
      {
        final Object o = groupStack.get(i);
        if (o instanceof IgnoredContentIndicator)
        {
          continue;
        }

        final GroupSection section = (GroupSection) o;

        final RenderBox groupBox = section.getGroupBox();
        final RenderBox addBox = section.getAddBox();

        // step 1: Check whether addbox is a child of groupbox
        RenderBox c = addBox;
        while (c != groupBox)
        {
          c = c.getParent();
          if (c == null)
          {
            throw new IllegalStateException("Failed to locate parent");
          }
        }

        c = addBox;
        while (c != null)
        {
          if (c.isOpen() == false)
          {
            throw new IllegalStateException(
                "Add-Box is not open: " + c.isMarkedOpen() + ' ' + c.isMarkedSeen() + ' ' + c);
          }
          c = c.getParent();
        }
      }
    }
  }

  public Object clone() throws CloneNotSupportedException
  {
    return super.clone();
  }

  public void addPagebreak(final ReportStateKey stateKey)
  {
    if (readOnly)
    {
      throw new IllegalStateException();
    }

    if (isIgnoreContent())
    {
      return;
    }

    if (this.manualBreakBoxStyle == null)
    {
      final ManualBreakIndicatorStyleSheet mbis =
          new ManualBreakIndicatorStyleSheet(BandDefaultStyleSheet.getBandDefaultStyle());
      this.manualBreakBoxStyle = new SimpleStyleSheet(mbis);
    }

    final RenderBox sectionBox = new BreakMarkerRenderBox
        (manualBreakBoxStyle, new InstanceID(), BoxDefinition.EMPTY, AutoLayoutBoxType.INSTANCE,
            ReportAttributeMap.EMPTY_MAP, stateKey);
    sectionBox.setName("pagebreak");
    sectionBox.close();
    addBox(sectionBox);
  }

  public boolean clearPendingPageStart(final LayoutPagebreakHandler layoutPagebreakHandler)
  {
    // intentionally left empty.
    return false;
  }

  public boolean isCurrentPageEmpty()
  {
    return false;
  }

  public boolean isPageStartPending()
  {
    return false;
  }

  public boolean isDirty()
  {
    return dirty;
  }

  public void createRollbackInformation()
  {
    if (pageBox != null)
    {
      commitStep.compute(pageBox);
    }
  }

  public void applyRollbackInformation()
  {
    if (pageBox != null)
    {
      applyCommitStep.compute(pageBox);
    }
  }

  public void validateAfterCommit()
  {
    if (paranoidChecks)
    {
      final int stackSize = groupStack.size();
      for (int i = 0; i < stackSize; i++)
      {
        final Object o = groupStack.get(i);
        if (o instanceof IgnoredContentIndicator)
        {
          continue;
        }
        final GroupSection section = (GroupSection) o;

        final RenderBox groupBox = section.getGroupBox();
        final RenderBox addBox = section.getAddBox();
        if (addBox.getParent() == null)
        {
          throw new IllegalStateException("No longer there");
        }
        if (groupBox.isMarkedSeen() == false)
        {
          throw new IllegalStateException("No seen-marker at " + groupBox);
        }
        if (addBox.isMarkedSeen() == false)
        {
          throw new IllegalStateException("No seen-marker at add-box " + addBox);
        }
        if (addBox.isMarkedOpen() == false)
        {
          throw new IllegalStateException("No open-marker at " + addBox);
        }
      }
    }
  }

  public void rollback()
  {
    readOnly = false;
    if (pageBox != null)
    {
      rollbackStep.compute(pageBox);
      validateAfterCommit();
    }
  }


  public void applyAutoCommit()
  {
    if (pageBox != null)
    {
      applyAutoCommitStep.compute(pageBox);
    }
  }

  public LayoutBuilder createBufferedLayoutBuilder()
  {
    return normalFlowLayoutBuilder.createBufferedLayoutBuilder();
  }

  public boolean isPendingPageHack()
  {
    return false;
  }

  protected void markDirty()
  {
    dirty = true;
    lastValidateResult = null;
  }

  public void print()
  {
//    ModelPrinter.print(pageBox);
  }

  public void newPageStarted()
  {
    pageBox.getFooterArea().clear();
    pageBox.getRepeatFooterArea().clear();
    pageBox.getHeaderArea().clear();
    pageBox.getWatermarkArea().clear();
  }
}
