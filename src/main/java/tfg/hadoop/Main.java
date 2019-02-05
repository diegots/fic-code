package tfg.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import tfg.hadoop.types.PairWritable;

import java.io.IOException;

public class Main extends Configured implements Tool {

  public static final String SHARDS_NUMBER = "SHARDS_NUMBER";

  enum CachedData {
    shard0, shard1, shard2,
    encodedUserIds,
    encodedUsersKNeighbors,
    plainFrequencyTable,
    encodedReassignedSimMat
  }

  static final String[] cachedPaths = {
      "/cached/shard0",
      "/cached/shard1",
      "/cached/shard2",
      "/cached/encoded.user.ids",
      "/cached/encoded.users.k.neighbors",
      "/cached/plain.frequency.table",
      "/cached/encoded.reassigned.sim.mat"
  };

  public static void main(String[] args)
      throws Exception {

    Configuration conf = new Configuration();
    ToolRunner.run(conf, new Main(), args);
  }

  @Override
  public int run(String[] strings) throws Exception {

    // Expected params
    // strings[0] -> userIds to whom compute recommendations
    // strings[1] -> number of shards

    Configuration conf = getConf();
    conf.setInt(SHARDS_NUMBER, Integer.valueOf(strings[1]));

    Job job1 = Job.getInstance(conf);
    job1.setJobName("job1");
    job1.setJarByClass(tfg.hadoop.Main.class);

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
    final String ACTIVE_USERS_FILE_PATH = strings[0];
    FileInputFormat.addInputPath(job1, new Path(ACTIVE_USERS_FILE_PATH));
    FileOutputFormat.setOutputPath(job1, new Path("/" + job1.getJobName()+"-out"));

    // Add files to Distributed Cache
    for (CachedData fileName: CachedData.values()) {
      System.out.println("ordinal: " + fileName.ordinal()
          + ", cachedPath: " + cachedPaths[fileName.ordinal()]);
      job1.addCacheFile(new Path(cachedPaths[fileName.ordinal()]).toUri());
    }

    // Set number of reduce tasks
    job1.setNumReduceTasks(3);

    // Run this job
    return job1.waitForCompletion(true) ? 0: 1;
  }
}
