//******************************************************************************
//
// File:    ObjectMatrixBuf_1.java
// Package: benchmarks.detinfer.pj.edu.ritmp.buf
// Unit:    Class benchmarks.detinfer.pj.edu.ritmp.buf.ObjectMatrixBuf_1
//
// This Java source file is copyright (C) 2008 by Alan Kaminsky. All rights
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

package benchmarks.detinfer.pj.edu.ritmp.buf;

import benchmarks.detinfer.pj.edu.ritmp.Buf;
import benchmarks.detinfer.pj.edu.ritmp.ObjectBuf;

import benchmarks.detinfer.pj.edu.ritpj.reduction.ObjectOp;
import benchmarks.detinfer.pj.edu.ritpj.reduction.Op;

import benchmarks.detinfer.pj.edu.ritutil.Arrays;
import benchmarks.detinfer.pj.edu.ritutil.Range;

/**
 * Class ObjectMatrixBuf_1 provides a buffer for a matrix of object items
 * sent or received using the Message Protocol (MP). The matrix row and column
 * strides must both be 1. While an instance of class ObjectMatrixBuf_1 may
 * be constructed directly, normally you will use a factory method in class
 * {@linkplain benchmarks.detinfer.pj.edu.ritmp.ObjectBuf ObjectBuf}. See that class for further
 * information.
 *
 * @param  <T>  Data type of the objects in the buffer.
 *
 * @author  Alan Kaminsky
 * @version 03-Mar-2008
 */
public class ObjectMatrixBuf_1<T>
	extends ObjectMatrixBuf<T>
	{

// Exported constructors.

	/**
	 * Construct a new object matrix buffer. It is assumed that the rows and
	 * columns of <TT>theMatrix</TT> are allocated and that each row of
	 * <TT>theMatrix</TT> has the same number of columns.
	 *
	 * @param  theMatrix    Matrix.
	 * @param  theRowRange  Range of rows to include. The stride is assumed to
	 *                      be 1.
	 * @param  theColRange  Range of columns to include. The stride is assumed
	 *                      to be 1.
	 */
	public ObjectMatrixBuf_1
		(T[][] theMatrix,
		 Range theRowRange,
		 Range theColRange)
		{
		super (theMatrix, theRowRange, theColRange);
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
		return myMatrix
			[i2r(i) + myLowerRow]
			[i2c(i) + myLowerCol];
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
		myMatrix
			[i2r(i) + myLowerRow]
			[i2c(i) + myLowerCol] = item;
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
		return new ObjectMatrixReductionBuf_1<T>
			(myMatrix, myRowRange, myColRange, (ObjectOp<T>) op);
		}

	}
