//******************************************************************************
//
// File:    SharedIntegerArray.java
// Package: benchmarks.detinfer.pj.edu.ritpj.reduction
// Unit:    Class benchmarks.detinfer.pj.edu.ritpj.reduction.SharedIntegerArray
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

package benchmarks.detinfer.pj.edu.ritpj.reduction;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Class SharedIntegerArray provides an array reduction variable with elements
 * of type <TT>int</TT>.
 * <P>
 * Class SharedIntegerArray is multiple thread safe. The methods use lock-free
 * atomic compare-and-set.
 * <P>
 * <I>Note:</I> Class SharedIntegerArray is implemented using class
 * java.util.concurrent.atomic.AtomicIntegerArray.
 *
 * @author  Alan Kaminsky
 * @version 24-Aug-2007
 */
public class SharedIntegerArray
	{

// Hidden data members.

	private AtomicIntegerArray myArray;

// Exported constructors.

	/**
	 * Construct a new integer array reduction variable with the given length.
	 * Each array element is initially 0.
	 *
	 * @param  len  Length.
	 *
	 * @exception  NegativeArraySizeException
	 *     (unchecked exception) Thrown if <TT>len</TT> &lt; 0.
	 */
	public SharedIntegerArray
		(int len)
		{
		myArray = new AtomicIntegerArray (len);
		}

	/**
	 * Construct a new integer array reduction variable whose elements are
	 * copied from the given array.
	 *
	 * @param  array  Array to copy.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>array</TT> is null.
	 */
	public SharedIntegerArray
		(int[] array)
		{
		myArray = new AtomicIntegerArray (array);
		}

// Exported operations.

	/**
	 * Returns this array reduction variable's length.
	 *
	 * @return  Length.
	 */
	public int length()
		{
		return myArray.length();
		}

	/**
	 * Returns this array reduction variable's current value at the given index.
	 *
	 * @param  i  Index.
	 *
	 * @return  Current value.
	 */
	public int get
		(int i)
		{
		return myArray.get (i);
		}

	/**
	 * Set this array reduction variable at the given index to the given value.
	 *
	 * @param  i      Index.
	 * @param  value  New value.
	 */
	public void set
		(int i,
		 int value)
		{
		myArray.set (i, value);
		}

	/**
	 * Set this array reduction variable at the given index to the given value
	 * and return the previous value.
	 *
	 * @param  i      Index.
	 * @param  value  New value.
	 *
	 * @return  Previous value.
	 */
	public int getAndSet
		(int i,
		 int value)
		{
		return myArray.getAndSet (i, value);
		}

	/**
	 * Atomically set this array reduction variable at the given index to the
	 * given updated value if the current value equals the expected value.
	 *
	 * @param  i       Index.
	 * @param  expect  Expected value.
	 * @param  update  Updated value.
	 *
	 * @return  True if the update happened, false otherwise.
	 */
	public boolean compareAndSet
		(int i,
		 int expect,
		 int update)
		{
		return myArray.compareAndSet (i, expect, update);
		}

	/**
	 * Atomically set this array reduction variable at the given index to the
	 * given updated value if the current value equals the expected value. May
	 * fail spuriously.
	 *
	 * @param  i       Index.
	 * @param  expect  Expected value.
	 * @param  update  Updated value.
	 *
	 * @return  True if the update happened, false otherwise.
	 */
	public boolean weakCompareAndSet
		(int i,
		 int expect,
		 int update)
		{
		return myArray.weakCompareAndSet (i, expect, update);
		}

	/**
	 * Add one to this array reduction variable at the given index and return
	 * the previous value.
	 *
	 * @param  i  Index.
	 *
	 * @return  Previous value.
	 */
	public int getAndIncrement
		(int i)
		{
		return myArray.getAndIncrement (i);
		}

	/**
	 * Subtract one from this array reduction variable at the given index and
	 * return the previous value.
	 *
	 * @param  i  Index.
	 *
	 * @return  Previous value.
	 */
	public int getAndDecrement
		(int i)
		{
		return myArray.getAndDecrement (i);
		}

	/**
	 * Add the given value to this array reduction variable at the given index
	 * and return the previous value.
	 *
	 * @param  i      Index.
	 * @param  value  Value to add.
	 *
	 * @return  Previous value.
	 */
	public int getAndAdd
		(int i,
		 int value)
		{
		return myArray.getAndAdd (i, value);
		}

	/**
	 * Add one to this array reduction variable at the given index and return
	 * the new value.
	 *
	 * @param  i  Index.
	 *
	 * @return  New value.
	 */
	public int incrementAndGet
		(int i)
		{
		return myArray.incrementAndGet (i);
		}

	/**
	 * Subtract one from this array reduction variable at the given index and
	 * return the new value.
	 *
	 * @param  i  Index.
	 *
	 * @return  New value.
	 */
	public int decrementAndGet
		(int i)
		{
		return myArray.decrementAndGet (i);
		}

	/**
	 * Add the given value to this array reduction variable at the given index
	 * and return the new value.
	 *
	 * @param  i      Index.
	 * @param  value  Value to add.
	 *
	 * @return  New value.
	 */
	public int addAndGet
		(int i,
		 int value)
		{
		return myArray.addAndGet (i, value);
		}

	/**
	 * Combine this array reduction variable at the given index with the given
	 * value using the given operation. (This array <TT>[i]</TT>) is set to
	 * (this array <TT>[i]</TT>) <I>op</I> (<TT>value</TT>), then (this array
	 * <TT>[i]</TT>) is returned.
	 *
	 * @param  i      Index.
	 * @param  value  Value.
	 * @param  op     Binary operation.
	 *
	 * @return  (This array <TT>[i]</TT>) <I>op</I> (<TT>value</TT>).
	 */
	public int reduce
		(int i,
		 int value,
		 IntegerOp op)
		{
		for (;;)
			{
			int oldvalue = myArray.get (i);
			int newvalue = op.op (oldvalue, value);
			if (myArray.compareAndSet (i, oldvalue, newvalue)) return newvalue;
			}
		}

	/**
	 * Combine this array reduction variable with the given array using the
	 * given operation. For each index <TT>i</TT> from 0 to this array's
	 * length-1, (this array <TT>[i]</TT>) is set to (this array <TT>[i]</TT>)
	 * <I>op</I> (<TT>src[i]</TT>).
	 * <P>
	 * The <TT>reduce()</TT> method is multiple thread safe <I>on a per-element
	 * basis.</I> Each individual array element is updated atomically, but the
	 * array as a whole is not updated atomically.
	 *
	 * @param  src  Source array.
	 * @param  op   Binary operation.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>src</TT> is null. Thrown if
	 *     <TT>op</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if any array index would be out of
	 *     bounds.
	 */
	public void reduce
		(int[] src,
		 IntegerOp op)
		{
		reduce (0, src, 0, myArray.length(), op);
		}

	/**
	 * Combine a portion of this array reduction variable with a portion of the
	 * given array using the given operation. For each index <TT>i</TT> from 0
	 * to <TT>len</TT>-1, (this array <TT>[dstoff+i]</TT>) is set to (this array
	 * <TT>[dstoff+i]</TT>) <I>op</I> (<TT>src[srcoff+i]</TT>).
	 * <P>
	 * The <TT>reduce()</TT> method is multiple thread safe <I>on a per-element
	 * basis.</I> Each individual array element is updated atomically, but the
	 * array as a whole is not updated atomically.
	 *
	 * @param  dstoff  Index of first element to update in this array.
	 * @param  src     Source array.
	 * @param  srcoff  Index of first element to update from in the source
	 *                 array.
	 * @param  len     Number of array elements to update.
	 * @param  op      Binary operation.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>src</TT> is null. Thrown if
	 *     <TT>op</TT> is null.
	 * @exception  IndexOutOfBoundsException
	 *     (unchecked exception) Thrown if <TT>len</TT> &lt; 0. Thrown if any
	 *     array index would be out of bounds.
	 */
	public void reduce
		(int dstoff,
		 int[] src,
		 int srcoff,
		 int len,
		 IntegerOp op)
		{
		if
			(len < 0 ||
			 dstoff < 0 || dstoff+len > myArray.length() ||
			 srcoff < 0 || srcoff+len > src.length)
			{
			throw new IndexOutOfBoundsException();
			}
		while (len > 0)
			{
			updateLoop: for (;;)
				{
				int oldvalue = myArray.get (dstoff);
				int newvalue = op.op (oldvalue, src[srcoff]);
				if (myArray.compareAndSet (dstoff, oldvalue, newvalue))
					break updateLoop;
				}
			++ dstoff;
			++ srcoff;
			-- len;
			}
		}

	/**
	 * Returns a string version of this array reduction variable.
	 *
	 * @return  String version.
	 */
	public String toString()
		{
		return myArray.toString();
		}

	}
