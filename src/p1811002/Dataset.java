package p1811002;

import org.apache.commons.collections4.map.HashedMap;

import java.io.*;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Dataset {


  void read (String inPath);
  Map<Integer, Double> getUserProfile (int userId);
  Set<Integer> getUserIds ();


  class MovieLensDataset implements Dataset {

    private final Map<Integer, Map<Integer, Double>> dataset;

    public MovieLensDataset() {
      dataset = new HashedMap<>();
    }

    @Override
    public void read (String inPath) {

      System.out.print("Reading input data...");

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

      System.out.println(" done.");
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
