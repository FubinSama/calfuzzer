package wfb.testcases;

public class Test {
    static int x = 0;
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(){
            public void run() {
                x = 1;
            }
        };
        Thread t2 = new Thread(){
            public void run() {
                x = 2;
            }
        };
        t1.start();
        t2.start();
        t1.join();
        t2.join();
    }
}
