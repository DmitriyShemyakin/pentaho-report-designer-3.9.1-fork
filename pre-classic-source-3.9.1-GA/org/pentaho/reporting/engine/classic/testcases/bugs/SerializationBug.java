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

package org.pentaho.reporting.engine.classic.testcases.bugs;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

public class SerializationBug
{
  private static void testSerialization (Object in)
          throws IOException, ClassNotFoundException
  {
    final ByteArrayOutputStream bo = new ByteArrayOutputStream();

    final ObjectOutputStream oout = new ObjectOutputStream(bo);
    oout.writeObject(in);
    oout.close();
    final ByteArrayInputStream bin = new ByteArrayInputStream(bo.toByteArray());
    final ObjectInputStream oin = new ObjectInputStream(bin);
    final Object o = oin.readObject();

  }

  private static final String DECIMALFORMAT_DEFAULT_PATTERN =
    "#,###.###################################################" +
    "#########################################################" +
    "#########################################################" +
    "#########################################################" +
    "#########################################################" +
    "#########################################################" +
    "####";

  public static void main (String[] args)
          throws IOException, ClassNotFoundException
  {
    final SimpleDateFormat dfmt = new SimpleDateFormat("");
    System.out.println(dfmt.toPattern());

    final DecimalFormat dcfmt = new DecimalFormat(DECIMALFORMAT_DEFAULT_PATTERN);
    System.out.println(dcfmt.toPattern());

    testSerialization(dfmt);
    testSerialization(dcfmt);
  }
}
