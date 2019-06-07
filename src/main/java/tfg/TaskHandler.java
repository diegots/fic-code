package tfg;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TaskHandler {

    void handle(List<Task> tasks) {

        launchTasks(tasks);
        waitForTasksToFinish(tasks);
    }

    public static List<Task> prepareTasks(List<String> distribution, String taskClassName) {

        List<Task> tasks = new ArrayList<>();

        for (int threadId=0; threadId<distribution.size(); threadId++) {

            String distributionItem = distribution.get(threadId);
            int min = Integer.valueOf(distributionItem.split(":")[0]);
            int max = Integer.valueOf(distributionItem.split(":")[1]);

            Class<?> clazz = null;
            Constructor<?> ctor = null;
            Task t = null;

            try {
                clazz = Class.forName(taskClassName);
                ctor = clazz.getConstructor(String.class, Integer.class, Integer.class);
                t = (Task) ctor.newInstance(new Object[] {Integer.toString(threadId), min, max});

            } catch (ClassNotFoundException
                    | NoSuchMethodException
                    | InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e)
            {
                e.printStackTrace();
            }

            tasks.add(t);
        }

        return tasks;
    }

    private void launchTasks(List<Task> tasks) {
        for (Task task: tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void waitForTasksToFinish(List<Task> tasks) {
        for (Task task: tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
