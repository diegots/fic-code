package tfg;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class TaskHandler {

    final Context context;

    public TaskHandler(Context context) {
        this.context = context;
    }

    void handle(List<Task> tasks) {

        launchTasks(tasks);
        waitForTasksToFinish(tasks);
    }


    public List<Task> prepareTasks(List<String> distribution, String taskClassName) {

        List<Task> tasks = new ArrayList<>();

        for (int threadId=0; threadId<distribution.size(); threadId++) {

            String distributionItem = distribution.get(threadId);
            int min = Integer.valueOf(distributionItem.split(":")[0]);
            int max = Integer.valueOf(distributionItem.split(":")[1]);

            Class<?> aClass;
            Constructor<?> constructor;
            Task task = null;

            try {
                aClass = Class.forName(taskClassName);
                constructor = aClass.getConstructor(TaskData.class, Context.class);
                task = (Task) constructor.newInstance(new Object[] {new TaskData(min, max, threadId+""), context});

            } catch (ClassNotFoundException
                    | NoSuchMethodException
                    | InstantiationException
                    | IllegalAccessException
                    | InvocationTargetException e)
            {
                e.printStackTrace();
            }

            tasks.add(task);
        }

        return tasks;
    }


    private void launchTasks(List<Task> tasks) {
        System.out.println("launchTasks");
        for (Task task: tasks) {
            task.start();
        }
    }


    private void waitForTasksToFinish(List<Task> tasks) {
        System.out.println("waitForTasksToFinish");
        for (Task task: tasks) {
            try {
                task.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
