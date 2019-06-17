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

        Writer writer = buildWriterFromFile(new File("sorted"+getThreadName()));
        if (writer == null) {
            return;
        }

        BufferedReader reader;
        Integer numberFiles = getContext().getInteger(Context.THREADS_NUMBER, 0);
        int usersPerStep = getContext().getInteger(Context.USERS_PER_STEP, -1);
        String separator = getContext().getString(Context.SEPARATOR, "");
        List<Map<Integer, Integer>> usersMaps = initializeUsersMaps(usersPerStep);

        String line;
        for (int userId = getMin(); userId<=getMax(); userId+=usersPerStep) {


            for (int fileCounter = 0; fileCounter<numberFiles; fileCounter++) {

                try {
                    reader = new BufferedReader(new FileReader("similarity"+fileCounter), 1000 * 8192);
                    while ((line = reader.readLine()) != null) {
                        String [] lineContents = line.split(separator);
                        int userA = Integer.valueOf(lineContents[0]);
                        int userB = Integer.valueOf(lineContents[1]);

                        for (int i=0; (i+userId) <= getMax() && i < usersPerStep; i++) {

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

                writeContentToFile(writer, sb.append("\n").toString());
            }
        } // end userId for loop

        closeWriter(writer);
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


    Writer buildWriterFromFile(File destination) {
        try {
            return new BufferedWriter(new FileWriter(destination));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    List<Map<Integer, Integer>> initializeUsersMaps(int mapSize) {

        List<Map<Integer, Integer>> maps = new ArrayList<>(mapSize);
        for (int i=0; i<mapSize; i++) {
            maps.add(new TreeMap<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer t1) {
                    return (integer.compareTo(t1));
                }
            }));
        }
        return maps;
    }

    void closeWriter(Writer writer) {
        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeContentToFile(Writer writer, String contents) {
        try {
            writer.append(contents);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
