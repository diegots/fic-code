package tfg.hadoop.generate.unique.items;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Main extends Configured implements Tool {

  public static final String ALL_ITEMS_PATH = "ALL_ITEMS_PATH";

  public static void main(String[] args)
      throws Exception {

    ToolRunner.run(new Configuration(), new Main(), args);
  }

  @Override
  public int run(String[] strings) throws Exception {

    /* **********
     * **********
     * JOB 0
     * **********
     * ********** */

    /*
     * Expected params
     * IN  -> strings[0] -> dataset path
     * OUT -> strings[1] -> output path
     * IN  -> strings[2] -> number of splits */
    Job job0 = Job.getInstance(getConf());
    job0.setJobName("job0");
    job0.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job0.setMapperClass(Job0.Map.class);
    job0.setReducerClass(Job0.Reduce.class);

    // Map and Reducer output types
    job0.setOutputKeyClass(IntWritable.class);
    job0.setOutputValueClass(IntWritable.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job0, new Path(strings[0]));
    FileOutputFormat.setOutputPath(job0, new Path(strings[1]+"j0"));

    // Set number of reduce tasks
    job0.setNumReduceTasks(Integer.valueOf(strings[2]));

    // Run this job
    int result = job0.waitForCompletion(true) ? 0: 1;
    if (0 != result) {
      return result;
    }

    /* **********
     * **********
     * JOB 1
     * **********
     * ********** */
    Job job1 = Job.getInstance(getConf());
    job1.setJobName("job1");
    job1.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job1.setMapperClass(Job1.Map.class);
    job1.setReducerClass(Job1.Reduce.class);

    // Map and Reducer output types
    job1.setOutputKeyClass(Text.class);
    job1.setOutputValueClass(Text.class);

    // Map output types
    job1.setMapOutputKeyClass(LongWritable.class);
    job1.setMapOutputValueClass(Text.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job1, new Path(strings[1]+"j0"));
    FileOutputFormat.setOutputPath(job1, new Path(strings[1]+"j1"));

    // Set number of reduce tasks
    job1.setNumReduceTasks(1);

    result = job1.waitForCompletion(true) ? 0: 1;
    if (0 != result) {
      return result;
    }

    /* **********
     * **********
     * JOB 2
     * **********
     * ********** */

    Configuration conf = getConf();
    conf.set(ALL_ITEMS_PATH, strings[1]+"j1");

    Job job2 = Job.getInstance(conf);
    job2.setJobName("job2");
    job2.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job2.setMapperClass(Job2.Map.class);
    job2.setReducerClass(Job2.Reduce.class);

    // Map and Reducer output types
    job2.setOutputKeyClass(IntWritable.class);
    job2.setOutputValueClass(Text.class);

    // Map output types
    job2.setMapOutputKeyClass(IntWritable.class);
    job2.setMapOutputValueClass(IntWritable.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job2, new Path(strings[0]));
    FileOutputFormat.setOutputPath(job2, new Path(strings[1]));

    // Set number of reduce tasks
    job2.setNumReduceTasks(1);

    //job0.addCacheFile(new Path(cachedShards[shardFileName.ordinal()]).toUri());
    job2.addCacheFile(new Path("/input/active-user.csv").toUri());

    return job2.waitForCompletion(true) ? 0: 1;
  }
}
