package tfg.hadoop.recommend;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import tfg.common.stream.StreamIn;
import tfg.common.util.Utilities;
import tfg.generate.model.FrequencyTable;
import tfg.hadoop.recommend.cached.UserIds;
import tfg.hadoop.recommend.cached.UsersKNeighbors;
import tfg.hadoop.recommend.model.ActiveUser;
import tfg.hadoop.recommend.model.PairWritable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

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
      extends Reducer<IntWritable, ActiveUser, IntWritable, PairWritable> {

    private UserIds userIds;
    private FrequencyTable frequencyTable;
    private UsersKNeighbors usersKNeighbors;

    private FileInputStream similaritiesFIS;
    private StreamIn.DeltraStreamInRow similarities;

    private static final Logger logger = Logger.getLogger(Reduce.class.getName());
    private static final Level level = Level.INFO;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

      // Set logger level for all reducers
      logger.setLevel(Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).getLevel());

      // Reads encoded.user.ids
      FileInputStream fileInputStream = new FileInputStream(
          new File(Main.cachedSimilarities[Main.CachedSimilarities.encodedUserIds.ordinal()]).getName());
      userIds = new UserIds(new StreamIn.DeltaStreamIn(fileInputStream).read());
      fileInputStream.close();

      // User's neighborhoods
      fileInputStream = new FileInputStream(
          new File(Main.cachedSimilarities[Main.CachedSimilarities.encodedUsersKNeighbors.ordinal()]).getName());
      usersKNeighbors = new UsersKNeighbors(new StreamIn.DeltaStreamIn(fileInputStream).read());
      fileInputStream.close();

      logger.log(level, usersKNeighbors.toString());

      // Frequency table
      fileInputStream = new FileInputStream(
          new File(Main.cachedSimilarities[Main.CachedSimilarities.plainFrequencyTable.ordinal()]).getName());
      frequencyTable = new FrequencyTable(new StreamIn.PlainStreamIn(fileInputStream).read());
      fileInputStream.close();

      // Seekable similarity data
      similaritiesFIS = new FileInputStream(
          new File(Main.cachedSimilarities[Main.CachedSimilarities.encodedReassignedSimMat.ordinal()]).getName());
      similarities = new StreamIn.DeltraStreamInRow(similaritiesFIS);
    }

    @Override
    protected void reduce(IntWritable key, Iterable<ActiveUser> values, Context context) throws IOException, InterruptedException {

      logger.log(level, "Start worker: " + key.get());

      // Access shard for reading. Shard depends on key value
      java.util.Map<Integer, java.util.Map<Integer, Double>> shard =
          Utilities.objectFromFile(new FileInputStream(new Path(Main.cachedShards[key.get()]).getName()));

      for (ActiveUser activeUser: values) {

        logger.log(level,"Active user: " + activeUser.getUserId().get()
            + ", with index: " + userIds.findIndex(activeUser.getUserId().get()));

        List<Integer> neighbors = usersKNeighbors.getNeighbors(userIds.findIndex(activeUser.getUserId().get()));
        logger.log(level,"Number of neighbors: " + neighbors.size());
        for (Integer neighborIndex: neighbors) {
          logger.log(level,"Neighbor: " + userIds.findId(neighborIndex));

          if (activeUser.getUserId().get() == userIds.findId(neighborIndex)) {
            logger.log(level,"Skipping neighbor because it's the active user");
            continue;
          }

          if (userIds.findId(neighborIndex) %
              context.getConfiguration().getInt(Main.SHARDS_NUMBER, 0) == key.get()) {

            logger.log(level,"Neighbor in this shard!");

            // Get similarity between users
            List<Integer> l = similarities.read(userIds.findIndex(activeUser.getUserId().get()));
            if (null == l) {
              logger.log(level,"Similarities row null");
              similarities.reset();
            } else {
              logger.log(level,"Similarities row length: " + l.size());

              logger.log(level,l.get(0) + ": " + frequencyTable.decodeValue(l.get(0)));
              for (int i=1; i<l.size(); i++)
                logger.log(level,", " + l.get(i) +": " + frequencyTable.decodeValue(l.get(i)));
            }
            logger.log(level,"");

            // Now find actual values, these are reassigned
            int a = l.get(neighborIndex);
            logger.log(level,"Similarity between " + activeUser.getUserId().get()
                + " and " + userIds.findId(neighborIndex) + " (reassigned): " + a);
            double b = frequencyTable.decodeValue(a);
            logger.log(level,"Actual similarity: " + b + " and divided: " + (b/1000));

            double similarity = b / 1000;
            for (Integer item: activeUser.getNonRatedItemsArray()) {
              Double rating = shard.get(userIds.findId(neighborIndex)).get(item);
              if (rating != null) {
                logger.log(level,"Rating for item '" + item + "' is: " + rating
                    + " - weight: " + (similarity * rating));
                context.write(
                    activeUser.getUserId(),
                    new PairWritable(new IntWritable(item), new DoubleWritable(similarity * rating))
                );
              }
            }
          }
        }

        //break; // DEBUG - Stops after the first active user
//        if (activeUser.getUserId().get() == 71)
//          break;
      }

      logger.log(level,"End worker: " + key.get());

    }

    @Override
    protected void cleanup(Context context) throws IOException, InterruptedException {
      similaritiesFIS.close();
    }
  }
}
