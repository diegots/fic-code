package tfg;

import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.io.Writer;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class SortSimilaritiesTest {

    Context prepareContext(int neighborhoodSize, int similarityThreshold, int usersPerStep) {
        final Context context = new Context();
        context.putInteger(Context.NEIGHBORHOOD_SIZE, neighborhoodSize);
        context.putInteger(Context.SIMILARITY_THRESHOLD, similarityThreshold);
        context.putInteger(Context.USERS_PER_STEP, usersPerStep);
        context.putString(Context.SEPARATOR, ",");
        return context;
    }


    List<SimilaridadYVecinoTreeMap> prepareUsersAsList(int size) {

        List<SimilaridadYVecinoTreeMap> listOfMaps = (SortSimilarities.initListOfTreeMapWithSize(size));
        listOfMaps.get(0).put(new SimilaridadYVecino(4, 102));
        listOfMaps.get(0).put(new SimilaridadYVecino(5, 103));
        listOfMaps.get(0).put(new SimilaridadYVecino(1, 104));
        listOfMaps.get(0).put(new SimilaridadYVecino(3, 105));
        listOfMaps.get(0).put(new SimilaridadYVecino(6, 106));

        return listOfMaps;
    }


    List<SimilaridadYVecinoTreeMap> prepareOneUserAsList() {
        return prepareUsersAsList(1);
    }


    SimilaridadYVecinoTreeMap prepareOneUserAsMap() {
        return prepareOneUserAsList().get(0);
    }


    @Test
    public void initializeUsersMapsSizeTest() {

        List<SimilaridadYVecinoTreeMap> initializedResult = SortSimilarities.initListOfTreeMapWithSize(10);
        assertEquals(10, initializedResult.size());
    }


    @Test
    public void initializeUsersMapsContentsTest() {

        SimilaridadYVecinoTreeMap userData = prepareOneUserAsMap();

        int counter = 0;
        for (SimilaridadYVecino entry: userData.keySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(6, entry.getSimilarity());
                    assertEquals(106, entry.getUserId());
                    break;
                case 1:
                    assertEquals(5, entry.getSimilarity());
                    assertEquals(103, entry.getUserId());
                    break;
                case 2:
                    assertEquals(4, entry.getSimilarity());
                    assertEquals(102, entry.getUserId());
                    break;
                case 3:
                    assertEquals(3, entry.getSimilarity());
                    assertEquals(105, entry.getUserId());
                    break;
                case 4:
                    assertEquals(1, entry.getSimilarity());
                    assertEquals(104, entry.getUserId());
                    break;
                default:
                    fail();
            }
        }
    }


    @Test
    public void addElementSizeUnderThresholdTest() {

        final SimilaridadYVecinoTreeMap userTopNeighbors = prepareOneUserAsMap();
        final int dataSize = userTopNeighbors.size();

        Context context = prepareContext(dataSize, 20, 10);

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
        final SimilaridadYVecinoTreeMap userTopNeighbors = prepareOneUserAsMap();
        final int neighborhoodSize = userTopNeighbors.size();
        final int threshold = 20;
        Context context = prepareContext(neighborhoodSize, threshold, 10);

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
        final SimilaridadYVecinoTreeMap userTopNeighbors = prepareOneUserAsMap();
        final int neighborhoodSize = userTopNeighbors.size();
        final int threshold = 20;
        Context context = prepareContext(neighborhoodSize, threshold, 10);

        SortSimilarities sortSimilarities =
                new SortSimilarities(new TaskData(0,0,""), context);

        sortSimilarities.addElement(userTopNeighbors, 1000, 117);

        int counter = 0;
        for (Map.Entry<SimilaridadYVecino, Integer> entry: userTopNeighbors.entrySet()) {
        //for (Map.Entry<Integer, Integer> entry: userTopNeighbors.entrySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(1000, entry.getKey().getSimilarity());
                    assertEquals(117, entry.getKey().getUserId());
                    break;
                case 1:
                    assertEquals(6, entry.getKey().getSimilarity());
                    assertEquals(106, entry.getKey().getUserId());
                    break;
                case 2:
                    assertEquals(5, entry.getKey().getSimilarity());
                    assertEquals(103, entry.getKey().getUserId());
                    break;
                case 3:
                    assertEquals(4, entry.getKey().getSimilarity());
                    assertEquals(102, entry.getKey().getUserId());
                    break;
                case 4:
                    assertEquals(3, entry.getKey().getSimilarity());
                    assertEquals(105, entry.getKey().getUserId());
                    break;
                default:
                    fail();
            }
        }

        sortSimilarities.addElement(userTopNeighbors, 999, 118);

        counter = 0;
        for (Map.Entry<SimilaridadYVecino, Integer> entry: userTopNeighbors.entrySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(1000, entry.getKey().getSimilarity());
                    assertEquals(117, entry.getKey().getUserId());
                    break;
                case 1:
                    assertEquals(999, entry.getKey().getSimilarity());
                    assertEquals(118, entry.getKey().getUserId());
                    break;
                case 2:
                    assertEquals(6, entry.getKey().getSimilarity());
                    assertEquals(106, entry.getKey().getUserId());
                    break;
                case 3:
                    assertEquals(5, entry.getKey().getSimilarity());
                    assertEquals(103, entry.getKey().getUserId());
                    break;
                case 4:
                    assertEquals(4, entry.getKey().getSimilarity());
                    assertEquals(102, entry.getKey().getUserId());
                    break;
                default:
                    fail();
            }
        }

        sortSimilarities.addElement(userTopNeighbors, 3, 105);

        counter = 0;
        for (Map.Entry<SimilaridadYVecino, Integer> entry: userTopNeighbors.entrySet()) {
            switch (counter++) {
                case 0:
                    assertEquals(1000, entry.getKey().getSimilarity());
                    assertEquals(117, entry.getKey().getUserId());
                    break;
                case 1:
                    assertEquals(999, entry.getKey().getSimilarity());
                    assertEquals(118, entry.getKey().getUserId());
                    break;
                case 2:
                    assertEquals(6, entry.getKey().getSimilarity());
                    assertEquals(106, entry.getKey().getUserId());
                    break;
                case 3:
                    assertEquals(5, entry.getKey().getSimilarity());
                    assertEquals(103, entry.getKey().getUserId());
                    break;
                case 4:
                    assertEquals(4, entry.getKey().getSimilarity());
                    assertEquals(102, entry.getKey().getUserId());
                    break;
                default:
                    fail();
            }
        }
    }

    @Test
    public void writeUserNeighborsTest() throws IOException {
        final List<SimilaridadYVecinoTreeMap> userTopNeighbors = prepareOneUserAsList();
        final int neighborhoodSize = userTopNeighbors.size();
        final int threshold = 20;
        final int usersPerStep = 10;
        Context context = prepareContext(neighborhoodSize, threshold, usersPerStep);

        SortSimilarities sortSimilarities =
                new SortSimilarities(new TaskData(1,1,""), context);
        Writer writerMock = mock(Writer.class);

        sortSimilarities.writeUserNeighbors(1, writerMock, userTopNeighbors);
        verify(writerMock, Mockito.times(1)).append("1,106,103,102,105,104\n");
    }
}
