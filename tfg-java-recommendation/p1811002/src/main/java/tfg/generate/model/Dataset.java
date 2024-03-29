package tfg.generate.model;

import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.collections4.map.LinkedMap;
import tfg.common.model.User;
import tfg.generate.Conf;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Represents any recommendation dataset readSeekable into memory.
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

  public User getUser(int userId);

  /**
   * MovieLens dataset specific implementation.
   */
  class MovieLensDataset implements Dataset {

    /**
     * Stores readSeekable dataset. userIds are kept ordered because the map is initialized as a LinkedMap.
     */
    private final Map<Integer, Map<Integer, Double>> dataset;

    public MovieLensDataset() {
      dataset = new LinkedMap<>();
    }

    @Override
    public void read (String inPath) {

      Conf.get().getMessages().printMessage("Reading input data...");

      final String DELIMITER = ",";
      final String HEADER_ITEM = "userId";
      Map<Integer, Double> userProfile;
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

            userProfile = dataset.get(userId);
            if (null == userProfile) {
              userProfile = new HashedMap<>();
            }

            itemId = Integer.valueOf(ss[1]);
            userProfile.put(itemId, Double.parseDouble(ss[2]));
            dataset.put(userId, userProfile);
            items.add(itemId);
          }
        }

        br.close();

      } catch (IOException e) {
        e.printStackTrace();
      }

      Conf.get().getMessages().printMessageln(" done.");
    }

    @Override
    public Map<Integer, Double> getUserProfile(int userId) {
      return dataset.get(userId);
    }

    @Override
    public Set<Integer> getUserIds() {
      return dataset.keySet();
    }

    @Override
    public User getUser(int userId) {
      return new User(userId, dataset.get(userId));
    }
  }
}
