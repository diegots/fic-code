package tfg.util;

import java.io.*;
import java.util.*;

public class Util {

    public static <K, V> Map<K, V> mapFromFile(InputStream stream) {

        Map<K, V> results = null;

        try {
            ObjectInputStream objectStream = new ObjectInputStream(stream);
            results = (Map<K, V>) objectStream.readObject();

            objectStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return results;
    }


    public static void removeNRandomIndexes(Random generator, int nItems, List<Integer> items) {

        final int NON_VALID_ITEM_ID = -1;

        Set<Integer> indexes = new LinkedHashSet<>();
        while (indexes.size() < nItems) {
            int index = generator.ints(1, 0, items.size()).toArray()[0];
            indexes.add(index);
        }

        for (Integer index: indexes) {
            items.set(index, NON_VALID_ITEM_ID);
        }

        List<Integer> tmp = new ArrayList<>(items.size()-nItems);
        for (Integer item: items) {
            if (item != NON_VALID_ITEM_ID) {
                tmp.add(item);
            }
        }

        items.clear();
        items.addAll(tmp);
    }


    public static Random getRandomGenerator(int seed) {
        return new Random(seed);
    }


    public static void readLines(File file, ReadFileInterface readFile) {
        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                readFile.doSomething(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static List<Integer> generateNRandomValues(Random generator, Integer min, Integer max, Integer amount) {

        Integer actualMax = max - 1;
        if (amount > actualMax-min+1) {
            throw new IllegalArgumentException("Available values must be greater than the amount requested");
        }

        Set<Integer> values = new LinkedHashSet<>();
        Iterator<Integer> iterator = generator.ints(min, actualMax).iterator();
        while (values.size() < amount) {
            values.add(iterator.next());
        }

        return new ArrayList<>(values);
    }


    public static void writeLine(String outFile, String line) {
        BufferedWriter bufferedWriter;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(outFile, true));
            bufferedWriter.append(line);
            bufferedWriter.append('\n');
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
