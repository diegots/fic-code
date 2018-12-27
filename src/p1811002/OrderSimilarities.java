package p1811002;

import p1811002.utils.Utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OrderSimilarities {

    private String similaritiesPath;
    private String idxPath;
    private final Messages messages;

    public OrderSimilarities(String similaritiesPath, String idxPath, Messages messages) {
        this.similaritiesPath = similaritiesPath;
        this.idxPath = idxPath;
        this.messages = messages;
    }

    public long order () {

        messages.printMessageln("Start ordering");

        final long start = System.currentTimeMillis();
        final String DELIMITER = ",";
        HashMap<Integer, Double> map;
        Map<Integer, Double> aux;
        Iterator<Integer> iterator;
        StringBuilder idx;

        int count = 1;
        String s;
        String [] ss;
        File f = new File(similaritiesPath);

        try {
            BufferedReader br = new BufferedReader(new FileReader(f));

            while ((s = br.readLine()) != null) {
                ss = s.split(DELIMITER);

                // Put every element into a map
                map = new HashMap();
                for (int i=0; i<ss.length; i++) {
                    map.put(i, Double.parseDouble(ss[i]));
                }

                aux = Utilities.sortMapByValue(map);

                idx = new StringBuilder();
                iterator = aux.keySet().iterator();
                while (iterator.hasNext()) {
                    idx.append(iterator.next() + ",");
                }
                idx.delete(idx.length()-1, idx.length());

                Utilities.writeLine(idxPath, idx.toString());

                if (count++ % 100 == 0) {
                    messages.printDoing();
                }
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        messages.printMessageln(" done.");

        return System.currentTimeMillis() - start;
    }
}