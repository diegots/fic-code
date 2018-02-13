package com.company.recommender;

import java.util.HashMap;

public class UserSimilarity {

    private int userId;

    // Similarities between userId and every other user
    private HashMap<Integer, Double> similarities; // HashMap <NeighborId, Similarity>


    public UserSimilarity(Integer userId) {
        similarities = new HashMap<>();
    }


    public int getUserId() {
        return userId;
    }


    public HashMap<Integer, Double> getSimilarities() {
        HashMap<Integer, Double> res = new HashMap<>();
        res.putAll(similarities);
        return res;
    }


    public void setSimilarities(HashMap<Integer, Double> similarities) {
        this.similarities = similarities;
    }


}
