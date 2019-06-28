package tfg.evaluation;


import java.util.ArrayList;
import java.util.List;

public class Main {


    public static void main(String[] args) {


        /*
         * TODO process all users in the input (recommendations) file
         *  1. Read file
         *  2. Extract file data
         */
        List<Integer> data = new ArrayList<Integer>(){{
            add(1); add(4); add(7); add(24); add(76); add(35); add(12); add(45);}};

        Conf conf = CLIArgument.getInputArgRead(args);
        ActionBuilder actionBuilder = new ActionBuilder();
        List<Action> actions =  actionBuilder.buildActions(conf);
        Main.getActionDone(actions);
    }


    static void getActionDone(List<Action> actions) {

        for (Action action: actions) {
            action.doIt();
        }
    }




    /*
     * TODO get evaluation profile
     *  --------------------------
     *  - Profile directories need some default path
     *  - These profiles depend on:
     *      - dataset/userId/technique-at-n-value,
     *      - input-100k/userId-56/all-but-5
     *      - input-100k/userId-56/given-5
     *      - input-100k/userId-56/percentage-5
     *  1. Check if evaluation profile exists on disk
     *  2. if do not exists:
     *      a. Using the random generator, compute user profile with the desired technique reading dataset
     *      b. Store evaluation profile on disk
     *     else check next userId
     */


    /*
     * TODO with all input (recommendations) in memory and knowing
     *  that all evaluation profiles are available:
     *  On every userId,
     *      a. read it's evaluation profile
     *      b. compute metric(s)
     *  When done,
     *      Write metrics to stdout
     */
}
