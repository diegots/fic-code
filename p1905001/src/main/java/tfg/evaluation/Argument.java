package tfg.evaluation;

public enum Argument {

    DATA_PATH, HELP, PRECISION, RECALL, SEED;


    static void showHelp() {

        System.out.println
                ( "* ******************************************************************** *\n"
                + "*                                                                      *\n"
                + "* This program EVALUATES a kNN algorithm with Precision/Recall metrics *\n"
                + "*                                                                      *\n"
                + "* ******************************************************************** *\n"
                + "                                                                        \n"
                + "   Usage / Valid command line options:                                  \n"
                + "   -----------------------------------                                  \n"
                + "    Recommendation data:     -d <path>  or --datapath <path>            \n"
                + "    Show this help:          -h         or --help: shows this help      \n"
                + "    Use Precision metric:    -p <value> or --precision <value>          \n"
                + "    Use Recall metric:       -r <value> or --recall <value>             \n"
                + "    Seed value:              -s <value> or --seed <value>               \n"
                + "* ******************************************************************** *\n");
    }


    public boolean equalsForm(String value) {
        return this.shortForm().equals(value) || this.longForm().equals(value);
    }


    private String shortForm() {

        switch (this) {
            case DATA_PATH:
                return "-d";
            case HELP:
                return "-h";
            case PRECISION:
                return "-p";
            case RECALL:
                return "-r";
            case SEED:
                return "-s";
            default:
                return "";
        }
    }


    private String longForm() {

        switch (this) {
            case DATA_PATH:
                return "--datapath";
            case HELP:
                return "--help";
            case PRECISION:
                return "--precision";
            case RECALL:
                return "--recall";
            case SEED:
                return "--seed";
            default:
                return "";
        }
    }
}
