package tfg;

import org.junit.Test;

import static org.junit.Assert.*;

public class CliParseTest {

    @Test
    public void getOperationModeFromArgsTest() {

        CliParse cliParse = new CliParse();

        assertEquals(OperationMode.HELP, cliParse.getOperationModeFromArgs(new String[] {"-help"}));
        assertEquals(OperationMode.NO_ARGUMENTS, cliParse.getOperationModeFromArgs(new String[] {}));
        assertEquals(OperationMode.SIMILARITIES, cliParse.getOperationModeFromArgs(new String[] {"-similarities"}));
        assertEquals(OperationMode.SIMILARITIES_SORT, cliParse.getOperationModeFromArgs(new String[] {"-similarities-sort"}));
        assertEquals(OperationMode.WRONG_ARGUMENTS, cliParse.getOperationModeFromArgs(new String[] {"-foo"}));
    }


    @Test
    public void numberCliArgsIsValidForModeTest() {

        CliParse cliParse = new CliParse();

        String[] cliArgsOnlySimilarity = {"-similarities", "path/to/dataset", "5"};
        assertTrue(cliParse.numberCliArgsIsValidForMode(cliArgsOnlySimilarity, OperationMode.SIMILARITIES));

        String[] cliArgsWithSort = {"-similarities-sort", "path/to/dataset", "5", "60", "31"};
        assertTrue(cliParse.numberCliArgsIsValidForMode(cliArgsWithSort, OperationMode.SIMILARITIES_SORT));

        String[] cliArgsWithSortBadLength = {"-similarities-sort", "path/to/dataset", "5", "60", "31", "foo", "bar"};
        assertFalse(cliParse.numberCliArgsIsValidForMode(cliArgsWithSortBadLength, OperationMode.SIMILARITIES_SORT));

        String[] cliArgsModeHelp = {"-help"};
        assertTrue(cliParse.numberCliArgsIsValidForMode(cliArgsModeHelp, OperationMode.HELP));

        String[] cliArgNoLength = {};
        assertTrue(cliParse.numberCliArgsIsValidForMode(cliArgNoLength, OperationMode.NO_ARGUMENTS));

        String[] cliArgWrong = {"foo", "bar", "baz"};
        assertFalse(cliParse.numberCliArgsIsValidForMode(cliArgWrong, OperationMode.WRONG_ARGUMENTS));
    }
}
