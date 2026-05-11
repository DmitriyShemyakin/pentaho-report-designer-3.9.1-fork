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

package org.pentaho.reporting.engine.classic.core.util.serializers;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.pentaho.reporting.engine.classic.core.layout.BandLayoutManager;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.serializer.SerializeMethod;

/**
 * A SerializeMethod implementation that handles BandLayoutManagers.
 *
 * @author Thomas Morgner
 * @see org.pentaho.reporting.engine.classic.core.layout.BandLayoutManager
 */
public class BandLayoutManagerSerializer implements SerializeMethod
{
  /**
   * Default Constructor.
   */
  public BandLayoutManagerSerializer()
  {
  }

  /**
   * Writes a serializable object description to the given object output stream. As bandlayoutmanagers need to be
   * instantiable by their default constructor, it is sufficient to write the class of the layout manager.
   *
   * @param o   the to be serialized object.
   * @param out the outputstream that should receive the object.
   * @throws IOException if an I/O error occured.
   */
  public void writeObject(final Object o, final ObjectOutputStream out)
      throws IOException
  {
    out.writeObject(o.getClass().getName());
  }

  /**
   * Reads the object from the object input stream. This will read a serialized class name of the BandLayoutManager. The
   * specified class is then instantiated using its default constructor.
   *
   * @param in the object input stream from where to read the serialized data.
   * @return the generated object.
   * @throws IOException            if reading the stream failed.
   * @throws ClassNotFoundException if serialized object class cannot be found.
   */
  public Object readObject(final ObjectInputStream in)
      throws IOException, ClassNotFoundException
  {
    final String cn = (String) in.readObject();

    try
    {
      return ObjectUtilities.loadAndInstantiate(cn, BandLayoutManagerSerializer.class, BandLayoutManager.class);
    }
    catch (Exception e)
    {
      throw new NotSerializableException(cn);
    }
  }

  /**
   * The class of the object, which this object can serialize.
   *
   * @return the class <code>org.pentaho.reporting.engine.classic.core.layout.BandLayoutManager</code>.
   */
  public Class getObjectClass()
  {
    return BandLayoutManager.class;
  }
}
