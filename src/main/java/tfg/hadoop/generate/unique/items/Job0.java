package tfg.hadoop.generate.unique.items;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
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
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      // context.getNumReduceTasks() * FACTOR should be greater than max itemId
      // max item id is 163949
      final int FACTOR = 1000 * 55;

      int itemId = Integer.valueOf(value.toString().split(",")[1]);
      for (int reducerId=0; reducerId<context.getNumReduceTasks(); reducerId++) { // 0, 1, 2
        int from = reducerId * FACTOR;
        int to = (reducerId+1) * FACTOR -1;

        if (itemId >= from && itemId <= to) {
          context.write(
              new IntWritable(reducerId),
              new IntWritable(itemId));
        }
      }
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, IntWritable, Text, IntWritable> {

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

      Set<Integer> allItems = new LinkedHashSet();
      for (IntWritable itemId: values) {
        allItems.add(itemId.get());
      }

      for (Integer itemId: allItems) {
        context.write(new Text(), new IntWritable(itemId));
      }
    }
  }
}
