package tfg.evaluation;

import java.util.*;

public class Conf {

    enum ConfParams {SEED, RECOMMENDATION_PATH, PRECISION_AT, RECALL_AT}


    enum ConfAction {PRECISION, RECALL, HELP}


    private final Map<ConfParams, Integer> integerValues;
    private final Map<ConfParams, String> textValues;
    private final List<ConfAction> actionList;


    Conf() {
        integerValues = new HashMap<>();
        textValues = new HashMap<>();
        actionList = new ArrayList<>();
    }


    Integer getInteger(ConfParams key) {

        Integer result = integerValues.getOrDefault(key, null);

        if (result == null) {
            throw new ConfNotFoundException();
        }

        return result;
    }


    String getText(ConfParams key) {

        String result = textValues.getOrDefault(key, null);

        if (result == null) {
            throw new ConfNotFoundException();
        }

        return result;
    }


    List<ConfAction> getActions() {
        return Collections.unmodifiableList(actionList);
    }


    void putTextParam(ConfParams confParams, String text) {
        textValues.put(confParams, text);
    }


    void putAction(ConfAction confAction) {
        actionList.add(confAction);
    }


    void putIntegerParam(ConfParams confParams, Integer integer) {
        integerValues.put(confParams, integer);
    }
}
