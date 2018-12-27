package p1811002;

import p1811002.utils.Units;

public class Main {

  public static void main(String[] args) {

    /**
     *  This programm uses the following parameters:
     *  args[0] dataset file -> userId | movieId | rating | timestamp
     *  args[1] similatiries matrix path
     *  args[2] similarities matrix's indexes
     *  args[3] ordered indexes by weight
     */

    final Messages messages = new Messages.Symbol(".");

    // Read dataset
    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(args[0]);

    // Compute similarity
    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(args[1], args[2], messages);
    long t0 = neighborhoodSimilarity.compute(dataset);
    messages.printMessageln("Computing similarities took " + Units.milisecondsToSeconds(t0) + " seconds.");

    // Compute ordered indexes
    OrderSimilarities orderSimilarities = new OrderSimilarities(args[1], args[3], messages);
    long t1 = orderSimilarities.order();
    messages.printMessageln("Ordering similarities took " + Units.milisecondsToSeconds(t1) + " seconds.");
  }
}
