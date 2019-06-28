package tfg.evaluation;

import java.util.ArrayList;
import java.util.List;

public class ActionBuilder {

    boolean isEmptyActionArgument(List<Conf.ConfAction> actions) {
        return actions.isEmpty();
    }


    boolean isHelpActionArgumentPresent(List<Conf.ConfAction> actions) {

        for (Conf.ConfAction action: actions) {
            if (Conf.ConfAction.HELP.equals(action)) {
                return true;
            }
        }

        return false;
    }


    List<Action> buildActions(Conf conf) {

        if (isEmptyActionArgument(conf.getActions())) {
            return new ArrayList<Action>(){{
                add(new Action.ActionEmpty("No action recognized!!"));
            }};

        } else if (isHelpActionArgumentPresent(conf.getActions())) {
            return new ArrayList<Action>(){{
               add(new Action.ActionHelp());
            }};
        }

        List<Action> currentActions = new ArrayList<>();
        for (Conf.ConfAction argAction: conf.getActions()) {

            if (Conf.ConfAction.PRECISION.equals(argAction)) {
                currentActions.add(new Action.ActionPrecision(conf));

            } else if (Conf.ConfAction.RECALL.equals(argAction)) {
                currentActions.add(new Action.ActionRecall(conf));
            }
        }

        return  currentActions;
    }
}
