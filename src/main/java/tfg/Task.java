package tfg;

class Task extends Thread {

    private final TaskData taskData;
    private final Context context;


    Task(TaskData taskData, Context context) {
        this.taskData = taskData;
        this.context = context;
    }


    public int getMin() {
        return taskData.min;
    }


    public int getMax() {
        return taskData.max;
    }


    public String getThreadName() {
        return taskData.threadName;
    }


    public Context getContext() {
        return context;
    }
}
