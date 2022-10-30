public class Lab1Main {
    static String matrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\matrix4.txt";
    static String windowMatrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\windowMatrix4.txt";
    static String outputMatrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\outputMatrix4.txt";
    static int no_threads = 4;

    public static void main(String[] args) throws Exception {
        if (args.length > 0)
            no_threads = Integer.parseInt(args[0]);

        double[][] matrix = FileUtils.ReadFileAsMatrix(matrixPath);
        double[][] windowMatrix = FileUtils.ReadFileAsMatrix(windowMatrixPath);

        Sequential sequentialSolver = new Sequential();
//        Parallel parallelSolver = new Parallel();

        long start = System.nanoTime();
        double[][] outputMatrixSeq = sequentialSolver.Solve(matrix, windowMatrix);
        long finish = System.nanoTime();
        FileUtils.WriteMatrixToFile(outputMatrixPath, outputMatrixSeq);

//        long start = System.nanoTime();
//        double[][] outputMatrixPar = parallelSolver.Solve(matrix, windowMatrix, no_threads);
//        long finish = System.nanoTime();

//        System.out.println("Time sequential: " + (finishSeq - startSeq) + " nanoseconds");
//        System.out.println("Time parallel:   " + (finishPar - startPar) + " nanoseconds");
//        if (MatrixUtils.SameElements(outputMatrixSeq, outputMatrixPar))
//            System.out.println("Same elements");
//        else
//            System.out.println("Not same elements");
        System.out.println((double)(finish - start)/1E6);
    }
}