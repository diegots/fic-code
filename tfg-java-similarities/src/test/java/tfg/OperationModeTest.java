package tfg;

import org.junit.Test;

import static org.junit.Assert.*;

public class OperationModeTest {

    @Test
    public void modeHasWorkToDoTest() {
        assertFalse(OperationMode.HELP.modeHasWorkToDo());
        assertFalse(OperationMode.NO_ARGUMENTS.modeHasWorkToDo());
        assertTrue(OperationMode.SIMILARITIES.modeHasWorkToDo());
        assertTrue(OperationMode.SIMILARITIES_SORT.modeHasWorkToDo());
        assertFalse(OperationMode.WRONG_ARGUMENTS.modeHasWorkToDo());
    }
}
