package tfg.hadoop.generate.shards;

import tfg.hadoop.recommend.model.TripleWritable;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class Main extends Configured implements Tool {

    public static final String SHARDS_NUMBER = "SHARDS_NUMBER";
    public static final String SHARDS_DESTINATION = "SHARDS_DESTINATION";
    public static final String SHARDS_PREFIX = "SHARDS_PREFIX";

    public static void main(String[] args)
            throws Exception {

        ToolRunner.run(new Configuration(), new Main(), args);
    }

    @Override
    public int run(String[] strings) throws Exception {

        /* Hadoop job expected params
         * IN  -> strings[0] -> dataset dir
         * OUT -> strings[1] -> shards path destination
         * IN  -> strings[2] -> number of shards to consider
         * IN  -> strings[3] -> shards filename prefix */

        /* ********************* *
         * Job0
         * ********************* */
        Configuration conf = getConf();

        int shardsNumber = Integer.valueOf(strings[2]);
        conf.setInt(SHARDS_NUMBER, shardsNumber);
        conf.set(SHARDS_DESTINATION, strings[1]);
        conf.set(SHARDS_PREFIX, strings[3]);

        Job job0 = Job.getInstance(conf);
        job0.setJobName("job0");
        job0.setJarByClass(Main.class);

        // Mapper and Reducer classes
        job0.setMapperClass(Job0.Map.class);
        job0.setReducerClass(Job0.Reduce.class);

        // Map and Reducer output types
        job0.setOutputKeyClass(NullWritable.class);
        job0.setOutputValueClass(NullWritable.class);

        // Map output types
        job0.setMapOutputKeyClass(IntWritable.class);
        job0.setMapOutputValueClass(TripleWritable.class);

        // Input and output dirs for this job
        FileInputFormat.addInputPath(job0, new Path(strings[0]));
        FileOutputFormat.setOutputPath(job0, new Path(strings[1]));

        // Set number of reduce tasks
        job0.setNumReduceTasks(shardsNumber);

        return job0.waitForCompletion(true) ? 0 : 1;
    }
}