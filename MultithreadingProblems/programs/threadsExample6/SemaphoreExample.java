import java.util.concurrent.Semaphore;

public class SemaphoreExample {
    public static void main(String[] args) throws InterruptedException {
        Semaphore s = new Semaphore(1);

        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    // Without finally and releasing T2 will block forever!!
                    s.acquire();
                    System.out.println("T1: Acquired semaphore!!");
                    try {
                        throw new RuntimeException("Sorry! I am bad");
                    } catch (Exception e) {

                    } finally {
                        System.out.println("T1 thread releasing semahore.");
                        s.release();
                    }
                } catch (InterruptedException e) {

                }
            }
        });

        t1.start();
        Thread t2 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("T2: Trying to acquire!");
                    s.acquire();
                } catch (InterruptedException e) {

                }
            }
        });
        t2.start();
        t1.join();
        t2.join();
        System.out.println("Exiting Program");
    }    
}