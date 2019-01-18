package hadoop;

import common.stream.StreamIn;
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
import java.util.List;

public class Job1 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    private int shardsNumber;

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

    private int shardsNumber;
    private List<Integer> userIdsOrder;
    private List<Integer> kNeighbors;
    private List<Integer> compressed;

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

      // Get current shard assigned number
      shardsNumber = Integer.valueOf(context.getConfiguration().get(Main.SHARDS_NUMBER));

      // Prepare neighborhood access
      FileInputStream inputStream = new FileInputStream(new Path(context.getCacheFiles()[Main.CachedData.userIdsOrder.ordinal()]).getName());
      userIdsOrder = new StreamIn.DeltaStreamIn().read(inputStream);
      inputStream.close();

      kNeighbors = new StreamIn.DeltaStreamIn().read(
          new FileInputStream(new Path(context.getCacheFiles()[Main.CachedData.kNeighbors.ordinal()]).getName())
      );

      compressed = new StreamIn.DeltaStreamIn().read(
          new FileInputStream(new Path(context.getCacheFiles()[Main.CachedData.compressed.ordinal()]).getName())
      );
    }

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

      //System.out.println("-> Start " + key.get());

      // Prepare rating matrix
      if (null != context.getCacheFiles() && context.getCacheFiles().length > 0) {
        //System.out.println("-> Open shard at " + key.get());
        Path path = new Path(context.getCacheFiles()[key.get()]);
        FileInputStream stream = new FileInputStream(path.getName());
        java.util.Map<Integer, java.util.Map<Integer, Double>> shard = Utilities.objectFromFile(stream);
        stream.close();
        //System.out.println("-> Shard read" + key.get());
      }

      /**
       * Manage reassigned Ids:
       *    Encode: list.indexOf (rawId)
       *    Decode: list.get(Id)
       * Example:
       *    compressed.indexOf (999) yields 3
       *    compressed.get(3) yields 999
       */
      for (IntWritable activeUser: values) {


        // Find active user neighborhood
        for (int i=0; i<kNeighbors.size(); i++) {

          if (kNeighbors.get(i) == 1000000) {
            //System.out.println(" ->    row");
            continue; // row delimiter
          }

          //System.out.print(" " + kNeighbors.get(i));
        }
        //System.out.println("-> Active user: " + activeUser.get());

        break; // DEBUG - Stops after the first active user

        // This active user has the following neighbors

        // If at least one neighbor is accesible from this shard

        // Obtain activeUser non rated values

        // for every non rated item, compute

            // W_item = Similarity (activeuser, currentNeighbor) * Rating (currentNeighbor, currentItem)

            // write results: context.write (activeuser, new PairWritable(itemId, W_item))

      }
      //System.out.println("-> End " + key.get());
    }
  }
}
