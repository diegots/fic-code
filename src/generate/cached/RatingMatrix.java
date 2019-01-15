package generate.cached;

import generate.Conf;
import generate.model.Dataset;
import common.util.Utilities;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Prepare data to be proccessed in Hadoop. Reorganizes the data according how
 * is going to be accessed.
 */
public class RatingMatrix {

  private final Dataset dataset;

  public RatingMatrix(Dataset dataset) {
    this.dataset = dataset;
  }

  // TODO missing messages
  public void distribureUsersToShards() {

    // Initialize shards
    List<Map<Integer, Map<Integer, Double>>> shards = new ArrayList<>();
    for (int i=0; i<Conf.getConf().getShardsNumber(); i++) {
      shards.add(i, new HashMap<>());
    }

    Set<Integer> userIds = dataset.getUserIds();
    Iterator<Integer> iterator = userIds.iterator();
    while (iterator.hasNext()) {

      int userId = iterator.next();
      Map<Integer, Double> userProfile = dataset.getUserProfile(userId);

      int shardId = userId % Conf.getConf().getShardsNumber();

      Map<Integer, Map<Integer, Double>> shard = shards.get(shardId);
      shard.put(userId, userProfile);
    }

    for (int i=0; i<Conf.getConf().getShardsNumber(); i++) {

      String shardPath = Conf.getConf().getRatingMatrixPath() + i;
      try {
        FileOutputStream outputStream = new FileOutputStream(shardPath);
        Utilities.objectToFile(outputStream, shards.get(i));
        outputStream.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
