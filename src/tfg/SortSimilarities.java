package tfg;

import java.io.*;
import java.util.*;

public class SortSimilarities extends Thread {

        private Integer USERS_STEP = 60;
        private final String threadName;
        private final int startId;
        private final int endId;

        public SortSimilarities(String threadName, int startId, int endId) {
            this.threadName = threadName;
            this.startId = startId;
            this.endId = endId;
        }

    @Override
    public void run() {

        System.out.println("Running thread " + threadName + ", startId: " + startId + ", endId: " + endId);
        BufferedWriter writer = null;
        BufferedReader reader;
        String line;

        try {
            writer = new BufferedWriter(new FileWriter(new File("sorted"+threadName+"-"+Main.outFile), false));
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Map<Integer, Integer>> usersMaps = new ArrayList<>(USERS_STEP);
        for (int i=0; i<USERS_STEP; i++) {
            usersMaps.add(new TreeMap<>(new Comparator<Integer>() {
                @Override
                public int compare(Integer integer, Integer t1) {
                    return (integer.compareTo(t1));
                }
            }));
        }

        for (int userId = startId; userId<=endId; userId+=USERS_STEP) {
            for (int fileCounter = 0; fileCounter<Main.numberFiles; fileCounter++) {
                try {
                    reader = new BufferedReader(new FileReader("similarity"+fileCounter+"-"+Main.outFile));
                    while ((line = reader.readLine()) != null) {
                        String [] lineContents = line.split(Main.separator);
                        int userA = Integer.valueOf(lineContents[0]);
                        int userB = Integer.valueOf(lineContents[1]);

                        for (int i=0; i+userId <= endId && i < USERS_STEP; i++) {
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

            for (int i=0; i+userId <= endId && i < USERS_STEP; i++) {
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


    private Map<Integer, Integer> addElement (Map<Integer, Integer> map, int userId, String similarity) {

        Integer s = Integer.valueOf(similarity);
        if (s > Main.threshold) {
            map.put(s, userId);
            if (map.size() > Main.neighborhoodSize) {
                for (Integer i: map.keySet()) {
                    map.remove(i);
                    break;
                }
            }
        }

        return map;
    }


    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> kvEntry, Map.Entry<K, V> t1) {
                return (t1.getValue()).compareTo(kvEntry.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
            if (result.size() >= Main.neighborhoodSize) { return result; }
        }

        return result;
    }
}
