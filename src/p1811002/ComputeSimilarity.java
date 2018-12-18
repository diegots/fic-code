package p1811002;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.collections4.map.HashedMap;
import p1811002.utils.Utilities;

import java.io.*;
import java.util.*;

public class ComputeSimilarity {

    private String dataSetPath, similaritiesPath, neighborsPath;

    /**
     * neighborSimilarityIdx contains all read data from input file. This is, all the ratings from
     * every user and item. Timestamps were not preserved.
     */
    private Map<Integer, Map<Integer, Double>> neighborSimilarityIdx;


    /**
     * Denominators from every user as needed by cosine similarity.
     */
    private Map<Integer, Double> denominators;


    public ComputeSimilarity(String dataSetPath, String similaritiesPath, String neighborsPath) {
        this.dataSetPath = dataSetPath;
        this.similaritiesPath = similaritiesPath;
        this.neighborsPath = neighborsPath;
    }


    /**
     * Start the algorithm. Steps:
     *  1. Read the dataset from the input dataSetPath.
     *  2. Compute denominators.
     *  3. Store user's items into TreeList objects.
     *  4. Compute cosine similarity writing every row to disk.
     */
    public void start () {

        try {
            neighborSimilarityIdx = readDataset(dataSetPath);
            denominators = computeDenom(neighborSimilarityIdx);
            cosineSimilarity(neighborSimilarityIdx, denominators);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Build a TreeList for every user's items
     * @param mData
     * @return
     */
    private Map<Integer, TreeList<Integer>> userItemsToTreeList (
            Map<Integer, Map<Integer, Double>> mData) {

        System.out.print("Storing user's items to TreeLists");

        Map<Integer, TreeList<Integer>> res = new HashMap<>();
        TreeList<Integer> treeList;
        int idx, count=0;

        for (Integer user: mData.keySet()) {

            if (count++ % 1000 == 0) {
                System.out.print(".");
            }

            idx = 0;

            treeList = new TreeList<>();
            for (Integer item: mData.get(user).keySet()) {
                treeList.add(idx++, item);
            }
            res.put(user, treeList);
        }

        System.out.println(" done.");

        return res;
    }


    /**
     * Compute cosine similarity for every pair of users if an userJ's id is greater than userI's id.
     * This way only the upper triangular part of the matrix is computed, almost halving the computing time needed.
     * Retriving similarity have to be always done like sim = neighMatrix (minor, major) fashion.
     *
     * Also, for every row computed its line writed to the similaritiesPath. Data is not stored in memory because
     * that approach becomes impossible with any big enough dataset.
     *
     * Every shared item is taken into account only once. This avoids lots of iterations in the most consumnig part of
     * the algorithm, computing shared items for every pair of users.
     * @param mData
     * @param mDenom
     */
    private void cosineSimilarity(
            Map<Integer, Map<Integer, Double>> mData,
            Map<Integer, Double> mDenom) {

        System.out.print("Computing similarities");

        long start = System.currentTimeMillis();
        double sum, ratingJ;
        int count=0;
        int MAX_USERS = 1384;

        Double res;
        MapIterator<Integer, Double> mapIterator;
        Iterator<Integer> iteratorUsersJ, iteratorUsersI;
        List<Integer> neighborIds = new ArrayList<>();
        Integer item, userJ, userI;
        HashedMap<Integer, Double> hm;
        List<Integer> similarities;

        iteratorUsersI = mData.keySet().iterator();
        while (iteratorUsersI.hasNext()) {

            userI = iteratorUsersI.next();
            //System.out.print(".");

            neighborIds.add(userI);
            similarities = new ArrayList<>();

            iteratorUsersJ = mData.keySet().iterator();
            while (iteratorUsersJ.hasNext()) {
                userJ = iteratorUsersJ.next();

                if (userJ < userI) {
                    similarities.add(0);
                } else {

                    sum = 0.0;

                    // Change to MapIterator
                    hm = (HashedMap<Integer, Double>) mData.get(userI);
                    mapIterator = hm.mapIterator();

                    while (mapIterator.hasNext()) {
                        item = mapIterator.next();

                        if (mData.get(userJ).containsKey(item)) {
                            ratingJ = mData.get(userJ).get(item);
                        } else {
                            continue;
                        }

                        sum += mapIterator.getValue() * ratingJ;
                    }

                    res = (sum / (mDenom.get(userI) * mDenom.get(userJ))) * 1000;
                    similarities.add(res.intValue());

                }
            }

            Utilities.writeLine(similaritiesPath, similarities);

            if (neighborIds.size() >= 100) {
                Utilities.writeLine(neighborsPath, neighborIds);
                neighborIds = new ArrayList<>();
            }

            count++;
            // Stop algorithm after MAX_USERS was reached. This is for testing purposes
            if (count >= MAX_USERS) {
                break;
            }
        }

        System.out.println(" for " + (count) + " users took " + ((System.currentTimeMillis() - start)/1000) + " seconds.");
    }


    /**
     * Compute denominator of cosine similarity formula for every user in the dataset.
     * @param m
     * @return
     */
    private Map<Integer, Double> computeDenom(Map<Integer, Map<Integer, Double>> m) {

        System.out.print("Computing denominators...");

        double d, sum;
        int userId;

        Map<Integer, Double> userData;
        Map<Integer, Double> md = new HashedMap<>();
        List<Integer> userIds = new ArrayList<>(m.keySet());
        Iterator<Integer> iteratorItems, iteratorUsers = userIds.iterator();

        while (iteratorUsers.hasNext()) {
            userId = iteratorUsers.next();
            md.put(userId, 0.0);
        }

        iteratorUsers = userIds.iterator();
        while (iteratorUsers.hasNext()) {
            userId = iteratorUsers.next();
            sum = 0.0;
            userData = m.get(userId);

            iteratorItems = userData.keySet().iterator();
            while (iteratorItems.hasNext()) {
                d = userData.get(iteratorItems.next());
                sum += Math.pow(d, 2);
            }

            md.put(userId, Math.sqrt(sum));
        }

        System.out.println(" done.");
        return md;
    }


    /**
     * Read data from input dataSetPath and returns a Map holding it.
     * @param inputPath
     * @return
     * @throws IOException
     */
    private Map<Integer, Map<Integer, Double>> readDataset(String inputPath) throws IOException {

        System.out.print("Reading input data...");

        final String DELIMITER = ",";
        final String HEADER_ITEM = "userId";
        Map<Integer, Map<Integer, Double>> m = new HashedMap<>();
        Map<Integer, Double> mu;
        Set<Integer> items = new HashSet<>();
        Integer userId, itemId;

        String s;
        String [] ss;
        File f = new File(inputPath);
        BufferedReader br = new BufferedReader(new FileReader(f));

        while ((s = br.readLine()) != null) {
            ss = s.split(DELIMITER);

            if (!HEADER_ITEM.equals(ss[0])) {

                userId = new Integer(ss[0]);

                mu = m.get(userId);
                if (null == mu) {
                    mu = new HashedMap<>();
                }

                itemId = new Integer(ss[1]);
                mu.put(itemId, new Double(ss[2]).doubleValue());
                m.put(userId, mu);
                items.add(itemId);
            }
        }

        br.close();
        System.out.println(" done.");
        return m;
    }
}
