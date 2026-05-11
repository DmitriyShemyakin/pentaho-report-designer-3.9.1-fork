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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

public class FooterLayoutBuilder implements LayoutBuilder
{
  private static final Log logger = LogFactory.getLog(FooterLayoutBuilder.class);

  private DefaultLayoutBuilder backend;
  private ArrayList<RenderNode> slots;
  private int slotCounter;

  public FooterLayoutBuilder(final OutputProcessorMetaData metaData)
  {
    this.backend = new DefaultLayoutBuilder(metaData);
    this.backend.setCollapseProgressMarker(false);
    this.slots = new ArrayList<RenderNode>();
  }

  public void startSection(final RenderBox pageArea, final boolean limitedSubReports)
  {
    slots.clear();
    slotCounter = 0;
    // check what slots are filled and update the list
    final RenderNode firstChild = pageArea.getFirstChild();
    if (firstChild instanceof RenderBox)
    {
      final RenderBox slottedContent = (RenderBox) firstChild;
      RenderNode box = slottedContent.getFirstChild();
      if (logger.isDebugEnabled())
      {
        logger.debug("Start Section: " + pageArea);
        logger.debug("      Section: " + slottedContent);
        logger.debug("      Section: " + box);
      }
      
      boolean sticky = false;
      while (box != null)
      {
        if (box.getStyleSheet().getBooleanStyleProperty(BandStyleKeys.STICKY))
        {
          sticky = true;
        }
        if (sticky)
        {
          if (logger.isDebugEnabled())
          {
            logger.debug("Added Slot[]: " + box);
            logger.debug("      Slot[]: " + box.getElementType());
            logger.debug("      Slot[]: " + box.getStateKey());
          }
          slots.add(box);
        }
        box = box.getNext();

      }
    }
    else
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Added Reverse Section: " + slotCounter + " " + slots.size() + " " + firstChild);
      }
    }

    backend.startSection(pageArea, limitedSubReports);
  }

  public void add(final RenderBox parent,
                  final Band band,
                  final ExpressionRuntime runtime,
                  final ReportStateKey stateKey) throws ReportProcessingException
  {
    backend.add(parent, band, runtime, stateKey);
    slotCounter += 1;
  }

  public void addEmptyRootLevelBand(final RenderBox parent,
                                    final ReportStateKey stateKey) throws ReportProcessingException
  {
    backend.addEmptyRootLevelBand(parent, stateKey);
    slotCounter += 1;
  }

  public InlineSubreportMarker[] endSection(final RenderBox pageArea, RenderBox sectionBox)
  {
    final InlineSubreportMarker[] retval = backend.endSection(pageArea, sectionBox);

    // check which slots have not been activated and add them ..
    if (logger.isDebugEnabled())
    {
      logger.debug("Slot counter: " + slotCounter + " " + slots.size());
      for (int i = 0; i < slots.size(); i++)
      {
        logger.debug("Slots[" + i + "]: " + slots.get(i));
      }
    }
    // this is not correct ... we should insert the new band before the old one ..
    if (slotCounter < slots.size())
    {
      logger.debug ("Rebuilding footer");
      final RenderBox newSectionBox = (RenderBox) sectionBox.derive(false);
      // first insert the saved ones ...
      for (int i = slots.size() - slotCounter - 1; i >= 0; i--)
      {
        final RenderNode node = slots.get(i);
        final RenderNode derived = node.derive(true);

        if (logger.isDebugEnabled())
        {
          logger.debug("Rescued[" + i + "]: " + slots.get(i));
        }
        newSectionBox.addGeneratedChild(derived);
      }
      // and then add the newly generated ones ..
      RenderNode child = sectionBox.getFirstChild();
      while (child != null)
      {
        final RenderNode next = child.getNext();
        sectionBox.remove(child);
        newSectionBox.addGeneratedChild(child);
        if (logger.isDebugEnabled())
        {
          logger.debug("New[" + "]: " + child);
        }
        child = next;
      }

      sectionBox = newSectionBox;
    }

    if (logger.isDebugEnabled())
    {
      logger.debug("CLEAR footer for reslotting!");
    }
    pageArea.clear();
    pageArea.addChild(sectionBox);
    return retval;
  }
}
