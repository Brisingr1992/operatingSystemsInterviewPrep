import java.util.Random;

public class UnsafeThreads {
    static class Counter {
        int counter;

        public void increment() {
            counter++;
        }

        public void decrement() {
            counter--;
        }
    }

    static Random random;
    public static void main(String[] args) throws InterruptedException {
        random = new Random(System.currentTimeMillis());
        Counter c = new Counter();
        Thread t1 = new Thread(new Runnable(){
            public void run() {
                for (int i = 0; i < 100; i++) {
                    c.increment();
                    sleep();
                }
            }
        });

        Thread t2 = new Thread(new Runnable(){
            public void run() {
                for (int i = 0; i < 100; i++) {
                    c.decrement();
                    sleep();
                }
            }
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        System.out.println("Final Counter Value: " + c.counter);
    } 

    private static void sleep() {
        try {
            Thread.sleep(random.nextInt(10));
        } catch (InterruptedException e) {

        }
    }
}