package tfg.generate;

import tfg.generate.engine.RowDelimiterException;

public class Conf {

  enum Mode {MATRIX, NEIGHBORHOOD, UNDEFINED}
  private Mode mode;

  // Neighborhood similarity mode
  private static final int LOWER_VALUE = 0;
  private static final int UPPER_VALUE = 1000;

  private int rowDelimiter;
  private int k;

  private String dataPath;
  private String similaritiesPath;
  private String neighborhoodPath;
  private String orderedIndexesPath;
  private String reassignedSimilaritiesPath;

  // Rating matrix mode
  private String ratingMatrixPath;
  private int shardsNumber;

  public int getShardsNumber() {
    return shardsNumber;
  }

  public void setShardsNumber(int shardsNumber) {
    this.shardsNumber = shardsNumber;
  }

  public Mode getMode() {
    return mode;
  }

  public void setMode(Mode mode) {
    this.mode = mode;
  }

  public String getRatingMatrixPath() {
    return ratingMatrixPath;
  }

  public void setRatingMatrixPath(String ratingMatrixPath) {
    this.ratingMatrixPath = ratingMatrixPath;
  }

  public String getReassignedSimilaritiesPath() {
    return reassignedSimilaritiesPath;
  }

  public void setReassignedSimilaritiesPath(String reassignedSimilaritiesPath) {
    this.reassignedSimilaritiesPath = reassignedSimilaritiesPath;
  }

  private static final Conf conf = new Conf();

  private Conf () {
    mode = Mode.UNDEFINED;
  }

  public static final Conf getConf () {
    return conf;
  }

  public int getRowDelimiter() {
    return rowDelimiter;
  }

  public void setRowDelimiter(int rowDelimiter) throws RowDelimiterException {
    Conf.validRowDelimiter(rowDelimiter);
    this.rowDelimiter = rowDelimiter;
  }

  public static void validRowDelimiter (int value) throws RowDelimiterException {
    if (value >= LOWER_VALUE && value <= UPPER_VALUE ) {
      throw new RowDelimiterException();
    }
  }

  public String getDataPath() {
    return dataPath;
  }

  public void setDataPath(String dataPath) {
    this.dataPath = dataPath;
  }

  public String getSimilaritiesPath() {
    return similaritiesPath;
  }

  public void setSimilaritiesPath(String similaritiesPath) {
    this.similaritiesPath = similaritiesPath;
  }

  public String getNeighborhoodPath() {
    return neighborhoodPath;
  }

  public void setNeighborhoodPath(String neighborhoodPath) {
    this.neighborhoodPath = neighborhoodPath;
  }

  public String getOrderedIndexesPath() {
    return orderedIndexesPath;
  }

  public void setOrderedIndexesPath(String orderedIndexesPath) {
    this.orderedIndexesPath = orderedIndexesPath;
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }
}
