package tfg;

public class TaskData {

    final int min;
    final int max;
    final String threadName;


    public TaskData(int min, int max, String threadName) {
        this.min = min;
        this.max = max;
        this.threadName = threadName;
    }


    public int getMin() {
        return min;
    }


    public int getMax() {
        return max;
    }


    public String getThreadName() {
        return threadName;
    }
}
