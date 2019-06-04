package tfg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This algorithm computes and sorts similarities between users using multiple threads.
 */
public class Main {

    // Parámetros
    final static String separator = ",";
    final static int threshold = 100;

    // Argumentos
    private static int threadsNumber;
    static File inputFile;
    static int neighborhoodSize;
    static int usersPerStep;
    static String inputPrefix;

    // Valores calculados
    private static int maxUserId;
    static int numberFilesByThreads;


    public static void main(String[] args) {

        /*
         * Manage arguments
         */
        switch (args[0]) {
            case "-similarities":
                inputPrefix = args[1];
                threadsNumber = Integer.valueOf(args[2]);
                break;
            case "-sort":
            case "-both":
                inputFile = new File(args[1]);
                threadsNumber = Integer.valueOf(args[2]);
                neighborhoodSize = Integer.valueOf(args[3]);
                usersPerStep = Integer.valueOf(args[4]);
                break;
            default:
                System.err.println("Available modes:");
                System.err.println("    -similarities <input-file> <number-threads>");
                System.err.println("    -sort <input-file> <number-threads> <neighborhood-size> <users-per-step>");
                System.err.println("    -both <input-file> <number-threads> <neighborhood-size> <users-per-step>");
                System.exit(1);
        }

        /*
         * Start computing tasks
         */
        switch (args[0]) {
            case "-similarities":
                maxUserId = maxUserId();
                computeSimilarities();
                break;
            case "-sort":
                maxUserId = maxUserId();
                sortSimilarities();
                break;
            case "-both":
                maxUserId = maxUserId();
                computeSimilarities();
                sortSimilarities();
                break;
        }

        System.out.println("Exiting main");
    }


    private static int maxUserId() {
        /*
         * UserId máximo
         */
        int maxUserId = 0;
        System.out.print("Calcula el userId máximo... ");
        String readLine;
        try {
            BufferedReader br = new BufferedReader(new FileReader(inputFile));
            while ((readLine = br.readLine()) != null) {
                if (Integer.valueOf(readLine.split(separator)[0]) > maxUserId) {
                    maxUserId = Integer.valueOf(readLine.split(separator)[0]);
                }
            }
            br.close();
            System.out.println(maxUserId);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

        return maxUserId;
    }


    private static void computeSimilarities() {
        /*
         * Calcular perfiles de usuario
         */
        if (threadsNumber > maxUserId) {
            System.err.println("Thread number can't be greater than userIds.");
            System.exit(1);
        }

        List<ComputeProfile> computeProfileThreads = new ArrayList<>();
        List<ComputeSimilarities> computeSimilarityThreads = new ArrayList<>();
        int step = maxUserId / threadsNumber;
        System.out.println("step: " + step);
        for (int i = 0; i<threadsNumber; i++) {
            System.out.println("f: " + (step * i + 1) + " to: " + (step * (i+1)));
            int from = step * i + 1;
            int to = step * (i+1);
            computeProfileThreads.add(new ComputeProfile(""+i, from, to));
            computeSimilarityThreads.add(new ComputeSimilarities(""+i, from, to));
        }

        if (maxUserId % threadsNumber != 0) {
            System.out.println("f: " + (step * threadsNumber + 1) + " to: " + (maxUserId));
            computeProfileThreads.add(
                    new ComputeProfile("" + threadsNumber, (step * threadsNumber + 1), maxUserId));
            computeSimilarityThreads.add(
                    new ComputeSimilarities("" + threadsNumber, (step * threadsNumber + 1), maxUserId));
        }

        for (ComputeProfile thread: computeProfileThreads) {
            thread.start();
        }

        for (ComputeProfile thread: computeProfileThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


        /*
         * Calcular similaridades
         */
        numberFilesByThreads = computeProfileThreads.size();

        for (ComputeSimilarities thread: computeSimilarityThreads) {
            thread.start();
        }

        for (ComputeSimilarities thread: computeSimilarityThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private static void sortSimilarities() {

        if (threadsNumber > maxUserId) {
            System.err.println("Thread number can't be greater than userIds.");
            System.exit(1);
        }

        List<SortSimilarities> sortSimilaritiesThreads = new ArrayList<>();

        int step = maxUserId / threadsNumber;
        System.out.println("step: " + step);
        for (int i = 0; i<threadsNumber; i++) {
            System.out.println("f: " + (step * i + 1) + " to: " + (step * (i+1)));
            int from = step * i + 1;
            int to = step * (i+1);
            sortSimilaritiesThreads.add(new SortSimilarities(""+i, from, to));
        }

        if (maxUserId % threadsNumber != 0) {
            System.out.println("f: " + (step * threadsNumber + 1) + " to: " + (maxUserId));
            sortSimilaritiesThreads.add(
                    new SortSimilarities("" + threadsNumber, (step * threadsNumber + 1), maxUserId));

        }

        numberFilesByThreads = sortSimilaritiesThreads.size();

        for (SortSimilarities thread: sortSimilaritiesThreads) {
            thread.start();
        }

        for (SortSimilarities thread: sortSimilaritiesThreads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
