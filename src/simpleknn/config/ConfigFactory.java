package simpleknn.config;

public class ConfigFactory {

    public ConfigMethod getConfigMethod (String method) {
        if (method == null)
            return null;
        else if (method.equalsIgnoreCase("CLI"))
            return new CommandLineInput();
        else if (method.equalsIgnoreCase("FILE"))
            return new ConfigFile();
        else
            return null;
    }

}
