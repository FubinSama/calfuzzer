//******************************************************************************
//
// File:    InvalidMatrixFileException.java
// Package: benchmarks.detinfer.pj.edu.ritio
// Unit:    Class benchmarks.detinfer.pj.edu.ritio.InvalidMatrixFileException
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

package benchmarks.detinfer.pj.edu.ritio;

import java.io.IOException;

/**
 * Class InvalidMatrixFileException provides an exception thrown when the
 * contents of a matrix file are invalid. The detail message and/or chained
 * exception give further information about the problem.
 *
 * @author  Alan Kaminsky
 * @version 07-Jan-2008
 */
public class InvalidMatrixFileException
	extends IOException
	{

// Exported constructors.

	/**
	 * Construct a new invalid matrix file exception with no detail message and
	 * no cause.
	 */
	public InvalidMatrixFileException()
		{
		super();
		}

	/**
	 * Construct a new invalid matrix file exception with the given detail
	 * message and no cause.
	 *
	 * @param  message  Detail message.
	 */
	public InvalidMatrixFileException
		(String message)
		{
		super (message);
		}

	/**
	 * Construct a new invalid matrix file exception with the given cause and
	 * the default detail message.
	 *
	 * @param  cause  Cause.
	 */
	public InvalidMatrixFileException
		(Throwable cause)
		{
		super();
		initCause (cause);
		}

	/**
	 * Construct a new invalid matrix file exception with the given detail
	 * message and the given cause.
	 *
	 * @param  message  Detail message.
	 * @param  cause  Cause.
	 */
	public InvalidMatrixFileException
		(String message,
		 Throwable cause)
		{
		super (message);
		initCause (cause);
		}

	}
