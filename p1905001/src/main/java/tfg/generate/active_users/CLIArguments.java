package tfg.generate.active_users;

import java.util.ArrayList;
import java.util.List;

public class CLIArguments {

    final static int MAX_NUMBER_ARGUMENTS = 5;


    List<String> read(String[] args) {

        List<String> arguments = initializeArguments();

        for (int i=0; i<args.length; i++) {
            if ("--help".equals(args[i])) {
                arguments.set(0, "help");
            } else if ("--dataset".equals(args[i])) {
                arguments.set(1, args[++i]);
            } else if ("--output-file".equals(args[i])) {
                arguments.set(2, args[++i]);
            } else if ("--n-active-users".equals(args[i])) {
                arguments.set(3, args[++i]);
            } else if ("--seed".equals(args[i])) {
                arguments.set(4, args[++i]);
            }
        }

        int actualNoArguments = countArguments(arguments);
        if (actualNoArguments != MAX_NUMBER_ARGUMENTS-1) {
            arguments.set(0, "help");
        }

        return arguments;
    }


    List<String> initializeArguments() {

        List<String> arguments = new ArrayList<>();
        for (int i=0; i<MAX_NUMBER_ARGUMENTS; i++) {
            arguments.add("");
        }

        return arguments;
    }


    int countArguments(List<String> arguments) {

        int actualArguments = 0;
        for (int i=1; i<MAX_NUMBER_ARGUMENTS; i++) {
            if (!"".equals(arguments.get(i))) {
                actualArguments++;
            }
        }

        return actualArguments;
    }


}
