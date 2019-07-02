package tfg.hadoop.generate.unique.items;

import tfg.hadoop.util.MapFileUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main extends Configured implements Tool {

  public static final String MAX_ITEMID = "MAX_ITEMID";

  public static void main(String[] args)
      throws Exception {

    ToolRunner.run(new Configuration(), new Main(), args);
  }

  @Override
  public int run(String[] strings) throws Exception {

    showHelp();

    Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).setLevel(Level.WARNING);
    Logger logger = Logger.getLogger(Main.class.getName());
    Level level = Level.INFO;

    /* Expected params
     * IN  -> strings[0] -> dataset path
     * OUT -> strings[1] -> output path
     * IN  -> strings[2] -> number of splits */


    /* ********************* *
     * MaxItemId
     * ********************* */
    Job maxItemId = Job.getInstance(getConf());
    maxItemId.setJobName("mastringsxItemId");
    maxItemId.setJarByClass(Main.class);

    // Mapper and Reducer classes
    maxItemId.setMapperClass(MaxItemId.Map.class);
    maxItemId.setReducerClass(MaxItemId.Reduce.class);

    // Map and Reducer output types
    maxItemId.setOutputKeyClass(IntWritable.class);
    maxItemId.setOutputValueClass(Text.class);

    // Map output types
    maxItemId.setMapOutputKeyClass(IntWritable.class);
    maxItemId.setMapOutputValueClass(IntWritable.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(maxItemId, new Path(strings[0]));
    FileOutputFormat.setOutputPath(maxItemId, new Path(strings[1]+"maxItemId"));

    // Set number of reduce tasks
    maxItemId.setNumReduceTasks(1);

    // Run this job
    int result = maxItemId.waitForCompletion(true) ? 0: 1;
    if (0 != result) {
      return result;
    }


    /* ********************* *
     * JOB 0:
     * ********************* */
    Configuration conf = getConf();

    List<FileStatus> lf = MapFileUtil.getNonEmptyFilesInHDFSFolder(strings[1]+"maxItemId");
    FileStatus actualFile = lf.get(0);
    List<Integer> values = MapFileUtil.readFileInHDFS(actualFile);
    conf.setInt(MAX_ITEMID, values.get(0));
    logger.log(level, "Read Max Item Id: " + values.get(0) + " , values read: " + values.size());

    Job job0 = Job.getInstance(conf);
    job0.setJobName("job0");
    job0.setJarByClass(Main.class);

    // Mapper and Reducer classes
    job0.setMapperClass(Job0.Map.class);
    job0.setReducerClass(Job0.Reduce.class);

    // Map output types
    job0.setMapOutputKeyClass(IntWritable.class);
    job0.setMapOutputValueClass(IntWritable.class);

    // Map and Reducer output types
    job0.setOutputKeyClass(IntWritable.class);
    job0.setOutputValueClass(NullWritable.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job0, new Path(strings[0]));
    FileOutputFormat.setOutputPath(job0, new Path(strings[1]+"j0"));

    // Set number of reduce tasks
    job0.setNumReduceTasks(Integer.valueOf(strings[2]));

    // Run this job
    result = job0.waitForCompletion(true) ? 0: 1;
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
    job1.setMapOutputKeyClass(IntWritable.class);
    job1.setMapOutputValueClass(Text.class);

    // Input and output dirs for this job
    FileInputFormat.addInputPath(job1, new Path(strings[1]+"j0"));
    FileOutputFormat.setOutputPath(job1, new Path(strings[1]));

    // Set number of reduce tasks
    job1.setNumReduceTasks(1);

    return job1.waitForCompletion(true) ? 0: 1;
  }


  void showHelp() {

    System.out.println
            ( "* ************************************ *\n"
            + "*                                      *\n"
            + "* This is a HADOOP program that        *\n"
            + "* that COMPUTES a list of UNIQUE ITEMS *\n"
            + "* and the MAX USER ID                  *\n"
            + "* within a recommendation dataset      *\n"
            + "*                                      *\n"
            + "* ************************************ *");
  }
}
