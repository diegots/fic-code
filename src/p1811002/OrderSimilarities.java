package p1811002;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.bidimap.TreeBidiMap;
import org.apache.commons.collections4.map.HashedMap;
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

    public OrderSimilarities(String similaritiesPath, String idxPath) {
        this.similaritiesPath = similaritiesPath;
        this.idxPath = idxPath;
    }

    public void start () {

        System.out.print("Start ordering");

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
                    map.put(i, new Double(ss[i]).doubleValue());
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
                    System.out.print(".");
                }
            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(" done.");
    }
}