public class Parallel {
    public Double[][] Solve(Double[][] inpMatrix, Double[][] inpWindowMatrix, int no_threads) {
        double[][] outputMatrix = new double[inpMatrix.length][inpMatrix[0].length];
        double[][] matrix = new double[inpMatrix.length][inpMatrix[0].length];
        double[][] windowMatrix = new double[inpWindowMatrix.length][inpWindowMatrix[0].length];
        for (int i = 0; i < inpMatrix.length; i++)
            for (int j = 0; j < inpMatrix[i].length; j++) {
                matrix[i][j] = inpMatrix[i][j];
            }
        for (int i = 0; i < windowMatrix.length; i++)
            for (int j = 0; j < windowMatrix[i].length; j++) {
                windowMatrix[i][j] = inpWindowMatrix[i][j];
            }

        Thread[] p = new Thread[no_threads];
        int start, end, r;
        start = 0;
        int size = inpMatrix.length;
        end = size / no_threads;
        r = size % no_threads;
        for (int i = 0; i < no_threads; i++) {
            if (r > 0) {
                end++;
                r--;
            }
            p[i] = new MyThread(matrix, windowMatrix, outputMatrix, start, end);
            start = end;
            end += size / no_threads;
        }
        for (Thread t : p)
            t.start();
        for (Thread t : p)
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        Double[][] returnMatrix = new Double[outputMatrix.length][outputMatrix[0].length];
        for (int i = 0; i < inpMatrix.length; i++)
            for (int j = 0; j < inpMatrix[i].length; j++)
                returnMatrix[i][j] = outputMatrix[i][j];
        return returnMatrix;
    }

    private static class MyThread extends Thread {
        private final double[][] matrix, windowMatrix, outputMatrix;
        private final int start, end;

        private MyThread(double[][] matrix, double[][] windowMatrix, double[][] outputMatrix, int start, int end) {
            this.matrix = matrix;
            this.windowMatrix = windowMatrix;
            this.outputMatrix = outputMatrix;
            this.start = start;
            this.end = end;
        }

        public void run() {
            for (int i = this.start; i < this.end; i++) {
                for (int j = 0; j < matrix[i].length; j++)
                    outputMatrix[i][j] = Common.CalculateValue(i, j, matrix, windowMatrix);
            }
        }
    }
}
