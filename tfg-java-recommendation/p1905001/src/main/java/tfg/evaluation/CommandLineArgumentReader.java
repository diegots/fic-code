package tfg.evaluation;

public class CommandLineArgumentReader {

    static Conf readInput(String[] args) {

        Conf currentConf = new Conf();
        for (int argIndex=0; argIndex<args.length; argIndex++) {

            if (Argument.DATA_PATH.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putTextParam(Argument.DATA_PATH, args[argIndex]);

            } else if (Argument.HELP.equalsForm(args[argIndex])) {
                currentConf.putAction(new Action.ActionHelp());

            } else if (Argument.PRECISION.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putAction(new Action.ActionPrecision(Integer.valueOf(args[argIndex])));

            } else if (Argument.RECALL.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putAction(new Action.ActionRecall(Integer.valueOf(args[argIndex])));

            } else if (Argument.SEED.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putIntegerParam(Argument.DATA_PATH, Integer.valueOf(args[argIndex]));
            }
        }

        return currentConf;
    }
}
