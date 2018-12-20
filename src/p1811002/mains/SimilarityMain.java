package p1811002.mains;

import p1811002.Dataset;
import p1811002.NeighborhoodSimilarity;

public class SimilarityMain {

  public static void main(String[] args) {

    /**
     *  Input arguments
     *  args[0] ratings file -> userId | movieId | rating | timestamp
     *  args[1] fSimilarities
     *  args[2] fNeighbors
     */
    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(args[0]);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(args[1], args[2], true);
    long timeSpent = neighborhoodSimilarity.compute(dataset);

    System.out.println("Computing similarities took " + timeSpent + " seconds.");
  }
}
