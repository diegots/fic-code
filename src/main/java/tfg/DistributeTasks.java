package tfg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class was thought as a way to evenly distribute tasks to threads but it
 * just receives the number of available tasks and threads and returns the
 * distribution as a List of Strings. There are four possible situations:
 *
 *  1. #threads == 0, trivial case, do not distribute
 *
 *  2. #tasks <= #threads
 *
 *  3. #tasks > #threads AND:
 *
 *      a) #tasks % #threads == 0,
 *
 *      b) #tasks % #threads != 0, remainder is not zero so there are some
 *         extra tasks to be distributed, this is same situation as 2.
 */
public class DistributeTasks {

    private final int nTasks;
    private final int nThreads;
    private List<String> distribution;

    public DistributeTasks(int nTasks, int nThreads) {
        this.nThreads = nThreads;
        this.nTasks = nTasks;
    }

    private List<String> distribute() {

        List<String> result;

        // First case
        if (nThreads == 0) {
            return new ArrayList<>();
        }

        result = new ArrayList<>();
        if (nTasks <= nThreads) {
            // Second case


        } else if (nTasks % nThreads == 0) {
            // Case 3.a
            int times = new Double(nTasks / nThreads).intValue();
            for (int thread=0; thread<nThreads; thread++) {

                int min = thread*times + 1;
                int max = (thread+1) * times;
                result.add(min + ":" + max);
            }

        } else {
            // Case 3.b

        }

        return result;

    }

    private List<String> distributeRemainder() {
        return null;
    }

    List<String> getDistribution() {

        if (distribution == null) {
            distribution = distribute();
        }

        return Collections.unmodifiableList(distribution);

    }



}
