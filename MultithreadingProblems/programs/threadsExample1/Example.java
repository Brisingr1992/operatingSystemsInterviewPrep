public class Example {
    static class SumExample {
        long sum;
        int lo, hi;
        private void sum() {
            for (int i = lo; i <= hi; i++) {
                sum += i;
            }
        }

        public SumExample(int lo, int hi) {
            this.sum = 0;
            this.lo = lo;
            this.hi = hi;
        }
    }

    private static void twoThread() throws InterruptedException {
        long start = System.currentTimeMillis();
        SumExample s1 = new SumExample(0, Integer.MAX_VALUE / 4);
        SumExample s2 = new SumExample(Integer.MAX_VALUE / 4 + 1, Integer.MAX_VALUE / 2);

        Thread t1 = new Thread(() -> s1.sum());
        Thread t2 = new Thread(() -> s2.sum());

        t1.start();
        t2.start();

        t1.join();
        t2.join();
        long finalSum = s1.sum + s2.sum;
        long end = System.currentTimeMillis();
        System.out.println("Final sum is: " + finalSum + " and time taken: " + (end - start));
    }

    private static void oneThread() {
        long start = System.currentTimeMillis();
        SumExample s1 = new SumExample(0, Integer.MAX_VALUE / 2);
        s1.sum();
        long end = System.currentTimeMillis();
        System.out.println("Final sum is: " + s1.sum + " and time taken: " + (end - start));
    }
    public static void main(String[] args) throws InterruptedException {
        oneThread();
        twoThread();
    }
}