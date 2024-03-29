package tfg;


class CliParse {

    final static int ARGUMENT_MODE_POSITION = 0;
    final static int DATASET_PATH_POSITION = 1;
    final static int THREADS_NUMBER_POSITION = 2;
    final static int NEIGHBORHOOD_SIZE_POSITION = 3;
    final static int USERS_PER_STEP_POSITION = 4;

    private final Context context;


    CliParse() {
        this.context = new Context();
    }


    Context getContext(String[] cliArgs) {

        OperationMode operationMode = getOperationModeFromArgs(cliArgs);
        boolean validArgsLength = numberCliArgsIsValidForMode(cliArgs, operationMode);
        context.putInteger(Context.OPERATION_MODE, operationMode.ordinal());

        if (operationMode.modeHasWorkToDo() && validArgsLength) {
            fillContextWithCliArgs(cliArgs, operationMode);
        }

        return context;
    }


    OperationMode getOperationModeFromArgs(String[] cliArgs) {

        if (cliArgs.length <= 0) {
            return OperationMode.NO_ARGUMENTS;
        }

        for (OperationMode operationMode: OperationMode.values()) {
            if (cliArgs[ARGUMENT_MODE_POSITION].equals(operationMode.toString())) {
                return operationMode;
            }
        }

        return OperationMode.WRONG_ARGUMENTS;
    }


    boolean numberCliArgsIsValidForMode(String[] cliArgs, OperationMode operationMode) {
        try {
            return cliArgs.length == operationMode.numberCliArgs();
        } catch (WrongArgumentsLengthException e) {
            return false;
        }
    }


    void fillContextWithCliArgs(String[] cliArgs, OperationMode operationMode) {

        context.putString(Context.DATASET_PATH, cliArgs[DATASET_PATH_POSITION]);
        context.putInteger(Context.THREADS_NUMBER, Integer.valueOf(cliArgs[THREADS_NUMBER_POSITION]));

        switch (operationMode) {
            case SIMILARITIES_SORT:
                context.putInteger(Context.NEIGHBORHOOD_SIZE, Integer.valueOf(cliArgs[NEIGHBORHOOD_SIZE_POSITION]));
                context.putInteger(Context.USERS_PER_STEP, Integer.valueOf(cliArgs[USERS_PER_STEP_POSITION]));
                break;
        }
    }


    static void showHelp(String... messages) {

        for (String message: messages) {
            System.err.println(message);
        }

        System.err.println
                ( "* *************************************************************************** *\n"
                + "*                                                                             *\n"
                + "* This program *computes* and *sorts* similarities for a kNN algorithm.       *\n"
                + "*                                                                             *\n"
                + "* Similarity between users A and B is a representation of how alike they are. *\n"
                + "* This is a multithreaded implementation for handing big datasets, leveraging *\n"
                + "* on multicore cpus.                                                          *\n"
                + "*                                                                             *\n"
                + "* *************************************************************************** *\n"
                + "                                                                               \n"
                + "  Usage / Valid command line options:                                          \n"
                + "  -----------------------------------                                          \n"
                + "      -help                                                                    \n"
                + "      -similarities <input-file> <number-threads>                              \n"
                + "      -similarities-sort                                                       \n"
                + "          <input-file>                                                         \n"
                + "          <number-threads>                                                     \n"
                + "          <neighborhood-size>                                                  \n"
                + "          <users-per-step>                                                     \n"
                + "* *************************************************************************** *\n");
    }
}
