package tfg;

import java.io.*;

public class ComputeSimilarities extends Task {

    public ComputeSimilarities(TaskData taskData, Context context) {
        super(taskData, context);
    }

    @Override
    public void run() {
        System.out.println("Running thread " + getThreadName() + ", startId: " + getMin() + ", endId: " + getMax());

        String separator = getContext().getString(Context.SEPARATOR, "");
        Integer numberFiles = getContext().getInteger(Context.THREADS_NUMBER, 0);

        BufferedWriter writer = null;
        BufferedReader readerA = null;
        BufferedReader readerB = null;

        try {
            writer = new BufferedWriter(new FileWriter(new File("similarity"+getThreadName()), false));

            for (int fileCounterA = 0; fileCounterA<numberFiles; fileCounterA++) {
                readerA = new BufferedReader(new FileReader("profile"+fileCounterA));
                String lineA;
                while ((lineA = readerA.readLine()) != null) {

                    String [] valuesA = lineA.split(separator);
                    int userA = Integer.valueOf(valuesA[0]);
                    double denomA = Double.valueOf(valuesA[valuesA.length-1]);
                    if (userA < getMin()) {
                        continue;
                    } else if (userA > getMax()) {
                        break;
                    } else {
                        for (int fileCounterB = 0; fileCounterB<numberFiles; fileCounterB++) {
                            readerB = new BufferedReader(new FileReader("profile"+fileCounterB));
                            String lineB;
                            while ((lineB = readerB.readLine()) != null) {
                                String [] valuesB = lineB.split(separator);
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
                                    writer.append(userA + separator + userB + separator + sim + "\n");
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

        System.out.println("Thread " + getThreadName() + " exiting");
    }
}
