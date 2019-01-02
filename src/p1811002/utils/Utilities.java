package p1811002.utils;

import it.unimi.dsi.io.InputBitStream;
import it.unimi.dsi.io.OutputBitStream;
import p1811002.Conf;

import java.io.*;
import java.util.*;

public class Utilities {

    public static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new LinkedList<>(map.entrySet());

        Collections.sort( list, new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(Map.Entry<K, V> o1, Map.Entry<K, V> o2) {
                return (o1.getValue()).compareTo(o2.getValue());
            }
        });

        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list)
            result.put(entry.getKey(), entry.getValue());

        return result;
    }

    public static void writeLine (String outFile, String line) {
        BufferedWriter bw;
        try {
            bw = new BufferedWriter(new FileWriter(outFile, true));
            bw.append(line);
            bw.append('\n');
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeLine (String outFile, List<Integer> line) {

        Iterator<Integer> iterator = line.iterator();
        try {
            FileOutputStream fos = new FileOutputStream(outFile, true);
            OutputBitStream obs = new OutputBitStream(fos);

            while (iterator.hasNext()) {
                obs.writeDelta(iterator.next());
            }
            obs.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> readAllFile(String inFile) {

        List<Integer> res = new ArrayList();

        try {
            FileInputStream fis = new FileInputStream(inFile);
            InputBitStream ibs = new InputBitStream(fis);

            while (ibs.available() > 0) {
                int i = ibs.readDelta();
                res.add(i);
            }

            ibs.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (EOFException e) {
            // This is not an exception, the input was read completelly

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static List<Integer> readOneRow(String inFile) {
        List<Integer> res = new ArrayList<>();

        try {
            FileInputStream fis = new FileInputStream(inFile);
            InputBitStream ibs = new InputBitStream(fis);

            while (ibs.available() > 0) {
                int i = ibs.readDelta();

                if (Conf.getConf().getRowDelimiter() == i) {
                    break;
                }

                res.add(i);
            }
            ibs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static void checkNull (Object o) {
        if (null == o) {
            throw new NullPointerException();
        }
    }
}
