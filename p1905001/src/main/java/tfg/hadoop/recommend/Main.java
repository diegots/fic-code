package tfg.hadoop.recommend;

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

import java.util.Random;

public class Main extends Configured implements Tool {

    static final String ACTIVE_USERS_FILE_PATH = "ACTIVE_USERS_FILE_PATH";
    static final String UNIQUE_ITEMS_FILE_PATH = "UNIQUE_ITEMS_PATH";
    static final String SHARDS_NUMBER = "SHARDS_NUMBER";
    static final String EVALUATION_RATIO = "EVALUATION_RATIO";
    static final String EVALUATION_N = "EVALUATION_N";
    static final String EVALUATION_SEED = "EVALUATION_SEED";

    static Random generator;

    static final String shardNamePrefix = "/input/shards/shard";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new Main(), args);
    }

    @Override
    public int run(String[] strings) throws Exception {

        /* Expected params
         * IN  -> strings[0] -> dataset dir
         * OUT -> strings[1] -> computed recommendations
         * IN  -> strings[2] -> active users file path
         * IN  -> strings[3] -> unique items file path
         * IN  -> strings[4] -> number of shards */
        Configuration conf = getConf();
        conf.set(ACTIVE_USERS_FILE_PATH, strings[2]);
        conf.set(UNIQUE_ITEMS_FILE_PATH, strings[3]);
        conf.setInt(SHARDS_NUMBER, Integer.valueOf(strings[4]));

        /* ********************* *
         * JOB 0: generates a file with non rated items for every active user
         * ********************* */
        Job job0 = Job.getInstance(conf);
        job0.setJobName("job0");
        job0.setJarByClass(Main.class);

        // Mapper and Reducer classes
        job0.setMapperClass(Job0.Map.class);

        // Manage reduce type on job 0 for evaluation purposes
        // strings[5] job0 reduce type: Std, Percentage, GivenN, AllButN
        if ("Std".equals(strings[5])) {
            job0.setReducerClass(Job0.ReduceStd.class);

        } else {

            generator = new Random(Long.valueOf(strings[7]));

            if ("-percentage".equals(strings[5])) {
                job0.setReducerClass(Job0.ReducePercentage.class);
                conf.setFloat(EVALUATION_RATIO, Float.valueOf(strings[6]));

            } else if ("-givenN".equals(strings[5])) {
                job0.setReducerClass(Job0.ReduceGivenN.class);
                conf.setInt(EVALUATION_N, Integer.valueOf(strings[7]));

            } else if ("-allButN".equals(strings[5])) {
                job0.setReducerClass(Job0.ReduceAllButN.class);
                conf.setInt(EVALUATION_N, Integer.valueOf(strings[7]));
            }
        }

        // Map and Reducer output types
        job0.setOutputKeyClass(IntWritable.class);
        job0.setOutputValueClass(Text.class);

        // Map output types
        job0.setMapOutputKeyClass(IntWritable.class);
        job0.setMapOutputValueClass(IntWritable.class);

        // Input and output dirs for this job
        FileInputFormat.addInputPath(job0, new Path(strings[0]));
        FileOutputFormat.setOutputPath(job0, new Path(strings[1] + "recomm-j0"));

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


        /* ********************* *
         * JOB 2: sums weights and sort recommendations
         * ********************* */


        /* ********************* *
         * JoinData: puts all results into one file
         * ********************* */


        return 0;
    }
}
