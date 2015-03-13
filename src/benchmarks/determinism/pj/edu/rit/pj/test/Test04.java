//******************************************************************************
//
// File:    Test04.java
// Package: benchmarks.determinism.pj.edu.ritpj.test
// Unit:    Class benchmarks.determinism.pj.edu.ritpj.test.Test04
//
// This Java source file is copyright (C) 2005 by Alan Kaminsky. All rights
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

package benchmarks.determinism.pj.edu.ritpj.test;

import benchmarks.determinism.pj.edu.ritpj.ParallelRegion;
import benchmarks.determinism.pj.edu.ritpj.ParallelTeam;

/**
 * Class Test04 is a unit test main program for classes {@linkplain
 * benchmarks.determinism.pj.edu.ritpj.ParallelTeam ParallelTeam} and {@linkplain
 * benchmarks.determinism.pj.edu.ritpj.ParallelRegion ParallelRegion}. Each thread in the team throws an
 * exception.
 * <P>
 * Usage: java -Dpj.nt=<I>k</I> benchmarks.determinism.pj.edu.ritpj.test.Test04
 * <BR><I>k</I> = Number of parallel threads
 *
 * @author  Alan Kaminsky
 * @version 29-Aug-2005
 */
public class Test04
	{

// Prevent construction.

	private Test04()
		{
		}

// Main program.

	/**
	 * Unit test main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		new ParallelTeam().execute (new ParallelRegion()
			{
			public void start()
				{
				System.out.println ("Start");
				}

			public void run() throws Exception
				{
				int i = getThreadIndex();
				Thread.sleep (1000L * i);
				System.out.println ("Run thread " + i);
				throw new Exception ("Exception in thread " + i);
				}

			public void finish()
				{
				System.out.println ("Finish");
				}
			});
		}

	}
