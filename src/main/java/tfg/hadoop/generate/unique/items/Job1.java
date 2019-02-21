package tfg.hadoop.generate.unique.items;

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
      extends Mapper<LongWritable, Text, LongWritable, Text> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
      super.map(new LongWritable(1), value, context);
    }
  }

  public static class Reduce
      extends Reducer<LongWritable, Text, Text, Text> {

    @Override
    protected void reduce(LongWritable key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
      super.reduce(key, values, context);
    }
  }
}
