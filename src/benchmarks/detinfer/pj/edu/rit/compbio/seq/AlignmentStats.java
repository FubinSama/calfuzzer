//******************************************************************************
//
// File:    AlignmentStats.java
// Package: benchmarks.detinfer.pj.edu.ritcompbio.seq
// Unit:    Interface benchmarks.detinfer.pj.edu.ritcompbio.seq.AlignmentStats
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

package benchmarks.detinfer.pj.edu.ritcompbio.seq;

import java.io.PrintStream;

/**
 * Interface AlignmentStats specifies the interface for an object that computes
 * statistics of an {@linkplain Alignment}. Methods are provided to compute the
 * raw score, the bit score, and the <I>E</I>-value. The formulas for these
 * statistics depend on the procedure used to produce the alignment.
 *
 * @author  Alan Kaminsky
 * @version 01-Jul-2008
 */
public interface AlignmentStats
	{

// Exported operations.

	/**
	 * Returns the raw score for the given alignment. A larger raw score
	 * signifies a greater degree of similarity between the query sequence and
	 * subject sequence that were aligned.
	 *
	 * @param  alignment  Alignment.
	 *
	 * @return  Raw score.
	 */
	public double rawScore
		(Alignment alignment);

	/**
	 * Returns the bit score for the given alignment. A larger bit score
	 * signifies a greater degree of similarity between the query sequence and
	 * subject sequence that were aligned.
	 * <P>
	 * The bit score is the raw score normalized to units of "bits." Bit scores
	 * for different alignment procedures may be compared, whereas raw
	 * (unnormalized) scores for different alignment procedures may not be
	 * compared.
	 *
	 * @param  alignment  Alignment.
	 *
	 * @return  Bit score.
	 */
	public double bitScore
		(Alignment alignment);

	/**
	 * Returns the <I>E</I>-value (expect value) for the given alignment. A
	 * smaller <I>E</I>-value signifies a more statistically significant degree
	 * of similarity between the query sequence and subject sequence that were
	 * aligned.
	 * <P>
	 * The <I>E</I>-value is the expected number of alignments with a score
	 * greater than or equal to the <TT>alignment</TT>'s score when a
	 * randomly-chosen query of the same length as the query that produced the
	 * <TT>alignment</TT> is matched against the database.
	 *
	 * @param  alignment  Alignment.
	 *
	 * @return  Bit score.
	 */
	public double eValue
		(Alignment alignment);

	/**
	 * Print information about this alignment statistics object on the given
	 * print stream.
	 *
	 * @param  out  Print stream.
	 */
	public void print
		(PrintStream out);

	}
