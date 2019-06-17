package tfg;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * This algorithm computes and sorts similarities between users using multiple threads.
 */
public class Main {

    public static void main(String[] args) {


        // Manage CLI arguments and config values for the project
        CliParse cliParse = new CliParse();
        Context context = cliParse.getContext(args);
        context.putString(Context.SEPARATOR, ",");
        context.putInteger(Context.SIMILARITY_THRESHOLD, 150);
        context.putInteger(Context.USERS_PER_STEP, 40); // TODO compute a reasonable value attending to #Threads & #Tasks


        int operationModeOrdinal = context.getInteger(Context.OPERATION_MODE, -1);
        OperationMode operationMode = OperationMode.getOperationModeFromOrdinal(operationModeOrdinal);

        if (!operationMode.modeHasWorkToDo()) {
            CliParse.showHelp();

        } else {
            // Compute the max useId
            MaxUserId maxUserId = new MaxUserId(
                    new File(context.getString(Context.DATASET_PATH, "")),
                    context.getString(Context.SEPARATOR, ""));


            // Compute distribution of userIds over threads
            DistributeTasks distributeTasks =
                    new DistributeTasks(maxUserId.getMaxUserId(), context.getInteger(Context.THREADS_NUMBER, 0));
            List<String> distribution = distributeTasks.getDistribution();


            // Select tasks to run
            List<String> taskNames = new ArrayList<>();
            taskNames.add(ComputeProfile.class.getName());
            System.out.println("Adding task: " + ComputeProfile.class.getName());
            taskNames.add(ComputeSimilarities.class.getName());
            System.out.println("Adding task: " + ComputeSimilarities.class.getName());

            if (operationModeOrdinal == OperationMode.SIMILARITIES_SORT.ordinal()) {
                taskNames.add(SortSimilarities.class.getName());
                System.out.println("Adding task: " + SortSimilarities.class.getName());

            }


            // Launch threads
            TaskHandler taskHandler = new TaskHandler(context);
            for (String taskName: taskNames) {
                List<Task> tasks = taskHandler.prepareTasks(distribution, taskName);
                taskHandler.handle(tasks);
            }
        }

        System.out.println("Done");
    }
}
