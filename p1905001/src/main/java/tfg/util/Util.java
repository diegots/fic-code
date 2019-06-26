package tfg.hadoop.recommend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Util {
    static <K, V> Map<K, V> mapFromFile(InputStream stream) {

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


    public static void selectNIndexFromList(Random generator, int nItems, List<Integer> items) {

        int[] indexes = generator.ints(nItems, 0, items.size()-1).toArray();

        for (Integer i: indexes) {
            items.remove(i);
        }
    }
}
