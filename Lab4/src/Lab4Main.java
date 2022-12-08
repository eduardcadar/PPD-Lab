import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Lab4Main {
    public final static String[] inputFilesEx = {
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomEx.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomEx2.txt"
    };

    public final static String[] inputFilesA = {
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA1.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA2.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA3.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA4.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA5.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA6.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA7.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA8.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA9.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomA10.txt",
    };

    public final static String[] inputFilesB = {
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files/polinomB1.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files/polinomB2.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files/polinomB3.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files/polinomB4.txt",
            "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files/polinomB5.txt",
    };

    public static int noThreads = 4;
    public static String resultFile = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomBResult.txt";
    public static String solutionFile = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomBSolution.txt";

    public static String[] inputFiles;

    public static void main(String[] args) {
        inputFiles = inputFilesB;
        if (args.length > 0) {
            if (args.length != 2) {
                System.out.println("Give case number, then number of threads");
                return;
            }
            String caseNumber = args[0];
            switch (caseNumber) {
                case "A" -> inputFiles = inputFilesA;
                case "B" -> inputFiles = inputFilesB;
                default -> {
                    System.out.println("Wrong case number (should be A or B)");
                    return;
                }
            }
            resultFile = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinom" + caseNumber + "Result.txt";
            solutionFile = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinom" + caseNumber + "Solution.txt";
            noThreads = Integer.parseInt(args[1]);
            if (noThreads < 1) {
                System.out.println("Wrong noThreads");
                return;
            }
        }
        NodeList nodeList = new NodeList();
        long start = System.nanoTime();
        if (noThreads == 1)
            sequential(nodeList);
        else
            parallel(nodeList, noThreads);
        long finish = System.nanoTime();
        writeToFile(resultFile, nodeList.toString());
        System.out.println((double)(finish - start)/1E6);
        if (!compareFiles(resultFile, solutionFile))
            System.out.println("Wrong");
    }

    public static void sequential(NodeList nodeList) {
        for (String file : inputFiles) {
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] lineElements = line.split(" ");
                    int coef = Integer.parseInt(lineElements[0]);
                    int exp = Integer.parseInt(lineElements[1]);
                    if (coef == 0 || exp < 0)
                        throw new RuntimeException("Wrong input");
                    nodeList.add(new Node(coef, exp));
                }
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    public static void parallel(NodeList nodeList, int noThreads) {
        SyncQueue<Node> queue = new SyncQueue<>();
        Thread[] threads = new Thread[noThreads - 1];
        Thread t1 = new Thread(() -> {
            for (String file : inputFiles) {
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] lineElements = line.split(" ");
                        int coef = Integer.parseInt(lineElements[0]);
                        int exp = Integer.parseInt(lineElements[1]);
                        if (coef == 0 || exp < 0)
                            throw new RuntimeException("Wrong input");
                        queue.add(new Node(coef, exp));
                    }
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            queue.setFinished();
        });
        t1.start();

        for (int i = 0; i < noThreads - 1; i++)
            threads[i] = new Thread(() -> {
                Node node = queue.take();
                while (node != null) {
                    nodeList.add(node);
                    node = queue.take();
                }
            });

        for (Thread t : threads)
            t.start();

        try {
            t1.join();
            for (Thread t : threads)
                    t.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static class SyncQueue<T> {
        private final Queue<T> queue = new LinkedList<>();
        private boolean finished = false;
        ReentrantLock lock = new ReentrantLock();
        Condition queueEmpty = lock.newCondition();

        public void setFinished() {
            try {
                lock.lock();
                finished = true;
                queueEmpty.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public void add(T el) {
            try {
                lock.lock();
                queue.add(el);
                queueEmpty.signalAll();
            } finally {
                lock.unlock();
            }
        }

        public T take() {
            try {
                lock.lock();
                while (queue.isEmpty()) {
                    if (finished)
                        return null;
                    queueEmpty.await();
                    if (finished && queue.isEmpty())
                        return null;
                }
                return queue.poll();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
            }
        }
    }

    public static class Node {
        public int coef;
        public int exp;
        public Node next;

        public Node(int coef, int exp, Node node) {
            this.coef = coef;
            this.exp = exp;
            this.next = node;
        }

        public Node(int coef, int exp) {
            this.coef = coef;
            this.exp = exp;
        }

        @Override
        public String toString() {
            return coef + "x^" + exp;
        }
    }

    public static class NodeList {
        private final Node first;
        private final Node last;

        public NodeList() {
            last = new Node(0, Integer.MAX_VALUE, null);
            first = new Node(0, -1, last);
        }

        public synchronized void add(Node node) {
            Node prev = first;
            Node crt = first;
            while (crt != last && crt.exp < node.exp) {
                prev = crt;
                crt = crt.next;
            }
            if (crt.exp > node.exp) {
                Node newNode = new Node(node.coef, node.exp);
                newNode.next = crt;
                prev.next = newNode;
            } else {
                crt.coef += node.coef;
                if (crt.coef == 0)
                    prev.next = crt.next;
            }
        }

        @Override
        public String toString() {
            if (first.next == null)
                return "0";
            StringBuilder sb = new StringBuilder();
            sb.append(first.next);
            for (Node crt = first.next.next; crt != last; crt = crt.next)
                sb.append(" + ").append(crt);
            return sb.toString();
        }
    }

    private static boolean createFile(String filePath) throws IOException {
        File myObj = new File(filePath);
        return myObj.createNewFile();
    }

    public static void writeToFile(String filePath, String text) {
        try {
            createFile(filePath);
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

    public static String readFileAsString(String filePath) {
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

    public static void generateFile(String filePath, int noMonomials, int maxExp) {
        final int maxCoef = 1000;
        try {
            createFile(filePath);
        } catch (IOException e) {
            System.out.println("Error creating file");
            return;
        }
        ArrayList<Integer> usedExps = new ArrayList<>();
        Random rand = new Random();
        try (FileWriter fileWriter = new FileWriter(filePath)) {
            for (int i = 0; i < noMonomials; i++) {
                int coef = rand.nextInt(maxCoef + 1);
                if (coef == 0)
                    continue;
                int exp = rand.nextInt(maxExp);
                while (usedExps.contains(exp))
                    exp = rand.nextInt(maxExp);
                fileWriter.write(coef + " " + exp + System.lineSeparator());
                usedExps.add(exp);
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static boolean compareFiles(String filePath1, String filePath2) {
        String fileText1 = readFileAsString(filePath1);
        String fileText2 = readFileAsString(filePath2);
        return Objects.equals(fileText1, fileText2);
    }
}
