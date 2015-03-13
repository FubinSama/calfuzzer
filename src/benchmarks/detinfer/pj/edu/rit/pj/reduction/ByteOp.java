//******************************************************************************
//
// File:    ByteOp.java
// Package: benchmarks.detinfer.pj.edu.ritpj.reduction
// Unit:    Class benchmarks.detinfer.pj.edu.ritpj.reduction.ByteOp
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

/**
 * Class ByteOp is the abstract base class for a binary operation on byte
 * values, used to do reduction in a parallel program.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2007
 */
public abstract class ByteOp
	extends Op
	{

// Hidden constructors.

	/**
	 * Construct a new byte binary operation.
	 */
	protected ByteOp()
		{
		super();
		}

// Exported operations.

	/**
	 * Perform this binary operation.
	 *
	 * @param  x  First argument.
	 * @param  y  Second argument.
	 *
	 * @return  (<TT>x</TT> <I>op</I> <TT>y</TT>), where <I>op</I> stands for
	 *          this binary operation.
	 */
	public abstract byte op
		(byte x,
		 byte y);

// Exported constants.

	/**
	 * The byte sum binary operation.
	 */
	public static final ByteOp SUM =
		new ByteOp()
			{
			public byte op
				(byte x,
				 byte y)
				{
				return (byte) (x + y);
				}
			};

	/**
	 * The byte product binary operation.
	 */
	public static final ByteOp PRODUCT =
		new ByteOp()
			{
			public byte op
				(byte x,
				 byte y)
				{
				return (byte) (x * y);
				}
			};

	/**
	 * The byte minimum binary operation.
	 */
	public static final ByteOp MINIMUM =
		new ByteOp()
			{
			public byte op
				(byte x,
				 byte y)
				{
				return (byte) Math.min (x, y);
				}
			};

	/**
	 * The byte maximum binary operation.
	 */
	public static final ByteOp MAXIMUM =
		new ByteOp()
			{
			public byte op
				(byte x,
				 byte y)
				{
				return (byte) Math.max (x, y);
				}
			};

	}
