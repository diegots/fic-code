package tfg.hadooprec.cached;

import org.apache.commons.collections4.map.LinkedMap;

import java.io.InputStream;
import java.util.Map;

public class SimilarityMatrix {
  private final Map<Integer, Map<Integer, Double>> similarityMatrix;

  public SimilarityMatrix(InputStream inputStream) {
    similarityMatrix = new LinkedMap<>();
  }


}
