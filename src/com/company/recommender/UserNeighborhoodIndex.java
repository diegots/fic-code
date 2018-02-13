package com.company.recommender;

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
        HashMap<Integer, Double> userSimilarities;

        if (usersSimilarities.containsKey(user)) // Already computed, just obtain them
            userSimilarities = usersSimilarities.get(user).getSimilarities();

        else { // Similarities for user still not computed, so compute them
            userSimilarities = computeAllUserSimilarities(user);
            UserSimilarity u = new UserSimilarity(user);
            u.setSimilarities(userSimilarities);
            usersSimilarities.put(user, u); // Save user similarities
        }

        Set<Integer> allNeighbors = userSimilarities.keySet(); // Get all neighbors for user

        // Get all similarity values and sort them
        List<Double> sortedSimilarities = new ArrayList<>(userSimilarities.values());
        Collections.sort(sortedSimilarities);

        List<Integer> userNeighborhood = new ArrayList<>();
        Double kMax;
        for (int i=0; i<k; i++) {
            kMax = sortedSimilarities.get(sortedSimilarities.size()-1-i);
            for (Integer neighbor : allNeighbors) {
                if (userSimilarities.get(neighbor).equals(kMax))
                    userNeighborhood.add(neighbor);
            }
        }

        return userNeighborhood;
    }

}
