//******************************************************************************
//
// File:    DefaultRandom.java
// Package: benchmarks.detinfer.pj.edu.ritutil
// Unit:    Class benchmarks.detinfer.pj.edu.ritutil.DefaultRandom
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

package benchmarks.detinfer.pj.edu.ritutil;

/**
 * Class DefaultRandom provides a default pseudorandom number generator (PRNG)
 * designed for use in parallel scientific programming. To create an instance of
 * class DefaultRandom, either use the <TT>DefaultRandom()</TT> constructor, or
 * use the static <TT>getInstance(long)</TT> method in class {@linkplain
 * Random}.
 * <P>
 * Class DefaultRandom generates random numbers by hashing successive counter
 * values. The seed initializes the counter. The hash function is defined in W.
 * Press et al., <I>Numerical Recipes: The Art of Scientific Computing, Third
 * Edition</I> (Cambridge University Press, 2007), page 352. The hash function
 * applied to the counter value <I>i</I> is:
 * <P>
 * <I>x</I> := 3935559000370003845 * <I>i</I> + 2691343689449507681 (mod 2<SUP>64</SUP>)
 * <BR><I>x</I> := <I>x</I> xor (<I>x</I> right-shift 21)
 * <BR><I>x</I> := <I>x</I> xor (<I>x</I> left-shift 37)
 * <BR><I>x</I> := <I>x</I> xor (<I>x</I> right-shift 4)
 * <BR><I>x</I> := 4768777513237032717 * <I>x</I> (mod 2<SUP>64</SUP>)
 * <BR><I>x</I> := <I>x</I> xor (<I>x</I> left-shift 20)
 * <BR><I>x</I> := <I>x</I> xor (<I>x</I> right-shift 41)
 * <BR><I>x</I> := <I>x</I> xor (<I>x</I> left-shift 5)
 * <BR>Return <I>x</I>
 * <P>
 * (The shift and arithmetic operations are all performed on unsigned 64-bit
 * numbers.)
 *
 * @author  Alan Kaminsky
 * @version 30-Mar-2008
 */
public class DefaultRandom
	extends Random
	{

// Hidden data members.

	// Seed for this PRNG.
	private long seed;

	// 128 bytes of extra padding to avert cache interference.
	private transient long p0, p1, p2, p3, p4, p5, p6, p7;
	private transient long p8, p9, pa, pb, pc, pd, pe, pf;

// Exported constructors.

	/**
	 * Construct a new PRNG with the given seed. Any seed value is allowed.
	 *
	 * @param  seed  Seed.
	 */
	public DefaultRandom
		(long seed)
		{
		setSeed (seed);
		}

// Exported operations.

	/**
	 * Set this PRNG's seed. Any seed value is allowed.
	 *
	 * @param  seed  Seed.
	 */
	public void setSeed
		(long seed)
		{
		this.seed = hash (seed);
		}

// Hidden operations.

	/**
	 * Return the next 64-bit pseudorandom value in this PRNG's sequence.
	 *
	 * @return  Pseudorandom value.
	 */
	protected long next()
		{
		++ seed;
		return hash (seed);
		}

	/**
	 * Return the 64-bit pseudorandom value the given number of positions ahead
	 * in this PRNG's sequence.
	 *
	 * @param  skip  Number of positions to skip, assumed to be &gt; 0.
	 *
	 * @return  Pseudorandom value.
	 */
	protected long next
		(long skip)
		{
		seed += skip;
		return hash (seed);
		}

	/**
	 * Return the hash of the given value.
	 */
	private static long hash
		(long x)
		{
		x = 3935559000370003845L * x + 2691343689449507681L;
		x = x ^ (x >>> 21);
		x = x ^ (x << 37);
		x = x ^ (x >>> 4);
		x = 4768777513237032717L * x;
		x = x ^ (x << 20);
		x = x ^ (x >>> 41);
		x = x ^ (x << 5);
		return x;
		}

	}
