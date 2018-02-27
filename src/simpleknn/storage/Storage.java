package simpleknn.storage;

import java.util.List;

public interface Storage {

    void storeUsers(List<Integer> usersList);
    void storeSimilarity(int userA, int userU, double similarity);
    void createTables();
    Double getSimilarity(int userA, int userU);

}
