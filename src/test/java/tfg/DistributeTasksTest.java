package tfg;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class DistributeTasksTest {


    @Test
    public void distributeCaseWithOutThreadsTest() {
        DistributeTasks dt = new DistributeTasks(1500, 0);
        List<String> result = dt.getDistribution();
        assertEquals(0, result.size());
    }


    @Test
    public void distributeCaseWithOutTasksTest() {
        DistributeTasks dt = new DistributeTasks(0, 123);
        List<String> result = dt.getDistribution();
        assertEquals(0, result.size());
    }


    @Test
    public void distributeLessTasksThanThreadsTest() {

        int nTasks = 36;
        DistributeTasks dt = new DistributeTasks(nTasks, 150);
        List<String> result = dt.getDistribution();
        assertEquals(nTasks, result.size());

        for (int i=0; i<result.size(); i++) {
            assertEquals((i+1)+":"+(i+1), result.get(i));
        }
    }


    @Test
    public void distributeSameTasksAndThreadsTest() {

        int testValue = 45;

        DistributeTasks dt = new DistributeTasks(testValue, testValue);
        List<String> result = dt.getDistribution();
        assertEquals(testValue, result.size());

        for (int i=0; i<result.size(); i++) {
            assertEquals((i+1)+":"+(i+1), result.get(i));
        }
    }


    @Test
    public void distributeRemainderZeroTest() {
        int nTasks = 138;
        int nThreads = 3;
        DistributeTasks dt = new DistributeTasks(nTasks, nThreads);
        List<String> result = dt.getDistribution();
        assertEquals(nThreads, result.size());

        assertEquals("1:46", result.get(0));
        assertEquals("47:92", result.get(1));
        assertEquals("93:138", result.get(2));
    }


    @Test
    public void distributeWithRemainder1() {
        int nTasks = 137;
        int nThreads = 4;
        DistributeTasks dt = new DistributeTasks(nTasks, nThreads);
        List<String> result = dt.getDistribution();
        assertEquals(nThreads, result.size());

        assertEquals("1:35", result.get(0));
        assertEquals("36:69", result.get(1));
        assertEquals("70:103", result.get(2));
        assertEquals("104:137", result.get(3));
    }


    @Test
    public void distributeWithRemainder2() {
        int nTasks = 138;
        int nThreads = 4;
        DistributeTasks dt = new DistributeTasks(nTasks, nThreads);
        List<String> result = dt.getDistribution();
        assertEquals(nThreads, result.size());

        assertEquals("1:35", result.get(0));
        assertEquals("36:70", result.get(1));
        assertEquals("71:104", result.get(2));
        assertEquals("105:138", result.get(3));
    }


    @Test
    public void distributeWithRemainder3() {
        int nTasks = 139;
        int nThreads = 4;
        DistributeTasks dt = new DistributeTasks(nTasks, nThreads);
        List<String> result = dt.getDistribution();
        assertEquals(nThreads, result.size());

        assertEquals("1:35", result.get(0));
        assertEquals("36:70", result.get(1));
        assertEquals("71:105", result.get(2));
        assertEquals("106:139", result.get(3));
    }
}
