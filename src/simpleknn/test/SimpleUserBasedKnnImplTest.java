package simpleknn.test.recommender;

import org.junit.jupiter.api.Assertions;
import simpleknn.recommender.SimpleUserBasedKnn;
import simpleknn.recommender.SimpleUserBasedKnnImpl;

import java.util.Collections;
import java.util.List;

class SimpleUserBasedKnnImplTest {

    String PATH = "/home/diego/1.workspace/fic-tfg/dataset-movielens-small/u.data";

    @org.junit.jupiter.api.Test
    void getItems() {

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        List<Integer> items = simpleUserBasedKnn.getItems();

        Assertions.assertEquals(1682, items.size());
    }


    @org.junit.jupiter.api.Test
    void compareAllItems() {

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        List<Integer> items = simpleUserBasedKnn.getItems();
        Collections.sort(items);

        for (int i=0; i<items.size(); i++)
            Assertions.assertEquals(i+1, items.get(i).intValue());

    }

    @org.junit.jupiter.api.Test
    void getUsers() {
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        List<Integer> users = simpleUserBasedKnn.getUsers();

        Assertions.assertEquals(943, users.size());
    }


    @org.junit.jupiter.api.Test
    void getRatedItemsBy() {

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        int resultRating = simpleUserBasedKnn.getRatingForItem(721,262);

        Assertions.assertEquals(3, resultRating);
    }


    @org.junit.jupiter.api.Test
    void getRatingForItemNotRated() {

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        int resultRating = simpleUserBasedKnn.getRatingForItem(721,2);
        Assertions.assertEquals(0, resultRating);
    }


    @org.junit.jupiter.api.Test
    void getRatingForItem() {

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        int resultRating = simpleUserBasedKnn.getRatingForItem(339,190);
        Assertions.assertEquals(4, resultRating);
    }


    @org.junit.jupiter.api.Test
    void getSimilaritySameUser() {
        //String PATH = "/home/diego/1.workspace/fic-tfg/u.data";
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        Double resultSimilarity = simpleUserBasedKnn.getSimilarity(5,5);
        Assertions.assertEquals(1.0, resultSimilarity, 0.1);
    }


    @org.junit.jupiter.api.Test
    void getSimilarityNotNull() {

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        Double resultSimilarity = simpleUserBasedKnn.getSimilarity(721,913);
        Assertions.assertNotNull(resultSimilarity);
    }


    @org.junit.jupiter.api.Test
    void neighborsNotEmpty() {

        int k = 5;
        int user = 721;
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        List<Integer> neighbors = simpleUserBasedKnn.getNeighbors(user, k);
        Assertions.assertTrue(neighbors.size() == k);

        k = 20;
        neighbors = simpleUserBasedKnn.getNeighbors(user, k);
        Assertions.assertTrue(neighbors.size() == k);
    }

    @org.junit.jupiter.api.Test
    void neighborsNotZero() {

        int k = 5;
        int user = 721;

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        List<Integer> neighbors = simpleUserBasedKnn.getNeighbors(user, k);
        Assertions.assertTrue(neighbors.size() == k);

        Double neighborSimilarity;
        for (int i=0; i<k; i++) {
            neighborSimilarity = simpleUserBasedKnn.getSimilarity(user, neighbors.get(i));
            Assertions.assertEquals(0, neighborSimilarity,0.6);

        }

    }

    @org.junit.jupiter.api.Test
    void obtainNumberOfRecommendations() {

        int user = 2;
        int n = 5;
        int k = 10;

        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(PATH);
        List<Integer> recommendations = simpleUserBasedKnn.recommendedItems(user,n,k);

//        for (Integer i: recommendations)
//            System.err.println("Recomm item: " + i);

        Assertions.assertEquals(n, recommendations.size());
    }
}