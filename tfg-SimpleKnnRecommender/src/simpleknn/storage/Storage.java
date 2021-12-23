package simpleknn.storage;

import java.util.List;

public interface Storage {

    void createTables();

    void storeUsers(List<Integer> usersList);

    void storeSimilarity(int userA, int userU, double similarity);
    Double getSimilarity(int userA, int userU);

    void storeNeighborhoodForUser (int userA, List<Integer> neighborhood);
    List<Integer> getNeighborhoodForUser (int userA);


}
