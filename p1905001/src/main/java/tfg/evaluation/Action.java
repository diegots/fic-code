package tfg.evaluation;

abstract class Action {

    final private Conf conf;


    private Action() {
        conf = new Conf();
    }


    private Action(Conf conf) {
        this.conf = conf;
    }


    public Conf getConf() {
        return conf;
    }

    abstract void doIt();


    static class ActionEmpty extends Action {

        String message;


        public ActionEmpty(String message) {
            this.message = message;
        }


        @Override
        void doIt() {
            System.out.println("doIt from ActionEmpty");
        }
    }


    static class ActionHelp extends Action {

        ActionHelp() {}


        @Override
        void doIt() {
            System.out.println("doIt from ActionHelp");
            Argument.showHelp();
        }
    }


    private static abstract class ActionAlgorithm extends Action {

        public ActionAlgorithm(Conf conf) {
            super(conf);
        }


        Integer getSeed() {
            return getConf().getInteger(Conf.ConfParams.SEED);
        }
    }


    static class ActionPrecision extends ActionAlgorithm {

        ActionPrecision(Conf conf) {
            super(conf);
        }


        Integer getPrecisionAt() {
            return getConf().getInteger(Conf.ConfParams.PRECISION_AT);
        }


        @Override
        void doIt() {
            System.out.println("doIt from ActionPrecision");
        }
    }


    static class ActionRecall extends Action {

        ActionRecall(Conf conf) {
            super(conf);
        }


        Integer getRecallAt() {
            return getConf().getInteger(Conf.ConfParams.RECALL_AT);
        }


        @Override
        void doIt() {
            System.out.println("doIt from ActionRecall");
        }
    }
}
