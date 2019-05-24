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
    static final String NUMBER_OF_SHARDS = "NUMBER_OF_SHARDS";
    private static final String EVALUATION_TYPE = "EVALUATION_TYPE";
    static final String EVALUATION_N_VALUE = "EVALUATION_N_VALUE";
    static final String EVALUATION_SEED = "EVALUATION_SEED";
    static final String SIMILARITY_NEIGHBOR_NUMBER = "SIMILARITY_NEIGHBOR_NUMBER";

    static final String SHARD_NAME_PREFIX = "/input/shards/shard";
    static final String SIMILARITY_NEIGHBOR_PREFIX = "/input/similarities/sorted";

    enum EvaluationType {None, Percentage, givenN, allButN}

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
         * IN  -> strings[5] -> number of similarity files
         * IN  -> strings[6] -> without evaluation / evaluation type
         * IN  -> strings[7] -> evaluation-n-value
         * IN  -> strings[8] -> seed for random generator */
        String inputPathDataset = "";
        String outputResults = "";
        String activeUsersFilePath = "";
        String uniqueItemsFilePath = "";
        Integer numberOfShards = null;
        Integer numberOfSimilarityFiles = null;
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
                numberOfSimilarityFiles = Integer.valueOf(strings[i]);
            } else if (6 == i) {
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
            } else if (8 == i) {
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
        Job job1 = Job.getInstance(new Configuration());
        job1.setJobName("job1");
        job1.setJarByClass(Main.class);

        FileInputFormat.addInputPath(job1, new Path(outputResults + job0.getJobName()));
        FileOutputFormat.setOutputPath(job1, new Path(outputResults + job1.getJobName()));

        job1.getConfiguration().setInt(NUMBER_OF_SHARDS, numberOfShards);
        job1.getConfiguration().setInt(SIMILARITY_NEIGHBOR_NUMBER, numberOfSimilarityFiles);

        job1.setMapperClass(Job1.Map.class);
        job1.setReducerClass(Job1.Reduce.class);

        // Map output types
        job1.setMapOutputKeyClass(IntWritable.class);
        job1.setMapOutputValueClass(ActiveUser.class);

        // Map and Reducer output types
        job1.setOutputKeyClass(IntWritable.class);
        job1.setOutputValueClass(PairWritable.class);

        // Añade shards a DistributedCache
        for (int j=0; j<numberOfShards; j++) {
            job1.addCacheFile(new Path(SHARD_NAME_PREFIX + j).toUri());
        }

        // Añade vecinos a DistributedCache
        for (int j=0; j<numberOfSimilarityFiles; j++) {
            job1.addCacheFile(new Path(
                    SIMILARITY_NEIGHBOR_PREFIX + j)
                    .toUri());
        }

        // Añade similaridades a DistributedCache
        for (int j=0; j<numberOfSimilarityFiles; j++) {
            job1.addCacheFile(new Path(
                    "/input/similarities/similarity" + j)
                    .toUri());
        }

        job1.setNumReduceTasks(numberOfShards);

        res = job1.waitForCompletion(true) ? 0: 1;
        if (res != 0) {
            return res;
        }

        /* ********************* *
         * JOB 2: sums weights and sort recommendations
         * ********************* */
        Job job2 = Job.getInstance(new Configuration());
        job2.setJobName("job2");
        job2.setJarByClass(Main.class);

        FileInputFormat.addInputPath(job2, new Path(outputResults + job1.getJobName()));
        FileOutputFormat.setOutputPath(job2, new Path(outputResults + job2.getJobName()));

        job2.getConfiguration().setInt(NUMBER_OF_SHARDS, numberOfShards);

        job2.setMapperClass(Job2.Map.class);
        job2.setReducerClass(Job2.Reduce.class);

        // Mapper output types
        job2.setMapOutputKeyClass(IntWritable.class);
        job2.setMapOutputValueClass(TripleWritable.class);

        // Mapper and Reducer output types
        job2.setOutputKeyClass(IntWritable.class);
        job2.setOutputValueClass(Text.class);

        job2.setNumReduceTasks(numberOfShards);

        res = job2.waitForCompletion(true) ? 0: 1;
        if (res != 0) {
            return res;
        }

        /* ********************* *
         * JoinData: puts all results into one file
         * ********************* */
        Job jobJoinData = Job.getInstance(new Configuration());
        jobJoinData.setJobName("jobJoinData");
        jobJoinData.setJarByClass(Main.class);

        FileInputFormat.addInputPath(jobJoinData, new Path(outputResults + job2.getJobName()));
        FileOutputFormat.setOutputPath(jobJoinData, new Path(outputResults));

        jobJoinData.setMapperClass(JobJoinData.Map.class);
        jobJoinData.setReducerClass(JobJoinData.Reduce.class);

        // Mapper output types
        jobJoinData.setMapOutputKeyClass(IntWritable.class);
        jobJoinData.setMapOutputValueClass(Text.class);

        // Mapper and Reducer output types
        jobJoinData.setOutputKeyClass(Text.class);
        jobJoinData.setOutputValueClass(Text.class);

        jobJoinData.setNumReduceTasks(1);

        return jobJoinData.waitForCompletion(true) ? 0: 1;
    }
}
