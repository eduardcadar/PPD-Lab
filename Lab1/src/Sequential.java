public class Sequential {
    public Double[][] Solve(Double[][] matrix, Double[][] windowMatrix) {
        Double[][] outputMatrix = new Double[matrix.length][matrix[0].length];
        for (int i = 0; i < outputMatrix.length; i++)
            for (int j = 0; j < outputMatrix[i].length; j++)
                outputMatrix[i][j] = Common.CalculateValue(i, j, matrix, windowMatrix);
        return outputMatrix;
    }
}
