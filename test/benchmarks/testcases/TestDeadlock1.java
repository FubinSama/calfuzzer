package benchmarks.testcases;

class Thread1 extends Thread {
    Object l1;
    Object l2;
    Object l3;
    Object l4;

    public Thread1(Object o1, Object o2, Object o3, Object o4) {
        l1 = o1;
        l2 = o2;
        l3 = o3;
        l4 = o4;
    }

    public void run() {
    	synchronized (l1) {
    		synchronized (l2) {
    			
    		}
    	}
    	try {
    		sleep(1);
    	}
    	catch (Exception e) {
    		System.err.println("Error while sleeping in Thread1");
    	}
    	
    	synchronized (l3) {
    		synchronized (l4) {
    			
    		}
    	}
    }
}

class Thread2 extends Thread {
    Object l2;
    Object l3;

    public Thread2(Object o2, Object o3) {
        l2 = o2;
        l3 = o3;
    }

    public void run() {
    	synchronized (l2) {
    		synchronized (l3) {
    			
    		}
    	}
    }
}

public class TestDeadlock1 {

    static Object o1 = new Object();
    static Object o2 = new Object();
    static Object o3 = new Object();
    static Object o4 = new Object();


    public static void main(String[] args) {
        Thread t1 = new Thread1(o1, o2, o3, o4);
        //Thread t2 = new Thread2(o2, o3);
        //Thread t3 = new Thread2(o4, o1);
        Thread t4 = new Thread2(o4, o3);
        /*
         	ObserverForActiveTesting.myStartBefore(0, t1);
			ObserverForActiveTesting.myLockBefore(2, t1, "start()");
         */
        t1.start();
        /*
         	ObserverForActiveTesting.myUnlockAfter(3);
	        ObserverForActiveTesting.myStartAfter(1, t1);
	        
	        ObserverForActiveTesting.myStartBefore(4, t4);
	        ObserverForActiveTesting.myLockBefore(6, t4, "start()");
         */
        //t2.start();
        //t3.start();
        t4.start();
        /*
         	ObserverForActiveTesting.myUnlockAfter(7);
        	ObserverForActiveTesting.myStartAfter(5, t4);
         */

        try {
        	/*
        	 	ObserverForActiveTesting.myLockBefore(9, t1, "join()");
        	 */
            t1.join();
            /*
             	ObserverForActiveTesting.myUnlockAfter(10);
	            ObserverForActiveTesting.myJoinAfter(8, t1);
	            ObserverForActiveTesting.myLockBefore(12, t4, "join()");
             */
            //t2.join();
            //t3.join();
            t4.join();
            /*
             	ObserverForActiveTesting.myUnlockAfter(13);
            	ObserverForActiveTesting.myJoinAfter(11, t4);
             */
        }
        catch (Exception e) {
            System.err.println("Exception occurred while waiting for threads " + e.toString());
            /*
            PrintStream var5 = System.err;
            StringBuilder var2 = new StringBuilder();
            ObserverForActiveTesting.myLockBefore(14, var2, "append(java.lang.String)");
            var2 = var2.append("Exception occurred while waiting for threads ");
            ObserverForActiveTesting.myUnlockAfter(15);
            ObserverForActiveTesting.myLockBefore(16, var3, "toString()");
            String var6 = var3.toString();
            ObserverForActiveTesting.myUnlockAfter(17);
            ObserverForActiveTesting.myLockBefore(18, var2, "append(java.lang.String)");
            StringBuilder var7 = var2.append(var6);
            ObserverForActiveTesting.myUnlockAfter(19);
            ObserverForActiveTesting.myLockBefore(20, var7, "toString()");
            var6 = var7.toString();
            ObserverForActiveTesting.myUnlockAfter(21);
            ObserverForActiveTesting.myLockBefore(22, var5, "println(java.lang.String)");
            var5.println(var6);
            ObserverForActiveTesting.myUnlockAfter(23);
             */
        }

    }

}
