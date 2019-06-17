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

        System.err.println("Choose one from the following available modes when calling this program:");
        System.err.println("    -help");
        System.err.println("    -similarities <input-file> <number-threads>");
        System.err.println("    -similarities-sort <input-file> <number-threads> <neighborhood-size> <users-per-step>");
    }
}
