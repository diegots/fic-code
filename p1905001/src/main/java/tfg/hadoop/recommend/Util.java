package tfg.hadoop.recommend;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Map;

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
}
