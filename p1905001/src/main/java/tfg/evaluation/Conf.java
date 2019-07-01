package tfg.evaluation;

import java.util.*;

public class Conf {

    private final Map<Argument, Integer> integerValues;
    private final Map<Argument, String> textValues;
    private final List<Action> actionList;


    Conf() {
        integerValues = new HashMap<>();
        textValues = new HashMap<>();
        actionList = new ArrayList<>();
    }


    Integer getInteger(Argument key) {

        Integer result = integerValues.getOrDefault(key, null);

        if (result == null) {
            throw new ConfNotFoundException();
        }

        return result;
    }


    String getText(Argument key) {

        String result = textValues.getOrDefault(key, null);

        if (result == null) {
            throw new ConfNotFoundException();
        }

        return result;
    }


    List<Action> getActions() {
        return Collections.unmodifiableList(actionList);
    }


    void putAction(Action action) {
        actionList.add(action);
    }


    void putTextParam(Argument key, String text) {
        textValues.put(key, text);
    }


    void putIntegerParam(Argument key, Integer integer) {
        integerValues.put(key, integer);
    }
}
