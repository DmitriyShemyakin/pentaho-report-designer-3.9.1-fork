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

package org.pentaho.reporting.engine.classic.testcases.base.basic;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;

/**
 * @deprecated moved into engine core
 */
public class ElementTest extends TestCase
{
  public ElementTest(final String s)
  {
    super(s);
  }

  public void testElementCreate()
  {
    final Element e = new Element();
    assertNotNull(e.getDataSource());
    assertNotNull(e.getStyle());
    assertNotNull(e.getName());
    assertTrue(e.isVisible());
    assertNull(e.getParent());
  }

  public void testElementClone()
          throws CloneNotSupportedException
  {
    final Band band = new Band();
    final Element e = new Element();
    band.addElement(e);
    assertNotNull(e.getParent());
    assertNotNull(e.getDataSource());
    assertNotNull(e.getStyle());
    assertNotNull(e.getName());
    assertTrue(e.isVisible());

    final Element clone = (Element) e.clone();
    assertNull(clone.getParent());
    assertNotNull(clone.getDataSource());
    assertNotNull(clone.getStyle());
    assertNotNull(clone.getName());
    assertTrue(clone.isVisible());

    final Band clonedBand = (Band) band.clone();
    assertNull(clonedBand.getParent());
    assertNotNull(clonedBand.getDataSource());
    assertNotNull(clonedBand.getStyle());
    assertNotNull(clonedBand.getName());
    assertTrue(clonedBand.isVisible());

    final Element clientElement = (Element) clonedBand.getElement(0);
    assertNotNull(clientElement.getParent());
    assertNotNull(clientElement.getDataSource());
    assertNotNull(clientElement.getStyle());
    assertNotNull(clientElement.getName());
    assertTrue(clientElement.isVisible());
    assertEquals(clonedBand, clientElement.getParent());

    clonedBand.getStyle().setStyleProperty(ElementStyleKeys.DYNAMIC_HEIGHT, Boolean.TRUE);
    assertTrue(clientElement.isDynamicContent());
    assertFalse(e.isDynamicContent());
  }

  public void testElementMethods()
  {
    final Element e = new Element();
    assertTrue(e.isVisible());
    e.setVisible(false);
    assertTrue(e.isVisible() == false);
    e.setVisible(true);
    assertTrue(e.isVisible());

    try
    {
      e.setDataSource(null);
      fail();
    }
    catch (NullPointerException npe)
    {
      // expected, ignored
    }
    e.toString();
  }

  public void testSerialize() throws Exception
  {
    final Element e = new Element();
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
    final ObjectOutputStream out = new ObjectOutputStream(bo);
    out.writeObject(e);

    final ObjectInputStream oin = new ObjectInputStream(new ByteArrayInputStream(bo.toByteArray()));
    final Element e2 = (Element) oin.readObject();
    assertNotNull(e2); // cannot assert equals, as this is not implemented ...
  }
}
