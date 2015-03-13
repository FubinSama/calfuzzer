//******************************************************************************
//
// File:    LongSchedule.java
// Package: benchmarks.detinfer.pj.edu.ritpj
// Unit:    Class benchmarks.detinfer.pj.edu.ritpj.LongSchedule
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

package benchmarks.detinfer.pj.edu.ritpj;

import benchmarks.detinfer.pj.edu.ritutil.LongRange;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Class LongSchedule provides an object that determines how to schedule the
 * iterations of a {@linkplain ParallelForLoop} among the threads in a
 * {@linkplain ParallelTeam}. The loop index is type <TT>long</TT>.
 * <P>
 * To create a schedule object, call one of the following static methods:
 * <UL>
 * <LI><TT>LongSchedule.fixed()</TT>
 * <LI><TT>LongSchedule.dynamic()</TT>
 * <LI><TT>LongSchedule.guided()</TT>
 * <LI><TT>LongSchedule.runtime()</TT>
 * <LI><TT>LongSchedule.parse()</TT>
 * </UL>
 * <P>
 * The Parallel Java Library includes three built-in schedule implementations:
 * fixed, dynamic, and guided. You can create instances of these by calling the
 * <TT>fixed()</TT>, <TT>dynamic()</TT>, and <TT>guided()</TT> methods. You can
 * also create your own schedule implementation by writing a subclass of class
 * LongSchedule. The subclass must have a no-argument constructor and a
 * constructor whose argument is an array of Strings; see the <TT>parse()</TT>
 * method for further information about how these constructors are used.
 *
 * @author  Alan Kaminsky
 * @version 18-Jul-2008
 */
public abstract class LongSchedule
	extends Schedule
	{

// Hidden data members.

	// Loop index for ordered() construct.
	AtomicLong myOrderedIndex = new AtomicLong();

// Hidden constructors.

	/**
	 * Construct a new schedule object.
	 */
	protected LongSchedule()
		{
		super();
		}

// Exported operations.

	/**
	 * Returns a fixed schedule object. The loop iterations are apportioned
	 * among the parallel team threads once at the beginning of the parallel for
	 * loop, with each thread getting a fixed number of iterations, the same
	 * number of iterations for each thread (plus or minus one).
	 *
	 * @return  Fixed schedule object.
	 */
	public static LongSchedule fixed()
		{
		return new FixedLongSchedule();
		}

	/**
	 * Returns a dynamic schedule object with a chunk size of 1. The loop
	 * iterations are apportioned into chunks of size 1 (one iteration per
	 * chunk). Each parallel team thread repeatedly performs the next available
	 * iteration until there are no more iterations.
	 *
	 * @return  Dynamic schedule object.
	 */
	public static LongSchedule dynamic()
		{
		return new DynamicLongSchedule (1);
		}

	/**
	 * Returns a dynamic schedule object with the given chunk size. The loop
	 * iterations are apportioned into chunks of size <TT>theChunkSize</TT>
	 * (<TT>theChunkSize</TT> iterations per chunk). Each parallel team thread
	 * repeatedly performs the next available chunk of iterations until there
	 * are no more chunks. The final chunk may be smaller than
	 * <TT>theChunkSize</TT>.
	 *
	 * @param  theChunkSize  Chunk size, &gt;= 1.
	 *
	 * @return  Dynamic schedule object.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theChunkSize</TT> &lt; 1.
	 */
	public static LongSchedule dynamic
		(long theChunkSize)
		{
		return new DynamicLongSchedule (theChunkSize);
		}

	/**
	 * Returns a self-guided schedule object with a minimum chunk size of 1. The
	 * loop iterations are apportioned into chunks of exponentially diminishing
	 * sizes. Each successive chunk's size is half the remaining number of
	 * iterations divided by the number of threads in the parallel team.
	 * However, each chunk's size is at least 1 (a minimum of one iteration per
	 * chunk). Each parallel team thread repeatedly performs the next available
	 * chunk of iterations until there are no more chunks.
	 *
	 * @return  Self-guided schedule object.
	 */
	public static LongSchedule guided()
		{
		return new GuidedLongSchedule (1);
		}

	/**
	 * Returns a self-guided schedule object with the given minimum chunk size.
	 * The loop iterations are apportioned into chunks of exponentially
	 * diminishing sizes. Each successive chunk's size is half the remaining
	 * number of iterations divided by the number of threads in the parallel
	 * team. However, each chunk is at least <TT>theChunkSize</TT> (a minimum of
	 * <TT>theChunkSize</TT> iterations per chunk). Each parallel team thread
	 * repeatedly performs the next available chunk of iterations until there
	 * are no more chunks. The final chunk may be smaller than
	 * <TT>theChunkSize</TT>.
	 *
	 * @param  theChunkSize  Minimum chunk size, &gt;= 1.
	 *
	 * @return  Self-guided schedule object.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>theChunkSize</TT> &lt; 1.
	 */
	public static LongSchedule guided
		(long theChunkSize)
		{
		return new GuidedLongSchedule (theChunkSize);
		}

	/**
	 * Returns a schedule object of a type determined at run time. If the
	 * <TT>"pj.schedule"</TT> Java property is specified, the property's value
	 * is parsed by the <TT>parse()</TT> method, and that gives the type of
	 * schedule. If the <TT>"pj.schedule"</TT> Java property is not specified,
	 * the default is a fixed schedule. You can specify the schedule on the Java
	 * command line like this (note that quotation marks may be needed):
	 * <PRE>
	 *     java -Dpj.schedule="dynamic(5)" . . .
	 * </PRE>
	 *
	 * @return  Schedule object.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if the <TT>"pj.schedule"</TT> property
	 *     value cannot be parsed.
	 */
	public static LongSchedule runtime()
		{
		return runtime (LongSchedule.fixed());
		}

	/**
	 * Returns a schedule object of a type determined at run time, using the
	 * given default schedule. If the <TT>"pj.schedule"</TT> Java property is
	 * specified, the property's value is parsed by the <TT>parse()</TT> method,
	 * and that gives the type of schedule. If the <TT>"pj.schedule"</TT> Java
	 * property is not specified, the given <TT>defaultSchedule</TT> is
	 * returned. You can specify the schedule on the Java command line like this
	 * (note that quotation marks may be needed):
	 * <PRE>
	 *     java -Dpj.schedule="dynamic(5)" . . .
	 * </PRE>
	 *
	 * @param  defaultSchedule  Schedule to use if the <TT>"pj.schedule"</TT>
	 *                          Java property is not specified.
	 *
	 * @return  Schedule object.
	 *
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if the <TT>"pj.schedule"</TT> property
	 *     value cannot be parsed.
	 */
	public static LongSchedule runtime
		(LongSchedule defaultSchedule)
		{
		String s = PJProperties.getPjSchedule();
		return s == null ? defaultSchedule : parse (s);
		}

	/**
	 * Returns a schedule object of a type determined by parsing the given
	 * string. The string must be one of the following:
	 * <UL>
	 * <P><LI><TT>"fixed"</TT> -- Fixed schedule.
	 * <P><LI><TT>"dynamic"</TT> -- Dynamic schedule with a chunk size of 1.
	 * <P><LI><TT>"dynamic(<I>n</I>)"</TT> -- Dynamic schedule with a chunk
	 * size of <TT><I>n</I></TT>, an integer &gt;= 1.
	 * <P><LI><TT>"guided"</TT> -- Self-guided schedule with a minimum chunk
	 * size of 1.
	 * <P><LI><TT>"guided(<I>n</I>)"</TT> -- Self-guided schedule with a
	 * minimum chunk size of <TT><I>n</I></TT>, an integer &gt;= 1.
	 * <P><LI><TT>"<I>classname</I>"</TT> -- Schedule that is an instance of the
	 * given class. <I>classname</I> is the fully-qualified class name of the
	 * schedule class, which must be a subclass of class LongSchedule. The
	 * instance is constructed using the subclass's no-argument constructor.
	 * <P><LI><TT>"<I>classname</I>(<I>arg</I>,<I>arg</I>,...)"</TT> -- Schedule
	 * that is an instance of the given class. <I>classname</I> is the
	 * fully-qualified class name of the schedule class, which must be a
	 * subclass of class LongSchedule. The arguments between the parentheses
	 * are split into separate strings separated by commas. There cannot be
	 * parentheses or commas within the arguments themselves. The instance is
	 * constructed using the subclass's constructor whose argument is an array
	 * of Strings, namely the individual arguments between the parentheses.
	 * </UL>
	 *
	 * @param  s  String to parse.
	 *
	 * @return  Schedule object.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>s</TT> is null.
	 * @exception  IllegalArgumentException
	 *     (unchecked exception) Thrown if <TT>s</TT> is not one of the above.
	 */
	public static LongSchedule parse
		(String s)
		{
		try
			{
			int p1 = s.indexOf ('(');

			if (p1 == -1)
				{
				// No arguments. s is the subclass name. Get subclass.
				Class<?> subclass =
					Class.forName
						(getSubclassName (s),
						 true,
						 Thread.currentThread().getContextClassLoader());

				// Instantiate subclass using no-argument constructor.
				return (LongSchedule) subclass.newInstance();
				}

			else
				{
				// Arguments. Verify syntax.
				int p2 = s.indexOf (')');
				if (p2 != s.length()-1)
					{
					throw new IllegalArgumentException
						("LongSchedule.parse(): Schedule \"" + s +
						 "\" illegal");
					}

				// Split arguments around commas.
				String[] args = s.substring (p1+1, p2) .split (",");

				// s up to '(' is the subclass name. Get subclass.
				Class<?> subclass =
					Class.forName
						(getSubclassName (s.substring (0, p1)),
						 true,
						 Thread.currentThread().getContextClassLoader());

				// Get constructor with one String[] argument.
				Constructor<?> constructor =
					subclass.getConstructor (String[].class);

				// Instantiate subclass using String[]-argument constructor.
				return (LongSchedule)
					constructor.newInstance ((Object) args);
				}
			}

		catch (ClassCastException exc)
			{
			throw new IllegalArgumentException
				("LongSchedule.parse(): Schedule \"" + s + "\" illegal",
				 exc);
			}
		catch (ClassNotFoundException exc)
			{
			throw new IllegalArgumentException
				("LongSchedule.parse(): Schedule \"" + s + "\" illegal",
				 exc);
			}
		catch (IllegalAccessException exc)
			{
			throw new IllegalArgumentException
				("LongSchedule.parse(): Schedule \"" + s + "\" illegal",
				 exc);
			}
		catch (InstantiationException exc)
			{
			throw new IllegalArgumentException
				("LongSchedule.parse(): Schedule \"" + s + "\" illegal",
				 exc);
			}
		catch (InvocationTargetException exc)
			{
			throw new IllegalArgumentException
				("LongSchedule.parse(): Schedule \"" + s + "\" illegal",
				 exc);
			}
		catch (NoSuchMethodException exc)
			{
			throw new IllegalArgumentException
				("LongSchedule.parse(): Schedule \"" + s + "\" illegal",
				 exc);
			}
		}

// Hidden operations.

	/**
	 * Get the name of the subclass to instantiate. The names <TT>"fixed"</TT>,
	 * <TT>"dynamic"</TT>, and <TT>"guided"</TT> are recognized as special
	 * cases.
	 *
	 * @param  name  Subclass name, or special case string.
	 *
	 * @return  Subclass name.
	 */
	private static String getSubclassName
		(String name)
		{
		if (name.equals ("fixed"))
			{
			return "benchmarks.detinfer.pj.edu.ritpj.FixedLongSchedule";
			}
		else if (name.equals ("dynamic"))
			{
			return "benchmarks.detinfer.pj.edu.ritpj.DynamicLongSchedule";
			}
		else if (name.equals ("guided"))
			{
			return "benchmarks.detinfer.pj.edu.ritpj.GuidedLongSchedule";
			}
		else
			{
			return name;
			}
		}

	/**
	 * Start a parallel for loop using this schedule. This method performs
	 * common processing, then calls the subclass-specific <TT>start()</TT>
	 * method.
	 *
	 * @param  K             Number of threads in the parallel team.
	 * @param  theLoopRange  Range of iterations for the entire parallel for
	 *                       loop. The stride may be 1 or greater.
	 */
	void commonStart
		(int K,
		 LongRange theLoopRange)
		{
		myBreak = false;
		myOrderedIndex.set (theLoopRange.lb());
		start (K, theLoopRange);
		}

	/**
	 * Start generating chunks of iterations for a parallel for loop using this
	 * schedule.
	 * <P>
	 * The <TT>start()</TT> method is only called by a single thread in the
	 * Parallel Java middleware.
	 *
	 * @param  K             Number of threads in the parallel team.
	 * @param  theLoopRange  Range of iterations for the entire parallel for
	 *                       loop. The stride may be 1 or greater.
	 */
	public abstract void start
		(int K,
		 LongRange theLoopRange);

	/**
	 * Obtain the next chunk of iterations for the given thread index. This
	 * method performs common processing, then calls the subclass-specific
	 * <TT>next()</TT> method.
	 *
	 * @param  theThreadIndex  Thread index in the range 0 .. <I>K</I>-1.
	 *
	 * @return  Chunk of iterations, or null if no more iterations.
	 */
	LongRange commonNext
		(int theThreadIndex)
		{
		if (myBreak)
			{
			return null;
			}
		else
			{
			return next (theThreadIndex);
			}
		}

	/**
	 * Obtain the next chunk of iterations for the given thread index. If there
	 * are more iterations, a range object is returned whose lower bound, upper
	 * bound, and stride specify the chunk of iterations to perform. The
	 * returned range object's stride is the same as that given to the
	 * <TT>start()</TT> method. The returned range object's lower bound and
	 * upper bound are contained within the range given to the <TT>start()</TT>
	 * method. If there are no more iterations, null is returned.
	 * <P>
	 * The <TT>next()</TT> method is called by multiple parallel team threads in
	 * the Parallel Java middleware. The <TT>next()</TT> method must be multiple
	 * thread safe.
	 *
	 * @param  theThreadIndex  Thread index in the range 0 .. <I>K</I>-1.
	 *
	 * @return  Chunk of iterations, or null if no more iterations.
	 */
	public abstract LongRange next
		(int theThreadIndex);

// Unit test main program.

//	/**
//	 * Unit test main program.
//	 */
//	public static void main
//		(String[] args)
//		{
//		if (args.length != 4)
//			{
//			System.err.println ("Usage: java [-Dpj.schedule=<SCHEDULE>] benchmarks.detinfer.pj.edu.ritpj.LongSchedule <K> <lb> <ub> <stride>");
//			System.exit (1);
//			}
//		int K = Integer.parseInt (args[0]);
//		long lb = Long.parseLong (args[1]);
//		long ub = Long.parseLong (args[2]);
//		long stride = Long.parseLong (args[3]);
//		LongSchedule schedule = LongSchedule.runtime();
//		schedule.start (K, new LongRange (lb, ub, stride));
//		LongRange chunk;
//		for (int k = 0; k < K; ++ k)
//			{
//			while ((chunk = schedule.next (k)) != null)
//				{
//				System.out.println ("Thread " + k + " chunk = " + chunk);
//				}
//			}
//		}

	}
