package tfg;

import java.io.*;

public class ComputeSimilarities extends Task {

    public ComputeSimilarities(String threadName, int max, int min) {
        super(threadName, max, min);
    }

    @Override
    public void run() {
        System.out.println("Running thread " + threadName + ", startId: " + min + ", endId: " + max);

        /*
         * Abre ficheros
         */
        BufferedWriter writer = null;
        BufferedReader readerA = null;
        BufferedReader readerB = null;

        try {
            writer = new BufferedWriter(new FileWriter(new File("similarity"+threadName), false));


            /**
             *
             */
            for (int fileCounterA = 0; fileCounterA<Main.numberFilesByThreads; fileCounterA++) {
                readerA = new BufferedReader(new FileReader("profile"+fileCounterA));
                String lineA;
                while ((lineA = readerA.readLine()) != null) {

                    String [] valuesA = lineA.split(Main.separator);
                    int userA = Integer.valueOf(valuesA[0]);
                    double denomA = Double.valueOf(valuesA[valuesA.length-1]);
                    if (userA < min) {
                        continue;
                    } else if (userA > max) {
                        break;
                    } else {
                        for (int fileCounterB = 0; fileCounterB<Main.numberFilesByThreads; fileCounterB++) {
                            readerB = new BufferedReader(new FileReader("profile"+fileCounterB));
                            String lineB;
                            while ((lineB = readerB.readLine()) != null) {
                                String [] valuesB = lineB.split(Main.separator);
                                int userB = Integer.valueOf(valuesB[0]);
                                if (userB < userA) {
                                    continue;
                                } else {
                                    double commonRatings = 0.0;
                                    for (int profileA = 1; profileA<valuesA.length-1; profileA+=2) {
                                        for (int profileB = 1; profileB<valuesB.length-1; profileB+=2) {

                                            if (valuesA[profileA].equals(valuesB[profileB])) {
                                                //System.out.println("[" + userA +","+ userB + "]" + "common item: " + valuesA[profileA]);
                                                commonRatings += Double.valueOf(valuesA[profileA+1]) * Double.valueOf(valuesB[profileB+1]);
                                            }
                                        }
                                    }

                                    //System.out.println("denom " + userA + ": " + denomA);
                                    //System.out.println("denom " + userB + ": " + valuesB[valuesB.length-1]);
                                    int sim = new Double((commonRatings / (denomA * Double.valueOf(valuesB[valuesB.length-1]))) * 1000).intValue();

                                    //System.out.println("[" + userA +","+ userB + "]" + " common ratings: " + commonRatings + " sim: " + sim);
                                    writer.append(userA + Main.separator + userB + Main.separator + sim + "\n");
                                }
                            }
                        }
                    }
                }
            }

            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Thread " + threadName + " exiting");
    }
}
