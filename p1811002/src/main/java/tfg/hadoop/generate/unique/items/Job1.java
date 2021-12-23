package tfg.hadoop.generate.unique.items;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Combines n files into on big file with all unique item Ids
 */
public class Job1 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      context.write(new IntWritable(1), value);
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, Text, Text, Text> {

    @Override
    protected void reduce(IntWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      for (Text t: values) {
        context.write(t, new Text());
      }
    }
  }
}
