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

    // Prepare stuff
    final Messages messages = new Messages.Symbol(".");
    //final Messages messages = new Messages.Void();

    Conf conf = Conf.getConf();
    try {
      conf.setRowDelimiter(1002);
    } catch (RowDelimiterException e) {
      messages.printErrln("Bad row delimiter");
      System.exit(1);
    }
    conf.setDataPath(args[0]);
    conf.setSimilaritiesPath(args[1]);
    conf.setNeighborhoodPath(args[2]);
    conf.setOrderedIndexesPath(args[3]);

    // Read dataset
    Dataset dataset = new Dataset.MovieLensDataset(messages);
    dataset.read(args[0]);

    // Compute similarities
    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(args[1], args[2], messages);
    long t0 = neighborhoodSimilarity.compute(dataset);
    messages.printMessageln("Computing similarities took " + Units.milisecondsToSeconds(t0) + " seconds.");

    // Compute ordered indexes
    OrderSimilarities orderSimilarities = new OrderSimilarities(args[3], messages);
    long t1 = orderSimilarities.order();
    messages.printMessageln("Ordering similarities took " + Units.milisecondsToSeconds(t1) + " seconds.");
  }
}
