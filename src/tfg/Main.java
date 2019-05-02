package tfg;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {

    static int numberFiles;
    static String separator = ",";
    static String outProfile;
    static File input;

    static File outProfiles;
    static File outFile;

    public static void main(String[] args) {

        input = new File(args[0]);
        outProfile = args[1];

        int threadsNumber = Integer.valueOf(args[2]);
        outProfiles = new File("profiles-" + args[1]);
        outFile = new File(args[1]);

        /**
         * UserId máximo
         */
        System.out.print("Calcula el userId máximo... ");
        int maxUserId = 0;
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
            computeProfileThreads.add(new ComputeProfile("" + threadsNumber, (step * threadsNumber + 1), maxUserId));
            computeSimilarityThreads.add(new ComputeSimilarities("" + threadsNumber, (step * threadsNumber + 1), maxUserId));
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

        /**
         * 
         */

        System.out.println("Exiting main");
    }
}
