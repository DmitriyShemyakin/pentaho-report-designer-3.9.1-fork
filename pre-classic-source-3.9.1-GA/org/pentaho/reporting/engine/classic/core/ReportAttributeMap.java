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

package org.pentaho.reporting.engine.classic.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.AttributeMap;

/**
 * A attribute map that keeps track of changes.
 *
 * @author Thomas Morgner
 */
public class ReportAttributeMap extends AttributeMap
{
  public static final ReportAttributeMap EMPTY_MAP = new ReportAttributeMap().createUnmodifiableMap();
  private static final Log logger = LogFactory.getLog(ReportAttributeMap.class);
  private long changeTracker;
  private boolean readOnly;

  public ReportAttributeMap()
  {
    this.changeTracker = 0;
  }

  public ReportAttributeMap(final long changeTracker)
  {
    this.changeTracker = changeTracker;
  }

  public ReportAttributeMap(final ReportAttributeMap copy)
  {
    super(copy);
    this.changeTracker = copy.changeTracker;
  }

  public ReportAttributeMap createUnmodifiableMap()
  {
    try
    {
      final ReportAttributeMap o = (ReportAttributeMap) super.clone();
      o.readOnly = true;
      return o;
    }
    catch (Exception e)
    {
      logger.error("Clone failed for ReportAttributeMap.createUnmodifiableMap", e);
      throw new IllegalStateException("Clone failed for ReportAttributeMap.createUnmodifiableMap");
    }
  }

  public long getChangeTracker()
  {
    return changeTracker;
  }

  public Object setAttribute(final String namespace, final String attribute, final Object value)
  {
    if (readOnly)
    {
      throw new UnsupportedOperationException("This collection is marked as read-only");
    }
    final Object oldValue = super.setAttribute(namespace, attribute, value);
    if (ObjectUtilities.equal(oldValue, value) == false)
    {
      changeTracker += 1;
    }
    return oldValue;
  }

  public void makeReadOnly()
  {
    this.readOnly = true;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public void putAll(final AttributeMap attributeMap)
  {
    if (isReadOnly())
    {
      throw new UnsupportedOperationException("This collection is marked as read-only");
    }
    super.putAll(attributeMap);
    changeTracker += 1;
  }
}
