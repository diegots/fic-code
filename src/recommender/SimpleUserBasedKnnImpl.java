package recommender;

import main.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleUserBasedKnnImpl implements SimpleUserBasedKnn {

    private String datasetPath;
    private final UserProfileIndex userProfileIndex;
    private final UserNeighborhoodIndex userNeighborhoodIndex;
    private final SimilarityAlg similarityAlg;


    public SimpleUserBasedKnnImpl(String datasetPath) {
        this.datasetPath = datasetPath;

        userProfileIndex = new UserProfileIndex();
        userProfileIndex.buildIndex(datasetPath);

        similarityAlg = new CosineVectorSimilarityAlg(userProfileIndex);
        userNeighborhoodIndex = new UserNeighborhoodIndex(userProfileIndex, similarityAlg);
    }


    public String getDatasetPath() {
        return datasetPath;
    }


    @Override
    public List<Integer> getItems() {
        return userProfileIndex.getItems();
    }


    @Override
    public List<Integer> getUsers() {
        return userProfileIndex.getUsers();
    }


    @Override
    public List<Integer> getRatedItemsBy(int user) {
        return userProfileIndex.getRatedItemsBy(user);
    }


    @Override
    public int getRatingForItem(int user, int item) {
        return userProfileIndex.getRatingForItem(user, item);
    }


    @Override
    public Double getSimilarity(int userA, int userU) {
        return similarityAlg.computeSimilarity(userA, userU);
    }


    @Override
    public List<Integer> getNeighbors(int user, int k) {
        return userNeighborhoodIndex.getUserNeighborhood(user, k);
    }

    @Override
    public List<Integer> recommendedItems(int user, int n, int k) {

        Map<Integer, Double> weights = new HashMap<>();
        List<Integer> neighbors = getNeighbors(user, k);
        Double numerator, denominator, weight;

        for (Integer item: getItems()) {
            numerator = denominator = 0.0;
            for (Integer neighbor : neighbors) {
                numerator += getSimilarity(user, neighbor) * getRatingForItem(neighbor, item);
                denominator += getSimilarity(user, neighbor);
            }
            weight = numerator / denominator;
            weights.put(item, weight);

        }

        weights = Util.sortMapByValue(weights);
        List<Integer> recommendations = new ArrayList<>();
        List<Integer> idx = new ArrayList(weights.keySet());

        for (int i=idx.size()-1; i>idx.size()-1-n; i--) {
            System.err.println("i: " + i + ", idx.get(i): " + idx.get(i) + ", Weight: " + weights.get(idx.get(i)));
            recommendations.add(idx.get(i));
        }

        return recommendations;
    }
}
