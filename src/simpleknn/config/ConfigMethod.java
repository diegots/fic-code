package simpleknn.config;

import simpleknn.exceptions.ArgumentsException;

import java.io.FileNotFoundException;
import java.util.Map;

public interface ConfigMethod {
    Map<String,String> readConfig(Object... input);

}
