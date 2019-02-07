package tfg.hadooprec;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import tfg.hadooprec.model.ActiveUser;
import tfg.hadooprec.types.PairWritable;

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

    /*
     * Expected params
     * IN  -> strings[0] -> active users list
     * OUT -> strings[1] -> computed recommendations
     * IN  -> strings[2] -> number of shards */
    Configuration conf = getConf();
    conf.setInt(SHARDS_NUMBER, Integer.valueOf(strings[2]));

    Job job1 = Job.getInstance(conf);
    job1.setJobName("job0");
    job1.setJarByClass(tfg.hadooprec.Main.class);

    // Mapper and Reducer classes
    job1.setMapperClass(Job1.Map.class);
    job1.setReducerClass(Job1.Reduce.class);

    // Map output types
    job1.setMapOutputKeyClass(IntWritable.class);
    job1.setMapOutputValueClass(ActiveUser.class);

    // Map and Reducer output types
    job1.setOutputKeyClass(IntWritable.class);
    job1.setOutputValueClass(PairWritable.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job1, new Path(strings[0]));
    FileOutputFormat.setOutputPath(job1, new Path(strings[1]+"j0"));

    // Add files to Distributed Cache
    for (CachedShards filename: CachedShards.values()) {
      job1.addCacheFile(new Path(cachedShards[filename.ordinal()]).toUri());
    }

    for (CachedSimilarities filename: CachedSimilarities.values()) {
      job1.addCacheFile(new Path(cachedSimilarities[filename.ordinal()]).toUri());
    }

    // Set number of reduce tasks
    job1.setNumReduceTasks(3);

    // Run this job
    return job1.waitForCompletion(true) ? 0: 1;
  }
}
