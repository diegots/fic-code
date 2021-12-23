package tfg.hadoop.generate.shards;

import tfg.common.util.Utilities;
import tfg.hadoop.recommend.model.TripleWritable;
import tfg.hadoop.util.Util;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

public class Job0 {
    public static class Map
            extends Mapper<LongWritable, Text, IntWritable, TripleWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

            if (!Util.containsHeader(value.toString())) {
                String [] fields = value.toString().split(",");

                Integer userId = Integer.valueOf(fields[0]);
                int shardsNumber = context.getConfiguration().getInt(Main.SHARDS_NUMBER, -1);
                context.write(
                        new IntWritable(userId % shardsNumber),
                        new TripleWritable(
                                new IntWritable(userId),
                                new IntWritable(Integer.valueOf(fields[1])),
                                new DoubleWritable(Double.valueOf(fields[2])))
                );
            }
        }
    }

    public static class Reduce
            extends Reducer<IntWritable, TripleWritable, NullWritable, NullWritable> {
        @Override
        protected void reduce(IntWritable key, Iterable<TripleWritable> values, Context context)
                throws InterruptedException, IOException {

            java.util.Map<Integer, java.util.Map<Integer, Double>> shardData = new LinkedHashMap<>();

            // Put every rating into userProfile
            for (TripleWritable value: values) {
                java.util.Map<Integer, Double> userProfile = shardData.get(value.getFirst().get());
                if (userProfile == null) {
                    userProfile = new LinkedHashMap<>();
                }
                userProfile.put(value.getSecond().get(), value.getThird().get());
            }

            // Write null to
            context.write(NullWritable.get(), NullWritable.get());

            FileOutputStream fileOutputStream = new FileOutputStream(
                    new Path(
                            context.getConfiguration().get(Main.SHARDS_DESTINATION)
                                    + context.getConfiguration().get(Main.SHARDS_PREFIX)
                                    + key.get()).toUri().getPath());

            Utilities.mapToFile(fileOutputStream, shardData);
            fileOutputStream.close();
        }
    }
}
