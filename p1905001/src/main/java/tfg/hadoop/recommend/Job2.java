package tfg.hadoop.recommend;

import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.shaded.org.apache.commons.math3.util.Precision;

import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.TreeMap;

public class Job2 {

    public static class Map
        extends Mapper<LongWritable, Text, IntWritable, TripleWritable> {
        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] values = value.toString().split("\t");

            context.write(
                    new IntWritable(Integer.valueOf(values[0]) % context.getConfiguration().getInt(Main.NUMBER_OF_SHARDS, 0)),
                    new TripleWritable(
                            new IntWritable(Integer.valueOf(values[0])), // active user
                            new IntWritable(Integer.valueOf(values[1])), // item id
                            new DoubleWritable(Double.valueOf(values[2])) // weight
                    )
            );
        }
    }

    public static class Reduce
        extends Reducer<IntWritable, TripleWritable, IntWritable, Text> {
        @Override
        protected void reduce(IntWritable key, Iterable<TripleWritable> values, Context context) throws IOException, InterruptedException {

            java.util.Map<Integer, java.util.Map<Double, Integer>> result = new HashMap<>();

            for (TripleWritable value: values) { // traverse all values user by user
                int activeUserId = value.getFirst().get();
                int itemId = value.getSecond().get();
                double weight = value.getThird().get();

                if (result.containsKey(activeUserId)) { // if this user has data already stored

                    // if this item has data already stored
                    if (result.get(activeUserId).containsValue(itemId)) {

                        for (java.util.Map.Entry<Double, Integer> entry: result.get(activeUserId).entrySet()) {
                            if (entry.getValue().equals(itemId)) {
                                result.get(activeUserId).put(
                                        entry.getKey() + weight,
                                        itemId
                                );
                                break;
                            }
                        }


                    } else {
                        result.get(activeUserId).put(weight, itemId);
                    }

                } else { // first time an user is treated
                    java.util.Map<Double, Integer> aux = new TreeMap<>(new Comparator<Double>() {
                        @Override
                        public int compare(Double t0, Double t1) {
                            return (t1.compareTo(t0));  
                        }
                    });
                    aux.put(weight, itemId);
                    result.put(activeUserId, aux);
                }
            }

            for (Integer activeUser: result.keySet()) {
                StringBuilder aux = new StringBuilder();
                int recomsNumber = 5;
                int decimalPlaces = 3;

                for (java.util.Map.Entry<Double, Integer> entry: result.get(activeUser).entrySet()) {
                    aux.append(", " + entry.getValue() + ":" + Precision.round(entry.getKey(), decimalPlaces));
                    if (--recomsNumber <= 0) {
                        break;
                    }
                }

                context.write(new IntWritable(activeUser), new Text("[" + aux.substring(2, aux.length()) + "]"));
            }
        }
    }
}
