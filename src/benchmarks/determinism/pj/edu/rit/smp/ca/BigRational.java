//******************************************************************************
//
// File:    BigRational.java
// Package: benchmarks.determinism.pj.edu.ritsmp.ca
// Unit:    Class benchmarks.determinism.pj.edu.ritsmp.ca.BigRational
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

package benchmarks.determinism.pj.edu.ritsmp.ca;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

/**
 * Class BigRational provides an arbitrary precision rational number. An
 * arbitrary precision rational number is the ratio of two arbitrary precision
 * integers (type java.math.BigInteger). Operations are provided for exact
 * arithmetic with rational numbers.
 * <P>
 * A rational number is said to be <B>normalized</B> if
 * GCD(numerator,denominator) = 1. The methods below do <B><I>not</I></B>
 * automatically normalize the rational number. Thus, the numerator and
 * denominator tend to get larger and larger as operations are performed on the
 * rational number. To reduce the numerator and denominator to lowest terms
 * again, call the <TT>normalize()</TT> method. It is up to you to decide
 * whether to normalize the rational number after each operation, or after a
 * series of operations.
 * <P>
 * Class BigRational is not multiple thread safe.
 *
 * @author  Alan Kaminsky
 * @version 05-Nov-2007
 */
public class BigRational
	{

// Hidden data members.

	private BigInteger numer;
	private BigInteger denom;

// Exported constructors.

	/**
	 * Construct a new rational number. Its value is 0.
	 */
	public BigRational()
		{
		numer = BigInteger.ZERO;
		denom = BigInteger.ONE;
		}

	/**
	 * Construct a new rational number. Its value is the same as <TT>x</TT>.
	 *
	 * @param  x  Rational number.
	 */
	public BigRational
		(BigRational x)
		{
		assign (x);
		}

	/**
	 * Construct a new rational number. Its value comes from the string
	 * <TT>s</TT>. The string must obey this syntax: optional minus sign
	 * (<TT>-</TT>), numerator (decimal integer), slash (<TT>/</TT>),
	 * denominator (decimal integer). The slash-and-denominator is optional. If
	 * present, the denominator must be greater than 0. There is no whitespace
	 * within the string.
	 *
	 * @param  s  String.
	 *
	 * @exception  NumberFormatException
	 *     (unchecked exception) Thrown if <TT>s</TT> cannot be parsed into a
	 *     rational number.
	 */
	public BigRational
		(String s)
		{
		assign (s);
		}

// Exported operations.

	/**
	 * Set this rational number to the given number.
	 *
	 * @param  x  Rational number.
	 *
	 * @return  This rational number, with its value set to <TT>x</TT>.
	 */
	public BigRational assign
		(BigRational x)
		{
		this.numer = x.numer;
		this.denom = x.denom;
		return this;
		}

	/**
	 * Set this rational number to the value parsed from the given string. The
	 * string must obey this syntax: optional minus sign (<TT>-</TT>), numerator
	 * (decimal integer), slash (<TT>/</TT>), denominator (decimal integer). The
	 * slash-and-denominator is optional. If present, the denominator must be
	 * greater than 0. There is no whitespace within the string.
	 *
	 * @param  s  String.
	 *
	 * @return  This rational number, with its value set to <TT>s</TT>.
	 *
	 * @exception  NumberFormatException
	 *     (unchecked exception) Thrown if <TT>s</TT> cannot be parsed into a
	 *     rational number.
	 */
	public BigRational assign
		(String s)
		{
		BigInteger numer, denom;
		int iSlash = s.indexOf ('/');
		if (iSlash == -1)
			{
			// No denominator.
			numer = new BigInteger (s);
			denom = BigInteger.ONE;
			}
		else if (iSlash+1 < s.length())
			{
			// Slash and denominator.
			numer = new BigInteger (s.substring (0, iSlash));
			denom = new BigInteger (s.substring (iSlash+1));
			if (denom.compareTo (BigInteger.ZERO) <= 0)
				{
				throw new NumberFormatException
					("BigRational.assign(): Negative denominator not allowed");
				}
			}
		else
			{
			// Slash but no denominator.
			throw new NumberFormatException
				("BigRational.assign(): Missing denominator after /");
			}
		this.numer = numer;
		this.denom = denom;
		return this;
		}

	/**
	 * Set this rational number to the fractional part of itself.
	 *
	 * @return  This rational number, with its value set to <TT>frac(this)</TT>.
	 */
	public BigRational fracPart()
		{
		numer = numer.remainder (denom);
		return this;
		}

	/**
	 * Set this rational number to the sum of itself and the given number.
	 *
	 * @param  x  Rational number.
	 *
	 * @return  This rational number, with its value set to <TT>this+x</TT>.
	 */
	public BigRational add
		(BigRational x)
		{
		this.numer =
			this.numer.multiply(x.denom).add (x.numer.multiply (this.denom));
		this.denom = this.denom.multiply (x.denom);
		return this;
		}

	/**
	 * Set this rational number to the product of itself and the given number.
	 *
	 * @param  x  Rational number.
	 *
	 * @return  This rational number, with its value set to <TT>this*x</TT>.
	 */
	public BigRational mul
		(BigRational x)
		{
		this.numer = this.numer.multiply (x.numer);
		this.denom = this.denom.multiply (x.denom);
		return this;
		}

	/**
	 * Normalize this rational number. Afterwards, the denominator is greater
	 * than or equal to 1, and the denominator does not divide the numerator; in
	 * other words, GCD(numerator,denominator) = 1.
	 *
	 * @return  This rational number, normalized.
	 */
	public BigRational normalize()
		{
		int sign = numer.signum() * denom.signum();
		numer = numer.abs();
		denom = denom.abs();
		BigInteger gcd = numer.gcd (denom);
		numer = numer.divide (gcd);
		denom = denom.divide (gcd);
		if (sign < 0) numer = numer.negate();
		return this;
		}

	/**
	 * Converts this rational number to a single precision floating point
	 * number.
	 *
	 * @return  Single precision floating point value of this rational number.
	 */
	public float floatValue()
		{
		BigDecimal n = new BigDecimal (numer);
		BigDecimal d = new BigDecimal (denom);
		return n.divide (d, 6, RoundingMode.HALF_UP) .floatValue();
		}

	/**
	 * Returns a string version of this rational number. If this rational
	 * number's denominator is 1, the string is in the form
	 * <TT>"&lt;numer&gt;"</TT>, otherwise the string is in the form
	 * <TT>"&lt;numer&gt;/&lt;denom&gt;"</TT>.
	 */
	public String toString()
		{
		if (this.denom.compareTo (BigInteger.ONE) == 0)
			{
			return this.numer.toString();
			}
		else
			{
			return this.numer + "/" + this.denom;
			}
		}

	}
