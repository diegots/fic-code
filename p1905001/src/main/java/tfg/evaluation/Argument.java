package tfg.evaluation;

public enum Argument {

    PRECISION, RECALL, HELP, SEED;


    private int value;


    static void showHelp() {
        System.out.println("This program.....");
        System.out.println(Argument.HELP.shortForm() + " or " + Argument.HELP.longForm());
        System.out.println(Argument.PRECISION.shortForm()  + " <value> or " + Argument.PRECISION.longForm() + " <value>");
        System.out.println(Argument.RECALL.shortForm()  + " <value> or " + Argument.RECALL.longForm() + " <value>");
        System.out.println(Argument.SEED.shortForm()  + " <value> or " + Argument.SEED.longForm() + " <value>");
    }


    public void setValue(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }


    public boolean equalsForm(String value) {
        return this.shortForm().equals(value) || this.longForm().equals(value);
    }


    private String shortForm() {

        switch (this) {
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
