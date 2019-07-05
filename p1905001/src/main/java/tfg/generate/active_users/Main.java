package tfg.generate.active_users;

import tfg.util.Util;

import java.io.File;
import java.util.*;

public class Main {

    // Read dataset
    public static void main(String[] args) {

        // Process arguments
        CLIArguments cliArguments = new CLIArguments();
        List<String> config = cliArguments.read(args);

        if ("help".equals(config.get(0))) {
            showHelp();

        } else {

               // Initialize random generator
            Random generator = Util.getRandomGenerator(Integer.valueOf(config.get(4)));

            // Open <dataset> for reading
            File datasetFile = new File(config.get(1));
            ReadFileImpl readFile = new ReadFileImpl();

            // Get userIds
            Util.readLines(datasetFile, readFile);
            List<Integer> userIds = readFile.getUserIds();

            // Select <number-users> random positions
            List<Integer> indexes = Util.generateNRandomValues(generator, 0, userIds.size(), Integer.valueOf(config.get(3)));


            // Write file to <output-file>
            WriteFile writeFile = new WriteFile(config.get(2));
            for (Integer index: indexes) {
                writeFile.write(""+userIds.get(index));
            }
            writeFile.closeFile();
        }
    }


    static void showHelp() {
        System.out.println
                ( "* *********************************************************** *\n"
                + "*                                                             *\n"
                + "* This program SELECTS a number of active users to feed a kNN *\n"
                + "* algorithm. Users are chosen using a random method           *\n"
                + "*                                                             *\n"
                + "* *********************************************************** *\n"
                + "                                                               \n"
                + "   Usage / Valid command line options:                         \n"
                + "   -----------------------------------                         \n"
                + "   java -jar JAR_NAME <arguments> as:                          \n"
                + "             --dataset <path>                                  \n"
                + "             --output-file <path>                              \n"
                + "             --n-active-users <value>                         \n"
                + "             --seed <value>                                     \n"
                + "* *********************************************************** *\n");
    }
}
