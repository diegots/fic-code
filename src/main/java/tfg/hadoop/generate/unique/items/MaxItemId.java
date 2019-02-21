package tfg.hadoop.generate.unique.items;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class MaxItemId {

  /**
   * Sends all itemIds to one reducer.
   */
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      if (!tfg.hadoop.util.Util.containsHeader(value.toString())) {
        context.write(
            new IntWritable(1),
            new IntWritable(
                Integer.valueOf(value.toString().split(",")[1])
            )
        );
      }
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, IntWritable, IntWritable, NullWritable> {
    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

      int maxItemid = 0;
      for (IntWritable value: values) {
        if (value.get() > maxItemid) {
          maxItemid = value.get();
        }
      }

      context.write(new IntWritable(maxItemid), NullWritable.get());
    }
  }
}
