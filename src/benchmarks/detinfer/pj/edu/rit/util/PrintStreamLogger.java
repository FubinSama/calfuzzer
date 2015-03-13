//******************************************************************************
//
// File:    PrintStreamLogger.java
// Package: benchmarks.detinfer.pj.edu.ritutil
// Unit:    Class benchmarks.detinfer.pj.edu.ritutil.PrintStreamLogger
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

import java.io.PrintStream;

import java.util.Date;

/**
 * Class PrintStreamLogger provides an object that logs messages to a print
 * stream.
 *
 * @author  Alan Kaminsky
 * @version 16-Apr-2008
 */
public class PrintStreamLogger
	implements Logger
	{

// Hidden operations.

	private PrintStream out;

// Exported constructors.

	/**
	 * Construct a new print stream logger that logs to <TT>System.err</TT>.
	 */
	public PrintStreamLogger()
		{
		this.out = System.err;
		}

	/**
	 * Construct a new print stream logger that logs to the given print stream.
	 *
	 * @param  out  Print stream.
	 *
	 * @exception  NullPointerException
	 *     (unchecked exception) Thrown if <TT>out</TT> is null.
	 */
	public PrintStreamLogger
		(PrintStream out)
		{
		if (out == null)
			{
			throw new NullPointerException
				("PrintStreamLogger(): Print stream is null");
			}
		this.out = out;
		}

// Exported operations.

	/**
	 * Log the given message.
	 *
	 * @param  msg  Message.
	 */
	public void log
		(String msg)
		{
		log (System.currentTimeMillis(), msg, null);
		}

	/**
	 * Log the given exception.
	 *
	 * @param  exc  Exception.
	 */
	public void log
		(Throwable exc)
		{
		log (System.currentTimeMillis(), null, exc);
		}

	/**
	 * Log the given message and exception.
	 *
	 * @param  msg  Message.
	 * @param  exc  Exception.
	 */
	public void log
		(String msg,
		 Throwable exc)
		{
		log (System.currentTimeMillis(), msg, exc);
		}

	/**
	 * Log the given date and message.
	 *
	 * @param  date  Date and time in milliseconds since midnight 01-Jan-1970
	 *               UTC.
	 * @param  msg   Message.
	 */
	public void log
		(long date,
		 String msg)
		{
		log (date, msg, null);
		}

	/**
	 * Log the given date and exception.
	 *
	 * @param  date  Date and time in milliseconds since midnight 01-Jan-1970
	 *               UTC.
	 * @param  exc   Exception.
	 */
	public void log
		(long date,
		 Throwable exc)
		{
		log (date, null, exc);
		}

	/**
	 * Log the given date, message, and exception.
	 *
	 * @param  date  Date and time in milliseconds since midnight 01-Jan-1970
	 *               UTC.
	 * @param  msg   Message.
	 * @param  exc   Exception.
	 */
	public void log
		(long date,
		 String msg,
		 Throwable exc)
		{
		synchronized (out)
			{
			out.print (new Date (date));
			if (msg != null)
				{
				out.print (' ');
				out.print (msg);
				}
			out.println();
			if (exc != null)
				{
				exc.printStackTrace (out);
				}
			}
		}

	}
