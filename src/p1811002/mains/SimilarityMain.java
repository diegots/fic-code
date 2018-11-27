package p1811002.mains;

import p1811002.ComputeSimilarity;

public class SimilarityMain {

    public static void main(String[] args) {

        /**
         *  Input arguments
         *  args[0] ratings file -> userId | movieId | rating | timestamp
         *  args[1] fSimilarities
         *  args[2] fNeighbors
         */
        new ComputeSimilarity(args[0], args[1], args[2]).start();
    }
}
