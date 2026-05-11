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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.testcases.base.basic.style;

import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleSheetCollection;


public class StyleSheetCollectionTest extends TestCase
{
  public StyleSheetCollectionTest()
  {
  }

  public StyleSheetCollectionTest(final String s)
  {
    super(s);
  }

  public void testSimpleClone ()
          throws CloneNotSupportedException
  {
    final StyleSheetCollection sc = new StyleSheetCollection();
    final ElementStyleSheet es1 = sc.createStyleSheet("one");
    final ElementStyleSheet es2 = sc.createStyleSheet("two");
    final ElementStyleSheet es3 = sc.createStyleSheet("three");
    final ElementStyleSheet es4 = sc.createStyleSheet("four");

    es1.addParent(es2);
    es1.addParent(es4);

    es2.addParent(es3);
    es3.addParent(es4);

    try
    {
      es4.addParent(es1);
      fail("Loop not detected");
    }
    catch(Exception e)
    {

    }

    final StyleSheetCollection scc = (StyleSheetCollection) sc.clone();
    final ElementStyleSheet esc1 = scc.getStyleSheet("one");
    final ElementStyleSheet esc2 = scc.getStyleSheet("two");
    final ElementStyleSheet esc3 = scc.getStyleSheet("three");
    final ElementStyleSheet esc4 = scc.getStyleSheet("four");

    assertEquals(es1.getId(), esc1.getId());
    assertEquals(es2.getId(), esc2.getId());
    assertEquals(es3.getId(), esc3.getId());
    assertEquals(es4.getId(), esc4.getId());

    final List parents = Arrays.asList(esc1.getParents());
    assertTrue(parents.contains(esc2));
    assertTrue(parents.contains(esc4));
    assertFalse(parents.contains(es2));
    assertFalse(parents.contains(es4));

    final List parentsOriginal = Arrays.asList(es1.getParents());
    assertTrue(parentsOriginal.contains(es2));
    assertTrue(parentsOriginal.contains(es4));
    assertFalse(parentsOriginal.contains(esc2));
    assertFalse(parentsOriginal.contains(esc4));
  }

  public void testForeignStyles ()
          throws CloneNotSupportedException
  {
    final StyleSheetCollection sc = new StyleSheetCollection();
    final ElementStyleSheet es1 = sc.createStyleSheet("one");
    final ElementStyleSheet es2 = sc.createStyleSheet("two");

    es1.addParent(es2);

    final StyleSheetCollection scc = (StyleSheetCollection) sc.clone();
    final ElementStyleSheet esc1 = scc.getStyleSheet("one");
    final ElementStyleSheet esc2 = scc.getStyleSheet("two");
    final List parents = Arrays.asList(esc1.getParents());
    assertTrue(parents.contains(esc2));
  }

}
