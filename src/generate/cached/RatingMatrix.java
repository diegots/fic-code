package generate.cached;

import generate.Conf;
import generate.dataset.Dataset;
import generate.dataset.User;
import generate.utils.Utilities;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Prepare data to be proccessed in Hadoop. Reorganizes the data according how
 * is going to be accessed.
 */
public class RatingMatrix {

  private final Dataset dataset;

  public RatingMatrix(Dataset dataset) {
    this.dataset = dataset;
  }

  public void distribureUsersToShards() {

    Set<Integer> userIds = dataset.getUserIds();
    Iterator<Integer> iterator = userIds.iterator();
    while (iterator.hasNext()) {

      int userId = iterator.next();
      Map<Integer, Double> userProfile = dataset.getUserProfile(userId);

      String shardPath = Conf.getConf().getRatingMatrixPath()
          + userId % Conf.getConf().getShardsNumber();

      Utilities.objectToFile(shardPath, new User(userId, userProfile));
    }
  }
}
