
public class InterruptExample {
    /*
        Thread.interrupted() => returns the interrupted status and clears it at the same time.
        Thread.currentThread.isInterrupted() => just returns the status.
    */
    public static void main(String[] args) throws InterruptedException {
        Thread t1 = new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("Going to sleep!!");
                    Thread.sleep(1000 * 60 * 60);
                } catch (InterruptedException e) {
                    // Thread interrupted status cleared in next line
                    System.out.println("My state is: " + Thread.interrupted() + " " + Thread.currentThread().isInterrupted());
                    // merely calling the interrupt method isn't responsible for throwing the interrupted exception.
                    // Rather the implementation should periodically check for the interrupt status and take appropriate action.
                    Thread.currentThread().interrupt(); // thread is awake nothing happens except status set to true
                    System.out.println("My new state is: " + Thread.currentThread().isInterrupted() + " " + Thread.interrupted());
                }
            }
        });

        t1.start();

        System.out.println("Interrupting sleep!");
        t1.interrupt(); // will set state and throw exception
        System.out.println("Woke thread up!");

        t1.join();
    }
}