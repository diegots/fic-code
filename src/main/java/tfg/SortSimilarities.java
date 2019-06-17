package tfg;

import java.io.*;
import java.util.*;

public class SortSimilarities extends Task {


    final static int userAPosition = 0;
    final static int userBPosition = 0;
    final static int similarityPosition = 0;

    int usersPerStep = getContext().getInteger(Context.USERS_PER_STEP, -1);


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

        String separator = getContext().getString(Context.SEPARATOR, "");
        List<TreeMap<Integer, Integer>> usersMaps = initListOfTreeMapWithSize(usersPerStep);

        String line;
        for (int userId = getMin(); userId<=getMax(); userId+=usersPerStep) {

            for (int fileCounter = 0; fileCounter<numberFiles; fileCounter++) {

                try {
                    reader = new BufferedReader(new FileReader("similarity"+fileCounter), 1000 * 8192);
                    while ((line = reader.readLine()) != null) {
                        String [] lineContents = line.split(separator);
                        int userA = Integer.valueOf(lineContents[userAPosition]);
                        int userB = Integer.valueOf(lineContents[userBPosition]);
                        int similarity = Integer.valueOf(lineContents[similarityPosition]);

                        for (int i=0; (i+userId) <= getMax() && i < usersPerStep; i++) {

                            final TreeMap<Integer, Integer> map = usersMaps.get(i);
                            if (userA == i+userId) {
                                addElement(map, userB, similarity);
                            } else if (userB == i+userId) {
                                addElement(map, userA, similarity);
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // end file counter for loop
            writeUserNeighbors(userId, writer, usersMaps);

        } // end userId for loop

        closeWriter(writer);
    }


    void addElement(TreeMap<Integer, Integer> map, Integer similarity, Integer userId) {

        Integer threshold = getContext().getInteger(Context.SIMILARITY_THRESHOLD, null);
        Integer neighborhoodSize = getContext().getInteger(Context.NEIGHBORHOOD_SIZE, null);

        if (similarity > threshold) {
            map.put(similarity, userId);
            if (map.size() > neighborhoodSize) {
                map.remove(map.lastKey());
            }
        }
    }


    Writer buildWriterFromFile(File destination) {
        try {
            return new BufferedWriter(new FileWriter(destination));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    static List<TreeMap<Integer, Integer>> initListOfTreeMapWithSize(int size) {

        List<TreeMap<Integer, Integer>> maps = new ArrayList<>(size);
        for (int i=0; i<size; i++) {
            maps.add(new TreeMap<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer t1) {
                    return (t1.compareTo(integer));
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


    void writeUserNeighbors (int userId, Writer writer, List<TreeMap<Integer, Integer>> usersMaps) {

        for (int i=0; i+userId <= getMax() && i < usersPerStep; i++) {
            Map<Integer, Integer> map = usersMaps.get(i);
            StringBuilder sb = new StringBuilder().append(i+userId);
            List<Integer> l = new ArrayList<>(map.keySet());
            for (int j=l.size()-1; j>=0; j--) {
                sb.append(",").append(map.get(l.get(j)));
            }

            writeContentToFile(writer, sb.append("\n").toString());
        }
    }
}
