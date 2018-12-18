package test;

import org.junit.After;
import org.junit.Test;
import p1811002.ComputeSimilarity;
import p1811002.utils.Utilities;

import java.io.File;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class ComputeSimilarityTest {

    final static String datasetPath = "./datasets/movielens-100k.csv";
    final static String similaritiesPath = "./out/sim.csv";
    final static String neighborsPath = "./out/neig.csv";

    @Test
    public void generateOutput () {
        new ComputeSimilarity(datasetPath, similaritiesPath, neighborsPath).start();
    }

    @Test
    public void readBackOutput () {
        new ComputeSimilarity(datasetPath, similaritiesPath, neighborsPath).start();

        List<Integer> readData = Utilities.readLine(neighborsPath);
        assertEquals(671, readData.size());
    }

    @After
    public void cleanEnviroment () {
        new File(similaritiesPath).delete();
        new File(neighborsPath).delete();
    }
}
