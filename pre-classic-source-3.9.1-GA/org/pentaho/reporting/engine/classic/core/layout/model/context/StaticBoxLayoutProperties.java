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

package org.pentaho.reporting.engine.classic.core.layout.model.context;

import java.io.Serializable;

import org.pentaho.reporting.engine.classic.core.layout.text.ExtendedBaselineInfo;
import org.pentaho.reporting.libraries.base.util.DebugLog;


/**
 * A static properties collection. That one is static; once computed it does not change anymore. It does not (under no
 * thinkable circumstances) depend on the given content. It may depend on static content of the parent.
 * <p/>
 * A box typically has two sets of margins. The first set is the declared margin set - it simply expresses the user's
 * definitions. The second set is the effective margin set, it is based on the context of the element in the document
 * tree and denotes the distance between the nodes edge and any oposite edge.
 *
 * @author Thomas Morgner
 */
public final class StaticBoxLayoutProperties implements Serializable
{
  private long marginLeft;
  private long marginRight;
  private long marginTop;
  private long marginBottom;
  private long borderLeft;
  private long borderRight;
  private long borderTop;
  private long borderBottom;

  private int dominantBaseline;
  private ExtendedBaselineInfo nominalBaselineInfo;
  private int widows;
  private int orphans;
  private boolean avoidPagebreakInside;
  private boolean preserveSpace;
  private boolean breakAfter;
  private String fontFamily;
  private long blockContextWidth;
  private long spaceWidth;
  private boolean overflowX;
  private boolean overflowY;
  private boolean invisibleConsumesSpace;
  private boolean visible;
  private boolean placeholderBox;
  private long computedWidth;
  
  public StaticBoxLayoutProperties()
  {
  }

  public long getComputedWidth()
  {
    return computedWidth;
  }

  /**
   * Defines the computed width. The computed-width is a static-property and is always specified as border-box size.
   *
   * @param computedWidth
   */
  public void setComputedWidth(final long computedWidth)
  {
    if (computedWidth < 0)
    {
      throw new IllegalArgumentException();
    }
    this.computedWidth = computedWidth;
  }

  public long getSpaceWidth()
  {
    return spaceWidth;
  }

  public void setSpaceWidth(final long spaceWidth)
  {
    this.spaceWidth = spaceWidth;
  }

  public long getMarginLeft()
  {
    return marginLeft;
  }

  public void setMarginLeft(final long marginLeft)
  {
    this.marginLeft = marginLeft;
  }

  public long getMarginRight()
  {
    return marginRight;
  }

  public void setMarginRight(final long marginRight)
  {
    this.marginRight = marginRight;
  }

  public long getMarginTop()
  {
    return marginTop;
  }

  public void setMarginTop(final long marginTop)
  {
    this.marginTop = marginTop;
  }

  public long getMarginBottom()
  {
    return marginBottom;
  }

  public void setMarginBottom(final long marginBottom)
  {
    this.marginBottom = marginBottom;
  }

  public long getBorderLeft()
  {
    return borderLeft;
  }

  public void setBorderLeft(final long borderLeft)
  {
    this.borderLeft = borderLeft;
  }

  public long getBorderRight()
  {
    return borderRight;
  }

  public void setBorderRight(final long borderRight)
  {
    this.borderRight = borderRight;
  }

  public long getBorderTop()
  {
    return borderTop;
  }

  public void setBorderTop(final long borderTop)
  {
    this.borderTop = borderTop;
  }

  public long getBorderBottom()
  {
    return borderBottom;
  }

  public void setBorderBottom(final long borderBottom)
  {
    this.borderBottom = borderBottom;
  }

  public int getDominantBaseline()
  {
    return dominantBaseline;
  }

  public void setDominantBaseline(final int dominantBaseline)
  {
    this.dominantBaseline = dominantBaseline;
  }

  public ExtendedBaselineInfo getNominalBaselineInfo()
  {
    return nominalBaselineInfo;
  }

  public void setNominalBaselineInfo(final ExtendedBaselineInfo nominalBaselineInfo)
  {
    if (nominalBaselineInfo == null)
    {
      throw new NullPointerException();
    }
    this.nominalBaselineInfo = nominalBaselineInfo;
  }

  public String getFontFamily()
  {
    return fontFamily;
  }

  public void setFontFamily(final String fontFamily)
  {
    this.fontFamily = fontFamily;
  }

  public int getWidows()
  {
    return widows;
  }

  public void setWidows(final int widows)
  {
    this.widows = widows;
  }

  public int getOrphans()
  {
    return orphans;
  }

  public void setOrphans(final int orphans)
  {
    this.orphans = orphans;
  }

  public boolean isAvoidPagebreakInside()
  {
    return avoidPagebreakInside;
  }

  public void setAvoidPagebreakInside(final boolean avoidPagebreakInside)
  {
    this.avoidPagebreakInside = avoidPagebreakInside;
  }

  public boolean isPreserveSpace()
  {
    return preserveSpace;
  }

  public void setPreserveSpace(final boolean preserveSpace)
  {
    this.preserveSpace = preserveSpace;
  }

  public boolean isBreakAfter()
  {
    return breakAfter;
  }

  public void setBreakAfter(final boolean breakAfter)
  {
    this.breakAfter = breakAfter;
  }

  /**
   * The block-context width is the computed width of the containing block of this element. Unlike the computed-width,
   * this value is non-zero for elements contained in a inline- or row-context.
   *
   * @return the block-context width
   */
  public long getBlockContextWidth()
  {
    return blockContextWidth;
  }

  /**
   * The block-context width is the computed width of the containing block of this element. Unlike the computed-width,
   * this value is non-zero for elements contained in a inline- or row-context.
   *
   * @param boxContextWidth the block-context width
   */
  public void setBlockContextWidth(final long boxContextWidth)
  {
    this.blockContextWidth = boxContextWidth;
  }

  public boolean isOverflowX()
  {
    return overflowX;
  }

  public void setOverflowX(final boolean overflowX)
  {
    this.overflowX = overflowX;
  }

  public boolean isOverflowY()
  {
    return overflowY;
  }

  public void setOverflowY(final boolean overflowY)
  {
    this.overflowY = overflowY;
  }

  public boolean isInvisibleConsumesSpace()
  {
    return invisibleConsumesSpace;
  }

  public void setInvisibleConsumesSpace(final boolean invisibleConsumesSpace)
  {
    this.invisibleConsumesSpace = invisibleConsumesSpace;
  }

  public boolean isVisible()
  {
    return visible;
  }

  public void setVisible(final boolean visible)
  {
    this.visible = visible;
  }

  public boolean isPlaceholderBox()
  {
    return placeholderBox;
  }

  public void setPlaceholderBox(final boolean placeholderBox)
  {
    this.placeholderBox = placeholderBox;
  }

  public String toString()
  {
    return "StaticBoxLayoutProperties{" +
//        "blockContextWidth=" + blockContextWidth +
        "marginLeft=" + marginLeft +
        ", marginRight=" + marginRight +
        ", marginTop=" + marginTop +
        ", marginBottom=" + marginBottom +
        ", borderLeft=" + borderLeft +
        ", borderRight=" + borderRight +
        ", borderTop=" + borderTop +
        ", borderBottom=" + borderBottom +
        ", dominantBaseline=" + dominantBaseline +
     //   ", nominalBaselineInfo=" + nominalBaselineInfo +
        ", widows=" + widows +
        ", orphans=" + orphans +
        ", avoidPagebreakInside=" + avoidPagebreakInside +
        ", preserveSpace=" + preserveSpace +
        ", visible=" + visible +
        ", placeholderBox=" + placeholderBox +
        ", invisibleConsumesSpace=" + invisibleConsumesSpace +
        '}';
  }
}
