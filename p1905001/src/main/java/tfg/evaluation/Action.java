package tfg.evaluation;

abstract class Action {

    abstract void doIt();


    static class ActionHelp extends Action {

        @Override
        void doIt() {
            System.out.println("doIt from ActionHelp");
            Argument.showHelp();
        }
    }


    static class ActionEmpty extends Action {

        String message;


        public ActionEmpty(String message) {
            this.message = message;
        }


        @Override
        void doIt() {
            System.out.println("doIt from ActionEmpty: " + message);
        }
    }


    private abstract static class ActionAlgorithm extends Action {

        Integer at;


        public ActionAlgorithm(Integer at) {
            this.at = at;
        }


        public Integer getAt() {
            return at;
        }
    }


    static class ActionRecall extends ActionAlgorithm {

        public ActionRecall(Integer at) {
            super(at);
        }


        @Override
        void doIt() {
            System.out.println("doIt from ActionRecall with at: " + getAt());
        }
    }


    static class ActionPrecision extends ActionAlgorithm {

        public ActionPrecision(Integer at) {
            super(at);
        }


        @Override
        void doIt() {
            System.out.println("doIt from ActionPrecision with at: " + getAt());
        }
    }
}
