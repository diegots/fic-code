package tfg;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class MaxUserIdTest {

    @Test
    public void treatLineTest() {

        String path = "src/test/java/resources/processByLineTestFile.txt";
        MaxUserId maxUserId1 = new MaxUserId(new File(path), ",");

        Integer result = maxUserId1.getMaxUserId();
        assertEquals(new Integer(100), result);

        result = maxUserId1.getMaxUserId();
        assertEquals(new Integer(100), result);
    }


    @Test
    public void getMaxUserIdTest() {

        String path = "src/test/java/resources/processByLineTestFile.txt";
        MaxUserId maxUserId1 = new MaxUserId(new File(path), ",");

        Integer result = maxUserId1.getMaxUserId();
        assertEquals(new Integer(100), result);
    }
}
