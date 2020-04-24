package wfb.testcases;

import java.util.concurrent.locks.LockSupport;

public class DeadLockDemo implements Runnable {
	Object L1, L2;
	Thread mainThread;
	boolean localFlag = false;

	DeadLockDemo(Object L1, Object L2, boolean flag, Thread mainThread) {
		this.L1 = L1;
		this.L2 = L2;
		this.localFlag = flag;
		this.mainThread = mainThread;
	}
	@Override
	public void run() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		synchronized (L1) {
			synchronized (L2) {
				if (this.localFlag == true)
					LockSupport.unpark(this.mainThread);
			}
		}
	}
	public static void main(String[] args) throws InterruptedException {
		Object o1 = new Object(), o2 = new Object();
		Thread mainThread = Thread.currentThread();
		Thread threadA = new Thread(new DeadLockDemo(o1, o2, true, mainThread));
		Thread threadB = new Thread(new DeadLockDemo(o2, o1, false, mainThread));
		threadA.start();
		// LockSupport.park();
		threadB.start();
		threadA.join();
	}
}
