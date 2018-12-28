package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import p1811002.*;
import p1811002.utils.Utilities;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ComputeSimilarityTest {

  private final static String datasetPath = "./datasets/movielens-100k.csv";
  private final static String similaritiesPath = "./out/sim.data";
  private final static String neighborsPath = "./out/neig.data";
  private Messages messages;
  private Conf conf;

  @Before
  public void prepareStuff () throws RowDelimiterException {
    //messages = new Messages.Symbol(".");
    messages = new Messages.Void();

    conf = Conf.getConf();
    conf.setRowDelimiter(2000);
  }

  @Test
  public void generateOutput () {
    Dataset dataset = new Dataset.MovieLensDataset(messages);
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(similaritiesPath, neighborsPath, messages);
    neighborhoodSimilarity.compute(dataset);
  }

  @Test
  public void readBackNeighborIds () {

    Dataset dataset = new Dataset.MovieLensDataset(messages);
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(similaritiesPath, neighborsPath, messages);
    neighborhoodSimilarity.compute(dataset);

    List<Integer> readData = Utilities.readAllFile(neighborsPath);
    assertEquals(671, readData.size());
  }

  @Test
  public void testSimilarityRowLimit () {
    Dataset dataset = new Dataset.MovieLensDataset(messages);
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(
        similaritiesPath, neighborsPath, messages);
    neighborhoodSimilarity.compute(dataset);

    List<Integer> readData = Utilities.readOneRow(similaritiesPath);

    int counter = 0;
    Iterator<Integer> iterator = readData.iterator();
    while (iterator.hasNext()) {
      counter++;
      messages.printMessage(iterator.next());
      messages.printMessage("-");
    }
    messages.printMessageln(counter + " items read.");
    assertEquals(671, counter);

  }

  @After
  public void cleanEnviroment () {
    new File(similaritiesPath).delete();
    new File(neighborsPath).delete();
  }
}
