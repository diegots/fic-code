package simpleknn.test.recommender;

import org.junit.jupiter.api.Assertions;
import simpleknn.Controller;
import simpleknn.recommender.SimpleUserBasedKnn;
import simpleknn.recommender.SimpleUserBasedKnnImpl;

import java.util.Collections;
import java.util.List;

class SimpleUserBasedKnnImplTest {


    String path = "/home/diego/1.workspace/fic-tfg/dataset-movielens-small/u.data";
    String [] PATH_ARRAY = {path};

    @org.junit.jupiter.api.Test
    void getItems() {

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        List<Integer> items = simpleUserBasedKnn.getItems();

        Assertions.assertEquals(1682, items.size());
    }


    @org.junit.jupiter.api.Test
    void compareAllItems() {

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        List<Integer> items = simpleUserBasedKnn.getItems();
        Collections.sort(items);

        for (int i=0; i<items.size(); i++)
            Assertions.assertEquals(i+1, items.get(i).intValue());

    }

    @org.junit.jupiter.api.Test
    void getUsers() {
        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        List<Integer> users = simpleUserBasedKnn.getUsers();

        Assertions.assertEquals(943, users.size());
    }


    @org.junit.jupiter.api.Test
    void getRatedItemsBy() {

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        int resultRating = simpleUserBasedKnn.getRatingForItem(721,262);

        Assertions.assertEquals(3, resultRating);
    }


    @org.junit.jupiter.api.Test
    void getRatingForItemNotRated() {

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        int resultRating = simpleUserBasedKnn.getRatingForItem(721,2);
        Assertions.assertEquals(0, resultRating);
    }


    @org.junit.jupiter.api.Test
    void getRatingForItem() {

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        int resultRating = simpleUserBasedKnn.getRatingForItem(339,190);
        Assertions.assertEquals(4, resultRating);
    }


    @org.junit.jupiter.api.Test
    void getSimilaritySameUser() {
        Controller controller = new Controller(PATH_ARRAY);
        //String PATH = "/home/diego/1.workspace/fic-tfg/u.data";
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        Double resultSimilarity = simpleUserBasedKnn.getSimilarity(5,5);
        Assertions.assertEquals(1.0, resultSimilarity, 0.1);
    }


    @org.junit.jupiter.api.Test
    void getSimilarityNotNull() {

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        Double resultSimilarity = simpleUserBasedKnn.getSimilarity(721,913);
        Assertions.assertNotNull(resultSimilarity);
    }


    @org.junit.jupiter.api.Test
    void neighborsNotEmpty() {

        int k = 5;
        int user = 721;
        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
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

        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
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
        Controller controller = new Controller(PATH_ARRAY);
        SimpleUserBasedKnn simpleUserBasedKnn = new SimpleUserBasedKnnImpl(controller, path);
        List<Integer> recommendations = simpleUserBasedKnn.recommendedItems(user,n,k);

//        for (Integer i: recommendations)
//            System.err.println("Recomm item: " + i);

        Assertions.assertEquals(n, recommendations.size());
    }
}