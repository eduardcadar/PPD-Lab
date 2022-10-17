import java.io.*;
import java.util.*;

public class FileUtils {
    public static boolean CompareFiles(String filePath1, String filePath2) {
        String fileText1 = ReadFileAsString(filePath1);
        String fileText2 = ReadFileAsString(filePath2);
        return Objects.equals(fileText1, fileText2);
    }

    public static List<Double> ReadFileAsNumbers(String filePath) {
        String resultAsStringArray = ReadFileAsString(filePath);
        String[] stringNumbers = resultAsStringArray.split(" ");
        return Arrays.stream(stringNumbers)
                .map(Double::valueOf)
                .toList();
    }

    public static Double[][] ReadFileAsMatrix(String filePath) {
        Double[][] matrix;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String[] lineElements = br.readLine().split(" ");
            int N = Integer.parseInt(lineElements[0]), M = Integer.parseInt(lineElements[1]);
            matrix = new Double[N][M];
            for (int i = 0; i < N; i++) {
                lineElements = br.readLine().split(" ");
                for (int j = 0; j < M; j++) {
                    matrix[i][j] = Double.parseDouble(lineElements[j]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            return new Double[0][0];
        }
        return matrix;
    }

    public static String ReadFileAsString(String filePath) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(" ");
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static List<String> ReadFileAsStringList(String filePath) {
        List<String> result = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = br.readLine();
            while (line != null) {
                result.add(line);
                line = br.readLine();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    public static void WriteToFile(String filePath, String text) {
        try {
            CreateFile(filePath);
        } catch (IOException e) {
            System.out.println("Error creating file");
            return;
        }
        try {
            FileWriter fileWriter = new FileWriter(filePath);
            fileWriter.write(text);
            fileWriter.close();
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static boolean CreateFile(String filePath) throws IOException {
        File myObj = new File(filePath);
        return myObj.createNewFile();
    }

    public static void GenerateMatrixFile(String filePath, int N, int M, int upperbound, ElementType elementType) {
        try {
            CreateFile(filePath);
        } catch (IOException e) {
            System.out.println("Error creating file");
            return;
        }
        Random rand = new Random();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            fileWriter.write(N + " " + M + System.lineSeparator());
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < M; j++) {
                    switch (elementType) {
                        case INT -> fileWriter.write(rand.nextInt(upperbound) + " ");
                        case DOUBLE -> fileWriter.write(rand.nextDouble() * upperbound + " ");
                    }
                }
                fileWriter.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void GenerateRandomFile(String filePath, int noOfElements, int upperbound, ElementType elementType) {
        try {
            CreateFile(filePath);
        } catch (IOException e) {
            System.out.println("Error creating file");
            return;
        }
        Random rand = new Random();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (int i = 0; i < noOfElements; i++) {
                switch (elementType) {
                    case INT -> fileWriter.write(rand.nextInt(upperbound) + " ");
                    case DOUBLE -> fileWriter.write(rand.nextFloat() * upperbound + " ");
                }
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
