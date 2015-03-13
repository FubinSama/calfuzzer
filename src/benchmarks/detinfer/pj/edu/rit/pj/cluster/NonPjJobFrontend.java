//******************************************************************************
//
// File:    NonPjJobFrontend.java
// Package: benchmarks.detinfer.pj.edu.ritpj.cluster
// Unit:    Class benchmarks.detinfer.pj.edu.ritpj.cluster.NonPjJobFrontend
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

package benchmarks.detinfer.pj.edu.ritpj.cluster;

import benchmarks.detinfer.pj.edu.ritmp.Channel;
import benchmarks.detinfer.pj.edu.ritmp.ChannelGroup;
import benchmarks.detinfer.pj.edu.ritmp.ChannelGroupClosedException;
import benchmarks.detinfer.pj.edu.ritmp.Status;

import benchmarks.detinfer.pj.edu.ritmp.ObjectBuf;

import benchmarks.detinfer.pj.edu.ritmp.buf.ObjectItemBuf;

import benchmarks.detinfer.pj.edu.ritpj.PJProperties;

import benchmarks.detinfer.pj.edu.ritutil.Timer;
import benchmarks.detinfer.pj.edu.ritutil.TimerTask;
import benchmarks.detinfer.pj.edu.ritutil.TimerThread;

import java.io.File;
import java.io.IOException;

import java.net.InetSocketAddress;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Class NonPjJobFrontend provides the message handler for a job frontend
 * process that communicates with the Job Scheduler to allocate backend nodes
 * but does not run a PJ program.
 *
 * @author  Alan Kaminsky
 * @version 21-May-2008
 */
public class NonPjJobFrontend
	implements Runnable, JobFrontendRef
	{

// Hidden data members.

	// User name.
	private String username;

	// Job number.
	private int jobnum;

	// Job resources.
	private int Np;

	// Timer thread for lease renewals and expirations.
	private TimerThread myLeaseTimerThread;

	// Timers for the lease with the Job Scheduler.
	private Timer mySchedulerRenewTimer;
	private Timer mySchedulerExpireTimer;

	// Timer for the job timeout if any.
	private Timer myJobTimer;

	// Middleware channel group.
	private ChannelGroup myMiddlewareChannelGroup;

	// Proxy for Job Scheduler Daemon.
	private JobSchedulerRef myJobScheduler;

	// Flag for shutting down the run() method.
	private boolean continueRun = true;

	// State of this job frontend.
	private State myState = State.RUNNING;
		private static enum State
			{RUNNING,
			 TERMINATE_CANCEL_JOB,
			 TERMINATING};

	// Error message if job canceled, or null if job finished normally.
	private String myCancelMessage = "User canceled job";

	// List of backend names assigned to the job.
	private LinkedList<String> myBackendNames = new LinkedList<String>();

// Exported constructors.

	/**
	 * Construct a new non-PJ job frontend object. The job frontend object will
	 * contact the Job Scheduler Daemon specified by the <TT>"pj.host"</TT> and
	 * <TT>"pj.port"</TT> Java system properties. See class {@linkplain
	 * benchmarks.detinfer.pj.edu.ritpj.PJProperties} for further information.
	 * <P>
	 * The non-PJ job frontend object will ask the Job Scheduler Daemon to run
	 * one process per node and to use all CPUs on each node. Other
	 * possibilities are not supported.
	 *
	 * @param  username  User name.
	 * @param  Np        Number of processes (&gt;= 1).
	 *
	 * @exception  JobSchedulerException
	 *     (subclass of IOException) Thrown if the job frontend object could not
	 *     contact the Job Scheduler Daemon.
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public NonPjJobFrontend
		(String username,
		 int Np)
		throws IOException
		{
		// Record arguments.
		this.username = username;
		this.Np = Np;

		// Set up shutdown hook.
		Runtime.getRuntime().addShutdownHook (new Thread()
			{
			public void run()
				{
				shutdown();
				}
			});

		// Set up lease timer thread.
		myLeaseTimerThread = new TimerThread();
		myLeaseTimerThread.setDaemon (true);
		myLeaseTimerThread.start();

		// Set up Job Scheduler lease timers.
		mySchedulerRenewTimer =
			myLeaseTimerThread.createTimer
				(new TimerTask()
					{
					public void action
						(Timer timer)
						{
						try
							{
							schedulerRenewTimeout();
							}
						catch (Throwable exc)
							{
							}
						}
					});
		mySchedulerExpireTimer =
			myLeaseTimerThread.createTimer
				(new TimerTask()
					{
					public void action
						(Timer timer)
						{
						try
							{
							schedulerExpireTimeout();
							}
						catch (Throwable exc)
							{
							}
						}
					});

		// Set up job timer.
		myJobTimer =
			myLeaseTimerThread.createTimer
				(new TimerTask()
					{
					public void action
						(Timer timer)
						{
						try
							{
							jobTimeout();
							}
						catch (Throwable exc)
							{
							}
						}
					});

		// Set up middleware channel group.
		myMiddlewareChannelGroup = new ChannelGroup();

		// Set up Job Scheduler proxy.
		InetSocketAddress js_address = null;
		Channel js_channel = null;
		try
			{
			js_address =
				new InetSocketAddress
					(PJProperties.getPjHost(),
					 PJProperties.getPjPort());
			js_channel = myMiddlewareChannelGroup.connect (js_address);
			}
		catch (IOException exc)
			{
			throw new JobSchedulerException
				("JobFrontend(): Cannot contact Job Scheduler Daemon at " +
					js_address,
				 exc);
			}
		myJobScheduler =
			new JobSchedulerProxy (myMiddlewareChannelGroup, js_channel);

		// Start Job Scheduler lease timers.
		mySchedulerRenewTimer.start
			(Constants.LEASE_RENEW_INTERVAL,
			 Constants.LEASE_RENEW_INTERVAL);
		mySchedulerExpireTimer.start
			(Constants.LEASE_EXPIRE_INTERVAL);

		// Kick off the job!
		myJobScheduler.requestJob (this, username, Np, Np, 0);
		}

// Exported operations.

	/**
	 * Obtain the job number assigned to this Non-PJ Job Frontend. This method
	 * blocks until the requested number of backend processes (a constructor
	 * parameter) have been assigned.
	 *
	 * @return  Job number.
	 *
	 * @exception  InterruptedException
	 *     Thrown if the calling thread is interrupted while blocked in this
	 *     method.
	 */
	public synchronized int getJobNumber()
		throws InterruptedException
		{
		while (myBackendNames.size() < Np) wait();
		return jobnum;
		}

	/**
	 * Obtain a list of the backend names assigned to this Non-PJ Job Frontend.
	 * This method blocks until the requested number of backend processes (a
	 * constructor parameter) have been assigned. The returned list is
	 * unmodifiable.
	 *
	 * @return  List of backend names.
	 *
	 * @exception  InterruptedException
	 *     Thrown if the calling thread is interrupted while blocked in this
	 *     method.
	 */
	public synchronized List<String> getBackendNames()
		throws InterruptedException
		{
		while (myBackendNames.size() < Np) wait();
		return Collections.unmodifiableList (myBackendNames);
		}

	/**
	 * Run this Non-PJ Job Frontend.
	 */
	public void run()
		{
		ObjectItemBuf<JobFrontendMessage> buf =
			ObjectBuf.buffer ((JobFrontendMessage) null);
		Status status = null;
		JobFrontendMessage message = null;

		try
			{
			while (continueRun)
				{
				// Receive a message from any channel.
				status = myMiddlewareChannelGroup.receive (null, null, buf);
				message = buf.item;

				// Process a message from the Job Scheduler.
				if (status.tag == Message.FROM_JOB_SCHEDULER)
					{
					message.invoke (this, myJobScheduler);
					}

				// Enable garbage collection of no-longer-needed objects while
				// waiting to receive next message.
				buf.item = null;
				status = null;
				message = null;
				}
			}
		catch (ChannelGroupClosedException exc)
			{
			}
		catch (Throwable exc)
			{
			terminateCancelJob (exc);
			}

		// Exit process if necessary.
		switch (myState)
			{
			case TERMINATE_CANCEL_JOB:
				System.exit (1);
				break;
			case RUNNING:
			case TERMINATING:
				break;
			}
		}

	/**
	 * Terminate this Non-PJ Job Frontend immediately, sending a "job finished"
	 * message to the Job Scheduler. This method must only be called by a thread
	 * other than the thread calling <TT>run()</TT>. This method calls
	 * <TT>System.exit(status)</TT> to terminate the process.
	 *
	 * @param  status  Status value for <TT>System.exit()</TT>.
	 */
	public void terminateJobFinished
		(int status)
		{
		boolean doExit = false;
		synchronized (this)
			{
			continueRun = false;
			if (myState == State.RUNNING)
				{
				myCancelMessage = null;
				doExit = true;
				}
			}

		// Cannot hold the synchronization lock while calling System.exit(),
		// otherwise a deadlock can occur between this thread and the shutdown
		// hook thread.
		if (doExit) System.exit (status);
		}

	/**
	 * Assign a backend process to the job.
	 *
	 * @param  theJobScheduler  Job Scheduler that is calling this method.
	 * @param  name             Backend node name.
	 * @param  host             Host name for SSH remote login.
	 * @param  jvm              Full pathname of Java Virtual Machine.
	 * @param  classpath        Java class path for PJ Library.
	 * @param  jvmflags         Array of JVM command line flags.
	 * @param  Nt               Number of CPUs assigned to the process.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void assignBackend
		(JobSchedulerRef theJobScheduler,
		 String name,
		 String host,
		 String jvm,
		 String classpath,
		 String[] jvmflags,
		 int Nt)
		throws IOException
		{
		// Record backend name.
		myBackendNames.add (name);

		// If all backends have been assigned, start job timer.
		if (myBackendNames.size() == Np)
			{
			int jobtime = PJProperties.getPjJobTime();
			if (jobtime > 0)
				{
				myJobTimer.start (jobtime * 1000L);
				}
			}

		notifyAll();
		}

	/**
	 * Assign a job number to the job. The host name for the job frontend's
	 * middleware channel group is also specified.
	 *
	 * @param  theJobScheduler  Job Scheduler that is calling this method.
	 * @param  jobnum           Job number.
	 * @param  pjhost           Host name for middleware channel group.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void assignJobNumber
		(JobSchedulerRef theJobScheduler,
		 int jobnum,
		 String pjhost)
		throws IOException
		{
		// Record job number.
		this.jobnum = jobnum;
		notifyAll();
		}

	/**
	 * Cancel the job.
	 *
	 * @param  theJobScheduler  Job Scheduler that is calling this method.
	 * @param  errmsg           Error message string.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void cancelJob
		(JobSchedulerRef theJobScheduler,
		 String errmsg)
		throws IOException
		{
		terminateCancelJob (errmsg);
		}

	/**
	 * Renew the lease on the job.
	 *
	 * @param  theJobScheduler  Job Scheduler that is calling this method.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void renewLease
		(JobSchedulerRef theJobScheduler)
		throws IOException
		{
		mySchedulerExpireTimer.start (Constants.LEASE_EXPIRE_INTERVAL);
		}

	/**
	 * Report that a backend process has finished executing the job.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void backendFinished
		(JobBackendRef theJobBackend)
		throws IOException
		{
		}

	/**
	 * Report that a backend process is ready to commence executing the job.
	 *
	 * @param  theJobBackend
	 *     Job Backend that is calling this method.
	 * @param  rank
	 *     Rank of the job backend process.
	 * @param  middlewareAddress
	 *     Host/port to which the job backend process is listening for
	 *     middleware messages.
	 * @param  worldAddress
	 *     Host/port to which the job backend process is listening for the world
	 *     communicator.
	 * @param  frontendAddress
	 *     Host/port to which the job backend process is listening for the
	 *     frontend communicator, or null if the frontend communicator does not
	 *     exist.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void backendReady
		(JobBackendRef theJobBackend,
		 int rank,
		 InetSocketAddress middlewareAddress,
		 InetSocketAddress worldAddress,
		 InetSocketAddress frontendAddress)
		throws IOException
		{
		}

	/**
	 * Cancel the job.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  errmsg         Error message string.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void cancelJob
		(JobBackendRef theJobBackend,
		 String errmsg)
		throws IOException
		{
		}

	/**
	 * Renew the lease on the job.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void renewLease
		(JobBackendRef theJobBackend)
		throws IOException
		{
		}

	/**
	 * Request the given resource from this job frontend's class loader.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  resourceName   Resource name.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void requestResource
		(JobBackendRef theJobBackend,
		 String resourceName)
		throws IOException
		{
		}

	/**
	 * Open the given output file for writing or appending.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  bfd            Backend file descriptor.
	 * @param  file           File.
	 * @param  append         True to append, false to overwrite.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void outputFileOpen
		(JobBackendRef theJobBackend,
		 int bfd,
		 File file,
		 boolean append)
		throws IOException
		{
		}

	/**
	 * Write the given bytes to the given output file. <TT>ffd</TT> = 1 refers
	 * to the job's standard output stream; <TT>ffd</TT> = 2 refers to the job's
	 * standard error stream; other values refer to a previously opened file.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  ffd            Frontend file descriptor.
	 * @param  buf            Array of bytes to write.
	 * @param  off            Index of first byte to write.
	 * @param  len            Number of bytes to write.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void outputFileWrite
		(JobBackendRef theJobBackend,
		 int ffd,
		 byte[] buf,
		 int off,
		 int len)
		throws IOException
		{
		}

	/**
	 * Flush accumulated bytes to the given output file.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  ffd            Frontend file descriptor.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void outputFileFlush
		(JobBackendRef theJobBackend,
		 int ffd)
		throws IOException
		{
		}

	/**
	 * Close the given output file.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  ffd            Frontend file descriptor.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void outputFileClose
		(JobBackendRef theJobBackend,
		 int ffd)
		throws IOException
		{
		}

	/**
	 * Open the given input file for reading.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  bfd            Backend file descriptor.
	 * @param  file           File.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void inputFileOpen
		(JobBackendRef theJobBackend,
		 int bfd,
		 File file)
		throws IOException
		{
		}

	/**
	 * Read bytes from the given input file. <TT>ffd</TT> = 1 refers to the
	 * job's standard input stream; other values refer to a previously opened
	 * file.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  ffd            Frontend file descriptor.
	 * @param  len            Number of bytes to read.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void inputFileRead
		(JobBackendRef theJobBackend,
		 int ffd,
		 int len)
		throws IOException
		{
		}

	/**
	 * Skip bytes from the given input file.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  ffd            Frontend file descriptor.
	 * @param  len            Number of bytes to skip.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void inputFileSkip
		(JobBackendRef theJobBackend,
		 int ffd,
		 long len)
		throws IOException
		{
		}

	/**
	 * Close the given input file.
	 *
	 * @param  theJobBackend  Job Backend that is calling this method.
	 * @param  ffd            Frontend file descriptor.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	public synchronized void inputFileClose
		(JobBackendRef theJobBackend,
		 int ffd)
		throws IOException
		{
		}

	/**
	 * Close communication with this Job Frontend.
	 */
	public void close()
		{
		}

// Hidden operations.

	/**
	 * Take action when the Job Scheduler's lease renewal timer times out.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private synchronized void schedulerRenewTimeout()
		throws IOException
		{
		if (mySchedulerRenewTimer.isTriggered())
			{
			myJobScheduler.renewLease (this);
			}
		}

	/**
	 * Take action when the Job Scheduler's lease expiration timer times out.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void schedulerExpireTimeout()
		throws IOException
		{
		boolean doExit = false;
		synchronized (this)
			{
			if (mySchedulerExpireTimer.isTriggered())
				{
				continueRun = false;
				if (myState == State.RUNNING)
					{
					myState = State.TERMINATE_CANCEL_JOB;
					myCancelMessage = "Job Scheduler failed";
					System.err.println (myCancelMessage);
					doExit = true;
					}
				}
			}

		// Cannot hold the synchronization lock while calling System.exit(),
		// otherwise a deadlock can occur between this thread (the timer thread)
		// and the shutdown hook thread.
		if (doExit) System.exit (1);
		}

	/**
	 * Take action when the job timer times out.
	 *
	 * @exception  IOException
	 *     Thrown if an I/O error occurred.
	 */
	private void jobTimeout()
		throws IOException
		{
		boolean doExit = false;
		synchronized (this)
			{
			if (myJobTimer.isTriggered())
				{
				continueRun = false;
				if (myState == State.RUNNING)
					{
					myState = State.TERMINATE_CANCEL_JOB;
					myCancelMessage = "Job exceeded maximum running time";
					System.err.println (myCancelMessage);
					doExit = true;
					}
				}
			}

		// Cannot hold the synchronization lock while calling System.exit(),
		// otherwise a deadlock can occur between this thread (the timer thread)
		// and the shutdown hook thread.
		if (doExit) System.exit (1);
		}

	/**
	 * Terminate this Job Frontend immediately, sending a "cancel job" message
	 * to the Job Scheduler. The error message is <TT>msg</TT>. This method must
	 * only be called by the thread calling <TT>run()</TT>.
	 *
	 * @param  msg  Error message.
	 */
	private void terminateCancelJob
		(String msg)
		{
		continueRun = false;
		if (myState == State.RUNNING)
			{
			myState = State.TERMINATE_CANCEL_JOB;
			myCancelMessage = msg;
			System.err.println (myCancelMessage);
			}
		}

	/**
	 * Terminate this Job Frontend immediately, sending a "cancel job" message
	 * to the Job Scheduler. The error message comes from the given exception.
	 * This method must only be called by the thread calling <TT>run()</TT>.
	 *
	 * @param  exc  Exception.
	 */
	private void terminateCancelJob
		(Throwable exc)
		{
		continueRun = false;
		if (myState == State.RUNNING)
			{
			myCancelMessage = exc.getClass().getName();
			String msg = exc.getMessage();
			if (msg != null)
				{
				myCancelMessage = myCancelMessage + ": " + msg;
				}
			System.err.println (myCancelMessage);
			exc.printStackTrace (System.err);
			}
		}

	/**
	 * Terminate this Job Frontend immediately, sending a "cancel job" message
	 * to the Job Scheduler. The error message comes from the given exception.
	 * This method must only be called by a thread other than the thread calling
	 * <TT>run()</TT>.
	 *
	 * @param  exc  Exception.
	 */
	void terminateCancelJobOther
		(Throwable exc)
		{
		boolean doExit = false;
		synchronized (this)
			{
			continueRun = false;
			if (myState == State.RUNNING)
				{
				myCancelMessage = exc.getClass().getName();
				String msg = exc.getMessage();
				if (msg != null)
					{
					myCancelMessage = myCancelMessage + ": " + msg;
					}
				System.err.println (myCancelMessage);
				exc.printStackTrace (System.err);
				doExit = true;
				}
			}

		// Cannot hold the synchronization lock while calling System.exit(),
		// otherwise a deadlock can occur between this thread and the shutdown
		// hook thread.
		if (doExit) System.exit (1);
		}

	/**
	 * Shut down this Job Frontend.
	 */
	private void shutdown()
		{
		synchronized (this)
			{
			// Stop all lease timers.
			mySchedulerRenewTimer.stop();
			mySchedulerExpireTimer.stop();

			// If state is RUNNING but myCancelMessage is not null, it means the
			// user canceled the job (e.g., by hitting CTRL-C).
			if (myState == State.RUNNING && myCancelMessage != null)
				{
				myState = State.TERMINATE_CANCEL_JOB;
				}

			// Inform Job Scheduler.
			switch (myState)
				{
				case RUNNING:
					// Send "job finished" message.
					if (myJobScheduler != null)
						{
						try
							{
							myJobScheduler.jobFinished (this);
							}
						catch (IOException exc)
							{
							}
						}
					break;
				case TERMINATE_CANCEL_JOB:
					// Send "cancel job" message.
					if (myJobScheduler != null)
						{
						try
							{
							myJobScheduler.cancelJob (this, myCancelMessage);
							}
						catch (IOException exc)
							{
							}
						}
					break;
				case TERMINATING:
					// Send nothing.
					break;
				}

			// Record that we are terminating.
			myState = State.TERMINATING;
			}

		// All proxies, channels, and channel groups will close when the process
		// exits.
		}

	}
