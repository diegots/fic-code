package tfg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static String separator = ",";
    static int threshold = 30;

    static File input;
    static File outFile;
    static String outProfile;
    static File outProfiles;
    static int threadsNumber;
    static int neighborhoodSize;

    private static int maxUserId;
    static int numberFiles;

    public static void main(String[] args) {

        input = new File(args[1]);
        outFile = new File(args[2]);

        outProfile = args[2];
        outProfiles = new File("profiles-" + args[2]);

        threadsNumber = Integer.valueOf(args[3]);

        if ("-similarities".equals(args[0])) {
            maxUserId = maxUserId();
            computeSimilarities();

        } else if ("-sort".equals(args[0])) {
            maxUserId = maxUserId();
            neighborhoodSize = Integer.valueOf(args[4]);
            sortSimilarities();

        } else if ("-all".equals(args[0])) {
            maxUserId = maxUserId();
            neighborhoodSize = Integer.valueOf(args[4]);
            computeSimilarities();
            sortSimilarities();

        } else {
            System.out.println("Available modes:");
            System.out.println("    -similarities <input file> <output pattern> <number threads>");
            System.out.println("    -sort <input file> <output pattern> <number threads> <neighborhood size>");
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
            BufferedReader br = new BufferedReader(new FileReader(input));
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
