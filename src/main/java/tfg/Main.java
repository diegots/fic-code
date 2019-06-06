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
        if (args.length == 0) {
            showHelp("Operation mode argument missing!");
            System.exit(1);
        }

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
            case "-help":
            default:
                showHelp();
                break;
        }

        /*
         * Start computing tasks
         */
        switch (args[0]) {
            case "-similarities":
                maxUserId = new MaxUserId(new File(""), ",").getMaxUserId();
                computeSimilarities();
                break;
            case "-sort":
                maxUserId = new MaxUserId(new File(""), ",").getMaxUserId();
                sortSimilarities();
                break;
            case "-both":
                maxUserId = new MaxUserId(new File(""),",").getMaxUserId();
                computeSimilarities();
                sortSimilarities();
                break;
        }

        System.out.println("Done");
    }


    private static void showHelp(String ... messages) {

        for (String message: messages) {
            System.err.println(message);
        }

        System.err.println("Choose one from the following available modes when calling this program:");
        System.err.println("    -help");
        System.err.println("    -similarities <input-file> <number-threads>");
        System.err.println("    -sort <input-file> <number-threads> <neighborhood-size> <users-per-step>");
        System.err.println("    -both <input-file> <number-threads> <neighborhood-size> <users-per-step>");
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
