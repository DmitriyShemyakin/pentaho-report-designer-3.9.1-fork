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

package org.pentaho.reporting.engine.classic.core.layout.model;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictBounds;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.DebugLog;

/**
 * Creation-Date: 03.04.2007, 13:16:06
 *
 * @author Thomas Morgner
 */
public abstract class RenderNode implements Cloneable
{
  private static Boolean paranoidModelChecks;

  protected static boolean isParanoidChecks()
  {
    if (paranoidModelChecks == null)
    {
      final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      if ("true".equals(configuration.getConfigProperty
          ("org.pentaho.reporting.engine.classic.core.layout.ParanoidChecks")))
      {
        paranoidModelChecks = Boolean.TRUE;
      }
      else
      {
        paranoidModelChecks = Boolean.FALSE;
      }
    }
    return paranoidModelChecks.booleanValue();    
  }

  public static final int HORIZONTAL_AXIS = 0;
  public static final int VERTICAL_AXIS = 1;

  public static final int CACHE_CLEAN = 0;
  public static final int CACHE_DIRTY = 1;
  public static final int CACHE_DEEP_DIRTY = 2;

  private long changeTracker;

  private RenderBox parentNode;
  private RenderNode nextNode;
  private RenderNode prevNode;

  private boolean frozen;
  private boolean hibernated;

  private long minimumChunkWidth;
  private long maximumBoxWidth;
  private NodeLayoutProperties nodeLayoutProperties;

  private long cachedAge;
  private long cachedParentWidth;

  private long cachedX;
  private long cachedY;
  private long cachedWidth;
  private long cachedHeight;

  private long x;
  private long y;
  private long width;
  private long height;

  private int cacheState;
  private boolean finishedPaginate;
  private boolean finishedTable;
  private boolean virtualNode;
  private ReportAttributeMap attributes;
  private ElementType elementType;

  protected RenderNode(final int majorAxis,
                       final int minorAxis,
                       final StyleSheet styleSheet,
                       final InstanceID instanceID,
                       final ElementType elementType,
                       final ReportAttributeMap attributes)
  {
    this(elementType, attributes, new NodeLayoutProperties(majorAxis, minorAxis, styleSheet, instanceID));
  }

  protected RenderNode(final ElementType elementType,
                       final ReportAttributeMap attributes,
                       final NodeLayoutProperties nodeLayoutProperties)
  {
    if (attributes == null)
    {
      throw new NullPointerException();
    }
    if (nodeLayoutProperties == null)
    {
      throw new NullPointerException();
    }
    if (elementType == null)
    {
      throw new NullPointerException();
    }

    this.elementType = elementType;
    this.attributes = attributes;
    this.nodeLayoutProperties = nodeLayoutProperties;
    this.cacheState = RenderNode.CACHE_DEEP_DIRTY;
  }

  public ElementType getElementType()
  {
    return elementType;
  }

  public ReportAttributeMap getAttributes()
  {
    return attributes;
  }

  /**
   * The content-ref-count counts inline-subreports.
   */
  public int getContentRefCount()
  {
    return 0;
  }

  public boolean isSizeSpecifiesBorderBox()
  {
    return true;
  }

  public abstract int getNodeType();

  public int getMinorAxis()
  {
    return this.nodeLayoutProperties.getMinorAxis();
  }

  public int getMajorAxis()
  {
    return this.nodeLayoutProperties.getMajorAxis();
  }

  public final NodeLayoutProperties getNodeLayoutProperties()
  {
    return nodeLayoutProperties;
  }

  public long getComputedWidth()
  {
     return 0;
  }

  public final long getX()
  {
    return x;
  }

  public final void setX(final long x)
  {
    this.x = x;
    //this.updateChangeTracker();
  }

  public final long getY()
  {
    return y;
  }

  public final void shift(final long amount)
  {
    this.y += amount;
    this.updateCacheState(RenderNode.CACHE_DEEP_DIRTY);
  }

  public final void setY(final long y)
  {
    this.y = y;
    this.updateCacheState(RenderNode.CACHE_DEEP_DIRTY);
  }

  protected final void updateCacheState(final int state)
  {
    switch (state)
    {
//      case CACHE_CLEAN:
//        this.cacheState = CACHE_CLEAN;
//        break;
      case RenderNode.CACHE_DIRTY:
        if (cacheState == RenderNode.CACHE_CLEAN)
        {
          this.cacheState = RenderNode.CACHE_DIRTY;
          final RenderBox parent = getParent();
          if (parent != null)
          {
            parent.updateCacheState(RenderNode.CACHE_DIRTY);
          }
        }
        // if cache-state either dirty or deep-dirty, no need to update.
        break;
      case RenderNode.CACHE_DEEP_DIRTY:
        if (cacheState == RenderNode.CACHE_CLEAN)
        {
          this.cacheState = RenderNode.CACHE_DEEP_DIRTY;
          final RenderBox parent = getParent();
          if (parent != null)
          {
            parent.updateCacheState(RenderNode.CACHE_DIRTY);
          }
        }
        break;
      default:
        throw new IllegalArgumentException();
    }
  }

  public final long getWidth()
  {
    return width;
  }

  public final void setWidth(final long width)
  {
    if (width < 0)
    {
      throw new IndexOutOfBoundsException("Width cannot be negative");
    }

    this.width = width;
    this.updateCacheState(RenderNode.CACHE_DIRTY);
    //this.updateChangeTracker();
  }

  public final long getHeight()
  {
    return height;
  }

  public final void setHeight(final long height)
  {
    if (height < 0)
    {
      throw new IndexOutOfBoundsException("Height cannot be negative");
    }
    this.height = height;
    this.updateCacheState(RenderNode.CACHE_DIRTY);
    //this.updateChangeTracker();
  }

  public final StyleSheet getStyleSheet()
  {
    return nodeLayoutProperties.getStyleSheet();
  }

  public InstanceID getInstanceId()
  {
    return nodeLayoutProperties.getInstanceId();
  }

  protected void updateChangeTracker()
  {
    changeTracker += 1;
    if (cacheState == RenderNode.CACHE_CLEAN)
    {
      cacheState = RenderNode.CACHE_DIRTY;
    }
    final RenderBox parent = getParent();
    if (parent != null)
    {
      parent.updateChangeTracker();
    }
  }

  public final long getChangeTracker()
  {
    return changeTracker;
  }

  public final RenderBox getParent()
  {
    return parentNode;
  }

  protected final void setParent(final RenderBox parent)
  {
    if (isParanoidChecks())
    {
      final RenderNode prev = getPrev();
      if (parent != null && prev == parent)
      {
        throw new IllegalStateException("Assertation failed: Cannot have a parent that is the same as a silbling.");
      }
      if (parent == null)
      {
        final RenderNode next = getNext();
        if (next != null)
        {
          throw new NullPointerException();
        }
        if (prev != null)
        {
          throw new NullPointerException();
        }
      }
    }
    // Object oldParent = this.parent;
    this.parentNode = parent;
  }

  public final RenderNode getPrev()
  {
    return prevNode;
  }

  protected final void setPrevUnchecked(final RenderNode prev)
  {
    this.prevNode = prev;
  }

  protected final void setPrev(final RenderNode prev)
  {
    this.prevNode = prev;
    if (isParanoidChecks() && prev != null)
    {
      final RenderBox parent = getParent();
      if (prev == parent)
      {
        throw new IllegalStateException();
      }

      if (parent != null)
      {
        if (parent.getFirstChild() == this)
        {
          throw new NullPointerException("Cannot have a prev node if the parent has me as first child.");
        }
      }
    }
  }

  public final RenderNode getNext()
  {
    return nextNode;
  }

  protected final void setNextUnchecked(final RenderNode next)
  {
    this.nextNode = next;
  }
  
  protected final void setNext(final RenderNode next)
  {
    this.nextNode = next;
    if (isParanoidChecks() && next != null)
    {
      final RenderBox parent = getParent();
      if (next == parent)
      {
        throw new IllegalStateException();
      }

      if (parent != null)
      {
        if (parent.getLastChild() == this)
        {
          throw new NullPointerException("Cannot have a next-node, if the parent has me as last child.");
        }
      }
    }
  }

  public LogicalPageBox getLogicalPage()
  {
    RenderNode parent = this;
    while (parent != null)
    {
      if ((parent.getNodeType() & LayoutNodeTypes.TYPE_BOX_LOGICALPAGE) == LayoutNodeTypes.TYPE_BOX_LOGICALPAGE)
      {
        return (LogicalPageBox) parent;
      }

      parent = parent.getParent();
    }
    return null;
  }

  /**
   * Clones this node. Be aware that cloning can get you into deep trouble, as the relations this node has may no longer
   * be valid.
   *
   * @return
   * @noinspection CloneDoesntDeclareCloneNotSupportedException
   */
  public Object clone()
  {
    try
    {
      return super.clone();
    }
    catch (final CloneNotSupportedException e)
    {
      // ignored ..
      throw new IllegalStateException("Clone failed for some reason.");
    }
  }

  /**
   * Derive creates a disconnected node that shares all the properties of the original node. The derived node will no
   * longer have any parent, silbling, child or any other relationships with other nodes.
   *
   * @param deep
   * @return
   */
  public RenderNode derive(final boolean deep)
  {
    final RenderNode node = (RenderNode) clone();
    node.parentNode = null;
    node.nextNode = null;
    node.prevNode = null;
    node.hibernated = false;
    if (deep)
    {
      node.cachedAge = -1;
      node.cacheState = CACHE_DEEP_DIRTY;
    }
    return node;
  }

  /**
   * Derives an hibernation copy. The resulting object should get stripped of all unnecessary caching information and
   * all objects, which will be regenerated when the layouting restarts. Size does matter here.
   *
   * @return
   */
  public RenderNode hibernate()
  {
    final RenderNode node = (RenderNode) clone();
    node.parentNode = null;
    node.nextNode = null;
    node.prevNode = null;
    node.hibernated = true;
    return node;
  }

  public RenderNode deriveFrozen(final boolean deep)
  {
    final RenderNode node = (RenderNode) clone();
    node.parentNode = null;
    node.nextNode = null;
    node.prevNode = null;
    node.frozen = true;
    node.hibernated = false;
    return node;
  }

  public boolean isFrozen()
  {
    return frozen;
  }

  public boolean isHibernated()
  {
    return hibernated;
  }

  protected void setHibernated(final boolean hibernated)
  {
    this.hibernated = hibernated;
  }

  public RenderNode findNodeById(final Object instanceId)
  {
    if (instanceId == getInstanceId())
    {
      return this;
    }
    return null;
  }

  public boolean isOpen()
  {
    return false;
  }

  public boolean isEmpty()
  {
    return false;
  }

  public boolean isDiscardable()
  {
    return false;
  }

  /**
   * If that method returns true, the element will not be used for rendering. For the purpose of computing sizes or
   * performing the layouting (in the validate() step), this element will treated as if it is not there.
   * <p/>
   * If the element reports itself as non-empty, however, it will affect the margin computation.
   *
   * @return
   */
  public boolean isIgnorableForRendering()
  {
    return isEmpty();
  }

  public void freeze()
  {
    frozen = true;
  }

  public long getMaximumBoxWidth()
  {
    return maximumBoxWidth;
  }

  public void setMaximumBoxWidth(final long maximumBoxWidth)
  {
    this.maximumBoxWidth = maximumBoxWidth;
  }

  public long getMinimumChunkWidth()
  {
    return minimumChunkWidth;
  }

  protected void setMinimumChunkWidth(final long minimumChunkWidth)
  {
    if (minimumChunkWidth < 0)
    {
      throw new IllegalArgumentException();
    }
    this.minimumChunkWidth = minimumChunkWidth;
  }

  public long getEffectiveMarginTop()
  {
    return 0;
  }

  public long getEffectiveMarginBottom()
  {
    return 0;
  }

  public VerticalTextAlign getVerticalTextAlignment()
  {
    return nodeLayoutProperties.getVerticalTextAlign();
  }
//
//  /**
//   * The sticky-Marker contains the original Y of this node.
//   * @return
//   */
//  public long getStickyMarker()
//  {
//    return stickyMarker;
//  }
//
//  public void setStickyMarker(final long stickyMarker)
//  {
//    this.stickyMarker = stickyMarker;
//  }

  public String getName()
  {
    return null;
  }

  public boolean isBreakAfter()
  {
    return false;
  }

  public final long getCachedAge()
  {
    return cachedAge;
  }

  public final void setCachedAge(final long cachedAge)
  {
    this.cachedAge = cachedAge;
  }

  public final long getCachedParentWidth()
  {
    return cachedParentWidth;
  }

  public final void setCachedParentWidth(final long cachedParentWidth)
  {
    this.cachedParentWidth = cachedParentWidth;
  }


  /**
   * Returns the cached y position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @return the cached x position
   */
  public final long getCachedX()
  {
    return cachedX;
  }

  /**
   * Defines the cached x position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @param cachedX the cached x position
   */
  public final void setCachedX(final long cachedX)
  {
    this.cachedX = cachedX;
  }

  /**
   * Returns the cached y position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @return the cached y position
   */
  public final long getCachedY()
  {
    return cachedY;
  }

  /**
   * Defines the cached y position. This position is known after all layouting steps have been finished. In most cases
   * the layouter tries to reuse the cached values instead of recomputing everything from scratch on each iteration.
   * <p/>
   * The cached positions always specify the border-box. If the user specified sizes as content-box sizes, the layouter
   * converts them into border-box sizes before filling the cache.
   *
   * @param cachedY the cached y position
   */
  public final void setCachedY(final long cachedY)
  {
    this.cachedY = cachedY;
  }

  public final void shiftCached(final long amount)
  {
    this.cachedY += amount;
  }

  public final long getCachedWidth()
  {
    return cachedWidth;
  }

  public final void setCachedWidth(final long cachedWidth)
  {
    if (cachedWidth < 0)
    {
      throw new IndexOutOfBoundsException("'cached width' cannot be negative.");
    }
    this.cachedWidth = cachedWidth;
  }

  public final long getCachedHeight()
  {
    return cachedHeight;
  }

  public final void setCachedHeight(final long cachedHeight)
  {
    if (cachedHeight < 0)
    {
      throw new IndexOutOfBoundsException("'cached height' cannot be negative.");
    }
    if (cacheState == CACHE_CLEAN && this.cachedHeight > 0 && cachedHeight == 0)
    {
      throw new IndexOutOfBoundsException("'cached height' cannot be set to null.");
    }
    this.cachedHeight = cachedHeight;
  }

  public void apply()
  {
    this.x = this.cachedX;
    this.y = this.cachedY;
    this.width = this.cachedWidth;
    this.height = this.cachedHeight;
    this.cachedAge = this.changeTracker;
    this.cacheState = RenderNode.CACHE_CLEAN;
    final RenderBox parent = getParent();
    if (parent == null)
    {
      this.cachedParentWidth = 0;
    }
    else
    {
      this.cachedParentWidth = parent.getWidth();
    }
  }

  public final boolean isCacheValid()
  {
    if (cachedAge != changeTracker)
    {
      return false;
    }

    final RenderBox parent = getParent();
    if (parent == null)
    {
      if (this.cachedParentWidth != 0)
      {
        return false;
      }
    }
    else
    {
      if (this.cachedParentWidth != parent.getWidth())
      {
        return false;
      }
    }
    return true;
  }

  /**
   * Checks whether this node can be removed. This flag is used by iterative streaming output targets to mark nodes that
   * have been fully processed.
   *
   * @return
   */
  public boolean isFinishedPaginate()
  {
    return finishedPaginate;
  }

  public void setFinishedPaginate(final boolean finished)
  {
    if (this.finishedPaginate == true && finished == false)
    {
      throw new IllegalStateException("Cannot undo a finished-marker");
    }
    this.finishedPaginate = finished;
  }

  public boolean isFinishedTable()
  {
    return finishedTable;
  }

  public void setFinishedTable(final boolean finished)
  {
    if (this.finishedTable == true && finished == false)
    {
      throw new IllegalStateException("Cannot undo a finished-marker");
    }
    this.finishedTable = finished;
  }

  public int getCacheState()
  {
    return cacheState;
  }

  public void markCacheClean()
  {
    if (cachedY != y)
    {
      throw new IllegalStateException();
    }
    cacheState = RenderNode.CACHE_CLEAN;
  }

  public ReportStateKey getStateKey()
  {
    return null;
  }

  public boolean isBoxOverflowX()
  {
    return false;
  }

  public boolean isBoxOverflowY()
  {
    return false;
  }
  public final boolean isNodeVisible(final StrictBounds drawArea, final boolean overflowX, final boolean overflowY)
  {
    final long drawAreaX0 = drawArea.getX();
    final long drawAreaY0 = drawArea.getY();
    return isNodeVisible(drawAreaX0, drawAreaY0, drawArea.getWidth(), drawArea.getHeight(), overflowX, overflowY);
  }

  public final boolean isNodeVisible(final StrictBounds drawArea)
  {
    final long drawAreaX0 = drawArea.getX();
    final long drawAreaY0 = drawArea.getY();
    return isNodeVisible(drawAreaX0, drawAreaY0, drawArea.getWidth(), drawArea.getHeight());
  }

  public final boolean isNodeVisible(final long drawAreaX0, final long drawAreaY0,
                                     final long drawAreaWidth, final long drawAreaHeight)
  {
    return isNodeVisible(drawAreaX0, drawAreaY0, drawAreaWidth, drawAreaHeight, isBoxOverflowX(), isBoxOverflowY());
  }

  public final boolean isNodeVisible(final long drawAreaX0, final long drawAreaY0,
                                     final long drawAreaWidth, final long drawAreaHeight,
                                     final boolean overflowX, final boolean overflowY)
  {
    if (getStyleSheet().getBooleanStyleProperty(ElementStyleKeys.VISIBLE) == false)
    {
      return false;
    }

    final long drawAreaX1 = drawAreaX0 + drawAreaWidth;
    final long drawAreaY1 = drawAreaY0 + drawAreaHeight;

    final long x2 = x + width;
    final long y2 = y + height;

    if (width == 0)
    {
      if (x2 < drawAreaX0)
      {
        return false;
      }
      if (x > drawAreaX1)
      {
        return false;
      }
    }
    else if (overflowX == false)
    {
      if (x2 <= drawAreaX0)
      {
        return false;
      }
      if (x >= drawAreaX1)
      {
        return false;
      }
    }
    if (height == 0)
    {
      if (y2 < drawAreaY0)
      {
        return false;
      }
      if (y > drawAreaY1)
      {
        return false;
      }
    }
    else if (overflowY == false)
    {
      if (y2 <= drawAreaY0)
      {
        return false;
      }
      if (y >= drawAreaY1)
      {
        return false;
      }
    }
    return true;
  }

  public boolean isVirtualNode()
  {
    return virtualNode;
  }

  public void setVirtualNode(final boolean virtualNode)
  {
    this.virtualNode = virtualNode;
  }


  public final boolean isBoxVisible(final StrictBounds drawArea)
  {
    return isBoxVisible(drawArea.getX(), drawArea.getY(), drawArea.getWidth(), drawArea.getHeight());
  }

  public final boolean isBoxVisible(final long x, final long y, final long width, final long height)
  {
    if (isNodeVisible(x, y, width, height) == false)
    {
      return false;
    }

    final RenderBox parent = getParent();
    if (parent == null)
    {
      return true;
    }

    final StyleSheet styleSheet = getStyleSheet();
    if (styleSheet.getStyleProperty(ElementStyleKeys.ANCHOR_NAME) != null)
    {
      return true;
    }


    if (parent.getStaticBoxLayoutProperties().isOverflowX() == false)
    {
      final long parentX1 = parent.getX();
      final long parentX2 = parentX1 + parent.getWidth();

      if (getWidth() == 0)
      {
        // could be a line ..
        return true;
      }

      final long boxX1 = getX();
      final long boxX2 = boxX1 + getWidth();

      if (boxX2 <= parentX1)
      {
        return false;
      }
      if (boxX1 >= parentX2)
      {
        return false;
      }
    }

    if (parent.getStaticBoxLayoutProperties().isOverflowY() == false)
    {
      // Compute whether the box is at least partially contained in the parent's bounding box.
      final long parentY1 = parent.getY();
      final long parentY2 = parentY1 + parent.getHeight();

      if (getHeight() == 0)
      {
        // could be a line ..
        return true;
      }

      final long boxY1 = getY();
      final long boxY2 = boxY1 + getHeight();

      if (boxY2 <= parentY1)
      {
        return false;
      }
      if (boxY1 >= parentY2)
      {
        return false;
      }
    }
    return true;
  }
}
