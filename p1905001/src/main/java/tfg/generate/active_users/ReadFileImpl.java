package tfg.generate.active_users;

import tfg.util.ReadFileInterface;

import java.util.*;

public class ReadFileImpl implements ReadFileInterface {

    private Set<Integer> userIds;


    public ReadFileImpl() {
        userIds = new LinkedHashSet<>();
    }


    public List<Integer> getUserIds() {
        return new ArrayList<>(userIds);
    }


    @Override
    public void doSomething(String line) {
        userIds.add(Integer.valueOf(line.split(",")[0]));
    }
}
