//******************************************************************************
//
// File:    JobSchedulerException.java
// Package: benchmarks.detinfer.pj.edu.ritpj.cluster
// Unit:    Class benchmarks.detinfer.pj.edu.ritpj.cluster.JobSchedulerException
//
// This Java source file is copyright (C) 2006 by Alan Kaminsky. All rights
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

package benchmarks.detinfer.pj.edu.ritpj.cluster;

import java.io.IOException;

/**
 * Class JobSchedulerException is thrown to indicate that a job frontend process
 * was unable to contact the Job Scheduler Daemon.
 *
 * @author  Alan Kaminsky
 * @version 24-Oct-2006
 */
public class JobSchedulerException
	extends IOException
	{

// Exported constructors.

	/**
	 * Create a new Job Scheduler exception with no detail message and no cause.
	 */
	public JobSchedulerException()
		{
		super();
		}

	/**
	 * Create a new Job Scheduler exception with the given detail message and no
	 * cause.
	 *
	 * @param  msg  Detail message.
	 */
	public JobSchedulerException
		(String msg)
		{
		super (msg);
		}

	/**
	 * Create a new Job Scheduler exception with no detail message and the given
	 * cause.
	 *
	 * @param  cause  Cause.
	 */
	public JobSchedulerException
		(Throwable cause)
		{
		super();
		initCause (cause);
		}

	/**
	 * Create a new Job Scheduler exception with the given detail message and
	 * the given cause.
	 *
	 * @param  msg    Detail message.
	 * @param  cause  Cause.
	 */
	public JobSchedulerException
		(String msg,
		 Throwable cause)
		{
		super (msg);
		initCause (cause);
		}

	}
