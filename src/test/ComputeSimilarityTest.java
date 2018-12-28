package test;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import p1811002.Dataset;
import p1811002.Messages;
import p1811002.NeighborhoodSimilarity;
import p1811002.utils.Utilities;

import java.io.File;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ComputeSimilarityTest {

  private final static String datasetPath = "./datasets/movielens-100k.csv";
  private final static String similaritiesPath = "./out/sim.data";
  private final static String neighborsPath = "./out/neig.data";
  private Messages messages;

  @Before
  public void prepareStuff () {
    messages = new Messages.Void();
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

  @After
  public void cleanEnviroment () {
    new File(similaritiesPath).delete();
    new File(neighborsPath).delete();
  }
}
