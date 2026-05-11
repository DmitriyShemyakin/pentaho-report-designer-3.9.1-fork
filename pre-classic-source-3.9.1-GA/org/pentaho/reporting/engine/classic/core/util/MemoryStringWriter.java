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

package org.pentaho.reporting.engine.classic.core.util;

import java.io.IOException;
import java.io.Writer;

/**
 * A string writer that is able to write large amounts of data. The original StringWriter contained in Java doubles its
 * buffersize everytime the buffer overflows. This is nice with small amounts of data, but awfull for huge buffers.
 *
 * @author Thomas Morgner
 */
public class MemoryStringWriter extends Writer
{
  private int cursor;
  private char[] buffer;
  private int maximumBufferIncrement;
  private boolean closed;

  /**
   * Create a new character-stream writer whose critical sections will synchronize on the writer itself.
   */
  public MemoryStringWriter()
  {
    this(4096);
  }

  /**
   * Create a new character-stream writer whose critical sections will synchronize on the writer itself.
   */
  public MemoryStringWriter(final int bufferSize)
  {
    this(bufferSize, bufferSize * 4);
  }


  public MemoryStringWriter(final int bufferSize, final int maximumBufferIncrement)
  {
    this.maximumBufferIncrement = maximumBufferIncrement;
    this.buffer = new char[bufferSize];
  }

  /**
   * Write a portion of an array of characters.
   *
   * @param cbuf Array of characters
   * @param off  Offset from which to start writing characters
   * @param len  Number of characters to write
   * @throws java.io.IOException If an I/O error occurs
   */
  public void write(final char[] cbuf, final int off, final int len) throws IOException
  {
    if (len < 0)
    {
      throw new IllegalArgumentException();
    }
    if (off < 0)
    {
      throw new IndexOutOfBoundsException();
    }
    if (cbuf == null)
    {
      throw new NullPointerException();
    }
    if ((len + off) > cbuf.length)
    {
      throw new IndexOutOfBoundsException();
    }
    if (closed)
    {
      throw new IOException("Stream already closed.");
    }

    ensureSize(cursor + len);

    System.arraycopy(cbuf, off, this.buffer, cursor, len);
    cursor += len;
  }

  private void ensureSize(final int size)
  {
    if (this.buffer.length >= size)
    {
      return;
    }

    final int computedSize = (int) Math.min((this.buffer.length + 1) * 1.5,
        this.buffer.length + maximumBufferIncrement);
    final int newSize = Math.max(size, computedSize);
    final char[] newBuffer = new char[newSize];
    System.arraycopy(this.buffer, 0, newBuffer, 0, cursor);
    this.buffer = newBuffer;
  }

  /**
   * Flush the stream.  If the stream has saved any characters from the various write() methods in a buffer, write them
   * immediately to their intended destination.  Then, if that destination is another character or byte stream, flush
   * it.  Thus one flush() invocation will flush all the buffers in a chain of Writers and OutputStreams.
   * <p/>
   * If the intended destination of this stream is an abstraction provided by the underlying operating system, for
   * example a file, then flushing the stream guarantees only that bytes previously written to the stream are passed to
   * the operating system for writing; it does not guarantee that they are actually written to a physical device such as
   * a disk drive.
   *
   * @throws java.io.IOException If an I/O error occurs
   */
  public void flush() throws IOException
  {
    if (closed)
    {
      throw new IOException("Stream already closed.");
    }
  }

  /**
   * Close the stream, flushing it first.  Once a stream has been closed, further write() or flush() invocations will
   * cause an IOException to be thrown.  Closing a previously-closed stream, however, has no effect.
   *
   * @throws java.io.IOException If an I/O error occurs
   */
  public void close() throws IOException
  {
    closed = true;
  }

  public int getCursor()
  {
    return cursor;
  }

  public MemoryStringReader createReader()
  {
    return new MemoryStringReader(buffer, 0, cursor);
  }
  
  public String toString()
  {
    return new String(buffer, 0, cursor);
  }
}
