import java.util.Random;

public class UnsafeThreads {
    static class RaceCondition {
        int rand;
        Random random = new Random(System.currentTimeMillis());

        private void printer() {
            int counter = 10000;
            while (counter-- > 0) {
                if (rand % 5 == 0) {
                    System.out.println(rand);
                }
            }
        }

        private void modifier() {
            int counter = 10000;
            while (counter-- > 0) {
                rand = random.nextInt(1000);
            }
        }

        public static void test() throws InterruptedException {
            final RaceCondition rc = new RaceCondition();
            Thread t1 = new Thread(new Runnable() {
                public void run() {
                    rc.printer();
                }
            });

            Thread t2 = new Thread(new Runnable() {
                public void run() {
                    rc.modifier();
                }
            });

            t1.start();
            t2.start();

            t1.join();
            t2.join();
        }
    }
    public static void main(String[] args) throws InterruptedException {
        RaceCondition.test();
    }
}