package tfg.evaluation;

public class CLIArgument {

    static Conf getInputArgRead(String[] args) {

        Conf currentConf = new Conf();
        for (int argIndex=0; argIndex<args.length; argIndex++) {

            if (Argument.PRECISION.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putIntegerParam(Conf.ConfParams.PRECISION_AT, Integer.valueOf(args[argIndex]));
                currentConf.putAction(Conf.ConfAction.PRECISION);

            } else if (Argument.RECALL.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putAction(Conf.ConfAction.RECALL);
                currentConf.putIntegerParam(Conf.ConfParams.RECALL_AT, Integer.valueOf(args[argIndex]));

            } else if (Argument.SEED.equalsForm(args[argIndex])) {
                argIndex++;
                currentConf.putIntegerParam(Conf.ConfParams.SEED, Integer.valueOf(args[argIndex]));

            } else if (Argument.HELP.equalsForm(args[argIndex])) {
                currentConf.putAction(Conf.ConfAction.HELP);
            }
        }

        return currentConf;
    }
}
