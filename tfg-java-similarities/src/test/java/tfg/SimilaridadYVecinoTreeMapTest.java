package tfg;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class SimilaridadYVecinoTreeMapTest {
    @Test
    public void sortTest() {
        SimilaridadYVecino s2 = new SimilaridadYVecino(123, 1);
        SimilaridadYVecino s1 = new SimilaridadYVecino(123, 2);
        SimilaridadYVecino s3 = new SimilaridadYVecino(124, 3);
        SimilaridadYVecino s4 = new SimilaridadYVecino(121, 4);
        SimilaridadYVecinoTreeMap tree = new SimilaridadYVecinoTreeMap();
        tree.put(s1);
        tree.put(s2);
        tree.put(s3);
        tree.put(s4);

        int counter = 0;
        for (SimilaridadYVecino s: tree.keySet()) {
            switch (++counter) {
                case 1:
                    assertEquals(124, s.getSimilarity());
                    assertEquals(3, s.getUserId());
                    break;
                case 2:
                    assertEquals(123, s.getSimilarity());
                    assertEquals(2, s.getUserId());
                    break;
                case 3:
                    assertEquals(123, s.getSimilarity());
                    assertEquals(1, s.getUserId());
                    break;
                case 4:
                    assertEquals(121, s.getSimilarity());
                    assertEquals(4, s.getUserId());
                    break;
            }
        }
    }
}
