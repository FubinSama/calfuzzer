//******************************************************************************
//
// File:    CommStatus.java
// Package: benchmarks.determinism.pj.edu.ritpj
// Unit:    Class benchmarks.determinism.pj.edu.ritpj.CommStatus
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

package benchmarks.determinism.pj.edu.ritpj;

/**
 * Class CommStatus provides the result of receiving a message from a
 * communicator (class {@linkplain Comm}).
 *
 * @author  Alan Kaminsky
 * @version 09-Mar-2006
 */
public class CommStatus
	{

// Exported data members.

	/**
	 * The rank of the source process that sent the message.
	 */
	public int fromRank;

	/**
	 * The tag from the message that was received.
	 */
	public int tag;

	/**
	 * The actual number of items in the message that was received.
	 */
	public int length;

// Hidden constructors.

	/**
	 * Construct a new status object.
	 *
	 * @param  fromRank  Source process rank.
	 * @param  tag       Tag.
	 * @param  length    Length.
	 */
	CommStatus
		(int fromRank,
		 int tag,
		 int length)
		{
		this.fromRank = fromRank;
		this.tag = tag;
		this.length = length;
		}

	}
