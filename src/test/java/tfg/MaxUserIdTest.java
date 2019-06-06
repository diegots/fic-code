package tfg;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class MaxUserIdTest {

    @Test
    public void treatLineTest() {

        MaxUserId maxUserId = new MaxUserId(new File(""), ",");
        Integer result = maxUserId.linesHandler.getResults();
        assertEquals(new Integer(0), result);
    }

    @Test
    public void treatLineTest_2() {

        String path = "src/test/java/resources/processByLineTestFile.txt";
        MaxUserId maxUserId1 = new MaxUserId(new File(path), ",");

        Utilities.processByLine(maxUserId1.file, maxUserId1.linesHandler);
        Integer result = maxUserId1.linesHandler.getResults();
        assertEquals(new Integer(100), result);

        result = maxUserId1.linesHandler.getResults();
        assertEquals(new Integer(100), result);
    }

}
