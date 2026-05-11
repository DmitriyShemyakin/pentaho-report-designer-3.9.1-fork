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

package org.pentaho.reporting.designer.core.util.table.expressions;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.function.Function;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;

/**
 * A helper util that filter all known function and expressions and returns only the expressions, but not the
 * functions.
 *
 * @author Thomas Morgner
 */
public class ExpressionUtil
{
  private static ExpressionUtil instance;

  public static synchronized ExpressionUtil getInstance()
  {
    if (instance == null)
    {
      instance = new ExpressionUtil();
    }
    return instance;
  }

  private ExpressionMetaData[] expressions;

  public ExpressionMetaData[] getKnownExpressions()
  {
    return expressions.clone();
  }

  private ExpressionUtil()
  {
    final ArrayList<ExpressionMetaData> allRealExpressions = new ArrayList<ExpressionMetaData>();
    final ExpressionMetaData[] allExpressionMetaDatas = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();
    for (int i = 0; i < allExpressionMetaDatas.length; i++)
    {
      final ExpressionMetaData metaData = allExpressionMetaDatas[i];
      if (metaData.isHidden())
      {
        continue;
      }

      if (Function.class.isAssignableFrom(metaData.getExpressionType()) == false)
      {
        allRealExpressions.add(metaData);
      }
    }
    this.expressions = allRealExpressions.toArray(new ExpressionMetaData[allRealExpressions.size()]);
  }

}
