package tfg;

enum OperationMode {

    HELP, NO_ARGUMENTS, SIMILARITIES, SIMILARITIES_SORT, WRONG_ARGUMENTS;


    @Override
    public String toString() {

        switch (this) {
            case HELP:
                return "-help";
            case NO_ARGUMENTS:
                return "no-arguments";
            case SIMILARITIES:
                return "-similarities";
            case SIMILARITIES_SORT:
                return "-similarities-sort";
            case WRONG_ARGUMENTS:
                return "wrong-arguments";
            default:
                return "not-valid";
        }
    }


    public int numberCliArgs() throws WrongArgumentsLengthException {

        switch (this) {
            case SIMILARITIES:
                return 3;
            case SIMILARITIES_SORT:
                return 5;
            case NO_ARGUMENTS:
                return 0;
            case HELP:
                return 1;
            default:
                throw new WrongArgumentsLengthException();
        }
    }


    public boolean modeHasWorkToDo() {

        switch (this) {
            case SIMILARITIES:
            case SIMILARITIES_SORT:
                return true;

            default: return false;
        }
    }


    public static OperationMode getOperationModeFromOrdinal(int operationModeOrdinal) {
        return OperationMode.values()[operationModeOrdinal];
    }
}
