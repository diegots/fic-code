package tfg;

import java.io.*;
import java.util.*;

public class SortSimilarities extends Task {

    public SortSimilarities(TaskData taskData, Context context) {
        super(taskData, context);
    }


    @Override
    public void run() {

        System.out.println("Running thread " + getThreadName() + ", min: " + getMin() + ", max: " + getMax());
        BufferedWriter writer = null;
        BufferedReader reader;
        String line;
        Integer numberFiles = getContext().getInteger(Context.THREADS_NUMBER, 0);
        int usersPerStep = getContext().getInteger(Context.USERS_PER_STEP, -1);
        String separator = getContext().getString(Context.SEPARATOR, "");

        try {
            writer = new BufferedWriter(new FileWriter(new File("sorted"+getThreadName()), false));
        } catch (IOException e) {
            e.printStackTrace();
        }


        List<Map<Integer, Integer>> usersMaps = new ArrayList<>(usersPerStep);
        for (int i=0; i<usersPerStep; i++) {
            usersMaps.add(new TreeMap<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer t1) {
                    return (integer.compareTo(t1));
                }
            }));
        }


        for (int userId = getMin(); userId<=getMax(); userId+=usersPerStep) {

            System.out.println("thread: " + getThreadName() + " - " + "min: " + getMin() + " - " + "max: " + getMax());
            for (int fileCounter = 0; fileCounter<numberFiles; fileCounter++) {
                System.out.println("thread: " + getThreadName() + " - " + "file: " + fileCounter);
                try {
                    reader = new BufferedReader(new FileReader("similarity"+fileCounter), 1000 * 8192);
                    while ((line = reader.readLine()) != null) {
                        String [] lineContents = line.split(separator);
                        int userA = Integer.valueOf(lineContents[0]);
                        int userB = Integer.valueOf(lineContents[1]);

                        for (int i=0; (i+userId) <= getMax() && i < usersPerStep; i++) {
                            //System.out.println("thread: " + getThreadName() + " - " + "userId: " + (i+userId));
                            Map<Integer, Integer> map = usersMaps.get(i);
                            if (userA == i+userId) {
                                map = addElement(map, userB, lineContents[2]);
                            } else if (userB == i+userId) {
                                map = addElement(map, userA, lineContents[2]);
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // end file counter for loop

            for (int i=0; i+userId <= getMax() && i < usersPerStep; i++) {
                Map<Integer, Integer> map = usersMaps.get(i);
                StringBuilder sb = new StringBuilder().append(i+userId);
                List<Integer> l = new ArrayList<>(map.keySet());
                for (int j=l.size()-1; j>=0; j--) {
                    sb.append(",").append(map.get(l.get(j)));
                }

                try {
                    writer.append(sb.append("\n").toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } // end userId for loop

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Map<Integer, Integer> addElement(Map<Integer, Integer> map, int userId, String similarity) {

        int threshold = getContext().getInteger(Context.SEPARATOR, -1);
        int neighborhoodSize = getContext().getInteger(Context.NEIGHBORHOOD_SIZE, -1);

        Integer s = Integer.valueOf(similarity);
        if (s > threshold) {
            map.put(s, userId);
            if (map.size() > neighborhoodSize) {
                for (Integer i: map.keySet()) {
                    map.remove(i);
                    break;
                }
            }
        }

        return map;
    }
}
