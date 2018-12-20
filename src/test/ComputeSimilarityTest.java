package test;

import org.junit.After;
import org.junit.Test;
import p1811002.Dataset;
import p1811002.NeighborhoodSimilarity;
import p1811002.utils.Utilities;

import java.io.File;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ComputeSimilarityTest {

  final static String datasetPath = "./datasets/movielens-100k.csv";
  final static String similaritiesPath = "./out/sim.data";
  final static String neighborsPath = "./out/neig.data";

  @Test
  public void generateOutput () {
    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(similaritiesPath, neighborsPath, true);
    neighborhoodSimilarity.compute(dataset);
  }

  @Test
  public void readBackNeighborIds () {

    Dataset dataset = new Dataset.MovieLensDataset();
    dataset.read(datasetPath);

    NeighborhoodSimilarity neighborhoodSimilarity = new NeighborhoodSimilarity.Impl(similaritiesPath, neighborsPath, true);
    neighborhoodSimilarity.compute(dataset);

    List<Integer> readData = Utilities.readLine(neighborsPath);
    assertEquals(671, readData.size());
  }

  @After
  public void cleanEnviroment () {
    new File(similaritiesPath).delete();
    new File(neighborsPath).delete();
  }
}
