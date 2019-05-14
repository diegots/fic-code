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

public class Main extends Configured implements Tool {

    static final String ACTIVE_USERS_FILE_PATH = "ACTIVE_USERS_FILE_PATH";
    static final String UNIQUE_ITEMS_FILE_PATH = "UNIQUE_ITEMS_FILE_PATH";
    private static final String EVALUATION_TYPE = "EVALUATION_TYPE";
    static final String EVALUATION_N_VALUE = "EVALUATION_N_VALUE";
    static final String EVALUATION_SEED = "EVALUATION_SEED";

    enum EvaluationType {None, Percentage, givenN, allButN}


//    static final String ACTIVE_USERS_FILE_PATH = "ACTIVE_USERS_FILE_PATH";
//    static final String UNIQUE_ITEMS_FILE_PATH = "UNIQUE_ITEMS_PATH";
//    static final String SHARDS_NUMBER = "SHARDS_NUMBER";
//    static final String EVALUATION_N_VALUE = "EVALUATION_N_VALUE";
//    static final String EVALUATION_SEED = "EVALUATION_SEED";
//    static final String shardNamePrefix = "/input/shards/shard";

    public static void main(String[] args) throws Exception {
        ToolRunner.run(new Configuration(), new Main(), args);
    }

    @Override
    public int run(String[] strings) throws Exception {

        /* Parameters extraction:
         *
         * IN  -> strings[0] -> <input-path-dataset>
         * OUT -> strings[1] -> <output-results>
         * IN  -> strings[2] -> active users file path
         * IN  -> strings[3] -> unique items file path
         * IN  -> strings[4] -> number of shards
         * IN  -> strings[5] -> without evaluation / evaluation type
         * IN  -> strings[6] -> seed for random generator */
        String inputPathDataset = "";
        String outputResults = "";
        String activeUsersFilePath = "";
        String uniqueItemsFilePath = "";
        Integer numberOfShards;
        EvaluationType evaluationType = null;
        float evaluationNValue = 0.0f;
        int evaluationSeed = 0;

        int i=0;
        while (i<strings.length) {
            if (0 == i) {
                inputPathDataset = strings[i];
            } else if (1 == i) {
                outputResults = strings[i];
            } else if (2 == i) {
                activeUsersFilePath = strings[i];
            } else if (3 == i) {
                uniqueItemsFilePath = strings[i];
            } else if (4 == i) {
                numberOfShards = Integer.valueOf(strings[i]);
            } else if (5 == i) {
                if ("-none".equals(strings[i])) {
                    evaluationType = EvaluationType.None;
                } else if ("-percentage".equals(strings[i])) {
                    evaluationType = EvaluationType.Percentage;
                    evaluationNValue = Float.valueOf(strings[++i]);
                } else if ("-givenn".equals(strings[i])) {
                    evaluationType = EvaluationType.givenN;
                    evaluationNValue = Float.valueOf(strings[++i]);
                } else if ("-allbutn".equals(strings[i])) {
                    evaluationType = EvaluationType.allButN;
                    evaluationNValue = Float.valueOf(strings[++i]);
                }
            } else if (7 == i) {
                evaluationSeed = Integer.valueOf(strings[i]);
            }
            i++;
        }

        /* ********************* *
         * JOB 0: generates a file with non rated items for every active user
         * ********************* */
        Job job0 = Job.getInstance(getConf());
        job0.setJobName("job0");
        job0.setJarByClass(Main.class);

        FileInputFormat.addInputPath(job0, new Path(inputPathDataset));
        FileOutputFormat.setOutputPath(job0, new Path(outputResults + job0.getJobName()));

        job0.getConfiguration().set(ACTIVE_USERS_FILE_PATH, activeUsersFilePath);
        job0.getConfiguration().set(UNIQUE_ITEMS_FILE_PATH, uniqueItemsFilePath);
        job0.getConfiguration().setInt(EVALUATION_TYPE, evaluationType.ordinal());
        job0.getConfiguration().setFloat(EVALUATION_N_VALUE, evaluationNValue);
        job0.getConfiguration().setInt(EVALUATION_SEED, evaluationSeed);

        // Mapper and Reducer classes
        job0.setMapperClass(Job0.Map.class);

        switch (evaluationType) {
            case None:
                job0.setReducerClass(Job0.ReduceNone.class);
                break;
            case Percentage:
                job0.setReducerClass(Job0.ReducePercentage.class);
                break;
            case givenN:
                job0.setReducerClass(Job0.ReduceGivenN.class);
                break;
            case allButN:
                job0.setReducerClass(Job0.ReduceAllButN.class);
                break;
        }

        // Map output types
        job0.setMapOutputKeyClass(IntWritable.class);
        job0.setMapOutputValueClass(IntWritable.class);

        // Map and Reducer output types
        job0.setOutputKeyClass(IntWritable.class);
        job0.setOutputValueClass(Text.class);

        // Set number of reduce tasks
        job0.setNumReduceTasks(1);

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
