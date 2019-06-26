package tfg.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

        int[] indexes = generator.ints(nItems, 0, items.size()).toArray();

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

}
