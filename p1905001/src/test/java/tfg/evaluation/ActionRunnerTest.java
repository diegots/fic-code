package tfg.evaluation;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class ActionRunnerTest {

    @Test
    public void getValidationDoneTest() {

        boolean result = new ActionRunner().isEmptyActionArgument(new ArrayList<>());
        assertTrue(result);

        result = new ActionRunner().isEmptyActionArgument(
                new ArrayList<Action>(){{add(new Action.ActionHelp());}});
        assertFalse(result);
    }

    @Test
    public void isHelpActionArgumentPresentTest() {
        boolean result = new ActionRunner().isHelpActionArgumentPresent(
                new ArrayList<Action>(){{add(new Action.ActionRecall(1));}});
        assertFalse(result);

        result = new ActionRunner().isHelpActionArgumentPresent(
                new ArrayList<Action>(){{add(new Action.ActionHelp());}});
        assertTrue(result);
    }
}
