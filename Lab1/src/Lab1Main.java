public class Lab1Main {
    static String matrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\matrix3.txt";
    static String windowMatrixPath = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\windowMatrix3.txt";
    static int no_threads = 4;

    public static void main(String[] args) {
//        boolean onlyCreate = true;
//        FileUtils.GenerateMatrixFile(matrixPath, 10000, 10, 20, ElementType.DOUBLE);
//        FileUtils.GenerateMatrixFile(windowMatrixPath, 5, 5, 2, ElementType.DOUBLE);
//        if (onlyCreate) return;

        Double[][] matrix = FileUtils.ReadFileAsMatrix(matrixPath);
        Double[][] windowMatrix = FileUtils.ReadFileAsMatrix(windowMatrixPath);

        Sequential sequentialSolver = new Sequential();
        Parallel parallelSolver = new Parallel();

        long startSeq = System.nanoTime();
        Double[][] outputMatrixSeq = sequentialSolver.Solve(matrix, windowMatrix);
        long finishSeq = System.nanoTime();

        long startPar = System.nanoTime();
        Double[][] outputMatrixPar = parallelSolver.Solve(matrix, windowMatrix, no_threads);
        long finishPar = System.nanoTime();

//        MatrixUtils.PrintMatrix(outputMatrixSeq);
//        System.out.println();
//        MatrixUtils.PrintMatrix(outputMatrixPar);
//        System.out.println();

        System.out.println("Time sequential: " + (finishSeq - startSeq) + " nanoseconds");
        System.out.println("Time parallel:   " + (finishPar - startPar) + " nanoseconds");
        if (MatrixUtils.SameElements(outputMatrixSeq, outputMatrixPar))
            System.out.println("Same elements");
        else
            System.out.println("Not same elements");
    }
}