package simpleknn.storage;

import java.util.List;

public class StorageSQL implements Storage {

    private String dbConnectionString;
    private String dbEndpoint;


    @Override
    public void storeUsers(List<Integer> usersList) {

    }

    @Override
    public void storeSimilarity(int userA, int userU, double similarity) {

    }

    @Override
    public Double getSimilarity(int userA, int userU) {
        return null;
    }

    @Override
    public void createTables() {

    }
}
