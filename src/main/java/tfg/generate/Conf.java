package tfg.generate;

import tfg.generate.utils.Messages;

public class Conf {

  public final int SIMILARITY_MAX_VALUE = 1000;
  public final int SIMILARITY_ROWS_DELIMITER = SIMILARITY_MAX_VALUE + 1;

  // TODO Find a safe value
  public final int USERS_ROWS_DELIMITER = 1000000;

  private Messages messages;

  enum Mode {MATRIX, NEIGHBORHOOD, UNDEFINED}
  private Mode mode;

  private int k;

  private String datasetInPath;
  private String encodedSimMatPath; // encoded.sim.mat
  private String encodedUserIdsPath; // encoded.user.ids
  private String encodedUsersKNeighborsPath; // encoded.users.k.neighbors
  private String plainFrequencyTablePath; // plain.frequency.table
  private String encodedReassignedSimMatPath; // encoded.reassigned sim mat

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

  public String getPlainFrequencyTablePath() {
    return plainFrequencyTablePath;
  }

  public void setPlainFrequencyTablePath(String plainFrequencyTablePath) {
    this.plainFrequencyTablePath = plainFrequencyTablePath;
  }

  public String getRatingMatrixPath() {
    return ratingMatrixPath;
  }

  public void setRatingMatrixPath(String ratingMatrixPath) {
    this.ratingMatrixPath = ratingMatrixPath;
  }

  public String getEncodedReassignedSimMatPath() {
    return encodedReassignedSimMatPath;
  }

  public void setEncodedReassignedSimMatPath(String encodedReassignedSimMatPath) {
    this.encodedReassignedSimMatPath = encodedReassignedSimMatPath;
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

  public String getEncodedSimMatPath() {
    return encodedSimMatPath;
  }

  public void setEncodedSimMatPath(String encodedSimMatPath) {
    this.encodedSimMatPath = encodedSimMatPath;
  }

  public String getEncodedUserIdsPath() {
    return encodedUserIdsPath;
  }

  public void setEncodedUserIdsPath(String encodedUserIdsPath) {
    this.encodedUserIdsPath = encodedUserIdsPath;
  }

  public String getEncodedUsersKNeighborsPath() {
    return encodedUsersKNeighborsPath;
  }

  public void setEncodedUsersKNeighborsPath(String encodedUsersKNeighborsPath) {
    this.encodedUsersKNeighborsPath = encodedUsersKNeighborsPath;
  }

  public int getK() {
    return k;
  }

  public void setK(int k) {
    this.k = k;
  }
}
