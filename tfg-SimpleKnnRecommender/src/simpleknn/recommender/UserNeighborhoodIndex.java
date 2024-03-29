package simpleknn.recommender;


import simpleknn.storage.Storage;
import simpleknn.util.Util;

import java.util.*;

public class UserNeighborhoodIndex {

    // usersSimilarities is a map that contains an entry for every user and holds it's similarities
    private HashMap<Integer, UserSimilarity> usersSimilarities; // HashMap <UserId, UserSimilarity object>

    private UserProfileIndex userProfileIndex;
    private SimilarityAlg similarity;
    private Storage storage;

    public UserNeighborhoodIndex(UserProfileIndex userProfileIndex, SimilarityAlg similarity, Storage storage) {
        // Save references
        this.userProfileIndex = userProfileIndex;
        this.similarity = similarity;
        this.storage = storage;

        usersSimilarities = new HashMap<>();
    }

    public HashMap<Integer, Double> computeAllUserSimilarities (int userA) {

        // If similarities for userA where already computed, just return them
        if (usersSimilarities.containsValue(userA))
            return usersSimilarities.get(userA).getSimilarities();

        // Compute all user similarities
        HashMap<Integer, Double> similarities = new HashMap<>();

        // Retrieve all users to compute similarity with but remove current one
        List<Integer> users = userProfileIndex.getUsers();
        int idx = users.indexOf(userA);
        users.remove(idx);

        // Compute similarity between current user and all others
        for (Integer userU : users)
            similarities.put(userU, similarity.computeSimilarity(userA, userU));

        // Save computed values
        UserSimilarity userSimilarity = new UserSimilarity(userA);
        userSimilarity.setSimilarities(similarities);
        usersSimilarities.put(userA, userSimilarity);

        return usersSimilarities.get(userA).getSimilarities();
    }


    public List<Integer> getUserNeighborhood (int user, int k) {

        // In order to obtain user neighborhood, similarity between current user and all other user is needed.
        Map<Integer, Double> userSimilarities;

        if (usersSimilarities.containsKey(user)) // Already computed, just obtain them
            userSimilarities = usersSimilarities.get(user).getSimilarities();

        else { // Similarities for user still not computed, so compute them
            userSimilarities = computeAllUserSimilarities(user);
            UserSimilarity u = new UserSimilarity(user);
            u.setSimilarities(userSimilarities);
            usersSimilarities.put(user, u); // Save user similarities
        }

        // Recover user's neighborhood from db
        List<Integer> keys = storage.getNeighborhoodForUser(user);

        if (keys.size() <= 0) {
            // If db is empty, compute and store it
            userSimilarities = Util.sortMapByValue(userSimilarities); // Sort neighborhood
            keys = new ArrayList(userSimilarities.keySet());
            storage.storeNeighborhoodForUser(user, keys);
        }

//        System.err.println("Neighbor size: " + keys.size());

        List<Integer> userNeighborhood = new ArrayList<>();
        for (int i=keys.size()-1; i>keys.size()-1-k; i--) {
            System.err.println("Neighbor: " + keys.get(i) + ", similarity: " + userSimilarities.get(keys.get(i)));
            userNeighborhood.add(keys.get(i));
        }

        return userNeighborhood;
    }

}