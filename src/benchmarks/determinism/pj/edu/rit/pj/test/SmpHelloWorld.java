//******************************************************************************
//
// File:    SmpHelloWorld.java
// Package: benchmarks.determinism.pj.edu.ritpj.test
// Unit:    Class benchmarks.determinism.pj.edu.ritpj.test.SmpHelloWorld
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

import benchmarks.determinism.pj.edu.ritpj.IntegerForLoop;
import benchmarks.determinism.pj.edu.ritpj.ParallelRegion;
import benchmarks.determinism.pj.edu.ritpj.ParallelTeam;

/**
 * Class SmpHelloWorld is an SMP parallel unit test main program for classes
 * {@linkplain benchmarks.determinism.pj.edu.ritpj.ParallelTeam ParallelTeam}, {@linkplain
 * benchmarks.determinism.pj.edu.ritpj.ParallelRegion ParallelRegion}, and {@linkplain
 * benchmarks.determinism.pj.edu.ritpj.IntegerForLoop IntegerForLoop}.
 * <P>
 * Usage: java -Dpj.nt=4 benchmarks.determinism.pj.edu.ritpj.test.SmpHelloWorld <I>iters</I>
 *
 * @author  Alan Kaminsky
 * @version 29-Aug-2005
 */
public class SmpHelloWorld
	{
	// Prevent construction.
	private SmpHelloWorld()
		{
		}

	/**
	 * Unit test main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		long time = -System.currentTimeMillis();
		final int iters = Integer.parseInt (args[0]);

		new ParallelTeam().execute (new ParallelRegion()
			{
			public void run() throws Exception
				{
				execute (1, 4, new IntegerForLoop()
					{
					public void run (int first, int last)
						{
						for (int i = first; i <= last; ++ i)
							{
							int k = 0;
							for (int j = 0; j < iters; ++ j) k = k + 1;
							System.out.println
								("Hello, world, i = " + i + ", k = " + k);
							}
						}
					});
				}
			});

		time += System.currentTimeMillis();
		System.out.println (time + " msec");
		}
	}
