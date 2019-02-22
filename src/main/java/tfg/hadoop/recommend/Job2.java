package tfg.hadoop.recommend;

import org.apache.commons.math3.util.Precision;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import tfg.common.util.Utilities;
import tfg.hadoop.recommend.model.TripleWritable;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Job2 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, TripleWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      String [] values = value.toString().split("\t");

      context.write(
          new IntWritable(Integer.valueOf(values[0]) % context.getConfiguration().getInt(Main.SHARDS_NUMBER,0)),
          new TripleWritable(
              new IntWritable(Integer.valueOf(values[0])), // active user
              new IntWritable(Integer.valueOf(values[1])), // item Id
              new DoubleWritable(Double.valueOf(values[2])) // weight
          )
      );
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, TripleWritable, IntWritable, Text> {

    private static final Logger logger = Logger.getLogger(Job2.Reduce.class.getName());
    private static final Level level = Level.INFO;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      logger.setLevel(Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).getLevel());
    }

    @Override
    protected void reduce(IntWritable key, Iterable<TripleWritable> values, Context context) throws IOException, InterruptedException {

      java.util.Map<Integer, java.util.Map<Integer, Double>> result = new HashMap<>();


      /* Extracts TripleWritables and constructs a map with data for every user */
      for (TripleWritable tw: values) {
        int activeUserId = tw.getFirst().get();
        int itemId = tw.getSecond().get();
        double weight = tw.getThird().get();

        logger.log(level,"activeUserId: " + activeUserId + " - itemId: " + itemId + " - weight: " + weight);

        if (result.containsKey(activeUserId)) {
          if (result.get(activeUserId).containsKey(itemId)) {
            result.get(activeUserId).put(
              itemId,
              result.get(activeUserId).get(itemId) + weight);
          } else {
            result.get(activeUserId).put(itemId, weight);
          }
        } else {
          java.util.Map<Integer, Double> aux = new HashMap<>();
          aux.put(itemId, weight);
          result.put(activeUserId, aux);
        }
      }

      /* Buils one string for every user with his recommendations */
      for (Integer activeUser: result.keySet()) {

        java.util.Map<Integer, Double> weights = Utilities.sortMapByValue(result.get(activeUser));
        StringBuilder aux = new StringBuilder();

        int recsNumber = context.getConfiguration().getInt(Main.RECS_NUMBER, 0);
        int decimalPlaces = context.getConfiguration().getInt(Main.DECIMAL_PLACES, -1);

        for (Integer itemId: weights.keySet()) {
          aux.append(", " + itemId + ":" + Precision.round(weights.get(itemId), decimalPlaces));
          if (--recsNumber <= 0) {
            break;
          }
        }

        context.write(new IntWritable(activeUser), new Text("[" + aux.substring(2, aux.length()) + "]"));
      }

    }
  }
}
