package generate.model;

import generate.utils.Messages;
import org.apache.commons.collections4.map.HashedMap;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents any recommendation dataset read into memory.
 */
public interface Dataset {

  /**
   * Reads the dataset from the filesystem.
   * @param inPath Path to the dataset.
   */
  void read (String inPath);

  /**
   * Retrieves one particular user profile as a Map.
   * @param userId Id for whom his profile will be retrieved.
   * @return User rated values.
   */
  Map<Integer, Double> getUserProfile (int userId);

  /**
   * Obtains all users Ids contained in the dataset.
   * @return Set with user's Id.
   */
  Set<Integer> getUserIds ();

  /**
   * MovieLens dataset specific implementation.
   */
  class MovieLensDataset implements Dataset {

    private final Map<Integer, Map<Integer, Double>> dataset;
    private final Messages messages;

    /**
     * @param messages Handler for printing messages out.
     */
    public MovieLensDataset(Messages messages) {
      dataset = new HashedMap<>();
      this.messages = messages;
    }

    @Override
    public void read (String inPath) {

      messages.printMessage("Reading input data...");

      final String DELIMITER = ",";
      final String HEADER_ITEM = "userId";
      Map<Integer, Double> mu;
      Set<Integer> items = new HashSet<>();
      Integer userId, itemId;

      String s;
      String [] ss;
      File f = new File(inPath);

      BufferedReader br = null;
      try {
        br = new BufferedReader(new FileReader(f));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
        System.exit(1);
      }

      try {
        while ((s = br.readLine()) != null) {
          ss = s.split(DELIMITER);

          if (!HEADER_ITEM.equals(ss[0])) {

            userId = Integer.valueOf(ss[0]);

            mu = dataset.get(userId);
            if (null == mu) {
              mu = new HashedMap<>();
            }

            itemId = Integer.valueOf(ss[1]);
            mu.put(itemId, Double.parseDouble(ss[2]));
            dataset.put(userId, mu);
            items.add(itemId);
          }
        }

        br.close();

      } catch (IOException e) {
        e.printStackTrace();
      }

      messages.printMessageln(" done.");
    }

    @Override
    public Map<Integer, Double> getUserProfile(int userId) {
      return dataset.get(userId);
    }

    @Override
    public Set<Integer> getUserIds() {
      return dataset.keySet();
    }
  }
}
