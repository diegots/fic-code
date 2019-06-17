package tfg;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SortSimilaritiesTest {

    Context prepareContext(int neighborhoodSize, int similarityThreshold) {
        final Context context = new Context();
        context.putInteger(Context.NEIGHBORHOOD_SIZE, neighborhoodSize);
        context.putInteger(Context.SIMILARITY_THRESHOLD, similarityThreshold);
        return context;
    }


    List<TreeMap<Integer, Integer>> prepareUsersAsList(int size) {

        List<TreeMap<Integer, Integer>> listOfMaps = (SortSimilarities.initListOfTreeMapWithSize(size));
        listOfMaps.get(0).put(4, 102);
        listOfMaps.get(0).put(5, 103);
        listOfMaps.get(0).put(1, 104);
        listOfMaps.get(0).put(3, 105);
        listOfMaps.get(0).put(6, 106);
        return listOfMaps;
    }


    List<TreeMap<Integer, Integer>> prepareOneUserAsList() {
        return prepareUsersAsList(1);
    }


    TreeMap<Integer, Integer> prepareOneUserAsMap() {
        return prepareOneUserAsList().get(0);
    }


    @Test
    public void initializeUsersMapsSizeTest() {

        List<TreeMap<Integer, Integer>> initializedResult = SortSimilarities.initListOfTreeMapWithSize(10);
        assertEquals(10, initializedResult.size());
    }


    @Test
    public void initializeUsersMapsContentsTest() {

        Map<Integer, Integer> userData = prepareOneUserAsMap();

        int counter = 0;
        for (Map.Entry<Integer, Integer> entry: userData.entrySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(new Integer(6), entry.getKey());
                    assertEquals(new Integer(106), entry.getValue());
                    break;
                case 1:
                    assertEquals(new Integer(5), entry.getKey());
                    assertEquals(new Integer(103), entry.getValue());
                    break;
                case 2:
                    assertEquals(new Integer(4), entry.getKey());
                    assertEquals(new Integer(102), entry.getValue());
                    break;
                case 3:
                    assertEquals(new Integer(3), entry.getKey());
                    assertEquals(new Integer(105), entry.getValue());
                    break;
                case 4:
                    assertEquals(new Integer(1), entry.getKey());
                    assertEquals(new Integer(104), entry.getValue());
                    break;
                default:
                    fail();
            }
        }
    }


    @Test
    public void addElementSizeUnderThresholdTest() {

        final TreeMap<Integer, Integer> userTopNeighbors = prepareOneUserAsMap();
        final int dataSize = userTopNeighbors.size();

        Context context = prepareContext(dataSize, 20);

        SortSimilarities sortSimilarities =
                new SortSimilarities(new TaskData(0,0,""), context);
        sortSimilarities.addElement(userTopNeighbors, 0, 110);
        sortSimilarities.addElement(userTopNeighbors, 10, 111);
        sortSimilarities.addElement(userTopNeighbors, 12, 112);
        sortSimilarities.addElement(userTopNeighbors, 15, 113);
        sortSimilarities.addElement(userTopNeighbors, 2, 114);
        assertEquals(dataSize, userTopNeighbors.size());
    }


    @Test
    public void addElementSizeOverThresholdTest() {
        final TreeMap<Integer, Integer> userTopNeighbors = prepareOneUserAsMap();
        final int neighborhoodSize = userTopNeighbors.size();
        final int threshold = 20;
        Context context = prepareContext(neighborhoodSize, threshold);

        SortSimilarities sortSimilarities =
                new SortSimilarities(new TaskData(0,0,""), context);
        sortSimilarities.addElement(userTopNeighbors, 30, 110);
        sortSimilarities.addElement(userTopNeighbors, 40, 111);
        sortSimilarities.addElement(userTopNeighbors, 41, 112);
        sortSimilarities.addElement(userTopNeighbors, 45, 113);
        sortSimilarities.addElement(userTopNeighbors, 42, 114);
        sortSimilarities.addElement(userTopNeighbors, threshold, 114);
        assertEquals(neighborhoodSize, userTopNeighbors.size());
    }


    @Test
    public void addElementTest() {
        final TreeMap<Integer, Integer> userTopNeighbors = prepareOneUserAsMap();
        final int neighborhoodSize = userTopNeighbors.size();
        final int threshold = 20;
        Context context = prepareContext(neighborhoodSize, threshold);

        SortSimilarities sortSimilarities =
                new SortSimilarities(new TaskData(0,0,""), context);

        sortSimilarities.addElement(userTopNeighbors, 1000, 117);

        int counter = 0;
        for (Map.Entry<Integer, Integer> entry: userTopNeighbors.entrySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(new Integer(1000), entry.getKey());
                    assertEquals(new Integer(117), entry.getValue());
                    break;
                case 1:
                    assertEquals(new Integer(6), entry.getKey());
                    assertEquals(new Integer(106), entry.getValue());
                    break;
                case 2:
                    assertEquals(new Integer(5), entry.getKey());
                    assertEquals(new Integer(103), entry.getValue());
                    break;
                case 3:
                    assertEquals(new Integer(4), entry.getKey());
                    assertEquals(new Integer(102), entry.getValue());
                    break;
                case 4:
                    assertEquals(new Integer(3), entry.getKey());
                    assertEquals(new Integer(105), entry.getValue());
                    break;
                default:
                    fail();
            }
        }

        sortSimilarities.addElement(userTopNeighbors, 999, 118);

        counter = 0;
        for (Map.Entry<Integer, Integer> entry: userTopNeighbors.entrySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(new Integer(1000), entry.getKey());
                    assertEquals(new Integer(117), entry.getValue());
                    break;
                case 1:
                    assertEquals(new Integer(999), entry.getKey());
                    assertEquals(new Integer(118), entry.getValue());
                    break;
                case 2:
                    assertEquals(new Integer(6), entry.getKey());
                    assertEquals(new Integer(106), entry.getValue());
                    break;
                case 3:
                    assertEquals(new Integer(5), entry.getKey());
                    assertEquals(new Integer(103), entry.getValue());
                    break;
                case 4:
                    assertEquals(new Integer(4), entry.getKey());
                    assertEquals(new Integer(102), entry.getValue());
                    break;
                default:
                    fail();
            }
        }
    }
}
