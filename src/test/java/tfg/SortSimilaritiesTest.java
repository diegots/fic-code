package tfg;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class SortSimilaritiesTest {

    List<Map<Integer, Integer>> prepareUsersAsList(int size) {

        List<Map<Integer, Integer>> listOfMaps = (SortSimilarities.initListOfTreeMapWithSize(size));
        listOfMaps.get(0).put(4, 102);
        listOfMaps.get(0).put(5, 103);
        listOfMaps.get(0).put(1, 104);
        listOfMaps.get(0).put(3, 105);
        listOfMaps.get(0).put(6, 106);
        return listOfMaps;
    }

    List<Map<Integer, Integer>> prepareOneUserAsList() {
        return prepareUsersAsList(1);
    }

    Map<Integer, Integer> prepareOneUserAsMap() {
        return prepareOneUserAsList().get(0);
    }

    @Test
    public void initializeUsersMapsSizeTest() {

        List<Map<Integer, Integer>> initializedResult = SortSimilarities.initListOfTreeMapWithSize(10);
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
    public void addElementTest() {

        int MAX_NUMBER_NEIGHBORS = 3;
        TaskData taskData = new TaskData(1, 9, "thread");
        Context context = new Context();
        context.putString(Context.SEPARATOR, ",");
        context.putInteger(Context.NEIGHBORHOOD_SIZE, MAX_NUMBER_NEIGHBORS);
        context.putInteger(Context.SIMILARITY_THRESHOLD, 100);

        SortSimilarities sortSimilarities = new SortSimilarities(taskData, context);

        //Map<Integer, Integer> userTopNeighbors = (sortSimilarities.initListOfTreeMapWithSize(1)).get(0);
        Map<Integer, Integer> userTopNeighbors = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer integer, Integer t1) {
                return (t1.compareTo(integer));
            }
        });

        userTopNeighbors.put(124, 6);
        userTopNeighbors.put(125, 5);
        userTopNeighbors.put(123, 7);

        Map<Integer, Integer> result = sortSimilarities.addElement(userTopNeighbors, 4, 300);

        assertEquals(3, result.size());

        for (Map.Entry<Integer, Integer> entry: result.entrySet()) {
            System.out.printf("similarity: %d, userId: %d\n", entry.getKey(), entry.getValue());
        }

    }
}
