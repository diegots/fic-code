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

public class Main extends Configured implements Tool {

  public static final String SHARDS_NUMBER = "SHARDS_NUMBER";

  enum CachedShards {
    shard0,
    shard1,
    shard2,
  }

  static final String[] cachedShards = {
      "/cached/shard0",
      "/cached/shard1",
      "/cached/shard2"
  };

  enum CachedSimilarities {
    encodedUserIds,
    encodedUsersKNeighbors,
    plainFrequencyTable,
    encodedReassignedSimMat
  }

  static final String[] cachedSimilarities = {
      "/cached/encoded.user.ids",
      "/cached/encoded.users.k.neighbors",
      "/cached/plain.frequency.table",
      "/cached/encoded.reassigned.sim.mat"
  };

  public static void main(String[] args)
      throws Exception {

    ToolRunner.run(new Configuration(), new Main(), args);
  }

  @Override
  public int run(String[] strings) throws Exception {

    // Expected params
    // strings[0] -> userIds to whom compute recommendations
    // strings[1] -> number of shards

    Configuration conf = getConf();
    conf.setInt(SHARDS_NUMBER, Integer.valueOf(strings[1]));

    final String job0Name = "job0";
    final String job0OutPath = "/" + job0Name + "-out";
    final int job0NumberReducers = 3;

    Job job0 = Job.getInstance(conf);
    job0.setJobName(job0Name);
    job0.setJarByClass(tfg.hadoop.Main.class);

    // Mapper and Reducer classes
    job0.setMapperClass(PrepareActiveUser.Map.class);
    job0.setReducerClass(PrepareActiveUser.Reduce.class);

    // Map output types
    job0.setMapOutputKeyClass(IntWritable.class);
    job0.setMapOutputValueClass(IntWritable.class);

    // Map and Reducer output types
    job0.setOutputKeyClass(IntWritable.class);
    job0.setOutputValueClass(PairWritable.class);

    // Input and output dirs for this job
    final String ACTIVE_USERS_FILE_PATH = strings[0];
    FileInputFormat.addInputPath(job0, new Path(ACTIVE_USERS_FILE_PATH));
    FileOutputFormat.setOutputPath(job0, new Path(job0OutPath));

    // Add files to Distributed Cache
    for (CachedShards shardFileName: CachedShards.values()) {
      job0.addCacheFile(new Path(cachedShards[shardFileName.ordinal()]).toUri());
    }

    // Set number of reduce tasks
    job0.setNumReduceTasks(job0NumberReducers);

    // Run this job
    return job0.waitForCompletion(true) ? 0: 1;
  }
}
