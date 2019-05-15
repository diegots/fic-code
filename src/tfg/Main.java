package tfg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Este programa calcula las similaridades entre vecinos.
 */
public class Main {

    static String separator = ",";
    static int threshold = 100;

    static File inputFile;
    static File outputFile;

    static String outProfile;
    static File outProfileFile;

    static int threadsNumber;
    static int neighborhoodSize;
    static int usersPerStep;

    private static int maxUserId;
    static int numberFiles;

    public static void main(String[] args) {

        /*
         * Manage arguments
         */
        if ("-similarities".equals(args[0])) {
            // Branch for similarities. No need to store arguments but for avoid else branch

        } else if ("-sort".equals(args[0])) {
            neighborhoodSize = Integer.valueOf(args[4]);
            usersPerStep = Integer.valueOf(args[5]);

        } else if ("-both".equals(args[0])) {
            neighborhoodSize = Integer.valueOf(args[4]);
            usersPerStep = Integer.valueOf(args[5]);

        } else {
            System.out.println("Available modes:");
            System.out.println("    -similarities <input-file> <output-pattern> <number-threads>");
            System.out.println("    -sort <input-file> <output-pattern> <number-threads> <neighborhood-size> <users-per-step>");
            System.out.println("    -both <input-file> <output-pattern> <number-threads> <neighborhood-size> <users-per-step>");
        }

        inputFile = new File(args[1]);
        outputFile = new File(args[2]);

        outProfile = args[2];
        outProfileFile = new File("profiles-" + args[2]);

        threadsNumber = Integer.valueOf(args[3]);

        /*
         * Start computing tasks
         */
        maxUserId = maxUserId();

        switch (args[0]) {
            case "-similarities":
                computeSimilarities();
                break;
            case "-sort":
                sortSimilarities();
                break;
            case "-both":
                computeSimilarities();
                sortSimilarities();
                break;
        }

        System.out.println("Exiting main");
    }

    private static int maxUserId() {
        /**
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
        /**
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


        /**
         * Calcular similaridades
         */
        numberFiles = computeProfileThreads.size();

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

        numberFiles = sortSimilaritiesThreads.size();

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
