package tfg.generate.cached;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.HashedMap;
import tfg.common.stream.StreamOut;
import tfg.common.util.Utilities;
import tfg.generate.Conf;
import tfg.generate.model.Dataset;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Computes neighborhood similarity
 */
public interface NeighborhoodSimilarity {


  long compute (Dataset dataset);

  class Denominators {

    private Map<Integer, Double> denominators = null;

    public void compute (Dataset dataset) {
      Conf.get().getMessages().printMessage("Computing denominators...");

      double d, sum;
      int userId;

      Map<Integer, Double> userData;
      Map<Integer, Double> md = new HashedMap<>();
      List<Integer> userIds = new ArrayList<>(dataset.getUserIds());
      Iterator<Integer> iteratorItems, iteratorUsers = userIds.iterator();

      while (iteratorUsers.hasNext()) {
        userId = iteratorUsers.next();
        md.put(userId, 0.0);
      }

      iteratorUsers = userIds.iterator();
      while (iteratorUsers.hasNext()) {
        userId = iteratorUsers.next();
        sum = 0.0;
        userData = dataset.getUserProfile(userId);

        iteratorItems = userData.keySet().iterator();
        while (iteratorItems.hasNext()) {

          d = userData.get(iteratorItems.next());
          sum += Math.pow(d, 2);
        }

        md.put(userId, Math.sqrt(sum));
      }

      Conf.get().getMessages().printMessageln(" done.");
      denominators = md;
    }

    double getDenominator (int userId) {
      return denominators.get(userId);
    }
  }

  class FullMatrix implements NeighborhoodSimilarity {

    @Override
    public long compute(Dataset dataset) {
      /* Fist compute denominators for all users */
      Denominators denominators = new Denominators();
      denominators.compute(dataset);

      Conf.get().getMessages().printMessage("Computing similarities ("
        + this.getClass().getSimpleName() + ")");

      final int MAX_USERS = 1384;
      final int TIMES = 1000;
      final int MAX_IDS_MEMORY_STORED = 1000;

      final long start = System.currentTimeMillis();
      double ratingJ;
      double sum;
      int count=0;

      Double res;
      MapIterator<Integer, Double> itemUserI;
      Integer item;
      Integer userI;
      Integer userJ;
      HashedMap<Integer, Double> hm;
      List<Integer> similarities;
      List<Integer> neighborIds = new ArrayList<>();
      Iterator<Integer> iteratorUsersJ;
      Iterator<Integer> iteratorUsersI = dataset.getUserIds().iterator();

      // Prepare write destination
      StreamOut streamOut = StreamOut.createDeltaStreamOut(Conf.get().getEncodedSimMatPath());

      while (iteratorUsersI.hasNext()) {

        userI = iteratorUsersI.next();

        Conf.get().getMessages().printDoing();

        neighborIds.add(userI);
        similarities = new ArrayList<>();

        iteratorUsersJ = dataset.getUserIds().iterator();
        while (iteratorUsersJ.hasNext()) {
          userJ = iteratorUsersJ.next();

          // Same user similarity stored as 1000 or 1.0
          if (userI == userJ) {
            similarities.add(1000);
            continue;
          }

          sum = 0.0;

          // Iterator on user I items
          itemUserI = ((HashedMap<Integer, Double>) dataset.getUserProfile(userI)).mapIterator();

          while (itemUserI.hasNext()) {
            item = itemUserI.next();

            if (dataset.getUserProfile(userJ).containsKey(item)) {
              ratingJ = dataset.getUserProfile(userJ).get(item);
            } else {
              continue; // Skips current iteration
            }
            System.out.println("item: " + item + ", rating I: " + itemUserI.getValue() + " - rating J: "+ ratingJ);
            sum += itemUserI.getValue() * ratingJ;
          }

          res = TIMES * (sum / (denominators.getDenominator(userI)
              * denominators.getDenominator(userJ)));
          similarities.add(res.intValue());
        }

        /* The rating needs some special Id to mark the end of a row in case some variable length
         * encoding is in use */
        similarities.add(Conf.get().SIMILARITY_ROWS_DELIMITER);

        streamOut.write(similarities);

        if (neighborIds.size() >= MAX_IDS_MEMORY_STORED) {
          Utilities.writeLine(Conf.get().getEncodedUserIdsPath(), neighborIds);
          for (int i=0; i<neighborIds.size(); i++) {
            System.err.print(", " + neighborIds.get(i));
          }
          neighborIds = new ArrayList<>();
        }

        count++;
        // Stop algorithm after MAX_USERS was reached. This is for testing purposes
        if (count >= MAX_USERS) {
          break;
        }
      }

      streamOut.close();

      /* Turn over the last batch of userIds */
      Utilities.writeLine(Conf.get().getEncodedUserIdsPath(), neighborIds);
      for (int i=0; i<neighborIds.size(); i++) {
        System.err.print(", " + neighborIds.get(i));
      }
      Conf.get().getMessages().printMessageln("done.");

      return System.currentTimeMillis() - start;
    }
  }
}
