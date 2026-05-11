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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.wizard;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.AbstractElementFormatFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.StructureFunction;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.wizard.model.ElementFormatDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.FieldDefinition;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.StringUtils;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class WizardOverrideFormattingFunction extends AbstractElementFormatFunction implements StructureFunction
{
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


  public WizardOverrideFormattingFunction()
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
    final Object maybeFormatData = e.getAttribute(AttributeNames.Wizard.NAMESPACE, "CachedWizardFormatData");
    if (maybeFormatData instanceof ElementFormatDefinition)
    {
      final ElementFormatDefinition formatDefinition = (ElementFormatDefinition) maybeFormatData;
      if (formatDefinition.getBackgroundColor() != null)
      {
        e.getStyle().setStyleProperty(ElementStyleKeys.BACKGROUND_COLOR, formatDefinition.getBackgroundColor());
        retval = true;
      }
      if (formatDefinition.getFontColor() != null)
      {
        e.getStyle().setStyleProperty(ElementStyleKeys.PAINT, formatDefinition.getFontColor());
        retval = true;
      }
      if (formatDefinition.getFontBold() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.BOLD, formatDefinition.getFontBold());
        retval = true;
      }
      if (formatDefinition.getFontItalic() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.ITALIC, formatDefinition.getFontItalic());
        retval = true;
      }
      if (formatDefinition.getFontName() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.FONT, formatDefinition.getFontName());
        retval = true;
      }
      if (formatDefinition.getFontUnderline() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.UNDERLINED, formatDefinition.getFontUnderline());
        retval = true;
      }
      if (formatDefinition.getFontItalic() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.ITALIC, formatDefinition.getFontItalic());
        retval = true;
      }
      if (formatDefinition.getFontSize() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.FONTSIZE, formatDefinition.getFontSize());
        retval = true;
      }
      if (formatDefinition.getFontStrikethrough() != null)
      {
        e.getStyle().setStyleProperty(TextStyleKeys.STRIKETHROUGH, formatDefinition.getFontStrikethrough());
        retval = true;
      }
      if (formatDefinition.getHorizontalAlignment() != null)
      {
        e.getStyle().setStyleProperty(ElementStyleKeys.ALIGNMENT, formatDefinition.getHorizontalAlignment());
        retval = true;
      }
      if (formatDefinition.getVerticalAlignment() != null)
      {
        e.getStyle().setStyleProperty(ElementStyleKeys.VALIGNMENT, formatDefinition.getVerticalAlignment());
        retval = true;
      }
    }

    final Object maybeFieldData = e.getAttribute(AttributeNames.Wizard.NAMESPACE, "CachedWizardFieldData");
    if (maybeFieldData instanceof FieldDefinition)
    {
      final FieldDefinition fieldDefinition = (FieldDefinition) maybeFieldData;

      if (fieldDefinition.getDataFormat() != null)
      {
        e.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING,
            fieldDefinition.getDataFormat());
        retval = true;
      }
      if (fieldDefinition.getNullString() != null)
      {
        e.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, fieldDefinition.getNullString());
        retval = true;
      }

      if ("label".equals(e.getElementTypeName()) && !StringUtils.isEmpty(fieldDefinition.getDisplayName()))
      {
        e.setAttribute(AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, fieldDefinition.getDisplayName());
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
    final WizardOverrideFormattingFunction eval = (WizardOverrideFormattingFunction) super.getInstance();
    eval.expressionsCache = new LFUMap(500);
    return eval;
  }

}
