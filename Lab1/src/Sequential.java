public class Sequential {
    public double[][] Solve(double[][] matrix, double[][] windowMatrix) {
        double[][] outputMatrix = new double[matrix.length][matrix[0].length];
        for (int i = 0; i < outputMatrix.length; i++)
            for (int j = 0; j < outputMatrix[i].length; j++)
                outputMatrix[i][j] = Common.CalculateValue(i, j, matrix, windowMatrix);
        return outputMatrix;
    }
}
