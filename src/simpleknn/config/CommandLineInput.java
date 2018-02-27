package simpleknn.config;

import org.apache.commons.cli.*;
import simpleknn.Controller;

import java.util.HashMap;
import java.util.Map;

public class CommandLineInput implements ConfigMethod {

    // Extract config params from command line

    @Override
    public Map<String,String> readConfig(Object... input) {

        String cmdHelpString = "Simple Knn Recommender options:";

        Options options = new Options();

        Option datasetPath = new Option("d","datasetPath",true,ToolConfig.getToolOptionDesc()[0]);
        datasetPath.setRequired(false);
        options.addOption(datasetPath);

        Option user = new Option("u","user",true,ToolConfig.getToolOptionDesc()[1]);
        user.setRequired(false);
        options.addOption(user);

        Option neighborSize = new Option("k","neighborSize",true,ToolConfig.getToolOptionDesc()[2]);
        neighborSize.setRequired(false);
        options.addOption(neighborSize);

        Option numberRecs = new Option("n","numberRecs",true,ToolConfig.getToolOptionDesc()[3]);
        numberRecs.setRequired(false);
        options.addOption(numberRecs);

        Option help = new Option("h","help",false, "Show tool options");
        help.setRequired(false);
        options.addOption(help);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine commandLine = null;

        try {
            commandLine = parser.parse(options, (String[]) input);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
            formatter.printHelp(cmdHelpString, options);

            System.exit(1);
        }

        if (commandLine.hasOption('h')) {
            formatter.printHelp(cmdHelpString, options);
            System.exit(1);
        }

        Map<String, String> res = new HashMap<>();
        res.put(ToolConfig.getToolOptionNames()[0], commandLine.getOptionValue("datasetPath"));
        res.put(ToolConfig.getToolOptionNames()[8], commandLine.getOptionValue("user"));
        res.put(ToolConfig.getToolOptionNames()[6], commandLine.getOptionValue("neighborSize"));
        res.put(ToolConfig.getToolOptionNames()[7], commandLine.getOptionValue("numberRecs"));


//        for (String s: res.keySet())
//            System.err.println("PARAM: " + res.get(s));

        return res;
    }
}
