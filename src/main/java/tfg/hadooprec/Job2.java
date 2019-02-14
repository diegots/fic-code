package tfg.hadooprec;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import tfg.common.util.Utilities;
import tfg.hadooprec.model.ActiveUser;
import tfg.hadooprec.model.PairWritable;
import tfg.hadooprec.model.TripleWritable;

import java.io.IOException;
import java.util.HashMap;

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
    @Override
    protected void reduce(IntWritable key, Iterable<TripleWritable> values, Context context) throws IOException, InterruptedException {

      java.util.Map<Integer, java.util.Map<Integer, Double>> result = new HashMap<>();

      for (TripleWritable tw: values) {
        int activeUserId = tw.getFirst().get();
        int itemId = tw.getSecond().get();
        double weight = tw.getThird().get();
        System.out.println("activeUserId: " + activeUserId + " - itemId: " + itemId + " - weight: " + weight);
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

      for (Integer activeUser: result.keySet()) {

        java.util.Map<Integer, Double> m = Utilities.sortMapByValue(result.get(activeUser));
        StringBuilder aux = new StringBuilder();

        for (Integer itemId: m.keySet()) {
          aux.append(", " + itemId + ":" + m.get(itemId));
        }

        context.write(new IntWritable(activeUser), new Text("[" + aux.substring(2, aux.length()) + "]"));
      }

    }
  }
}
