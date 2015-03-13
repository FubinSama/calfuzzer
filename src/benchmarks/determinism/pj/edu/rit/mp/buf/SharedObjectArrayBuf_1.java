//******************************************************************************
//
// File:    SharedObjectArrayBuf_1.java
// Package: benchmarks.determinism.pj.edu.ritmp.buf
// Unit:    Class benchmarks.determinism.pj.edu.ritmp.buf.SharedObjectArrayBuf_1
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
import benchmarks.determinism.pj.edu.ritmp.ObjectBuf;

import benchmarks.determinism.pj.edu.ritpj.reduction.ObjectOp;
import benchmarks.determinism.pj.edu.ritpj.reduction.Op;
import benchmarks.determinism.pj.edu.ritpj.reduction.SharedObjectArray;

import benchmarks.determinism.pj.edu.ritutil.Range;

/**
 * Class SharedObjectArrayBuf_1 provides a buffer for a multiple thread safe
 * array of object items sent or received using the Message Protocol (MP).
 * The array element stride must be 1. While an instance of class
 * SharedObjectArrayBuf_1 may be constructed directly, normally you will use
 * a factory method in class {@linkplain benchmarks.determinism.pj.edu.ritmp.ObjectBuf ObjectBuf}.
 * See that class for further information.
 *
 * @param  <T>  Data type of the objects in the buffer.
 *
 * @author  Alan Kaminsky
 * @version 26-Oct-2007
 */
public class SharedObjectArrayBuf_1<T>
	extends SharedObjectArrayBuf<T>
	{

// Exported constructors.

	/**
	 * Construct a new shared object array buffer.
	 *
	 * @param  theArray  Shared array.
	 * @param  theRange  Range of array elements to include in the buffer. The
	 *                   stride is assumed to be 1.
	 */
	public SharedObjectArrayBuf_1
		(SharedObjectArray<T> theArray,
		 Range theRange)
		{
		super (theArray, theRange);
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
	public T get
		(int i)
		{
		return myArray.get (myArrayOffset+i);
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
		 T item)
		{
		myArray.set (myArrayOffset+i, item);
		mySerializedItems = null;
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
		return new SharedObjectArrayReductionBuf_1<T>
			(myArray, myRange, (ObjectOp<T>) op);
		}

	}
