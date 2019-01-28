package tfg.generate.cached;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.HashedMap;
import tfg.common.stream.StreamOut;
import tfg.common.util.Utilities;
import tfg.generate.Conf;
import tfg.generate.model.Dataset;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

  class TopHalfMatrix implements NeighborhoodSimilarity {

    @Override
    public long compute (Dataset dataset) {

      /* Fist compute denominators for all users */
      Denominators denominators = new Denominators();
      denominators.compute(dataset);

      Conf.get().getMessages().printMessage("Computing similarities (TopHalfMatrix)");

      final int MAX_USERS = 1384;
      final int TIMES = 1000;
      final int MAX_IDS_MEMORY_STORED = 1000;

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
      Iterator<Integer> iteratorUsersI = dataset.getUserIds().iterator();

      // Prepare write destination
      StreamOut streamOut = null;
      try {
        streamOut = new StreamOut.DeltaStreamOut(
            new FileOutputStream(Conf.get().getSimilaritiesPath()));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      while (iteratorUsersI.hasNext()) {

        userI = iteratorUsersI.next();

        Conf.get().getMessages().printDoing();

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

            res = TIMES * (sum / (denominators.getDenominator(userI)
                * denominators.getDenominator(userJ)));
            similarities.add(res.intValue());

          }
        }

        /* The rating needs some special Id to mark the end of a row in case some variable length
         * encoding is in use */
        similarities.add(Conf.get().getRowDelimiter());

        streamOut.write(similarities);

        if (neighborIds.size() >= MAX_IDS_MEMORY_STORED) {
          Utilities.writeLine(Conf.get().getNeighborhoodPath(), neighborIds);
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
      Utilities.writeLine(Conf.get().getNeighborhoodPath(), neighborIds);
      Conf.get().getMessages().printMessageln("done.");

      return System.currentTimeMillis() - start;
    }
  }

  class FullMatrix implements NeighborhoodSimilarity {

    @Override
    public long compute(Dataset dataset) {
      /* Fist compute denominators for all users */
      Denominators denominators = new Denominators();
      denominators.compute(dataset);

      Conf.get().getMessages().printMessage("Computing similarities (TopHalfMatrix)");

      final int MAX_USERS = 1384;
      final int TIMES = 1000;
      final int MAX_IDS_MEMORY_STORED = 1000;

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
      Iterator<Integer> iteratorUsersI = dataset.getUserIds().iterator();

      // Prepare write destination
      StreamOut streamOut = null;
      try {
        streamOut = new StreamOut.DeltaStreamOut(
            new FileOutputStream(Conf.get().getSimilaritiesPath()));
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }

      while (iteratorUsersI.hasNext()) {

        userI = iteratorUsersI.next();

        Conf.get().getMessages().printDoing();

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

            res = TIMES * (sum / (denominators.getDenominator(userI)
                * denominators.getDenominator(userJ)));
            similarities.add(res.intValue());

          }
        }

        /* The rating needs some special Id to mark the end of a row in case some variable length
         * encoding is in use */
        similarities.add(Conf.get().getRowDelimiter());

        streamOut.write(similarities);

        if (neighborIds.size() >= MAX_IDS_MEMORY_STORED) {
          Utilities.writeLine(Conf.get().getNeighborhoodPath(), neighborIds);
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
      Utilities.writeLine(Conf.get().getNeighborhoodPath(), neighborIds);
      Conf.get().getMessages().printMessageln("done.");

      return System.currentTimeMillis() - start;
    }
  }
}
