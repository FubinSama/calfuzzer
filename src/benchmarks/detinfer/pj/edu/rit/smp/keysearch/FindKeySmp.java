//******************************************************************************
//
// File:    FindKeySmp.java
// Package: benchmarks.detinfer.pj.edu.ritsmp.keysearch
// Unit:    Class benchmarks.detinfer.pj.edu.ritsmp.keysearch.FindKeySmp
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

package benchmarks.detinfer.pj.edu.ritsmp.keysearch;

import benchmarks.detinfer.pj.edu.ritcrypto.blockcipher.AES256Cipher;

//import benchmarks.detinfer.pj.edu.ritpj.Comm;
import benchmarks.detinfer.pj.edu.ritpj.IntegerForLoop;
import benchmarks.detinfer.pj.edu.ritpj.ParallelRegion;
import benchmarks.detinfer.pj.edu.ritpj.ParallelTeam;

import benchmarks.detinfer.pj.edu.ritutil.Hex;

/**
 * Class FindKeySmp is an SMP parallel program that solves an AES partial key
 * search problem. The program's command line arguments are the plaintext
 * (128-bit hexadecimal number), the ciphertext (128-bit hexadecimal number),
 * the partial key with the <I>n</I> least significant bits set to 0 (256-bit
 * hexadecimal number), and <I>n</I>, the number of key bits to search for. The
 * ciphertext was created by encrypting the plaintext with the key; however, not
 * all bits of the key are provided. The problem is to find the complete key.
 * The program performs an exhaustive search, trying all possible values for the
 * missing key bits until it finds the key that reproduces the given ciphertext
 * from the given plaintext.
 * <P>
 * Usage: java [ -Dpj.nt=<I>K</I> ] benchmarks.detinfer.pj.edu.ritsmp.keysearch.FindKeySmp
 * <I>plaintext</I> <I>ciphertext</I> <I>partialkey</I> <I>n</I>
 * <BR><I>K</I> = Number of parallel threads
 * <BR><I>plaintext</I> = Plaintext (128-bit hexadecimal number)
 * <BR><I>ciphertext</I> = Ciphertext (128-bit hexadecimal number)
 * <BR><I>partialkey</I> = Partial key (256-bit hexadecimal number)
 * <BR><I>n</I> = Number of key bits to search for
 *
 * @author  Alan Kaminsky
 * @version 05-Aug-2008
 */
public class FindKeySmp
	{

// Prevent construction.

	private FindKeySmp()
		{
		}

// Shared variables.

	// Command line arguments.
	static byte[] plaintext;
	static byte[] ciphertext;
	static byte[] partialkey;
	static int n;

	// The least significant 32 bits of the partial key.
	static int keylsbs;

	// The maximum value for the missing key bits counter.
	static int maxcounter;

	// The complete key.
	static byte[] foundkey;

// Main program.

	/**
	 * AES partial key search main program.
	 */
	public static void main
		(String[] args)
		throws Exception
		{
		//Comm.init (args);

		// Start timing.
		long t1 = System.currentTimeMillis();

		// Parse command line arguments.
		if (args.length != 4) usage();
		plaintext = Hex.toByteArray (args[0]);
		ciphertext = Hex.toByteArray (args[1]);
		partialkey = Hex.toByteArray (args[2]);
		n = Integer.parseInt (args[3]);

		// Make sure n is not too small or too large.
		if (n < 0)
			{
			System.err.println ("n = " + n + " is too small");
			System.exit (1);
			}
		if (n > 30)
			{
			System.err.println ("n = " + n + " is too large");
			System.exit (1);
			}

		// Set up program shared variables for doing trial encryptions.
		keylsbs =
			((partialkey[28] & 0xFF) << 24) |
			((partialkey[29] & 0xFF) << 16) |
			((partialkey[30] & 0xFF) <<  8) |
			((partialkey[31] & 0xFF)      );
		maxcounter = (1 << n) - 1;

		// Do trial encryptions in parallel.
		new ParallelTeam().execute (new ParallelRegion()
			{
			public void run() throws Exception
				{
				execute (0, maxcounter, new IntegerForLoop()
					{
					// Thread local variables.
					byte[] trialkey;
					byte[] trialciphertext;
					AES256Cipher cipher;

					// Set up thread local variables.
					public void start()
						{
						trialkey = new byte [32];
						System.arraycopy (partialkey, 0, trialkey, 0, 32);
						trialciphertext = new byte [16];
						cipher = new AES256Cipher (trialkey);
						}

					// Try every possible combination of low-order key bits.
					public void run (int first, int last)
						{
						for (int counter = first; counter <= last; ++ counter)
							{
							// Fill in low-order key bits.
							int lsbs = keylsbs | counter;
							trialkey[28] = (byte) (lsbs >>> 24);
							trialkey[29] = (byte) (lsbs >>> 16);
							trialkey[30] = (byte) (lsbs >>>  8);
							trialkey[31] = (byte) (lsbs       );

							// Try the key.
							cipher.setKey (trialkey);
							cipher.encrypt (plaintext, trialciphertext);

							// If the result equals the ciphertext, we found the
							// key.
							if (match (ciphertext, trialciphertext))
								{
								byte[] key = new byte [32];
								System.arraycopy (trialkey, 0, key, 0, 32);
								foundkey = key;
								}
							}
						}
					});
				}
			});

		// Stop timing.
		long t2 = System.currentTimeMillis();

		// Print the key we found.
		System.out.println (Hex.toString (foundkey));
		System.out.println ((t2-t1) + " msec");
		}

// Hidden operations.

	/**
	 * Returns true if the two byte arrays match.
	 */
	private static boolean match
		(byte[] a,
		 byte[] b)
		{
		boolean matchsofar = true;
		int n = a.length;
		for (int i = 0; i < n; ++ i)
			{
			matchsofar = matchsofar && a[i] == b[i];
			}
		return matchsofar;
		}

	/**
	 * Print a usage message and exit.
	 */
	private static void usage()
		{
		System.err.println ("Usage: java [-Dpj.nt=<K>] benchmarks.detinfer.pj.edu.ritsmp.keysearch.FindKeySmp <plaintext> <ciphertext> <partialkey> <n>");
		System.err.println ("<K> = Number of parallel threads");
		System.err.println ("<plaintext> = Plaintext (128-bit hexadecimal number)");
		System.err.println ("<ciphertext> = Ciphertext (128-bit hexadecimal number)");
		System.err.println ("<partialkey> = Partial key (256-bit hexadecimal number)");
		System.err.println ("<n> = Number of key bits to search for");
		System.exit (1);
		}

	}
