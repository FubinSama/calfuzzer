//******************************************************************************
//
// File:    Lock.java
// Package: benchmarks.determinism.pj.edu.ritpj
// Unit:    Class benchmarks.determinism.pj.edu.ritpj.Lock
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

package benchmarks.determinism.pj.edu.ritpj;

import java.util.concurrent.locks.AbstractQueuedSynchronizer;

/**
 * Class Lock provides an object used for synchronizing parallel team threads in
 * a critical region. You don't call methods on a Lock object directly, rather
 * you pass a Lock object to the <TT>critical()</TT> or
 * <TT>criticalNonexclusive()</TT> methods of class {@linkplain ParallelRegion}.
 *
 * @author  Alan Kaminsky
 * @version 05-Jun-2007
 */
public class Lock
	{

// Hidden helper classes.

	/**
	 * Class Lock.Synchronizer does the actual work. The synchronizer state, a
	 * single <TT>int</TT>, is interpreted as follows: state = 0 means unlocked;
	 * state &gt; 0 means locked nonexclusively, with the state giving the
	 * number of threads that have acquired the lock; state = -1 means locked
	 * exclusively, with one thread having acquired the lock.
	 *
	 * @author  Alan Kaminsky
	 * @version 05-Jun-2007
	 */
	private static class Synchronizer
		extends AbstractQueuedSynchronizer
		{
		/**
		 * Construct a new lock synchronizer.
		 */
		public Synchronizer()
			{
			super();
			}

		/**
		 * Acquire the lock exclusively.
		 *
		 * @param  arg  Ignored.
		 *
		 * @return  True if acquired, false otherwise.
		 */
		protected boolean tryAcquire
			(int arg)
			{
			for (;;)
				{
				int oldstate = getState();
				if (oldstate != 0) return false;
				if (compareAndSetState (0, -1)) return true;
				}
			}

		/**
		 * Release the lock exclusively.
		 *
		 * @param  arg  Ignored.
		 *
		 * @return  True if released, false otherwise.
		 */
		protected boolean tryRelease
			(int arg)
			{
			setState (0);
			return true;
			}

		/**
		 * Acquire the lock nonexclusively.
		 *
		 * @param  arg  Ignored.
		 *
		 * @return  A positive value if successfully acquired nonexclusively,
		 *          zero if successfully acquired exclusively, a negative value
		 *          if not acquired.
		 */
		protected int tryAcquireShared
			(int arg)
			{
			for (;;)
				{
				int oldstate = getState();
				if (oldstate < 0) return -1;
				int newstate = oldstate + 1;
				if (compareAndSetState (oldstate, newstate)) return 1;
				}
			}

		/**
		 * Release the lock nonexclusively.
		 *
		 * @param  arg  Ignored.
		 *
		 * @return  True if released, false otherwise.
		 */
		protected boolean tryReleaseShared
			(int arg)
			{
			for (;;)
				{
				int oldstate = getState();
				int newstate = oldstate - 1;
				if (compareAndSetState (oldstate, newstate)) return true;
				}
			}
		}

// Hidden data members.

	private Synchronizer mySynchronizer = new Synchronizer();

// Exported constructors.

	/**
	 * Construct a new lock.
	 */
	public Lock()
		{
		}

// Hidden operations.

	/**
	 * Lock an exclusive lock.
	 */
	void lockExclusive()
		{
		mySynchronizer.acquire (0);
		}

	/**
	 * Unlock an exclusive lock.
	 */
	void unlockExclusive()
		{
		mySynchronizer.release (0);
		}

	/**
	 * Lock a nonexclusive lock.
	 */
	void lockNonexclusive()
		{
		mySynchronizer.acquireShared (0);
		}

	/**
	 * Unlock a nonexclusive lock.
	 */
	void unlockNonexclusive()
		{
		mySynchronizer.releaseShared (0);
		}

	}
