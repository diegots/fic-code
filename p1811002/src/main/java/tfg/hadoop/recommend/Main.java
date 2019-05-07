package tfg.hadoop.recommend;

import tfg.hadoop.recommend.model.ActiveUser;
import tfg.hadoop.recommend.model.PairWritable;
import tfg.hadoop.recommend.model.TripleWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Configured implements Tool {

  public static final String ACTIVE_USERS_FILE_PATH = "ACTIVE_USERS_FILE_PATH";
  public static final String DECIMAL_PLACES = "DECIMAL_PLACES";
  public static final String RECS_NUMBER = "RECS_NUMBER";
  public static final String SHARDS_NUMBER = "SHARDS_NUMBER";
  public static final String UNIQUE_ITEMS_FILE_PATH = "UNIQUE_ITEMS_PATH";

  static String[] cachedShards;
  static final List<String> cachedddShards = new ArrayList<>();

  enum CachedSimilarities {
    encodedUserIds,
    encodedUsersKNeighbors,
    plainFrequencyTable,
    encodedReassignedSimMat
  }

  static final String[] cachedSimilarities = {
      "/input/similarities/encoded.user.ids",
      "/input/similarities/encoded.users.k.neighbors",
      "/input/similarities/plain.frequency.table",
      "/input/similarities/encoded.reassigned.sim.mat"
  };

  static final String shardNamePrefix = "/input/shards/shard";

  public static void main(String[] args)
      throws Exception {

    ToolRunner.run(new Configuration(), new Main(), args);
  }

  @Override
  public int run(String[] strings) throws Exception {

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.WARNING);

    /* Expected params
     * IN  -> strings[0] -> dataset dir
     * OUT -> strings[1] -> computed recommendations
     * IN  -> strings[2] -> active users file path
     * IN  -> strings[3] -> unique items file path
     * IN  -> strings[4] -> number of shards */
    Configuration conf = getConf();
    conf.set(ACTIVE_USERS_FILE_PATH, strings[2]);
    conf.set(UNIQUE_ITEMS_FILE_PATH, strings[3]);
    Integer shardsNumber =  Integer.valueOf(strings[4]);
    conf.setInt(SHARDS_NUMBER, shardsNumber);


    /* ********************* *
     * JOB 0: generates a file with non rated items for every active user
     * ********************* */
    Job job0 = Job.getInstance(conf);
    job0.setJobName("job0");
    job0.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job0.setMapperClass(Job0.Map.class);
    job0.setReducerClass(Job0.Reduce.class);

    // Map and Reducer output types
    job0.setOutputKeyClass(IntWritable.class);
    job0.setOutputValueClass(Text.class);

    // Map output types
    job0.setMapOutputKeyClass(IntWritable.class);
    job0.setMapOutputValueClass(IntWritable.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job0, new Path(strings[0]));
    FileOutputFormat.setOutputPath(job0, new Path(strings[1]+"recomm-j0"));

    // Set number of reduce tasks
    job0.setNumReduceTasks(1);

    job0.addCacheFile(new Path(strings[2]).toUri());
    job0.addCacheFile(new Path(strings[3]).toUri());

    int res = job0.waitForCompletion(true) ? 0: 1;
    if (res != 0) {
      return res;
    }


    /* ********************* *
     * JOB 1: computes recommendations products part
     * ********************* */
    Job job1 = Job.getInstance(conf);
    job1.setJobName("job1");
    job1.setJarByClass(Main.class);

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
    FileInputFormat.addInputPath(job1, new Path(strings[1]+"recomm-j0"));
    FileOutputFormat.setOutputPath(job1, new Path(strings[1]+"recomm-j1"));

    // Add files to Distributed Cache
    for (int i=0; i<shardsNumber; i++) {
      job1.addCacheFile(new Path(shardNamePrefix+i).toUri());
    }

    for (CachedSimilarities filename: CachedSimilarities.values()) {
      job1.addCacheFile(new Path(cachedSimilarities[filename.ordinal()]).toUri());
    }

    // Set number of reduce tasks
    job1.setNumReduceTasks(shardsNumber);

    // Run this job
    res = job1.waitForCompletion(true) ? 0: 1;
    if (res != 0) {
      return res;
    }


    /* ********************* *
     * JOB 2: sums weights and sort recommendations
     * ********************* */
    conf.setInt(DECIMAL_PLACES, 3);
    conf.setInt(RECS_NUMBER, 5);

    Job job2 = Job.getInstance(conf);
    job2.setJobName("job2");
    job2.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job2.setMapperClass(Job2.Map.class);
    job2.setReducerClass(Job2.Reduce.class);

    // Map output types
    job2.setMapOutputKeyClass(IntWritable.class);
    job2.setMapOutputValueClass(TripleWritable.class);

    // Map and Reducer output types
    job2.setOutputKeyClass(IntWritable.class);
    job2.setOutputValueClass(Text.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job2, new Path(strings[1]+"recomm-j1"));
    FileOutputFormat.setOutputPath(job2, new Path(strings[1]+"recomm-j2"));

    job2.setNumReduceTasks(shardsNumber);

    res = job2.waitForCompletion(true) ? 0: 1;
    if (res != 0) {
      return res;
    }

    /* ********************* *
     * JoinData: puts all results into one file
     * ********************* */
    Job joinData = Job.getInstance(conf);
    joinData.setJobName("joindata");
    joinData.setJarByClass(Main.class);

    // Mapper and Reducer classes
    joinData.setMapperClass(JoinData.Map.class);
    joinData.setReducerClass(JoinData.Reduce.class);

    // Map output types
    joinData.setMapOutputKeyClass(IntWritable.class);
    joinData.setMapOutputValueClass(Text.class);

    // Map and Reducer output types
    joinData.setOutputKeyClass(Text.class);
    joinData.setOutputValueClass(Text.class);

    FileInputFormat.addInputPath(joinData, new Path(strings[1]+"recomm-j2"));
    FileOutputFormat.setOutputPath(joinData, new Path(strings[1]+"recomm"));

    joinData.setNumReduceTasks(1);

    return joinData.waitForCompletion(true) ? 0: 1;
  }
}
