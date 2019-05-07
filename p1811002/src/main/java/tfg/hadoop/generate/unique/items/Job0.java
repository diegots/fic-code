package tfg.hadoop.generate.unique.items;

import tfg.hadoop.util.Util;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Obtains n lists with the unique item Ids.
 */
public class Job0 {

  /**
   * Sends every item to one reducer. Each reducer takes care of one interval of itemIds
   */
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    Double numReducerTasks;
    int step;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      Double maxItemId = (double) context.getConfiguration().
          getInt(Main.MAX_ITEMID, 0);
      numReducerTasks = (double) context.getNumReduceTasks();
      step = Double.valueOf(Math.ceil(maxItemId / numReducerTasks)).intValue();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      if (!Util.containsHeader(value.toString())) {
        int itemId = Integer.valueOf(value.toString().split(",")[1]);

        for (int reducerId=0; reducerId<numReducerTasks; reducerId++) { // 0, 1, 2, ...
          int from = reducerId * step + 1;
          int to = (reducerId+1) * step;

          if (itemId >= from && itemId <= to) {
            context.write(
              new IntWritable(reducerId),
              new IntWritable(itemId));
          }
        }
      }
    }
  }

  /**
   * Discards duplicated itemIds. Implemented using a Java set.
   */
  public static class Reduce
      extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

      Set<Integer> allItems = new LinkedHashSet();
      for (IntWritable itemId: values) {
        allItems.add(itemId.get());
      }

      for (Integer itemId: allItems) {
        context.write(new IntWritable(itemId), NullWritable.get());
      }
    }
  }
}
