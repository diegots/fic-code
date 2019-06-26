package tfg.util;


import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class UtilTest {

    @Test
    public void removeNRandomIndexesNonRepeatedValuesTestSize() {

        final List<Integer> actual = new ArrayList<>();
        for (Integer value: new int[]{5, 72, 18, 34, 23, 67, 45, 2, 7, 8}) {
            actual.add(value);
        }

        List<Integer> expected = new ArrayList<>();
        for (Integer value: new int[]{72, 18, 34, 23, 67, 45, 2}) {
            expected.add(value);
        }

        int seed = 0;
        int nItems = 3;

        Random randomGenerator = new Random(seed);
        Util.removeNRandomIndexes(randomGenerator, nItems, actual);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void removeNRandomIndexesRepeatedValuesTest() {

        final List<Integer> actual = new ArrayList<>();
        for (Integer value: new int[]{5, 72, 18, 34, 23, 67, 45, 2, 7, 8}) {
            actual.add(value);
        }

        List<Integer> expected = new ArrayList<>();
        for (Integer value: new int[]{18, 34, 67, 45, 2, 7, 8}) {
            expected.add(value);
        }

        int seed = 3;
        int nItems = 3;

        Random randomGenerator = new Random(seed);
        Util.removeNRandomIndexes(randomGenerator, nItems, actual);

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }
}
