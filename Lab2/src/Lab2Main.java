import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class Lab2Main {
    static int no_threads = 4;

    public static void main(String[] args) {
        String matrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\matrix";
        String windowMatrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\windowMatrix";
        String outputMatrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\outputMatrix";
        String matrixNumber = "1";
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("Give matrix number, then number of threads");
                return;
            }
            matrixNumber = args[0];
            no_threads = Integer.parseInt(args[1]);
        }
        matrixPath += matrixNumber + ".txt";
        windowMatrixPath += matrixNumber + ".txt";
        outputMatrixPath += matrixNumber + ".txt";

        double[][] matrix = ReadFileAsMatrix(matrixPath);
        double[][] windowMatrix = ReadFileAsMatrix(windowMatrixPath);
        double[][] answerMatrix = ReadFileAsMatrix(outputMatrixPath);

        long start = System.nanoTime();
        solvePar(matrix, windowMatrix, no_threads);
        long finish = System.nanoTime();

        if (!SameElements(matrix, answerMatrix))
            throw new RuntimeException("Wrong answer");

        System.out.println((double)(finish - start)/1E6);
    }

    public static void solvePar(double[][] matrix, double[][] windowMatrix, int no_threads) {
        Thread[] threads = new Thread[no_threads];

        int start = 0, end, r;
        int size = matrix.length;
        end = size / no_threads;
        r = size % no_threads;
        for (int i = 0; i < no_threads; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            threads[i] = new MyThread(matrix, windowMatrix, start, end);
            start = end;
            end += size / no_threads;
        }

        for (Thread t : threads)
            t.start();
        for (Thread t : threads)
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
    }

    private static class MyThread extends Thread {
        private final double[][] matrix, windowMatrix;
        private final int first, last, M;
        private final static CyclicBarrier barrier = new CyclicBarrier(no_threads);

        private MyThread(double[][] matrix, double[][] windowMatrix, int start, int end) {
            this.matrix = matrix;
            this.windowMatrix = windowMatrix;
            this.first = start;
            this.last = end - 1;
            M = matrix[0].length;
        }

        public void run() {
            int N = matrix.length, M = matrix[0].length;
            int n = windowMatrix.length, m = windowMatrix[0].length;
            int allocSize = n / 2;
            double[][] borderBefore = new double[allocSize + 1][M];
            double[][] borderAfter = new double[allocSize][M];
            for (int i = 0; i < allocSize; i++)
                if (first - allocSize + i >= 0)
                    for (int j = 0; j < M; j++)
                        borderBefore[i][j] = matrix[first - allocSize + i][j];
            for (int j = 0; j < M; j++)
                borderBefore[allocSize][j] = matrix[first][j];
            for (int i = 0; i < allocSize; i++)
                if (last + allocSize - i < N)
                    for (int j = 0; j < M; j++)
                        borderAfter[i][j] = matrix[last + 1 + i][j];

            try {
                barrier.await();
                int line = 0;
                for (int i = first; i <= last - allocSize; i++) {
                    for (int j = 0; j < M; j++) {
                        double output = 0;
                        for (int k = -n / 2; k <= n / 2; k++) {
                            for (int l = -m / 2; l <= m / 2; l++) {
                                int a = i + k;
                                int b = j + l;
                                if (a < 0) a = 0;
                                if (b < 0) b = 0;
                                if (a >= N) a = N - 1;
                                if (b >= M) b = M - 1;

                                if (k <= 0)
                                    output += borderBefore[(line + k + borderBefore.length) % borderBefore.length][b]
                                            * windowMatrix[k + n / 2][l + m / 2];
                                else
                                    output += matrix[a][b]
                                            * windowMatrix[k + n / 2][l + m / 2];
                            }
                        }
                        for (int t = 0; t < M; t++)
                            borderBefore[line][t] = matrix[i + 1][t];
                        line++;
                        line %= borderBefore.length;
                        matrix[i][j] = output;
                    }
                }

                for (int i = last - allocSize + 1; i <= last; i++) {
                    for (int j = 0; j < M; j++) {
                        double output = 0;
                        for (int k = -n / 2; k <= n / 2; k++) {
                            for (int l = -m / 2; l <= m / 2; l++) {
                                int a = i + k;
                                int b = j + l;
                                if (a < 0) a = 0;
                                if (b < 0) b = 0;
                                if (a >= N) a = N - 1;
                                if (b >= M) b = M - 1;

                                if (k <= 0)
                                    output += borderBefore[(line + k + borderBefore.length) % borderBefore.length][b]
                                            * windowMatrix[k + n / 2][l + m / 2];
                                else
                                    output += matrix[a][b]
                                            * windowMatrix[k + n / 2][l + m / 2];
                            }
                        }
                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static double[][] ReadFileAsMatrix(String filePath) {
        double[][] matrix;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] lineElements = br.readLine().split(" ");
            int N = Integer.parseInt(lineElements[0]), M = Integer.parseInt(lineElements[1]);
            matrix = new double[N][M];
            for (int i = 0; i < N; i++) {
                lineElements = br.readLine().split(" ");
                for (int j = 0; j < M; j++) {
                    matrix[i][j] = Double.parseDouble(lineElements[j]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return new double[0][0];
        }
        return matrix;
    }

    public static boolean SameElements(double[][] matrix1, double[][] matrix2) {
        if (matrix1.length != matrix2.length)
            return false;
        for (int i = 0; i < matrix1.length; i++)
            if (!SameElements(matrix1[i], matrix2[i]))
                return false;
        return true;
    }

    public static boolean SameElements(double[] a, double[] b) {
        if (a.length != b.length)
            return false;
        for (int i = 0; i < a.length; i++)
            if (a[i] - b[i] > 0.0001)
                return false;
        return true;
    }
}
