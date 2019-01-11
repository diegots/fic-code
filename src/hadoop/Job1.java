package hadoop;

import hadoop.model.PairWritable;
import hadoop.model.TripleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Job1 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, PairWritable> {

    int shardsNumber;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      shardsNumber = Integer.valueOf(context.getConfiguration().get(Main.SHARDS_NUMBER));
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      // for every neighbor of this active user do a context.write with
      
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, PairWritable,
      IntWritable, TripleWritable> {
    @Override
    protected void reduce(IntWritable key, Iterable<PairWritable> values, Context context) throws IOException, InterruptedException {

    }
  }
}
