import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Test {
	
	static int x = 0;
	private static List<Integer> cache = new ArrayList<>(1);
	
	public static void main(String[] args) throws Exception {
        Thread t1 = new Thread() {
        	public void run() {
                synchronized(cache) {
                    try{
                        while(cache.isEmpty()) cache.wait();
                    } catch (Exception e) {e.printStackTrace();}
                    cache.remove(0);
                    cache.notify();
                }
                x = 1;
        	}
        };
        Thread t2 = new Thread() {
        	public void run() {
        		x = 2;
        		synchronized(cache) {
                    try{
                        while(!cache.isEmpty()) cache.wait();
                    } catch (Exception e) {e.printStackTrace();}
                    cache.add(1);
                    cache.notify();
                }
        	}
        };
        t1.start();
        t2.start();
        t1.join();
        t2.join();
	}
}
