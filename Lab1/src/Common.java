public class Common {
    public static double CalculateValue(int i, int j, double[][] matrix, double[][] windowMatrix) {
        double output = 0.0;
        for (int k = -windowMatrix.length / 2; k <= windowMatrix.length / 2; k++) {
            for (int l = -windowMatrix[0].length / 2; l <= windowMatrix[0].length / 2; l++) {
                int a = i + k;
                int b = j + l;
                if (a < 0) a = 0;
                if (b < 0) b = 0;
                if (a >= matrix.length) a = matrix.length - 1;
                if (b >= matrix[0].length) b = matrix[0].length - 1;

                output +=
                        matrix[a][b] * windowMatrix[k + windowMatrix.length / 2][l + windowMatrix[0].length / 2];
            }
        }
        return output;
    }
}
