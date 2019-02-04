package tfg.generate;

import tfg.generate.utils.Messages;

public class Conf {

  public final int SIMILARITY_ROWS_DELIMITER = 1001;
  public final int USERS_ROWS_DELIMITER = 0;

  public final int SIMILARITY_MAX_VALUE = 1000;

  private Messages messages;

  enum Mode {MATRIX, NEIGHBORHOOD, UNDEFINED}
  private Mode mode;

  private int k;

  private String datasetInPath;
  private String similaritiesOutPath; // encoded sim mat
  private String neighborhoodOutPath; // list of treated users
  private String orderedIndexesPath; // k neighbors
  private String reassignedSimilaritiesOutPath; // encoded.reassigned sim mat

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

  public String getReassignedSimilaritiesOutPath() {
    return reassignedSimilaritiesOutPath;
  }

  public void setReassignedSimilaritiesOutPath(String reassignedSimilaritiesOutPath) {
    this.reassignedSimilaritiesOutPath = reassignedSimilaritiesOutPath;
  }

  private static final Conf conf = new Conf();

  private Conf () {
    mode = Mode.UNDEFINED;
    messages = new Messages.Void();
  }

  public static final Conf get() {
    return conf;
  }

  public Messages getMessages() {
    return messages;
  }

  public void setMessages(Messages messages) {
    this.messages = messages;
  }

  public String getDatasetInPath() {
    return datasetInPath;
  }

  public void setDatasetInPath(String datasetInPath) {
    this.datasetInPath = datasetInPath;
  }

  public String getSimilaritiesOutPath() {
    return similaritiesOutPath;
  }

  public void setSimilaritiesOutPath(String similaritiesOutPath) {
    this.similaritiesOutPath = similaritiesOutPath;
  }

  public String getNeighborhoodOutPath() {
    return neighborhoodOutPath;
  }

  public void setNeighborhoodOutPath(String neighborhoodOutPath) {
    this.neighborhoodOutPath = neighborhoodOutPath;
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
