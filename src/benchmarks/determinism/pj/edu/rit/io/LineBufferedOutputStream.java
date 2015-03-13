//******************************************************************************
//
// File:    LineBufferedOutputStream.java
// Package: benchmarks.determinism.pj.edu.ritio
// Unit:    Class benchmarks.determinism.pj.edu.ritio.LineBufferedOutputStream
//
// This Java source file is copyright (C) 2006 by Alan Kaminsky. All rights
// reserved. For further information, contact the author, Alan Kaminsky, at
// ark@cs.rit.edu.
//
// This Java source file is part of the Parallel Java Library ("PJ"). PJ is free
// software; you can redistribute it and/or modify it under the terms of the GNU
// General Public License as published by the Free Software Foundation; either
// version 3 of the License, or (at your option) any later version.
//
// PJ is distributed in the hope that it will be useful, but WITHOUT ANY
// WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
// A PARTICULAR PURPOSE. See the GNU General Public License for more details.
//
// A copy of the GNU General Public License is provided in the file gpl.txt. You
// may also obtain a copy of the GNU General Public License on the World Wide
// Web at http://www.gnu.org/licenses/gpl.html.
//
//******************************************************************************

package benchmarks.determinism.pj.edu.ritio;

import java.io.ByteArrayOutputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class LineBufferedOutputStream provides a line buffer layered on top of an
 * underlying output stream. All writes to the line buffered output stream are
 * accumulated in an internal buffer. Calling <TT>flush()</TT> on the line
 * buffered output stream does nothing. The only times the internal buffer is
 * flushed to the underlying output stream are when a newline byte
 * (<TT>'\n'</TT>) is written and when the line buffered output stream is
 * closed.
 *
 * @author  Alan Kaminsky
 * @version 19-Oct-2006
 */
public class LineBufferedOutputStream
	extends FilterOutputStream
	{

// Hidden data members.

	private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

// Exported constructors.

	/**
	 * Construct a new line buffered output stream.
	 *
	 * @param  out  Underlying output stream.
	 */
	public LineBufferedOutputStream
		(OutputStream out)
		{
		super (out);
		}

// Exported operations.

	/**
	 * Write the given byte to this line buffered output stream. Only the least
	 * significant 8 bits of <TT>b</TT> are written.
	 *
	 * @param  b  Byte.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void write
		(int b)
		throws IOException
		{
		buffer.write (b);
		if (b == '\n') flushBuffer();
		}

	/**
	 * Write the given byte array to this line buffered output stream.
	 *
	 * @param  buf  Byte array.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void write
		(byte[] buf)
		throws IOException
		{
		write (buf, 0, buf.length);
		}

	/**
	 * Write a portion of the given byte array to this line buffered output
	 * stream.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to write.
	 * @param  len  Number of bytes to write.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>buf</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>off</TT> &lt; 0, <TT>len</TT>
	 *     &lt; 0, or <TT>off+len</TT> &gt; <TT>buf.length</TT>.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void write
		(byte[] buf,
		 int off,
		 int len)
		throws IOException
		{
		if (off < 0 || len < 0 || off+len > buf.length)
			{
			throw new IndexOutOfBoundsException();
			}
		while (len > 0)
			{
			int i = findNewlineIndex (buf, off, len);
			if (i == off + len)
				{
				buffer.write (buf, off, len);
				off += len;
				len = 0;
				}
			else
				{
				int n = i - off + 1;
				buffer.write (buf, off, n);
				flushBuffer();
				off += n;
				len -= n;
				}
			}
		}

	/**
	 * Flush this line buffered output stream. The <TT>flush()</TT> method does
	 * nothing.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void flush()
		throws IOException
		{
		}

	/**
	 * Close this line buffered output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public void close()
		throws IOException
		{
		try
			{
			flushBuffer();
			out.close();
			}
		finally
			{
			buffer = null;
			out = null;
			}
		}

// Hidden operations.

	/**
	 * Flush the line buffer to the underlying output stream.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void flushBuffer()
		throws IOException
		{
		buffer.writeTo (out);
		buffer.reset();
		}

	/**
	 * Find the next newline in the given byte array.
	 *
	 * @param  buf  Byte array.
	 * @param  off  Index of first byte to look at.
	 * @param  len  Number of bytes to look at.
	 *
	 * @return  Index of first newline at or after index <TT>off</TT>, or
	 *          <TT>off+len</TT> if there is no newline.
	 */
	private int findNewlineIndex
		(byte[] buf,
		 int off,
		 int len)
		{
		while (len > 0 && buf[off] != '\n')
			{
			++ off;
			-- len;
			}
		return off;
		}

	}
