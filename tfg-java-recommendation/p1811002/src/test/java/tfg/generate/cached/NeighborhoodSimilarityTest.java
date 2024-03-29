package tfg.generate.cached;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tfg.common.util.Utilities;
import tfg.generate.Conf;
import tfg.generate.model.Dataset;
import tfg.generate.util.Messages;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class NeighborhoodSimilarityTest {

  private final static String datasetPath = "./input/dataset/movielens-100k.csv";
  private final static String similaritiesPath = "./out/sim.data";
  private final static String neighborsPath = "./out/neig.data";
  private Messages messages;
  private Conf conf;

  @Before
  public void prepareStuff () {
    //messages = new Messages.Symbol(".");
    messages = new Messages.Void();

    conf = Conf.get();
    conf.setEncodedSimMatPath(similaritiesPath);
    conf.setDatasetInPath(datasetPath);
    conf.setEncodedUserIdsPath(neighborsPath);
  }

  @Test
  public void generateOutput () {
    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(conf.getDatasetInPath());

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.FullMatrix();
    neighborhoodSimilarity.compute(dataset);
  }

  @Test
  public void readBackNeighborIds () {

    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.FullMatrix();
    neighborhoodSimilarity.compute(dataset);

    List<Integer> readData = Utilities.readFileToListAsDeltas(neighborsPath);
    assertEquals(671, readData.size());
  }

  @Test
  public void testSimilarityRowLimit () {
    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.FullMatrix();
    neighborhoodSimilarity.compute(dataset);

    List<Integer> readData = Utilities.readOneRowAsDelta(similaritiesPath, Conf.get().SIMILARITY_ROWS_DELIMITER);

    int counter = 0;
    Iterator<Integer> iterator = readData.iterator();
    while (iterator.hasNext()) {
      counter++;
      messages.printMessage(iterator.next());
      messages.printMessage("-");
    }
    messages.printMessageln(counter + " items readSeekable.");
    assertEquals(671, counter);

  }

  @After
  public void cleanEnviroment () {
    new File(similaritiesPath).delete();
    new File(neighborsPath).delete();
  }
}
