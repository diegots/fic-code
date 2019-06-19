package tfg;

import java.io.*;
import java.util.*;

public class SortSimilarities extends Task {


    final static int userAPosition = 0;
    final static int userBPosition = 1;
    final static int similarityPosition = 2;

    int usersPerStep = getContext().getInteger(Context.USERS_PER_STEP, null);


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
        Integer numberFiles = getContext().getInteger(Context.THREADS_NUMBER, null);

        String separator = getContext().getString(Context.SEPARATOR, null);


        String line;
        for (int userIdDelta = getMin(); userIdDelta<=getMax(); userIdDelta+=usersPerStep) {

            List<SimilaridadYVecinoTreeMap> usersMaps = initListOfTreeMapWithSize(usersPerStep);

            for (int fileCounter = 0; fileCounter<numberFiles; fileCounter++) {

                try {
                    reader = new BufferedReader(new FileReader("similarity"+fileCounter), 1000 * 8192);
                    while ((line = reader.readLine()) != null) {
                        String [] lineContents = line.split(separator);
                        int userA = Integer.valueOf(lineContents[userAPosition]);
                        int userB = Integer.valueOf(lineContents[userBPosition]);
                        int similarity = Integer.valueOf(lineContents[similarityPosition]);

                        for (int i=0; i+userIdDelta <= getMax() && i<usersPerStep; i++) {

                            if (i+userIdDelta != 671) {
                                continue;
                            }

                            final SimilaridadYVecinoTreeMap map = usersMaps.get(i);
                            if (userA == i+userIdDelta) {
                                System.out.printf("userId: %d\n",i+userIdDelta);
                                addElement(map, similarity, userB);
                            } else if (userB == i+userIdDelta) {
                                System.out.printf("userId: %d\n",i+userIdDelta);
                                addElement(map, similarity, userA);
                            }
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } // end file counter for loop
            writeUserNeighbors(userIdDelta, writer, usersMaps);

        } // end userId for loop

        closeWriter(writer);
    }


    void addElement(SimilaridadYVecinoTreeMap map, Integer similarity, Integer userId) {

        Integer threshold = getContext().getInteger(Context.SIMILARITY_THRESHOLD, null);
        Integer neighborhoodSize = getContext().getInteger(Context.NEIGHBORHOOD_SIZE, null);

        if (similarity > threshold) {
            System.out.printf("addElement pub s[%d] u[%d]\n", similarity, userId);
            map.put(new SimilaridadYVecino(similarity, userId));
            if (map.size() > neighborhoodSize) {
                System.out.printf("addElement remove s[%d] u[%d]\n", map.lastKey().getSimilarity(), map.lastKey().getUserId());
                map.remove(map.lastKey());

            }

            if (similarity == 268 && userId == 13) {
                for (SimilaridadYVecino entry: map.keySet()) {
                    System.out.printf("s[%d], u[%d]\n", entry.getSimilarity(), entry.getUserId());
                }
            }
            System.out.println();
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


    static List<SimilaridadYVecinoTreeMap> initListOfTreeMapWithSize(int size) {

        List<SimilaridadYVecinoTreeMap> listOfMaps = new ArrayList<>(size);
        for (int i=0; i<size; i++) {
            listOfMaps.add(new SimilaridadYVecinoTreeMap());
        }

        return listOfMaps;
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


    void writeUserNeighbors (int userIdDelta, Writer writer, List<SimilaridadYVecinoTreeMap> usersMaps) {

        for (int i=0; i+userIdDelta <= getMax() && i < usersPerStep; i++) {
            SimilaridadYVecinoTreeMap map = usersMaps.get(i);
            StringBuilder sb = new StringBuilder().append(i+userIdDelta);

            for (SimilaridadYVecino j: map.keySet()) {
                sb.append(getContext().getString(Context.SEPARATOR, null)).append(j.getUserId());
            }

            writeContentToFile(writer, sb.append("\n").toString());
        }
    }
}
