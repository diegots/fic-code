package tfg.evaluation;

public enum Argument {

    DATA_PATH, HELP, PRECISION, RECALL, SEED;


    static void showHelp() {
        System.out.println("This program.....");
        System.out.println(Argument.DATA_PATH.shortForm()  + " <value> or " + Argument.DATA_PATH.longForm() + " <value>");
        System.out.println(Argument.HELP.shortForm() + " or " + Argument.HELP.longForm());
        System.out.println(Argument.PRECISION.shortForm()  + " <value> or " + Argument.PRECISION.longForm() + " <value>");
        System.out.println(Argument.RECALL.shortForm()  + " <value> or " + Argument.RECALL.longForm() + " <value>");
        System.out.println(Argument.SEED.shortForm()  + " <value> or " + Argument.SEED.longForm() + " <value>");
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
