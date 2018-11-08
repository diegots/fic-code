package p1811002;

import java.io.*;
import java.util.*;

public class Main {

    public static void main(String[] args) throws java.io.IOException {

        // args[0] ratings file -> userId | movieId | rating | timestamp
        // args[1] fSimilarities
        // args[2] fNeighbors

        Map<Integer, Map<Integer, Double>> mData = readData(args[0]);
        Map<Integer, Double> mDenom = computeDenom(mData);
        Map<Integer, Map<Integer, Double>> mSimil = computeSimil(mData, mDenom);
        Map<Integer, List<Integer>> mNeigh = orderNeigh(mData, mSimil);
        writeSimil (mData, mSimil, args[1]);
        writeNeigh(mNeigh, args[2]);
    }

    private static void writeNeigh (
            Map<Integer, List<Integer>> mNeigh, String outputFile)
            throws java.io.IOException {

        System.out.print("Writing neighbors to disk...");

        StringBuilder linea, cuerpo = new StringBuilder();

        for (int userId: mNeigh.keySet()) {
            linea = new StringBuilder();
            for (int neighborId: mNeigh.get(userId)) {
                linea.append("," + neighborId);
            }
            linea.delete(0,1);
            linea.append("\n");
            cuerpo.append(linea);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        bw.append(cuerpo.toString());
        bw.close();

        System.out.println(" done.");
    }

    private static void writeSimil (
            Map<Integer, Map<Integer, Double>> mData,
            Map<Integer, Map<Integer, Double>> mSimil,
            String outputFile) throws java.io.IOException {

        System.out.print("Writing similarities to disk...");

        // Cabecera en la primera linea con los userIds
        StringBuilder cabecera = new StringBuilder();
        for (int i: new TreeSet<>(mData.keySet())) {
            cabecera.append("," + i);
        }
        cabecera.delete(0, 1);
        cabecera.append("\n");

        // Similaridades
        StringBuilder linea, cuerpo = new StringBuilder();
        Map<Integer, Double> mu;
        for (int i: new TreeSet<>(mData.keySet())) {

            linea = new StringBuilder();
            mu = mSimil.get(i);
            for (int j: new TreeSet<>(mu.keySet())) {
                linea.append("," + mu.get(j));
            }

            linea.delete(0,1);
            linea.append("\n");
            cuerpo.append(linea);
        }

        BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
        bw.write(cabecera.toString());
        bw.append(cuerpo.toString());
        bw.close();

        System.out.println(" done.");
    }

    private static Map<Integer, List<Integer>> orderNeigh (
            Map<Integer, Map<Integer, Double>> m,
            Map<Integer, Map<Integer, Double>> mSimil) {

        System.out.print("Ordering neighbors...");

        Map<Integer, List<Integer>> mNeigh = new HashMap<>();
        Map<Integer, Double> mu, mOrdered;

        for (int i: m.keySet()) {
            mOrdered = sortMapByValue(mSimil.get(i));
            mNeigh.put(i, new ArrayList<>(mOrdered.keySet()));
        }

        System.out.println(" done.");
        return mNeigh;
    }

    private static <K, V extends Comparable<? super V>> Map<K, V> sortMapByValue(Map<K, V> map) {
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

    private static Map<Integer, Map<Integer, Double>> computeSimil (
            Map<Integer, Map<Integer, Double>> mData,
            Map<Integer, Double> mDenom) {

        System.out.print("Computing similarities");

        Map<Integer, Map<Integer, Double>> mSimil = new HashMap<>();
        Map<Integer, Double> mk;
        Set<Integer> itemsUserI , itemsUserJ, sharedItems;
        double sum, res;

        for (int userI: mData.keySet()) {
            System.out.print(".");

            mk = mSimil.get(userI);
            if (null == mk) {
                mk = new HashMap<>();
            }

            itemsUserI = new HashSet<>(mData.get(userI).keySet());
            for (int userJ: mData.keySet()) {
                itemsUserJ = mData.get(userJ).keySet();

                sharedItems = new HashSet<>(itemsUserI);
                sharedItems.retainAll(itemsUserJ); // items comunes

                sum = 0.0;
                for (int sharedItem: sharedItems) {
                    sum += mData.get(userI).get(sharedItem) * mData.get(userJ).get(sharedItem);
                }

                res = sum / (mDenom.get(userI) * mDenom.get(userJ));

                mk.put(userJ, res);
            }
            mSimil.put(userI, mk);
        }

        System.out.println(" done.");
        return mSimil;
    }

    private static Map<Integer, Double> computeDenom(Map<Integer, Map<Integer, Double>> m) {

        System.out.print("Computing denominators...");

        double d, sum;
        Map<Integer, Double> userData;
        Map<Integer, Double> md = new HashMap<>();
        List<Integer> userIds = new ArrayList<>(m.keySet());

        for (int userId: userIds) {
            md.put(userId, 0.0);
        }

        for (int userId: userIds) {
            sum = 0.0;
            userData = m.get(userId);
            for (int userItemId: userData.keySet()) {
                d = userData.get(userItemId);
                sum += Math.pow(d, 2);
            }
            md.put(userId, Math.sqrt(sum));
        }

        System.out.println(" done.");
        return md;
    }

    private static Map<Integer, Map<Integer, Double>> readData (String inputPath) throws IOException {

        System.out.print("Reading input data...");

        File f = new File(inputPath);
        BufferedReader br = new BufferedReader(new FileReader(f));
        String s;
        String [] ss;
        Map<Integer, Map<Integer, Double>> m = new HashMap<>();
        Map<Integer, Double> mu;
        Integer userId;

        while ((s = br.readLine()) != null) {
            ss = s.split(",");

            if (!"userId".equals(ss[0])) {

                userId = new Integer(ss[0]);

                mu = m.get(userId);
                if (null == mu) {
                    mu = new HashMap<>();
                }

                mu.put(new Integer(ss[1]), new Double(ss[2]));
                m.put(userId, mu);
            }
        }

        System.out.println(" done.");
        return m;
    }
}
