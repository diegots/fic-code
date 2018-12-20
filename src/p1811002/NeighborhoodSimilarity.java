package p1811002;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.HashedMap;
import p1811002.utils.Utilities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface NeighborhoodSimilarity {

  long compute (Dataset dataset);

  class Impl implements NeighborhoodSimilarity {

    private static String similaritiesPath;
    private static String neighborsPath;
    private final Messages messages;

    public Impl(String similaritiesPath, String neighborsPath, boolean withDots) {

      this.similaritiesPath = similaritiesPath;
      this.neighborsPath = neighborsPath;

      if (withDots) {
        messages = new Messages.Dot();
      } else {
        messages = new Messages.Void();
      }
    }

    class Denominators {

      private Map<Integer, Double> denominators;

      public void compute (Dataset dataset) {
        System.out.print("Computing denominators...");

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

        System.out.println(" done.");
        denominators = md;
      }
      double getDenominator (int userId) {
        return denominators.get(userId);
      }

    }

    @Override
    public long compute (Dataset dataset) {

      /* Fist compute denominators for all users */
      Denominators denominators = new Denominators();
      denominators.compute(dataset);

      final int MAX_USERS = 1384;
      final int TIMES = 1000;
      final int MAX_IDS_STORE = 1000;

      final long start = System.currentTimeMillis();
      double ratingJ;
      double sum;
      int count=0;

      Double res;
      MapIterator<Integer, Double> mapIterator;
      Integer item;
      Integer userI;
      Integer userJ;
      HashedMap<Integer, Double> hm;
      List<Integer> similarities;
      List<Integer> neighborIds = new ArrayList<>();
      Iterator<Integer> iteratorUsersJ;
      Iterator<Integer> iteratorUsersI = dataset.getUserIds().iterator();;

      while (iteratorUsersI.hasNext()) {

        userI = iteratorUsersI.next();

        messages.printDoing();

        neighborIds.add(userI);
        similarities = new ArrayList<>();

        iteratorUsersJ = dataset.getUserIds().iterator();
        while (iteratorUsersJ.hasNext()) {
          userJ = iteratorUsersJ.next();

          if (userJ < userI) {
            similarities.add(0);
          } else {

            sum = 0.0;

            // Change to MapIterator
            hm = (HashedMap<Integer, Double>) dataset.getUserProfile(userI);
            mapIterator = hm.mapIterator();

            while (mapIterator.hasNext()) {
              item = mapIterator.next();

              if (dataset.getUserProfile(userJ).containsKey(item)) {
                ratingJ = dataset.getUserProfile(userJ).get(item);
              } else {
                continue;
              }

              sum += mapIterator.getValue() * ratingJ;
            }

            res = TIMES * (sum / (denominators.getDenominator(userI) * denominators.getDenominator(userJ)));
            similarities.add(res.intValue());

          }
        }

        Utilities.writeLine(similaritiesPath, similarities);

        if (neighborIds.size() >= MAX_IDS_STORE) {
          Utilities.writeLine(neighborsPath, neighborIds);
          neighborIds = new ArrayList<>();
        }

        count++;
        // Stop algorithm after MAX_USERS was reached. This is for testing purposes
        if (count >= MAX_USERS) {
          break;
        }
      }

      /* Turn over the last batch of userIds */
      Utilities.writeLine(neighborsPath, neighborIds);

      return System.currentTimeMillis() - start / 1000;

//      System.out.println(" for " + (count) + " users "
//          + "took " + ((System.currentTimeMillis() - start)/1000) + " seconds.");


    }
  }

}
