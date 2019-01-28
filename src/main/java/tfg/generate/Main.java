package tfg.generate;

import tfg.common.DefaultValues;
import tfg.common.stream.StreamOut;
import tfg.common.util.Utilities;
import tfg.generate.cached.NeighborhoodSimilarity;
import tfg.generate.cached.RatingMatrix;
import tfg.generate.engine.ProccessRows;
import tfg.generate.engine.RowDelimiterException;
import tfg.generate.engine.RowTask;
import tfg.generate.model.Dataset;
import tfg.generate.model.FrequencyTable;
import tfg.generate.utils.Messages;

import java.util.ArrayList;
import java.util.List;

import static tfg.common.stream.StreamOut.createDeltaStreamOut;

public class Main {

  public static void main(String[] args) {

    // Prepare stuff
    Job job = new Job(Conf.get());
    job.parseCommandLine(args);
    job.start();
  }
}

class Job {

  static final String RATING_MATRIX_MODE = "-matrix";
  static final String NEIGHBORHOOD_MODE = "-neighborhood";
  static final String HELP_SHORT_MODE = "-h";
  static final String HELP_LONG_MODE = "-help";
  static final String VERBOSE_MODE = "-v";

  private final Conf conf;
  private Messages mainMessages = new Messages.Symbol("");

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
          conf.setShardsNumber(Integer.valueOf(args[i+2]));
          conf.setRatingMatrixPath(args[i+3]);
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
            conf.setRowDelimiter(DefaultValues.ROW_DELIMITER);
          } catch (RowDelimiterException e) {
            mainMessages.printErrln("Bad row delimiter!");
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
          conf.setMessages(new Messages.Symbol("."));
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
      mainMessages.printErrln("Mode not recognized! Showing usage.");
      help();
      System.exit(1);
    }

    // Read dataset
    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(conf.getDataPath());

    if (Conf.Mode.MATRIX.equals(conf.getMode())) {
      ratingMatrixComputing(dataset);

    } else if (Conf.Mode.NEIGHBORHOOD.equals(conf.getMode())) {
      neighborhoodComputing(dataset);
    }
  }

  private void help () {
    System.out.println("This program computes either a Neighborhood Similarity matrix"
        + " or the grouped Rating Matrix according to the requested number of shards.");
    System.out.println("");
    System.out.println("Usage:");
    System.out.println("        java -jar JAR_NAME [-v] -<mode> <dataset path> +mode-specific-params");
    System.out.println("");
    System.out.println("Mode is one of: '-h', '-help', '-matrix' or '-neighborhood',");
    System.out.println("");
    System.out.println("    '" + HELP_LONG_MODE + "' or '" + HELP_SHORT_MODE + "': show this help.");
    System.out.println("    '" + RATING_MATRIX_MODE + "' mode specific params:");
    System.out.println("        <INPUT : number of shards>");
    System.out.println("        <OUTPUT: rating matrix prefix filename>");
    System.out.println("    '" + NEIGHBORHOOD_MODE + "' mode specific params:");
    System.out.println("        <INPUT : k value>");
    System.out.println("        <OUTPUT: similarity matrix filename (encoded)>");
    System.out.println("        <OUTPUT: neighborhood Ids order list filename>");
    System.out.println("        <OUTPUT: reassigned indexes filename>");
    System.out.println("        <OUTPUT: similarity matrix filename (encoded, reassigned values)>");
  }

  private void ratingMatrixComputing (Dataset dataset) {
    RatingMatrix ratingMatrix = new RatingMatrix(dataset);
    ratingMatrix.distribureUsersToShards();
  }

  private void neighborhoodComputing(Dataset dataset)  {

    // Compute similarities
    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.FullMatrix();
    long t0 = neighborhoodSimilarity.compute(dataset);
    mainMessages.printMessageln("Computing similarities took "
        + Utilities.milisecondsToSeconds(t0) + " seconds.");

    // Get processing engine
    ProccessRows rowsEngine = new ProccessRows();

    // Compute k neighbors
    long t1 = rowsEngine.process(new RowTask.Order(),
        createDeltaStreamOut(conf.getOrderedIndexesPath()));
    mainMessages.printMessageln("Ordering similarities took "
        + Utilities.milisecondsToSeconds(t1) + " seconds.");

    // Frequency computing for compressing similarities
    final List<Integer> aux = new ArrayList<>();
    long t2 = rowsEngine.process(new RowTask.FrequencyCompute(), new StreamOut.Memory(aux));
    mainMessages.printMessageln("Frequency computing took " +
        Utilities.milisecondsToSeconds(t2) + " seconds.");

    // Do compress similarities based en frequency counts
    long t3 = rowsEngine.process(new RowTask.ReassignIds(new FrequencyTable(aux)),
        createDeltaStreamOut(conf.getReassignedSimilaritiesPath()));
    mainMessages.printMessageln("NeighborhoodSimilarity values reassignment took "
        + Utilities.milisecondsToSeconds(t3) + " seconds.");
  }
}
