public class MatrixUtils {
    public static Double[][] AddBorderToMatrix(Double[][] matrix) {
        Double[][] borderedMatrix = new Double[matrix.length + 2][matrix[0].length + 2];
        for (int i = 0; i < matrix.length; i++) {
            System.arraycopy(matrix[i], 0, borderedMatrix[i + 1], 1, matrix[i].length);
        }
        System.arraycopy(matrix[0], 0, borderedMatrix[0], 1, matrix[0].length);
        System.arraycopy(matrix[matrix.length - 1], 0, borderedMatrix[borderedMatrix.length - 1], 1, matrix[0].length);
        for (int i = 0; i < borderedMatrix.length; i++) {
            borderedMatrix[i][0] = borderedMatrix[i][1];
            borderedMatrix[i][borderedMatrix[0].length - 1] = borderedMatrix[i][borderedMatrix[0].length - 2];
        }
        return borderedMatrix;
    }

    public static boolean SameElements(double[][] matrix1, double[][] matrix2) {
        if (matrix1.length != matrix2.length)
            return false;
        for (int i = 0; i < matrix1.length; i++)
            if (!ArrayUtils.SameElements(matrix1[i], matrix2[i]))
                return false;
        return true;
    }

    public static void PrintMatrix(Double[][] matrix) {
        System.out.println(matrix.length + " " + matrix[0].length);
        for (Double[] line : matrix) {
            for (Double element : line) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}
