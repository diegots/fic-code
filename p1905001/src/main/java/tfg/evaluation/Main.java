package tfg.evaluation;


import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static int DEFAULT_SEED_VALUE = 0;


    public static void main(String[] args) {

        // TODO obtain data from file
        List<Integer> data = new ArrayList();
        for (Integer value: new int[] {1, 4, 7, 24, 76, 35, 12, 45}) {
            data.add(value);
        }

        List<Arg> readArgs = new CLIArguments().getInputArgRead(args);
        getConditionsChecked(readArgs);
        getArgsDone(readArgs);
    }


    static void getArgsDone(List<Arg> args) {

        //        int seed = 30;
        //        int numberItemsRemove = 3;
        //        Random randomGenerator = Util.getRandomGenerator(seed);
        //        Util.removeNRandomIndexes(randomGenerator, numberItemsRemove, data);

        int seed = getSeed(args);

        for (Arg arg: args) {
            if (Arg.RECALL.equals(arg)) {
                System.out.println("Recall at: "  + arg.getValue() + " with seed: " + seed);
            }

            if (Arg.PRECISION.equals(arg)) {
                System.out.println("Precision at: " + arg.getValue() + " with seed: " + seed);
            }
        }
    }


    /*
     * TODO process all users in the input (recommendations) file
     *  1. Read file
     *  2. Extract file data
     */


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



    static int getSeed(List<Arg> args) {

        for (Arg arg: args) {
            if (Arg.SEED.equals(arg)) {
                return arg.getValue();
            }
        }

        return DEFAULT_SEED_VALUE;
    }


    static class CLIArguments {

        List<Arg> getInputArgRead(String[] args) {

            List<Arg> readArgs = new ArrayList<>();
            for (int argIndex=0; argIndex<args.length; argIndex++) {

                if (Arg.PRECISION.equalsForm(args[argIndex])) {
                    argIndex++;
                    Arg arg = Arg.PRECISION;
                    arg.setValue(Integer.valueOf(args[argIndex]));
                    readArgs.add(arg);

                } else if (Arg.RECALL.equalsForm(args[argIndex])) {
                    argIndex++;
                    Arg arg = Arg.RECALL;
                    arg.setValue(Integer.valueOf(args[argIndex]));
                    readArgs.add(arg);

                } else if (Arg.SEED.equalsForm(args[argIndex])) {
                    argIndex++;
                    Arg arg = Arg.SEED;
                    arg.setValue(Integer.valueOf(args[argIndex]));
                    readArgs.add(arg);

                } else if (Arg.HELP.equalsForm(args[argIndex])) {
                    readArgs.add(Arg.HELP);
                }
            }

            return readArgs;
        }
    }


    static void getConditionsChecked(List<Arg> args) {

        if (isEmptyArg(args)
                || isHelpContained(args)) {

            Arg.showHelp();
            args.clear();
        }
    }


    static boolean isEmptyArg(List<Arg> args) {

        if (0 == args.size()) {
            return true;
        }

        return false;
    }


    static boolean isHelpContained(List<Arg> args) {

        for (Arg arg: args) {
            if (Arg.HELP.equals(arg)) {
                return true;
            }
        }

        return false;
    }
}
