package p1811002;

import org.apache.commons.collections4.list.TreeList;
import org.apache.commons.collections4.map.HashedMap;
import p1811002.utils.Utilities;

import java.io.*;
import java.util.*;

public class ComputeSimilarity {

    private String path, similaritiesPath, neighborsPath;

    /**
     * fullData contains all read data from input file. This is, all the ratings from
     * every user and item. Timestamps were not preserved.
     */
    private Map<Integer, Map<Integer, Double>> fullData;

    /**
     * Denominators from every user as needed by cosine similarity.
     */
    private Map<Integer, Double> denominators;

    /**
     * User's items as a map of TreeLists. TreeList was selectec because it's a faster
     * implementation of a list, compared to any of the standard API classes.
     */
    private Map<Integer, TreeList<Integer>> usersItems;


    public ComputeSimilarity(String path, String similaritiesPath, String neighborsPath) {
        this.path = path;
        this.similaritiesPath = similaritiesPath;
        this.neighborsPath = neighborsPath;
    }


    /**
     * Start the algorithm. Steps:
     *  1. Read the dataset from the input path.
     *  2. Compute denominators.
     *  3. Store user's items into TreeList objects.
     *  4. Compute cosine similarity writing every row to disk.
     */
    public void start () {

        try {
            fullData = readDataset(path);
            denominators = computeDenom(fullData);
            usersItems = userItemsToTreeList(fullData);
            cosineSimilarity(fullData, denominators);

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
        double sum, res;
        int idx, itemI, itemJ, count=0, sharedItem;
        int MAX_USERS = 1384;

        TreeList<Integer> itemsUserI, itemsUserJ, sharedItems;
        Iterator<Integer> iteratorJ, iteratorI, iterator;
        StringBuilder similaritiesString, neighborIds;

        for (int userI: mData.keySet()) {

            System.out.print(".");
            neighborIds = new StringBuilder().append(userI + "\t");
            similaritiesString = new StringBuilder();
            itemsUserI = usersItems.get(userI);

            for (int userJ: mData.keySet()) {

                if (userJ < userI) {
                    similaritiesString.append("0,");
                } else {
//                neighborIds.append(userJ + ",");

                    itemsUserJ = new TreeList<>(usersItems.get(userJ));
                    iteratorI = itemsUserI.iterator();

                    // Obtain shared items
                    idx = 0;
                    sharedItems = new TreeList<>();
                    while (iteratorI.hasNext()) {
                        itemI = iteratorI.next();
                        iteratorJ = itemsUserJ.iterator();
                        while (iteratorJ.hasNext()) {
                            itemJ = iteratorJ.next();

                            if (itemI == itemJ) {
                                sharedItems.add(idx++, itemI);
                                itemsUserJ.remove(itemsUserJ.indexOf(itemI));

                                break;
                            }
                        }
                    }

                    sum = 0.0;
                    iterator = sharedItems.iterator();
                    while (iterator.hasNext()) {
                        sharedItem = iterator.next();
                        sum += fullData.get(userI).get(sharedItem) * fullData.get(userJ).get(sharedItem);
                    }
                    res = sum / (mDenom.get(userI) * mDenom.get(userJ));

                    similaritiesString.append(res + ",");
                }
            }

            similaritiesString.delete(similaritiesString.length()-1, similaritiesString.length());
            Utilities.writeLine(similaritiesPath, similaritiesString.toString());

            Utilities.writeLine(neighborsPath, neighborIds.toString());

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
        Map<Integer, Double> userData;
        Map<Integer, Double> md = new HashedMap<>();
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


    /**
     * Read data from input path and returns a Map holding it.
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
        Integer userId;

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

                mu.put(new Integer(ss[1]), new Double(ss[2]).doubleValue());
                m.put(userId, mu);
            }
        }

        br.close();
        System.out.println(" done.");
        return m;
    }
}
