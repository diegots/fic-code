package similarity;

import similarity.dataset.Dataset;
import similarity.dataset.FrequencyTable;
import similarity.engine.ProccessRows;
import similarity.engine.RowDelimiterException;
import similarity.engine.RowTask;
import similarity.similarity.Neighborhood;
import similarity.stream.StreamOut;
import similarity.utils.Messages;
import similarity.utils.Units;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    /**
     *  This programm uses the following parameters:
     *  args[0] dataset file -> userId | movieId | rating | timestamp
     *  args[1] not reassigned similatiries matrix destination
     *  args[2] similarities matrix's indexes
     *  args[3] ordered indexes by weight
     *  args[4] reassigned similarities matrix destination
     */

    // Prepare stuff
    //final Messages messages = new Messages.Symbol(".");
    final Messages messages = new Messages.Void();

    Conf conf = Conf.getConf();
    try {
      conf.setRowDelimiter(1001);
    } catch (RowDelimiterException e) {
      messages.printErrln("Bad row delimiter");
      System.exit(1);
    }
    conf.setDataPath(args[0]);
    conf.setSimilaritiesPath(args[1]);
    conf.setNeighborhoodPath(args[2]);
    conf.setOrderedIndexesPath(args[3]);
    conf.setK(25);

    // Read dataset
    Dataset dataset = new Dataset.MovieLensDataset(messages);
    dataset.read(args[0]);

    // Compute similarities
    Neighborhood neighborhood = new Neighborhood.Impl(args[1], args[2], messages);
    long t0 = neighborhood.compute(dataset);
    messages.printMessageln("Computing similarities took " + Units.milisecondsToSeconds(t0) + " seconds.");

    // Get processing engine
    ProccessRows rowsEngine = new ProccessRows(messages);

    // Compute ordered indexes
    long t1 = rowsEngine.process(new RowTask.Order(), createDeltaStreamOut(args[3]));
    messages.printMessageln("Ordering similarities took " + Units.milisecondsToSeconds(t1) + " seconds.");

    // Frequency compuring
    final List<Integer> aux = new ArrayList<>();
    long t2 = rowsEngine.process(new RowTask.FrequencyCompute(), new StreamOut.Memory(aux));
    messages.printMessageln("Frequency computing took " + Units.milisecondsToSeconds(t2) + " seconds.");

    // Ids reassingment
    long t3 = rowsEngine.process(new RowTask.ReassignIds(new FrequencyTable(aux)), createDeltaStreamOut(args[4]));
    messages.printMessageln("Similarity values reassignment took " + Units.milisecondsToSeconds(t3) + " seconds.");
  }

  static StreamOut createDeltaStreamOut(String pathToFile) {
    StreamOut streamOut = null;
    try {
      streamOut = new StreamOut.DeltaStreamOut(new FileOutputStream(pathToFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return streamOut;
  }
}
