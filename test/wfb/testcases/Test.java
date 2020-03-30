package wfb.testcases;

import java.util.Random;

public class Test {
	  static int x = 0; //5
	  static boolean flag = true; //2
	  static final Object lock = new Object(); //6
	  public static void main(String[] args) throws InterruptedException { //t7
	    	flag = (new Random()).nextBoolean();
	    	Thread threadA = new Thread() {
	    		public void run() {
	    			x = 1;
	    			synchronized (lock) {
	    				flag = true;
				}
			}
	    	};
	    	Thread threadB = new Thread() {
	    		public void run() {
	    			boolean new_flag = false;
	    			synchronized (lock) {
	    				new_flag = flag;
				}
	    			if(new_flag) {
	    				x = 2;
	    	          }
			}
	    	};
	    	threadA.start(); //t8
	    	threadB.start(); //t9
	    	threadA.join();
	    	threadB.join();
	    	System.out.println("The value of x is "+ x);
	}
	}