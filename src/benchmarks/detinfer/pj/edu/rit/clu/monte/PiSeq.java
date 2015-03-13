//******************************************************************************
//
// File:    PiSeq.java
// Package: benchmarks.detinfer.pj.edu.ritclu.monte
// Unit:    Class benchmarks.detinfer.pj.edu.ritclu.monte.PiSeq
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

package benchmarks.detinfer.pj.edu.ritclu.monte;

import benchmarks.detinfer.pj.edu.ritpj.Comm;

import benchmarks.detinfer.pj.edu.ritutil.Random;

/**
 * Class PiSeq is a sequential program that calculates an approximate value for
 * &pi; using a Monte Carlo technique. The program generates a number of random
 * points in the unit square (0,0) to (1,1) and counts how many of them lie
 * within a circle of radius 1 centered at the origin. The fraction of the
 * points within the circle is approximately &pi;/4.
 * <P>
 * Usage: java benchmarks.detinfer.pj.edu.ritclu.monte.PiSeq <I>seed</I> <I>N</I>
 * <BR><I>seed</I> = Random seed
 * <BR><I>N</I> = Number of random points
 * <P>
 * The computation is performed sequentially in a single processor. The program
 * uses class benchmarks.detinfer.pj.edu.ritutil.Random for its pseudorandom number generator. The
 * program measures the computation's running time. This establishes a benchmark
 * for measuring the computation's running time on a parallel processor.
 *
 * @author  Alan Kaminsky
 * @version 31-Jan-2007
 */
public class PiSeq
	{

// Prevent construction.

	private PiSeq()
		{
		}

// Program shared variables.

	// Command line arguments.
	static long seed;
	static long N;

	// Pseudorandom number generator.
	static Random prng;

	// Number of points within the unit circle.
	static long count;

// Main program.

	/**
	 * Main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		// Start timing.
		long time = -System.currentTimeMillis();

		// Initialize middleware.
		Comm.init (args);

		// Validate command line arguments.
		if (args.length != 2) usage();
		seed = Long.parseLong (args[0]);
		N = Long.parseLong (args[1]);

		// Set up PRNG.
		prng = Random.getInstance (seed);

		// Generate n random points in the unit square, count how many are in
		// the unit circle.
		count = 0L;
		for (long i = 0L; i < N; ++ i)
			{
			double x = prng.nextDouble();
			double y = prng.nextDouble();
			if (x*x + y*y <= 1.0) ++ count;
			}

		// Stop timing.
		time += System.currentTimeMillis();

		// Print results.
		System.out.println (time + " msec total");
		System.out.println
			("pi = 4 * " + count + " / " + N + " = " +
			 (4.0 * count / N));
		}

// Hidden operations.

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java benchmarks.detinfer.pj.edu.ritclu.monte.PiSeq <seed> <N>");
		System.err.println ("<seed> = Random seed");
		System.err.println ("<N> = Number of random points");
		System.exit (1);
		}

	}
