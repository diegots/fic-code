package tfg;

class Task extends Thread {

    final int min;
    final int max;
    final String threadName;


    Task(String threadName, Integer min, Integer max) {
        this.min = min;
        this.max = max;
        this.threadName = threadName;
    }
}
