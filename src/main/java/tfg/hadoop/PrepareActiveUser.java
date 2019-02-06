package tfg.hadoop;

import org.apache.hadoop.io.FloatWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;


public class PrepareActiveUser {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {
    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      // Reducers for every shard have to treat this active user

      int userId = Integer.valueOf(value.toString());
      context.write(
          new IntWritable(userId % context.getConfiguration().getInt(Main.SHARDS_NUMBER, 0)),
          new IntWritable(userId)
      );
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, IntWritable,
      IntWritable, FloatWritable> {

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

    }

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

    }
  }
}
