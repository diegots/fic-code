package tfg.generate.active_users;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.is;


public class CLIArgumentsTest {

    @Test
    public void readTest() {

        CLIArguments cliArguments = new CLIArguments();

        List<String> result = cliArguments.read(new String[]{"2", "3"});
        assertThat(result.size(), is(5));
        assertThat(result.get(0), is("help"));

        String datasetPath = "file/to/data/set";
        String outputPath = "file/to/data/set";
        String nActiveUsers = "100";
        String seed = "123";

        result = cliArguments.read(new String[]{"--dataset", datasetPath});
        assertThat(result.get(0), is("help"));
        assertThat(result.get(1), is(datasetPath));

        result = cliArguments.read(new String[]{"--output-file", outputPath});
        assertThat(result.get(0), is("help"));
        assertThat(result.get(2), is(outputPath));

        result = cliArguments.read(new String[]{"--n-active-users", nActiveUsers});
        assertThat(result.get(0), is("help"));
        assertThat(result.get(3), is(nActiveUsers));

        result = cliArguments.read(new String[]{"--seed", seed});
        assertThat(result.get(0), is("help"));
        assertThat(result.get(4), is(seed));

        result = cliArguments.read(new String[]{
                "--dataset", datasetPath,
                "--output-file", outputPath,
                "--n-active-users", nActiveUsers,
                "--seed", seed});
        assertThat(result.get(0), is(""));
        assertThat(result.get(1), is(datasetPath));
        assertThat(result.get(2), is(outputPath));
        assertThat(result.get(3), is(nActiveUsers));
        assertThat(result.get(4), is(seed));


        result = cliArguments.read(new String[]{
                "--dataset", datasetPath,
                "--output-file", outputPath,
                "--n-active-users", nActiveUsers,
                "--seed", seed,
                "--help"});
        assertThat(result.get(0), is("help"));
    }
}
