import java.time.Duration;
import java.time.Instant;

public class Lab1Main {
    public static void main(String[] args) {
        Double[][] matrix = FileUtils.ReadFileAsMatrix("C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\matrix1.txt");
        Double[][] windowMatrix = FileUtils.ReadFileAsMatrix("C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab1\\src\\windowMatrix1.txt");

        Secvential secventialSolver = new Secvential();
        Double[][] borderedMatrix = MatrixUtils.AddBorderToMatrix(matrix);

        Instant start = Instant.now();
        secventialSolver.Solve(borderedMatrix, windowMatrix);
        Instant finish = Instant.now();
        System.out.println("Time: " + Duration.between(start, finish).toMillis() + " milliseconds");
    }
}
