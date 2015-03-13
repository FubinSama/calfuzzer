//******************************************************************************
//
// File:    Unsigned16BitIntegerItemBuf.java
// Package: benchmarks.determinism.pj.edu.ritmp.buf
// Unit:    Class benchmarks.determinism.pj.edu.ritmp.buf.Unsigned16BitIntegerItemBuf
//
// This Java source file is copyright (C) 2007 by Alan Kaminsky. All rights
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

package benchmarks.determinism.pj.edu.ritmp.buf;

import benchmarks.determinism.pj.edu.ritmp.Buf;
import benchmarks.determinism.pj.edu.ritmp.Unsigned16BitIntegerBuf;

import benchmarks.determinism.pj.edu.ritpj.reduction.IntegerOp;
import benchmarks.determinism.pj.edu.ritpj.reduction.Op;

import java.nio.ByteBuffer;

/**
 * Class Unsigned16BitIntegerItemBuf provides a buffer for a single unsigned
 * 16-bit integer item sent or received using the Message Protocol (MP). While
 * an instance of class Unsigned16BitIntegerItemBuf may be constructed directly,
 * normally you will use a factory method in class {@linkplain
 * benchmarks.determinism.pj.edu.ritmp.Unsigned16BitIntegerBuf Unsigned16BitIntegerBuf}. See that class
 * for further information.
 *
 * @author  Alan Kaminsky
 * @version 26-Oct-2007
 */
public class Unsigned16BitIntegerItemBuf
	extends Unsigned16BitIntegerBuf
	{

// Exported data members.

	/**
	 * Integer item to be sent or received.
	 */
	public int item;

// Exported constructors.

	/**
	 * Construct a new unsigned 16-bit integer item buffer.
	 */
	public Unsigned16BitIntegerItemBuf()
		{
		super (1);
		}

	/**
	 * Construct a new unsigned 16-bit integer item buffer with the given
	 * initial value.
	 *
	 * @param  item  Initial value of the {@link #item} field.
	 */
	public Unsigned16BitIntegerItemBuf
		(int item)
		{
		super (1);
		this.item = item;
		}

// Exported operations.

	/**
	 * Obtain the given item from this buffer.
	 * <P>
	 * The <TT>get()</TT> method must not block the calling thread; if it does,
	 * all message I/O in MP will be blocked.
	 *
	 * @param  i  Item index in the range 0 .. <TT>length()</TT>-1.
	 *
	 * @return  Item at index <TT>i</TT>.
	 */
	public int get
		(int i)
		{
		return this.item;
		}

	/**
	 * Store the given item in this buffer.
	 * <P>
	 * The <TT>put()</TT> method must not block the calling thread; if it does,
	 * all message I/O in MP will be blocked.
	 *
	 * @param  i     Item index in the range 0 .. <TT>length()</TT>-1.
	 * @param  item  Item to be stored at index <TT>i</TT>.
	 */
	public void put
		(int i,
		 int item)
		{
		this.item = item;
		}

	/**
	 * Create a buffer for performing parallel reduction using the given binary
	 * operation. The results of the reduction are placed into this buffer.
	 *
	 * @param  op  Binary operation.
	 *
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if this buffer's element data type and
	 *     the given binary operation's argument data type are not the same.
	 */
	public Buf getReductionBuf
		(Op op)
		{
		return new Unsigned16BitIntegerItemReductionBuf (this, (IntegerOp) op);
		}

// Hidden operations.

	/**
	 * Send as many items as possible from this buffer to the given byte
	 * buffer.
	 * <P>
	 * The <TT>sendItems()</TT> method must not block the calling thread; if it
	 * does, all message I/O in MP will be blocked.
	 *
	 * @param  i       Index of first item to send, in the range 0 ..
	 *                 <TT>length</TT>-1.
	 * @param  buffer  Byte buffer.
	 *
	 * @return  Number of items sent.
	 */
	protected int sendItems
		(int i,
		 ByteBuffer buffer)
		{
		if (buffer.remaining() >= 2)
			{
			buffer.putShort ((short) this.item);
			return 1;
			}
		else
			{
			return 0;
			}
		}

	/**
	 * Receive as many items as possible from the given byte buffer to this
	 * buffer.
	 * <P>
	 * The <TT>receiveItems()</TT> method must not block the calling thread; if
	 * it does, all message I/O in MP will be blocked.
	 *
	 * @param  i       Index of first item to receive, in the range 0 ..
	 *                 <TT>length</TT>-1.
	 * @param  num     Maximum number of items to receive.
	 * @param  buffer  Byte buffer.
	 *
	 * @return  Number of items received.
	 */
	protected int receiveItems
		(int i,
		 int num,
		 ByteBuffer buffer)
		{
		if (num >= 1 && buffer.remaining() >= 2)
			{
			this.item = buffer.getShort() & 0xFFFF;
			return 1;
			}
		else
			{
			return 0;
			}
		}

	}
