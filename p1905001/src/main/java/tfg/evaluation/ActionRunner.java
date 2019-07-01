package tfg.evaluation;

import java.util.List;

public class ActionRunner {

    boolean isEmptyActionArgument(List<Action> actions) {
        return actions.isEmpty();
    }


    boolean isHelpActionArgumentPresent(List<Action> actions) {

        for (Action action: actions) {
            if (new Action.ActionHelp().equals(action)) {
                return true;
            }
        }

        return false;
    }


    private void getValidationDone(List<Action> actions) {
        if (isEmptyActionArgument(actions)) {
            actions.add(new Action.ActionEmpty("Ups! no actions requested. Try with -h."));
        } else if (isHelpActionArgumentPresent(actions)) {
            actions.clear();
            actions.add(new Action.ActionHelp());
        }
    }


    void validateAndRunActions(List<Action> actions) {
        getValidationDone(actions);

        for (Action action: actions) {
            action.doIt();
        }
    }
}
