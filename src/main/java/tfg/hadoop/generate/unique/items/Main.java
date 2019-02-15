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

    /* Expected params
     * IN  -> strings[0] -> dataset path
     * OUT -> strings[1] -> output path
     * IN  -> strings[2] -> number of splits */
    Configuration conf = getConf();


    /* ********************* *
     * JOB 0:
     * ********************* */
    Job job0 = Job.getInstance(conf);
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


    /* ********************* *
     * JOB 1:
     * ********************* */
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
    FileOutputFormat.setOutputPath(job1, new Path(strings[1]));

    // Set number of reduce tasks
    job1.setNumReduceTasks(1);

    return job1.waitForCompletion(true) ? 0: 1;
  }
}
