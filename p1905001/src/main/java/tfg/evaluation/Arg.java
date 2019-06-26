package tfg.evaluation;

public enum Arg {

    PRECISION, RECALL, HELP, SEED;


    private int value;


    static void showHelp() {
        System.out.println("This program.....");
        System.out.println("This program.....");
        System.out.println(Arg.HELP.shortForm() + " or " + Arg.HELP.longForm());
        System.out.println(Arg.PRECISION.shortForm()  + " <value> or " + Arg.PRECISION.longForm() + " <value>");
        System.out.println(Arg.RECALL.shortForm()  + " <value> or " + Arg.RECALL.longForm() + " <value>");
        System.out.println(Arg.SEED.shortForm()  + " <value> or " + Arg.SEED.longForm() + " <value>");
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
