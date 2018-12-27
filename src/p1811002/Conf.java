package p1811002;

public class Conf {

  private static final int LOWER_VALUE = 0;
  private static final int UPPER_VALUE = 1000;

  private int rowDelimiter;
  private String dataPath;
  private String similaritiesPath;
  private String neighborhoodPath;
  private String orderedIndexesPath;

  private static final Conf conf = new Conf();

  private Conf () {}

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
    if (value >= LOWER_VALUE || value <= UPPER_VALUE ) {
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
}
