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

    public static void PrintMatrix(Double[][] matrix) {
        for (Double[] line : matrix) {
            for (Double element : line) {
                System.out.print(element + " ");
            }
            System.out.println();
        }
    }
}
