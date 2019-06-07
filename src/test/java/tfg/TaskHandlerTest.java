package tfg;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class TaskHandlerTest {

    @Test
    public void prepareTasksTest() {

        int nTasks = 1345;
        int nThreads = 15;


        List<String> distribution = new DistributeTasks(nTasks, nThreads).getDistribution();

        List<Task> tasks = TaskHandler.prepareTasks(distribution, ComputeProfile.class.getName());
        assertEquals(distribution.size(), tasks.size());

        Task task;
        for (int threadNmae=0; threadNmae<distribution.size(); threadNmae++) {

            String distributionItem = distribution.get(threadNmae);
            task = tasks.get(threadNmae);
            assertEquals(threadNmae+"", task.threadName);
            assertEquals(distributionItem.split(":")[0], task.min+"");
            assertEquals(distributionItem.split(":")[1], task.max+"");
        }
    }
}
