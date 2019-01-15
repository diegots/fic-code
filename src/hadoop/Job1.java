package hadoop;

import common.util.Utilities;
import hadoop.types.TripleWritable;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.FileInputStream;
import java.io.IOException;

public class Job1 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    int shardsNumber;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      // Get current shard assigned number
      shardsNumber = Integer.valueOf(context.getConfiguration().get(Main.SHARDS_NUMBER));
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      // Reducers for every shard have to treat this active user
      for (int shard=0; shard<shardsNumber; shard++) {
        context.write(new IntWritable(shard), new IntWritable(Integer.valueOf(value.toString())));
      }
    }
  }

  public static class Reduce
      extends Reducer<IntWritable, IntWritable, IntWritable, TripleWritable> {

    int shardsNumber;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

      // Get current shard assigned number
      shardsNumber = Integer.valueOf(context.getConfiguration().get(Main.SHARDS_NUMBER));
    }

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

      System.out.println("-> Start " + key.get());

      // Prepare rating matrix
      if (null != context.getCacheFiles() && context.getCacheFiles().length > 0) {
        System.out.println("-> Open shard at " + key.get());
        Path path = new Path(context.getCacheFiles()[key.get()]);
        FileInputStream stream = new FileInputStream(path.getName());
        java.util.Map<Integer, java.util.Map<Integer, Double>> shard = Utilities.objectFromFile(stream);
        stream.close();
        System.out.println("-> Shard read" + key.get());
      }

      for (IntWritable activeUser: values) {

        // Obtain neighbors
        // Or just supose userId == 1 is the only neighbor

        // If at least one neighbor is accesible from this shard

        // Obtain activeUser non rated values

        // for every non rated item, compute

            // W_item = Similarity (activeuser, currentNeighbor) * Rating (currentNeighbor, currentItem)

            // write results: context.write (activeuser, new PairWritable(itemId, W_item))

      }
      System.out.println("-> End " + key.get());
    }
  }
}
