package tfg.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import tfg.hadoop.types.PairWritable;

import java.io.IOException;

public class Main {

  public static final String SHARDS_NUMBER = "SHARDS_NUMBER";

  enum CachedData {
    ratingMatrix0, ratingMatrix1, ratingMatrix2,
    userIdsOrder, kNeighbors, compressed
  }

  static final String[] cachedPaths = {
      "/cached/rating-matrix0",
      "/cached/rating-matrix1",
      "/cached/rating-matrix2",
      "/cached/userIdsOrder",
      "/cached/kNeighbors",
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

    // Add files to Distributed Cache
    for (CachedData fileName: CachedData.values()) {
      System.out.println("ordinal: " + fileName.ordinal()
        + ", cachedPath: " + cachedPaths[fileName.ordinal()]);
      Path p = new Path(cachedPaths[fileName.ordinal()]);
      job1.addCacheFile(p.toUri());
    }

    // Set number of reduce tasks
    job1.setNumReduceTasks(3);

    // Run this job
    int result = job1.waitForCompletion(true) ? 0: 1;
    if (result == 1) {
      System.exit(result);
    }
  }
}
