package hadoop;

import hadoop.model.PairWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapred.join.TupleWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class Main {

  public static final String SHARDS_NUMBER = "SHARDS_NUMBER";

  public static void main(String[] args)
      throws IOException, ClassNotFoundException, InterruptedException {

    Configuration conf = new Configuration();
    conf.set(SHARDS_NUMBER, args[1]);
    
    Job job1 = Job.getInstance(conf);
    job1.setJobName("job_1");
    job1.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job1.setMapperClass(Job1.Map.class);
    job1.setReducerClass(Job1.Reduce.class);

    // Mapper and Reducer output types
    job1.setMapOutputKeyClass(IntWritable.class);
    job1.setMapOutputValueClass(TupleWritable.class);

    // Mapper output types
    job1.setMapOutputKeyClass(IntWritable.class);
    job1.setMapOutputValueClass(PairWritable.class);

    // Input and output dirs for this job
    final String ACTIVE_USERS_FILE_PATH = args[0];
    FileInputFormat.addInputPath(job1, new Path(ACTIVE_USERS_FILE_PATH));
    FileOutputFormat.setOutputPath(job1, new Path(ACTIVE_USERS_FILE_PATH+"out"));

    // Run this job
    int result = job1.waitForCompletion(true) ? 0: 1;
    if (result == 1) {
      System.exit(result);
    }
  }
}
