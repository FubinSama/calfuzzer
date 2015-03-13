//******************************************************************************
//
// File:    Test17.java
// Package: benchmarks.determinism.pj.edu.ritpj.test
// Unit:    Class benchmarks.determinism.pj.edu.ritpj.test.Test17
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

package benchmarks.determinism.pj.edu.ritpj.test;

import benchmarks.determinism.pj.edu.ritmp.IntegerBuf;

import benchmarks.determinism.pj.edu.ritpj.Comm;

import benchmarks.determinism.pj.edu.ritpj.reduction.IntegerOp;

/**
 * Class Test17 is a unit test main program for the exclusive-scan collective
 * communication operation in class {@linkplain benchmarks.determinism.pj.edu.ritpj.Comm}. The program
 * runs on a number of processors in the cluster. Each process sets up an array
 * of data, fills it in, and prints it; the processes exclusive-scan the arrays
 * using sum as the reduction operator; and each process prints its final array.
 * <P>
 * Usage: java -Dpj.np=<I>K</I> benchmarks.determinism.pj.edu.ritpj.test.Test17 <I>N</I>
 * <BR><I>K</I> = Number of parallel processes
 * <BR><I>N</I> = Array length
 *
 * @author  Alan Kaminsky
 * @version 03-May-2008
 */
public class Test17
	{

// Prevent construction.

	private Test17()
		{
		}

// Global variables.

	// World communicator.
	static Comm world;
	static int size;
	static int rank;

	// Command line arguments.
	static int N;

	// Array.
	static int[] data;

// Main program.

	/**
	 * Unit test main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		// Initialize middleware.
		Comm.init (args);
		world = Comm.world();
		size = world.size();
		rank = world.rank();

		// Parse command line arguments.
		if (args.length != 1) usage();
		N = Integer.parseInt (args[0]);

		// Set up array.
		data = new int [N];
		for (int i = 0; i < N; ++ i)
			{
			data[i] = 100 * rank + i + 1;
			}

		// Print array before scan.
		System.out.print (rank);
		System.out.print (" before:");
		for (int i = 0; i < N; ++ i)
			{
			System.out.print (' ');
			System.out.print (data[i]);
			}
		System.out.println();

		// Exclusive-scan using sum as reduction operator.
		world.exclusiveScan
			(IntegerBuf.buffer (data), IntegerOp.SUM, new Integer (0));

		// Print array after scan.
		System.out.print (rank);
		System.out.print (" after:");
		for (int i = 0; i < N; ++ i)
			{
			System.out.print (' ');
			System.out.print (data[i]);
			}
		System.out.println();
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java -Dpj.np=<K> benchmarks.determinism.pj.edu.ritpj.test.Test17 <N>");
		System.err.println ("<K> = Number of parallel processes");
		System.err.println ("<N> = Array length");
		System.exit (1);
		}

	}
