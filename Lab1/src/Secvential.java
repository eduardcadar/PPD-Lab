public class Secvential {
    public void Solve(Double[][] borderedMatrix, Double[][] windowMatrix) {
        Double[][] outputMatrix = new Double[borderedMatrix.length - 2][borderedMatrix[0].length - 2];
        for (int i = 0; i < outputMatrix.length; i++) {
            for (int j = 0; j < outputMatrix[0].length; j++) {
                outputMatrix[i][j] = 0.0;
                for (int k = -windowMatrix.length / 2; k <= windowMatrix.length / 2; k++) {
                    for (int l = -windowMatrix[0].length / 2; l <= windowMatrix[0].length / 2; l++) {
                        outputMatrix[i][j] +=
                                borderedMatrix[i + 1 + k][j + 1 + l] * windowMatrix[k + windowMatrix.length / 2][l + windowMatrix[0].length / 2];
                    }
                }
            }
        }

        MatrixUtils.PrintMatrix(outputMatrix);
    }
}
