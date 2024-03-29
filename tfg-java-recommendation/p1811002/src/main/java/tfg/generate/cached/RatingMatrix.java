package tfg.generate.cached;

import tfg.common.util.Utilities;
import tfg.generate.Conf;
import tfg.generate.model.Dataset;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Prepare data to be processed in Hadoop. Reorganizes the data according how
 * is going to be accessed.
 */
public class RatingMatrix {

  private final Dataset dataset;

  public RatingMatrix(Dataset dataset) {
    this.dataset = dataset;
  }

  public void distribureUsersToShards() {

    // Initialize shards
    List<Map<Integer, Map<Integer, Double>>> shards = new ArrayList<>();
    for (int i = 0; i< Conf.get().getShardsNumber(); i++) {
      shards.add(i, new HashMap<>());
    }

    Iterator<Integer> iterator = dataset.getUserIds().iterator();
    while (iterator.hasNext()) {

      int userId = iterator.next();
      Map<Integer, Double> userProfile = dataset.getUserProfile(userId);

      int shardId = userId % Conf.get().getShardsNumber();

      Map<Integer, Map<Integer, Double>> shard = shards.get(shardId);
      shard.put(userId, userProfile);
    }

    for (int i = 0; i<Conf.get().getShardsNumber(); i++) {
      try {
        FileOutputStream outputStream = new FileOutputStream(Conf.get().getRatingMatrixPath() + i);
        Utilities.mapToFile(outputStream, shards.get(i));
        outputStream.close();
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
