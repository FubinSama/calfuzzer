//******************************************************************************
//
// File:    TooManyIterationsException.java
// Package: benchmarks.detinfer.pj.edu.ritnumeric
// Unit:    Class benchmarks.detinfer.pj.edu.ritnumeric.TooManyIterationsException
//
// This Java source file is copyright (C) 2002-2004 by Alan Kaminsky. All rights
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

package benchmarks.detinfer.pj.edu.ritnumeric;

/**
 * Class TooManyIterationsException is an unchecked runtime exception thrown if
 * too many iterations have occurred when computing a function.
 *
 * @author  Alan Kaminsky
 * @version 08-Jan-2004
 */
public class TooManyIterationsException
	extends NumericRuntimeException
	{

// Exported constructors.

	/**
	 * Construct a new too-many-iterations exception with no detail message and
	 * no cause.
	 */
	public TooManyIterationsException()
		{
		super();
		}

	/**
	 * Construct a new too-many-iterations exception with the given detail
	 * message and no cause.
	 *
	 * @param  message  Detail message.
	 */
	public TooManyIterationsException
		(String message)
		{
		super (message);
		}

	/**
	 * Construct a new too-many-iterations exception with the given cause and
	 * the default detail message.
	 *
	 * @param  cause  Cause.
	 */
	public TooManyIterationsException
		(Throwable cause)
		{
		super (cause);
		}

	/**
	 * Construct a new too-many-iterations exception with the given detail
	 * message and the given cause.
	 */
	public TooManyIterationsException
		(String message,
		 Throwable cause)
		{
		super (message, cause);
		}

	}
