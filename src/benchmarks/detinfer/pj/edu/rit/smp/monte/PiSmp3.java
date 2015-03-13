//******************************************************************************
//
// File:    PiSmp3.java
// Package: benchmarks.detinfer.pj.edu.ritsmp.monte
// Unit:    Class benchmarks.detinfer.pj.edu.ritsmp.monte.PiSmp3
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

package benchmarks.detinfer.pj.edu.ritsmp.monte;

//import benchmarks.detinfer.pj.edu.ritpj.Comm;
import benchmarks.detinfer.pj.edu.ritpj.LongForLoop;
import benchmarks.detinfer.pj.edu.ritpj.ParallelTeam;
import benchmarks.detinfer.pj.edu.ritpj.ParallelRegion;

import benchmarks.detinfer.pj.edu.ritpj.reduction.SharedLong;

import benchmarks.detinfer.pj.edu.ritutil.Random;

import static javato.determinism.DeterminismInference.openDeterministicBlock;
import static javato.determinism.DeterminismInference.closeDeterministicBlock;

/**
 * Class PiSmp3 is an SMP parallel program that calculates an approximate value
 * for &pi; using a Monte Carlo technique. The program generates a number of
 * random points in the unit square (0,0) to (1,1) and counts how many of them
 * lie within a circle of radius 1 centered at the origin. The fraction of the
 * points within the circle is approximately &pi;/4.
 * <P>
 * Usage: java -Dpj.nt=<I>K</I> benchmarks.detinfer.pj.edu.ritsmp.monte.PiSmp3 <I>seed</I> <I>N</I>
 * <BR><I>K</I> = Number of parallel threads
 * <BR><I>seed</I> = Random seed
 * <BR><I>N</I> = Number of random points
 * <P>
 * The computation is performed in parallel in multiple threads. The program
 * uses class benchmarks.detinfer.pj.edu.ritutil.Random for its pseudorandom number generator. To
 * improve performance, each thread has its own pseudorandom number generator,
 * and the program uses the reduction pattern to determine the count. The
 * program uses the "sequence splitting" technique with the pseudorandom number
 * generators to yield results identical to the sequential version. The program
 * measures the computation's running time.
 *
 * @author  Alan Kaminsky
 * @version 29-Feb-2008
 */
public class PiSmp3
	{

// Prevent construction.

	private PiSmp3()
		{
		}

// Program shared variables.

	// Command line arguments.
	static long seed;
	static long N;

	// Number of points within the unit circle.
	static SharedLong count;

// Main program.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		//Comm.init (args);

		// Start timing.
		long time = -System.currentTimeMillis();

		// Validate command line arguments.
		if (args.length != 2) usage();
		seed = Long.parseLong (args[0]);
		N = Long.parseLong (args[1]);

                openDeterministicBlock(new String[] { "args", "time" },
                                       new Object[] {  args,   time });

		// Generate n random points in the unit square, count how many are in
		// the unit circle.
		count = new SharedLong (0);
		new ParallelTeam().execute (new ParallelRegion()
			{
			public void run() throws Exception
				{
				execute (0, N-1, new LongForLoop()
					{
					// Set up per-thread PRNG and counter.
					Random prng_thread = Random.getInstance (seed);
					long count_thread = 0;

					// Extra padding to avert cache interference.
					long p0, p1, p2, p3, p4, p5, p6, p7;
					long p8, p9, pa, pb, pc, pd, pe, pf;

					// Parallel loop body.
					public void run (long first, long last)
						{
						// Skip PRNG ahead to index <first>.
						prng_thread.setSeed (seed);
						prng_thread.skip (2 * first);

						// Generate random points.
						for (long i = first; i <= last; ++ i)
							{
							double x = prng_thread.nextDouble();
							double y = prng_thread.nextDouble();
							if (x*x + y*y <= 1.0) ++ count_thread;
							}
						}

					public void finish()
						{
						// Reduce per-thread counts into shared count.
						count.addAndGet (count_thread);
						}
					});
				}
			});

		// Stop timing.
		time += System.currentTimeMillis();

                closeDeterministicBlock(new String[] { "args", "time" },
                                        new Object[] {  args,   time });

		// Print results.
		System.out.println
			("pi = 4 * " + count + " / " + N + " = " +
			 (4.0 * count.doubleValue() / N));
		System.out.println (time + " msec");
		}

// Hidden operations.

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java -Dpj.nt=<K> benchmarks.detinfer.pj.edu.ritsmp.monte.PiSmp3 <seed> <N>");
		System.err.println ("<K> = Number of parallel threads");
		System.err.println ("<seed> = Random seed");
		System.err.println ("<N> = Number of random points");
		System.exit (1);
		}

	}
