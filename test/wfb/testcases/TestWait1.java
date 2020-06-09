package wfb.testcases;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestWait1 {
	
	public static void main(String[] args) {
		List<Integer> cache = new ArrayList<Integer>();
		new Thread(new Consumer(cache)).start();
		new Thread(new Producer(cache)).start();
	}
	
	static class Consumer implements Runnable{
		List<Integer> cache;
		
		public Consumer(List<Integer> cache) {
			this.cache = cache;
		}

		@Override
		public void run() {
			for (int i=0; i<1; ++i) {
				consume();
			}
		}

		private void consume() {
			synchronized (cache) {
				try {
					while(cache.isEmpty()) {
						cache.wait();
					}
					System.out.println("Consumer consumed [" + cache.remove(0) + "]");
					cache.notify();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	static class Producer implements Runnable{
		List<Integer> cache;
		
		public Producer(List<Integer> cache) {
			this.cache = cache;
		}

		@Override
		public void run() {
			for (int i=0; i<1; ++i) {
				produce();
			}
		}

		private void produce() {
			synchronized (cache) {
				try {
					while(cache.size() == 1) {
						cache.wait();
					}
					
					Thread.sleep(1000);
					cache.add(new Random().nextInt());
					
					cache.notify();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
}
