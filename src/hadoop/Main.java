package hadoop;

import hadoop.types.PairWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

  public static final String SHARDS_NUMBER = "SHARDS_NUMBER";

  enum cachedData {
    ratingMatrix0, ratingMatrix1, ratingMatrix2,
    orderIds, reassignedIds, compressed
  }

  static final String[] cachedPaths = {
      "/cached/rating-matrix0",
      "/cached/rating-matrix1",
      "/cached/rating-matrix2",
      "/cached/orderIds",
      "/cached/reassignedIds",
      "/cached/compressed"
  };

  public static void main(String[] args)
      throws IOException, ClassNotFoundException, InterruptedException {

    // Expected params
    // args[0] -> userIds to whom compute recommendations
    // args[1] -> number of shards

    Configuration conf = new Configuration();
    conf.set(SHARDS_NUMBER, args[1]);
    
    Job job1 = Job.getInstance(conf);
    job1.setJobName("job1");
    job1.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job1.setMapperClass(Job1.Map.class);
    job1.setReducerClass(Job1.Reduce.class);

    // Map output types
    job1.setMapOutputKeyClass(IntWritable.class);
    job1.setMapOutputValueClass(IntWritable.class);

    // Map and Reducer output types
    job1.setOutputKeyClass(IntWritable.class);
    job1.setOutputValueClass(PairWritable.class);

    // Input and output dirs for this job
    final String ACTIVE_USERS_FILE_PATH = args[0];
    FileInputFormat.addInputPath(job1, new Path(ACTIVE_USERS_FILE_PATH));
    FileOutputFormat.setOutputPath(job1, new Path("/" + job1.getJobName()+"-out"));

    // Adds rating matrix shards to Distributed Cache
    job1.addCacheFile(new Path(cachedPaths[cachedData.ratingMatrix0.ordinal()]).toUri());
    job1.addCacheFile(new Path(cachedPaths[cachedData.ratingMatrix1.ordinal()]).toUri());
    job1.addCacheFile(new Path(cachedPaths[cachedData.ratingMatrix2.ordinal()]).toUri());

    // Adds neighborhood similarities to Distributed Cache
    job1.addCacheFile(new Path(cachedPaths[cachedData.orderIds.ordinal()]).toUri());
    job1.addCacheFile(new Path(cachedPaths[cachedData.reassignedIds.ordinal()]).toUri());
    job1.addCacheFile(new Path(cachedPaths[cachedData.compressed.ordinal()]).toUri());

    // Set number of reduce tasks
    job1.setNumReduceTasks(3);

    // Run this job
    int result = job1.waitForCompletion(true) ? 0: 1;
    if (result == 1) {
      System.exit(result);
    }
  }
}
