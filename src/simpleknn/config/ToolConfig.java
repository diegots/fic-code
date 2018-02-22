package simpleknn.config;


import simpleknn.Controller;
import simpleknn.exceptions.ArgumentsException;

import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class ToolConfig {

    private Controller controller;
    private String [] args;
    private Map<String, String> toolConfig;
    private ConfigFactory configFactory;

    private final static String [] toolOptionNames = new String[] {
            "DATASET_PATH",
            "DB_CONNECTION_STRING",
            "DB_ENDPOINT",
            "DB_PASSWD",
            "DB_TYPE",
            "DB_USER",
            "NEIGHBOR_SIZE",
            "NUMBER_RECS",
            "USER_ID"
    };

    private final static String [] toolOptionDesc = new String[] {
            "Path to dataset file",
            "JDBC connector string",
            "Path or URL to DB endpoint",
            "DB Password",
            "What DB should be used",
            "DB User",
            "User's neighborhood size",
            "Number of recommendations to compute",
            "Default user to whom recommendations are computed"
    };

    public ToolConfig(Controller controller, String[] args) {
        toolConfig = new HashMap<>();
        configFactory = new ConfigFactory();

        this.controller = controller;
        this.args = args;
    }

    public static String[] getToolOptionNames() {
        return toolOptionNames;
    }

    public static String[] getToolOptionDesc() {
        return toolOptionDesc;
    }

    public void processRunningConfig() throws ArgumentsException {
        // Read config file first. Config file has the least priority
        ConfigMethod cm = configFactory.getConfigMethod("FILE");
        String [] input = {Controller.configFileName};
        Map<String, String> mFile = cm.readConfig((Object []) input);
        toolConfig.putAll(mFile);

        // Read command line params
        cm = configFactory.getConfigMethod("CLI");
        Map<String, String> mCLI = cm.readConfig((Object []) args);
        for (String s: toolOptionNames)
            if (mCLI.get(s) != null)
                toolConfig.put(s, mCLI.get(s));

        System.out.println(Controller.TAG + "[Options ]");
        for (String s: toolConfig.keySet())
            System.out.println(Controller.TAG + "\t" + s + ": " + toolConfig.get(s));

        // Save DB config
        controller.setDatabaseData(
                toolConfig.get(toolOptionNames[4]), //dbType
                toolConfig.get(toolOptionNames[1]), //dbConnectionString
                toolConfig.get(toolOptionNames[2]), //dbEndpoint
                toolConfig.get(toolOptionNames[5]), //dbUser
                toolConfig.get(toolOptionNames[3])  //dbPasswd
        );
    }

    public void runConfig() throws ArgumentsException {

        if ( toolConfig.get( toolOptionNames[0]) == null)
            throw new ArgumentsException("Missing " + toolOptionNames[0] + " param. Try with -d <path>"); //datasetPath
        if ( toolConfig.get( toolOptionNames[7]) == null)
            throw new ArgumentsException("Missing " + toolOptionNames[7] + " param. Try with -n <number>"); //numberRecs
        if ( toolConfig.get( toolOptionNames[6]) == null)
            throw new ArgumentsException("Missing " + toolOptionNames[6] + " param. Try with -k <number>"); //neighborSize

        if (! new File(toolConfig.get( toolOptionNames[0])).isFile())
            throw new ArgumentsException(toolConfig.get( toolOptionNames[0]) + " not found!");

        // compute recommendations for desired user
        if (toolConfig.get(toolOptionNames[8]) != null)
            controller.userProcess(
                    toolConfig.get(toolOptionNames[0]), //dataset
                    Integer.parseInt(toolConfig.get(toolOptionNames[8])), //user
                    Integer.parseInt(toolConfig.get(toolOptionNames[7])), //numberRecs
                    Integer.parseInt(toolConfig.get(toolOptionNames[6]))); //neighborSize

        // compute all recommendations in batch
        else
            controller.batchProcess(
                    toolConfig.get(toolOptionNames[0]), //dataset
                    Integer.parseInt(toolConfig.get(toolOptionNames[7])),//numberRecs
                    Integer.parseInt(toolConfig.get(toolOptionNames[6])));//neighborSize
    }
}
