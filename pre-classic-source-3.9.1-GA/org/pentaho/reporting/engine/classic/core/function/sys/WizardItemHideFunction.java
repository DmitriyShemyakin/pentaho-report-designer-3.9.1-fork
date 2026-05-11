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
 * Copyright (c) 2009 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.sys;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.libraries.base.util.LFUMap;

/**
 * Todo: Document me!
 * <p/>
 * Date: 06.04.2009
 * Time: 18:54:33
 *
 * @author Thomas Morgner.
 */
public class WizardItemHideFunction extends AbstractElementFormatFunction
        implements StructureFunction
{
  private static final Log logger = LogFactory.getLog(WizardItemHideFunction.class);
  private boolean pageStarted;

  private static class NeedEvalResult
  {
    private boolean needToRun;
    private long changeTracker;

    private NeedEvalResult(final boolean needToRun, final long changeTracker)
    {
      this.needToRun = needToRun;
      this.changeTracker = changeTracker;
    }

    public boolean isNeedToRun()
    {
      return needToRun;
    }

    public long getChangeTracker()
    {
      return changeTracker;
    }
  }

  private LFUMap expressionsCache;


  public WizardItemHideFunction()
  {
    expressionsCache = new LFUMap(500);
  }

  protected void processRootBand(final Band b)
  {
    final NeedEvalResult needToRun = (NeedEvalResult) expressionsCache.get(b.getObjectID());
    if (needToRun != null)
    {
      if (needToRun.isNeedToRun() == false)
      {
        if (b.getChangeTracker() == needToRun.getChangeTracker())
        {
          return;
        }
      }
    }

    final boolean needToRunVal = processBand(b);
    expressionsCache.put(b.getObjectID(), new NeedEvalResult(needToRunVal, b.getChangeTracker()));
  }

  private boolean processBand(final Band b)
  {
    boolean hasAttrExpressions = evaluateElement(b);

    if (b.isVisible() == false)
    {
      return hasAttrExpressions;
    }

    final Element[] elementBuffer = b.unsafeGetElementArray();
    final int length = elementBuffer.length;
    for (int i = 0; i < length; i++)
    {
      final Element element = elementBuffer[i];
      if (element instanceof Band)
      {
        if (processBand((Band) element))
        {
          hasAttrExpressions = true;
        }
      }
      else
      {
        if (evaluateElement(element))
        {
          hasAttrExpressions = true;
        }
      }
    }

    if (b instanceof RootLevelBand)
    {
      final RootLevelBand rlb = (RootLevelBand) b;
      final SubReport[] reports = rlb.getSubReports();
      for (int i = 0; i < reports.length; i++)
      {
        final SubReport subReport = reports[i];
        if (evaluateElement(subReport))
        {
          hasAttrExpressions = true;
        }
      }
    }
    return hasAttrExpressions;
  }

  public int getProcessingPriority()
  {
    // executed after the metadata has been applied, but before the style-expressions get applied.
    return 6000;
  }


  /**
   * Evaluates all defined style-expressions of the given element.
   *
   * @param e the element that should be updated.
   * @return true, if attributes or style were changed, false if no change was made.
   */
  protected boolean evaluateElement(final Element e)
  {
    if (e == null)
    {
      throw new NullPointerException();
    }

    boolean retval = false;

    final Object maybeShowChanging = e.getAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ONLY_SHOW_CHANGING_VALUES);
    if (Boolean.TRUE.equals(maybeShowChanging))
    {
      Object field = e.getAttribute(AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.LABEL_FOR);
      if (field == null)
      {
        field = e.getAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD);
      }
      if (field != null)
      {
        final String fieldText = String.valueOf(field);
        if (pageStarted || getDataRow().isChanged(fieldText))
        {
          e.setVisible(true);
        }
        else
        {
          e.setVisible(false);
        }
        retval = true;
      }
    }

    return retval;
  }

  /**
   * Returns the dependency level for the expression (controls evaluation order for expressions and functions).
   *
   * @return the level.
   */
  public int getDependencyLevel()
  {
    return LayoutProcess.LEVEL_PAGINATE;
  }


  public Expression getInstance()
  {
    final WizardItemHideFunction eval = (WizardItemHideFunction) super.getInstance();
    eval.expressionsCache = new LFUMap(500);
    return eval;
  }

  public void pageStarted(final ReportEvent event)
  {
    pageStarted = true;
    super.pageStarted(event);
  }

  public void itemsStarted(final ReportEvent event)
  {
    pageStarted = true;
    super.itemsStarted(event);
  }

  public void itemsAdvanced(final ReportEvent event)
  {
    super.itemsAdvanced(event);
    pageStarted = false;
  }
}
