package tfg.generate.cached;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.shaded.com.ctc.wstx.io.BufferRecycler;
import org.junit.Test;
import tfg.common.util.Utilities;
import tfg.generate.Conf;
import tfg.generate.model.Dataset;
import tfg.hadoop.recommend.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RatingMatrixTest {
  @Test
  public void distribureUsersToShardsTest() throws IOException {

    Map<Integer, Double> profile1 = new HashMap<>();
    profile1.put(101, 3.5);
    profile1.put(102, 2.0);
    profile1.put(103, 1.5);
    profile1.put(105, 4.0);

    Map<Integer, Double> profile2 = new HashMap<>();
    profile2.put(201, 4.5);
    profile2.put(202, 1.0);
    profile2.put(203, 2.0);
    profile2.put(205, 3.0);

    Map<Integer, Double> profile3 = new HashMap<>();
    profile3.put(301, 2.5);
    profile3.put(302, 1.5);
    profile3.put(303, 1.0);
    profile3.put(305, 2.0);

    Map<Integer, Double> profile4 = new HashMap<>();
    profile4.put(401, 2.5);
    profile4.put(402, 1.5);
    profile4.put(403, 1.0);
    profile4.put(405, 2.0);

    Map<Integer, Double> profile5 = new HashMap<>();
    profile5.put(501, 2.5);
    profile5.put(502, 1.5);
    profile5.put(503, 1.0);
    profile5.put(505, 2.0);

    Dataset dataset = mock(Dataset.MovieLensDataset.class);

    Set<Integer> userids = new HashSet<>();
    Collections.addAll(userids, new Integer[]{1, 2, 3, 4, 5});
    when(dataset.getUserIds()).thenReturn(userids);
    when(dataset.getUserProfile(1)).thenReturn(profile1);
    when(dataset.getUserProfile(2)).thenReturn(profile2);
    when(dataset.getUserProfile(3)).thenReturn(profile3);
    when(dataset.getUserProfile(4)).thenReturn(profile4);
    when(dataset.getUserProfile(5)).thenReturn(profile5);

    String shardPrefix = "shard";
    Conf.get().setShardsNumber(3);
    Conf.get().setRatingMatrixPath(shardPrefix);

    new RatingMatrix(dataset).distribureUsersToShards();

    // Check number of generated shards
    int actualShards = 0;
    while (Files.exists(Paths.get(shardPrefix+actualShards))) {
      actualShards++;
    }
    assertEquals(3, actualShards);

    // Checks shard contents
    Map<Integer, Map<Integer, Double>> expectedShard = new HashMap<>();
    expectedShard.put(3, profile3);
    Map<Integer, Double> actualShard = Utilities.mapFromFile(
        new FileInputStream(shardPrefix+0));
    assertEquals(expectedShard, actualShard);

    expectedShard = new HashMap<>();
    expectedShard.put(1, profile1);
    expectedShard.put(4, profile4);
    actualShard = Utilities.mapFromFile(
        new FileInputStream(shardPrefix+1));
    assertEquals(expectedShard, actualShard);

    expectedShard = new HashMap<>();
    expectedShard.put(2, profile2);
    expectedShard.put(5, profile5);
    actualShard = Utilities.mapFromFile(
        new FileInputStream(shardPrefix+2));
    assertEquals(expectedShard, actualShard);

    // Delete generated files
    for (int i=0; i<3; i++) {
      Utilities.deleteFile(Paths.get(shardPrefix+i));
    }
  }
}
