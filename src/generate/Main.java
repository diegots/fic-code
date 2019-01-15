package generate;

import generate.cached.NeighborhoodSimilarity;
import generate.cached.RatingMatrix;
import generate.model.Dataset;
import generate.model.FrequencyTable;
import generate.engine.ProccessRows;
import generate.engine.RowDelimiterException;
import generate.engine.RowTask;
import generate.stream.StreamOut;
import generate.utils.Messages;
import common.util.Utilities;

import java.util.ArrayList;
import java.util.List;

import static generate.stream.StreamOut.createDeltaStreamOut;

public class Main {

  public static void main(String[] args) {

    // Prepare stuff
    Job job = new Job(Conf.getConf());
    job.parseCommandLine(args);
    job.start();
  }
}

class Job {

  static final int ROW_DELIMITER = 1001;

  static final String RATING_MATRIX_MODE = "-matrix";
  static final String NEIGHBORHOOD_MODE = "-neighborhood";
  static final String HELP_SHORT_MODE = "-h";
  static final String HELP_LONG_MODE = "-help";
  static final String VERBOSE_MODE = "-v";

  private final Conf conf;
  private Messages messages = new Messages.Void();

  public Job(Conf conf) {
    this.conf = conf;
  }

  void parseCommandLine (String[] args) {

    int i = 0;
    while (i < args.length) {
      switch (args[i]) {
        case RATING_MATRIX_MODE:
          conf.setMode(Conf.Mode.MATRIX);
          conf.setDataPath(args[i+1]);
          conf.setRatingMatrixPath(args[i+2]);
          conf.setShardsNumber(Integer.valueOf(args[i+3]));
          i+=4;
          break;

        case NEIGHBORHOOD_MODE:
          conf.setMode(Conf.Mode.NEIGHBORHOOD);
          conf.setDataPath(args[i+1]);
          conf.setK(Integer.valueOf(args[i+2]));
          conf.setSimilaritiesPath(args[i+3]);
          conf.setNeighborhoodPath(args[i+4]);
          conf.setOrderedIndexesPath(args[i+5]);
          conf.setReassignedSimilaritiesPath(args[i+6]);
          i+=7;
          try {
            conf.setRowDelimiter(ROW_DELIMITER);
          } catch (RowDelimiterException e) {
            messages.printErrln("Bad row delimiter!");
            System.exit(1);
          }
          break;

        case HELP_SHORT_MODE:
          help();
          System.exit(0);

        case HELP_LONG_MODE:
          help();
          System.exit(0);

        case VERBOSE_MODE:
          messages = new Messages.Symbol(".");
          i++;
          break;

        default:
          help();
          System.exit(1);
      }
    }
  }

  public void start() {

    if (Conf.Mode.UNDEFINED.equals(conf.getMode())) {
      messages.printErrln("Mode not recognized! Showing usage.");
      help();
      System.exit(1);
    }

    // Read dataset
    Dataset dataset = new Dataset.MovieLensDataset(messages);
    dataset.read(conf.getDataPath());

    if (Conf.Mode.MATRIX.equals(conf.getMode())) {
      ratingMatrixComputing(dataset);

    } else if (Conf.Mode.NEIGHBORHOOD.equals(conf.getMode())) {
      neighborhoodComputing(dataset);
    }
  }

  // TODO Add more horizontal space in options
  // TODO Say that mode options have to be user in the same order
  private void help () {
    System.out.println("This program computes either a Neighborhood Similarity matrix"
        + " or the grouped Rating Matrix according to the requested number of shards.");
    System.out.println("");
    System.out.println("Usage:");
    System.out.println("        java -jar JAR_NAME [-v] -<mode> <dataset path> +mode-specific-params");
    System.out.println("");
    System.out.println("Mode is one of: '-h', '-help', '-matrix' or '-neighborhood',");
    System.out.println("    '" + HELP_LONG_MODE + "' or '" + HELP_SHORT_MODE + "': show this help.");
    System.out.println("    '" + RATING_MATRIX_MODE + "' mode specific params:");
    System.out.println("        <output path>");
    System.out.println("        <number of shards>");
    System.out.println("    '" + NEIGHBORHOOD_MODE + "' mode specific params:");
    System.out.println("        <k value>");
    System.out.println("        <uncompressed similarities matrix path>");
    System.out.println("        <neighborhood Ids order list path>");
    System.out.println("        <reassigned indexes path>");
    System.out.println("        <similarities matrix path>");
  }

  private void ratingMatrixComputing (Dataset dataset) {
    RatingMatrix ratingMatrix = new RatingMatrix(dataset);
    ratingMatrix.distribureUsersToShards();
  }

  private void neighborhoodComputing(Dataset dataset)  {

    // Compute similarities
    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(conf.getSimilaritiesPath(), conf.getNeighborhoodPath(), messages);
    long t0 = neighborhoodSimilarity.compute(dataset);
    messages.printMessageln("Computing similarities took " + Utilities.milisecondsToSeconds(t0) + " seconds.");

    // Get processing engine
    ProccessRows rowsEngine = new ProccessRows(messages);

    // Compute ordered indexes
    long t1 = rowsEngine.process(new RowTask.Order(), createDeltaStreamOut(conf.getOrderedIndexesPath()));
    messages.printMessageln("Ordering similarities took " + Utilities.milisecondsToSeconds(t1) + " seconds.");

    // Frequency compuring
    final List<Integer> aux = new ArrayList<>();
    long t2 = rowsEngine.process(new RowTask.FrequencyCompute(), new StreamOut.Memory(aux));
    messages.printMessageln("Frequency computing took " + Utilities.milisecondsToSeconds(t2) + " seconds.");

    // Ids reassingment
    long t3 = rowsEngine.process(new RowTask.ReassignIds(new FrequencyTable(aux)), createDeltaStreamOut(conf.getReassignedSimilaritiesPath()));
    messages.printMessageln("NeighborhoodSimilarity values reassignment took " + Utilities.milisecondsToSeconds(t3) + " seconds.");
  }
}
