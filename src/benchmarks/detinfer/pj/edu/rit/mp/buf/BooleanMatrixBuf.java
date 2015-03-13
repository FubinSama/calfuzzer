//******************************************************************************
//
// File:    BooleanMatrixBuf.java
// Package: benchmarks.detinfer.pj.edu.ritmp.buf
// Unit:    Class benchmarks.detinfer.pj.edu.ritmp.buf.BooleanMatrixBuf
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
import benchmarks.detinfer.pj.edu.ritmp.BooleanBuf;

import benchmarks.detinfer.pj.edu.ritpj.reduction.BooleanOp;
import benchmarks.detinfer.pj.edu.ritpj.reduction.Op;

import benchmarks.detinfer.pj.edu.ritutil.Arrays;
import benchmarks.detinfer.pj.edu.ritutil.Range;

import java.nio.ByteBuffer;

/**
 * Class BooleanMatrixBuf provides a buffer for a matrix of Boolean items sent
 * or received using the Message Protocol (MP). The matrix row and column
 * strides may be 1 or greater than 1. While an instance of class
 * BooleanMatrixBuf may be constructed directly, normally you will use a factory
 * method in class {@linkplain benchmarks.detinfer.pj.edu.ritmp.BooleanBuf BooleanBuf}. See that class
 * for further information.
 *
 * @author  Alan Kaminsky
 * @version 03-Mar-2008
 */
public class BooleanMatrixBuf
	extends BooleanBuf
	{

// Hidden data members.

	boolean[][] myMatrix;
	Range myRowRange;
	Range myColRange;
	int myLowerRow;
	int myRowCount;
	int myRowStride;
	int myLowerCol;
	int myColCount;
	int myColStride;

// Exported constructors.

	/**
	 * Construct a new Boolean matrix buffer. It is assumed that the rows and
	 * columns of <TT>theMatrix</TT> are allocated and that each row of
	 * <TT>theMatrix</TT> has the same number of columns.
	 *
	 * @param  theMatrix    Matrix.
	 * @param  theRowRange  Range of rows to include.
	 * @param  theColRange  Range of columns to include.
	 */
	public BooleanMatrixBuf
		(boolean[][] theMatrix,
		 Range theRowRange,
		 Range theColRange)
		{
		super (theRowRange.length() * theColRange.length());
		myMatrix = theMatrix;
		myRowRange = theRowRange;
		myColRange = theColRange;
		myLowerRow = theRowRange.lb();
		myRowCount = theRowRange.length();
		myRowStride = theRowRange.stride();
		myLowerCol = theColRange.lb();
		myColCount = theColRange.length();
		myColStride = theColRange.stride();
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
	public boolean get
		(int i)
		{
		return myMatrix
			[i2r(i) * myRowStride + myLowerRow]
			[i2c(i) * myColStride + myLowerCol];
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
		 boolean item)
		{
		myMatrix
			[i2r(i) * myRowStride + myLowerRow]
			[i2c(i) * myColStride + myLowerCol] = item;
		}

	/**
	 * Copy items from the given buffer to this buffer. The number of items
	 * copied is this buffer's length or <TT>theSrc</TT>'s length, whichever is
	 * smaller. If <TT>theSrc</TT> is this buffer, the <TT>copy()</TT> method
	 * does nothing.
	 *
	 * @param  theSrc  Source of items to copy into this buffer.
	 *
	 * @exception  ClassCastException
	 *     (unchecked exception) Thrown if <TT>theSrc</TT>'s item data type is
	 *     not the same as this buffer's item data type.
	 */
	public void copy
		(Buf theSrc)
		{
		if (theSrc == this)
			{
			}
		else if (theSrc instanceof BooleanMatrixBuf)
			{
			BooleanMatrixBuf src = (BooleanMatrixBuf) theSrc;
			Arrays.copy
				(src.myMatrix, src.myRowRange, src.myColRange,
				 this.myMatrix, this.myRowRange, this.myColRange);
			}
		else
			{
			BooleanBuf.defaultCopy ((BooleanBuf) theSrc, this);
			}
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
		return new BooleanMatrixReductionBuf
			(myMatrix, myRowRange, myColRange, (BooleanOp) op);
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
		int n = 0;
		int r = i2r(i);
		int row = r * myRowStride + myLowerRow;
		int c = i2c(i);
		int col = c * myColStride + myLowerCol;
		int ncols = Math.min (myColCount - c, buffer.remaining());
		while (r < myRowCount && ncols > 0)
			{
			boolean[] myMatrix_row = myMatrix[row];
			while (c < ncols)
				{
				buffer.put (myMatrix_row[col] ? (byte) 1 : (byte) 0);
				++ c;
				col += myColStride;
				}
			n += ncols;
			++ r;
			row += myRowStride;
			c = 0;
			col = myLowerCol;
			ncols = Math.min (myColCount, buffer.remaining());
			}
		return n;
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
		int n = 0;
		int r = i2r(i);
		int row = r * myRowStride + myLowerRow;
		int c = i2c(i);
		int col = c * myColStride + myLowerCol;
		int ncols = Math.min (myColCount - c, buffer.remaining());
		while (r < myRowCount && ncols > 0)
			{
			boolean[] myMatrix_row = myMatrix[row];
			while (c < ncols)
				{
				myMatrix_row[col] = buffer.get() != 0;
				++ c;
				col += myColStride;
				}
			n += ncols;
			++ r;
			row += myRowStride;
			c = 0;
			col = myLowerCol;
			ncols = Math.min (myColCount, buffer.remaining());
			}
		return n;
		}

	/**
	 * Convert the given buffer index to a row index.
	 */
	int i2r
		(int i)
		{
		return myColCount == 0 ? i : i / myColCount;
		}

	/**
	 * Convert the given buffer index to a column index.
	 */
	int i2c
		(int i)
		{
		return myColCount == 0 ? 0 : i % myColCount;
		}

	}
