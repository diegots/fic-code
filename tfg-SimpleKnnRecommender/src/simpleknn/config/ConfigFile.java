package simpleknn.config;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import simpleknn.Controller;
import simpleknn.exceptions.ArgumentsException;

import javax.naming.ldap.Control;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigFile implements ConfigMethod {

    // Parse en extract config params from 'simple-recommender.conf'
    private static Map<String, String> resMap;

    public ConfigFile() {
        resMap = new HashMap<>();
    }

    @Override
    public Map<String,String> readConfig(Object... input) {

        CharStream charStream = null;

        if (! new File(((String[]) input)[0]).isFile()) {
            System.err.println(Controller.TAG + "[Skipping] " + ((String[]) input)[0] + " was not found.");
            return resMap;
        }

        try {
            charStream = CharStreams.fromFileName(((String[]) input)[0]); // Path to input file
        } catch (IOException e) {
            e.printStackTrace();
        }

        ConfigFileLexer configFileLexer = new ConfigFileLexer(charStream);
        CommonTokenStream commonTokenStream = new CommonTokenStream(configFileLexer);
        ConfigFileParser configFileParser = new ConfigFileParser(commonTokenStream);
        ParseTree parseTree = configFileParser.start();

        ConfigFileBaseListener extractor = new ConfigFileBaseListener();
        ParseTreeWalker.DEFAULT.walk(extractor, parseTree);

        return resMap;
    }

    public static Map<String, String> getResMap() {
        return resMap;
    }
}
