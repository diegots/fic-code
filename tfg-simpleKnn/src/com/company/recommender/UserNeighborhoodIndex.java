package com.company.recommender;

import com.company.Util;

import java.util.*;

public class UserNeighborhoodIndex {

    // usersSimilarities is a map that contains an entry for every user and holds it's similarities
    private HashMap<Integer, UserSimilarity> usersSimilarities; // HashMap <UserId, UserSimilarity object>

    private UserProfileIndex userProfileIndex;
    private SimilarityAlg similarity;


    public UserNeighborhoodIndex(UserProfileIndex userProfileIndex, SimilarityAlg similarity) {
        // Save references
        this.userProfileIndex = userProfileIndex;
        this.similarity = similarity;

        usersSimilarities = new HashMap<>();
    }


    /**
     *
     * @param userA
     * @return
     */
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

        // Util.sortMapByValue
//        userSimilarities
        userSimilarities = Util.sortMapByValue(userSimilarities);
        List<Integer> keys = new ArrayList(userSimilarities.keySet());
        List<Integer> userNeighborhood = new ArrayList<>();

        for (int i=keys.size()-1; i>keys.size()-1-k; i--) {
            System.err.println("Neighbor: " + keys.get(i) + ", similarity: " + userSimilarities.get(keys.get(i)));
            userNeighborhood.add(keys.get(i));
        }

        return userNeighborhood;
    }

}
