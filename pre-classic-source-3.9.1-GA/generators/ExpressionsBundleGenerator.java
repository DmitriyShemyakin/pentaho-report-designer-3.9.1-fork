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

package generators;

import java.util.Locale;
import java.util.TreeMap;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.AbstractMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionPropertyMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ExpressionsBundleGenerator
{
  public static final String GLOBAL_BUNDLE = "org.pentaho.reporting.engine.classic.core.metadata.messages";

  private ExpressionsBundleGenerator()
  {
  }

  public static void main(final String[] args)
  {
    ClassicEngineBoot.getInstance().start();
    final TreeMap globalAttributes = new TreeMap();
    final ExpressionMetaData[] datas = ExpressionRegistry.getInstance().getAllExpressionMetaDatas();
    for (int i = 0; i < datas.length; i++)
    {
      final ExpressionMetaData data = datas[i];
      if (data instanceof AbstractMetaData == false)
      {
        continue;
      }
      printMetaBundle(data);
    }
  }

  private static void printMetaBundle(final ExpressionMetaData data)
  {
    System.out.println("-----------------------------------------------------");
    final AbstractMetaData amd = (AbstractMetaData) data;
    final String keyPrefix = amd.getKeyPrefix();

    System.out.println(keyPrefix + "display-name=" + amd.getName());
    System.out.println(keyPrefix + "grouping=" + filter(amd.getGrouping(Locale.ENGLISH), "Group"));
    System.out.println(keyPrefix + "description=" + filter(amd.getDescription(Locale.ENGLISH), ""));
    System.out.println(keyPrefix + "deprecated=" + filter(amd.getDeprecationMessage(Locale.ENGLISH), ""));

    final ExpressionPropertyMetaData[] attributes = data.getPropertyDescriptions();

    for (int j = 0; j < attributes.length; j++)
    {
      final ExpressionPropertyMetaData attribute = attributes[j];
      final AbstractMetaData aamd = (AbstractMetaData) attribute;
      final String akeyPrefix = aamd.getKeyPrefix();
      final String aname = attribute.getName();

      System.out.println(akeyPrefix + aname + ".display-name=" + aname);
      System.out.println(akeyPrefix + aname + ".grouping=" + filter(aamd.getGrouping(Locale.ENGLISH), "Group"));
      System.out.println(akeyPrefix + aname + ".description=" + filter(aamd.getDescription(Locale.ENGLISH), ""));
      System.out.println(akeyPrefix + aname + ".deprecated=" + filter(aamd.getDeprecationMessage(Locale.ENGLISH), ""));
    }

    System.out.println("-----------------------------------------------------");
  }


  private static String filter(final String grouping, final String defaultValue)
  {
    if (grouping.startsWith("!") && grouping.endsWith("!"))
    {
      return defaultValue;
    }
    return grouping;
  }
}