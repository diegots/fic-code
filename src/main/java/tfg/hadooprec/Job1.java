package tfg.hadooprec;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import tfg.common.util.Utilities;
import tfg.hadooprec.model.ActiveUser;
import tfg.hadooprec.model.Data;
import tfg.hadooprec.model.UsersKNeighbors;
import tfg.hadooprec.types.PairWritable;
import tfg.hadooprec.types.TripleWritable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class Job1 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, ActiveUser> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      // Reducers for every shard have to treat this active user
      for (int shard=0; shard<context.getConfiguration().getInt(Main.SHARDS_NUMBER, 0); shard++) {
        context.write(new IntWritable(shard), new ActiveUser(value));
      }
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, ActiveUser, IntWritable, TripleWritable> {

    private Data userIds;
    private UsersKNeighbors usersKNeighbors;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

      // User Ids
      FileInputStream fis = new FileInputStream(
          new File(Main.cachedSimilarities[Main.CachedSimilarities.encodedUserIds.ordinal()]).getName());
      userIds = new Data(fis);
      fis.close();

      // Users neighborhoods
      fis = new FileInputStream(
          new File(Main.cachedSimilarities[Main.CachedSimilarities.encodedUsersKNeighbors.ordinal()]).getName());
      usersKNeighbors = new UsersKNeighbors(fis);
      fis.close();
    }

    @Override
    protected void reduce(IntWritable key, Iterable<ActiveUser> values, Context context) throws IOException, InterruptedException {

      System.err.println("-> Start worker: " + key.get());

      // Access shard for reading. Shard depends on key value
      java.util.Map<Integer, java.util.Map<Integer, Double>> shard =
          Utilities.objectFromFile(new FileInputStream(new Path(Main.cachedShards[key.get()]).getName()));

      for (ActiveUser activeUser: values) {

        System.out.println("-> Active user: " + activeUser.getUserId().get()
            + ", with index: " + userIds.findIndex(activeUser.getUserId().get()));

        List<Integer> neighbors = usersKNeighbors.getNeighbors(userIds.findIndex(activeUser.getUserId().get()));
        for (Integer neighborIdx: neighbors) {
          System.out.println("    -> Neighbor: " + userIds.getId(neighborIdx));

          if (userIds.getId(neighborIdx) % context.getConfiguration().getInt(Main.SHARDS_NUMBER, 0) == key.get()) {
            System.out.println("        -> In this shard!");

            // Get similarity between users
            Double similarity = Double.valueOf(0.0);

            for (Integer item: activeUser.getNonRatedItemsArray()) {
              Double rating = shard.get(userIds.getId(neighborIdx)).get(item);
              if (rating != null) {
                Writable weight = new DoubleWritable(similarity * rating);
                System.out.println("            -> Rating for item '" + item + "' is: " + rating);
                context.write(
                    activeUser.getUserId(),
                    new TripleWritable(new IntWritable(0), new IntWritable(0), new IntWritable(0))
                );
              }
            }
          }
        }

        //break; // DEBUG - Stops after the first active user
        if (activeUser.getUserId().get() == 71)
          break;

      }
      System.err.println("-> End worker: " + key.get());
    }
  }
}
