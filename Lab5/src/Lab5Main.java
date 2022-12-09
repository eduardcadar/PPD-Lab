import java.io.*;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Lab5Main {
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
    public static int noReaderThreads = 2;
    public static String resultFile = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomAResult.txt";
    public static String solutionFile = "C:\\facultate\\Semestrul 5\\PPD\\PPD_LAB\\Lab4\\files\\polinomASolution.txt";

    public static String[] inputFiles;

    public static void main(String[] args) {
        String caseNumber = "A";
        if (args.length > 0) {
            if (args.length != 3) {
                System.out.println("Give case number, number of threads and number of reader threads");
                return;
            }
            caseNumber = args[0];
            noThreads = Integer.parseInt(args[1]);
            if (noThreads < 1) {
                System.out.println("Wrong noThreads");
                return;
            }
            noReaderThreads = Integer.parseInt(args[2]);
            if (noThreads < 1) {
                System.out.println("Wrong noReaderThreads");
                return;
            }
        }
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
        if (noThreads > 1 && noReaderThreads >= noThreads) {
            System.out.println("noReaderThreads should be smaller than noThreads");
            return;
        }
        NodeList nodeList = new NodeList();
        long start = System.nanoTime();
        if (noThreads == 1)
            sequential(nodeList);
        else
            parallel(nodeList, noThreads, noReaderThreads);
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

    public static void parallel(NodeList nodeList, int noThreads, int noReaderThreads) {
        SyncQueue<Node> nodeQueue = new SyncQueue<>();
        Thread[] readerThreads = new Thread[noReaderThreads];
        Thread[] workerThreads = new Thread[noThreads - noReaderThreads];

        SyncQueue<String> inputFilesQueue = new SyncQueue<>();
        for (String file : inputFiles)
            inputFilesQueue.add(file);
        inputFilesQueue.setFinished();

        for (int i = 0; i < noReaderThreads; i++)
            readerThreads[i] = new Thread(() -> {
                String inpFile = inputFilesQueue.take();
                while (inpFile != null) {
                    try (BufferedReader br = new BufferedReader(new FileReader(inpFile))) {
                        String line;
                        while ((line = br.readLine()) != null) {
                            String[] lineElements = line.split(" ");
                            int coef = Integer.parseInt(lineElements[0]);
                            int exp = Integer.parseInt(lineElements[1]);
                            if (coef == 0 || exp < 0)
                                throw new RuntimeException("Wrong input");
                            nodeQueue.add(new Node(coef, exp));
                        }
                    } catch (IOException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    inpFile = inputFilesQueue.take();
                }
                nodeQueue.setFinished();
            });
        for (Thread t : readerThreads)
            t.start();

        for (int i = 0; i < noThreads - noReaderThreads; i++)
            workerThreads[i] = new Thread(() -> {
                Node node = nodeQueue.take();
                while (node != null) {
                    nodeList.add(node);
                    node = nodeQueue.take();
                }
            });
        for (Thread t : workerThreads)
            t.start();

        try {
            for (Thread t : readerThreads)
                t.join();
            for (Thread t : workerThreads)
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
            if (finished)
                return;
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
        final ReentrantLock lock = new ReentrantLock();

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

        public void add(Node node) {
            Node prev = first;
            prev.lock.lock();
            Node crt = first.next;
            crt.lock.lock();
            while (crt != last && crt.exp < node.exp) {
                crt.next.lock.lock();
                prev.lock.unlock();
                prev = crt;
                crt = crt.next;
            }
            if (crt.exp > node.exp) {
                node.next = crt;
                prev.next = node;
            } else {
                crt.coef += node.coef;
                if (crt.coef == 0) {
                    crt.next.lock.lock();
                    prev.next = crt.next;
                    crt.next.lock.unlock();
                }
            }
            crt.lock.unlock();
            prev.lock.unlock();
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
