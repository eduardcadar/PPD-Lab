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
        int size = matrix.length * matrix[0].length;
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
            int N = matrix.length;
            int firstLine = first / M, firstCol = first % M;
            int lastLine = last / M, lastCol = last % M;
            int allocSize = windowMatrix.length / 2;
            int i, j, allocLinesBefore = 0, allocLinesAfter = 0;
            i = firstLine;
            while (i > 0 && allocLinesBefore < allocSize) {
                allocLinesBefore++;
                i--;
            }
            i = lastLine;
            while (i < N - 1 && allocLinesAfter < allocSize) {
                allocLinesAfter++;
                i++;
            }

            // copyMatrixLines - how many lines the copy matrix will have
            // the line number of the last element minus the line number of the first element + 1
            int copyMatrixLines = (last / M) - (first / M) + 1;
            // copyMatrixColumns - how many columns the copy matrix will have
            // max of the column number of the elements between start and end
            int copyMatrixColumns;
            int maxCol = Math.max(last % M + 1, first % M + 1);
            if (copyMatrixLines > 1 || maxCol + allocSize > M)
                copyMatrixColumns = M;
            else
                copyMatrixColumns = maxCol + allocSize;

            copyMatrixLines += allocLinesBefore + allocLinesAfter;
            double[][] matrixCopy = new double[copyMatrixLines][copyMatrixColumns];
            int lineDifference = first / M - allocLinesBefore;

            int a = firstLine - lineDifference - allocLinesBefore;
            int b = lastLine - lineDifference + allocLinesAfter;
            j = firstCol - allocSize;
            int k = lastCol + allocSize;
            while (j < 0) j++;
            int firstCopyCol = j;
            while (k > M - 1) k--;
            if (firstLine == lastLine) {
                for (i = a; i <= b; i++)
                    for (j = firstCopyCol; j <= k; j++)
                        matrixCopy[i][j] = matrix[i + lineDifference][j];
            } else {
                int firstCopy;
                if (allocLinesBefore < allocSize)
                    firstCopy = 0;
                else
                    firstCopy = a * M + j;
                int lastCopy;
                if (allocLinesAfter < allocSize)
                    lastCopy = copyMatrixLines * copyMatrixColumns - 1;
                else
                    lastCopy = b * M + k;

                for (i = firstCopy; i <= lastCopy; i++) {
                    matrixCopy[i / M][i % M] = matrix[i / M + lineDifference][i % M];
                }
            }

            try {
                barrier.await();

                for (i = first; i <= last; i++) {
                    double output = 0;
                    for (k = -windowMatrix.length / 2; k <= windowMatrix.length / 2; k++) {
                        for (int l = -windowMatrix[0].length / 2; l <= windowMatrix[0].length / 2; l++) {
                            a = (i / M) + k;
                            a -= lineDifference;
                            b = (i % M) + l;
                            if (a < 0) a = 0;
                            if (b < 0) b = 0;
                            if (a >= matrixCopy.length) a = matrixCopy.length - 1;
                            if (b >= matrixCopy[0].length) b = matrixCopy[0].length - 1;

                            output +=
                                    matrixCopy[a][b] * windowMatrix[k + windowMatrix.length / 2][l + windowMatrix[0].length / 2];
                        }
                    }
                    matrix[i / M][i % M] = output;
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
