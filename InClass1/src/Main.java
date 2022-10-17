import java.util.function.BinaryOperator;

public class Main {
    public static int no_threads = 16;
    public static int size = 5000000;
//    public static BinaryOperator<Double> operator = (x, y) -> x + y;
    public static BinaryOperator<Double> operator = (x, y) -> Math.sqrt(Math.pow(x, 3) + Math.pow(y, 3));

    public static void main(String[] args) {
        double[] a = new double[size];
        double[] b = new double[size];
        double[] cSeq = new double[size];
        double[] cPar = new double[size];

        for (int i = 0; i < size; i++) {
            a[i] = i*i;
            b[i] = -i;
        }

        long start1 = System.nanoTime();
        addSeq(a, b, cSeq, operator);
        long end1 = System.nanoTime();
        System.out.println("Time seq:       " + (end1 - start1) + " nanoseconds");

        long start2 = System.nanoTime();
        addPar(a, b, cPar, no_threads, operator, false);
        long end2 = System.nanoTime();
        System.out.println("Time par:       " + (end2 - start2) + " nanoseconds");

        long start3 = System.nanoTime();
        addPar(a, b, cPar, no_threads, operator, true);
        long end3 = System.nanoTime();
        System.out.println("Time par cycle: " + (end3 - start3) + " nanoseconds");

        boolean same = SameElements(cSeq, cPar);
        if (!same)
            System.out.println("Not same elements");
        else
            System.out.println("Same elements");
    }

    public static void addSeq(double[] a, double[] b, double[] c, BinaryOperator<Double> operator) {
        for (int i = 0; i < a.length; i++) {
            c[i] = operator.apply(a[i], b[i]);
        }
    }

    public static void addPar(double[] a, double[] b, double[] c, int no_threads,
                              BinaryOperator<Double> operator, boolean cycle) {
        Thread[] p = new Thread[no_threads];
        int start, end, r;
        start = 0;
        end = size / no_threads;
        r = size % no_threads;
        for (int i = 0; i < no_threads; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            if (!cycle)
                p[i] = new MyThread(a, b, c, start, end, operator);
            else
                p[i] = new MyCycleThread(a, b, c, i, no_threads, operator);
            start = end;
            end += size / no_threads;
        }
        for (Thread t : p)
            t.start();
        for (Thread t : p) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean SameElements(double[] a, double[] b) {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (a[i] - b[i] > 0.0001)
                return false;
        return true;
    }

    private static class MyThread extends Thread {
        private final double[] a, b, c;
        private final int start, end;
        private final BinaryOperator<Double> operator;

        public MyThread(double[] a, double[] b, double[] c, int start, int end, BinaryOperator<Double> operator) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.start = start;
            this.end = end;
            this.operator = operator;
        }

        public  void run() {
            for (int i = this.start; i < this.end; i++) {
                this.c[i] = operator.apply(this.a[i], this.b[i]);
            }
        }
    }

    private static class MyCycleThread extends Thread {
        private final double[] a, b, c;
        private final int p, step;
        private final BinaryOperator<Double> operator;

        public MyCycleThread(double[] a, double[] b, double[] c, int p, int step, BinaryOperator<Double> operator) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.p = p;
            this.step = step;
            this.operator = operator;
        }

        public  void run() {
            for (int i = this.p; i < a.length; i += step) {
                this.c[i] = operator.apply(this.a[i], this.b[i]);
            }
        }
    }
}
