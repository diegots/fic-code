package tfg;

import java.util.HashMap;
import java.util.Map;


/**
 * This class stores key pair values. The intended use is to share config data
 * and params across the project.
 */
public class Context {

    private final Map<String, String> stringValues;
    private final Map<String, Integer> integerValues;


    public Context() {
        this.stringValues = new HashMap<>();
        this.integerValues = new HashMap<>();
    }


    public String getString(String name, String defValue) {
        return stringValues.getOrDefault(name, defValue);
    }


    public Integer getInteger(String name, Integer defValue) {
        return integerValues.getOrDefault(name, defValue);
    }


    public void putString(String name, String value) {
        stringValues.put(name, value);
    }


    public void putInteger(String name, Integer value) {
        integerValues.put(name, value);
    }
}
