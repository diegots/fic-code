package tfg.evaluation;

import tfg.util.ReadFileInterface;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadFileInterfaceImpl implements ReadFileInterface {

    private final Map<Integer, List<Integer>> recommendations;


    public ReadFileInterfaceImpl() {
        recommendations = new HashMap<>();
    }


    public Map<Integer, List<Integer>> getRecommendations() {
        return Collections.unmodifiableMap(recommendations);
    }


    @Override
    public void doSomething(String line) {


    }
}
