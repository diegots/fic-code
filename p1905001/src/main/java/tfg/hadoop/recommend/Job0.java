package tfg.hadoop.recommend;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Job0 {

    static void readFileHDFS (Collection<Integer> data, Path path) throws IOException {
        FileSystem fs = FileSystem.get(new Configuration());
        FileStatus[] statuses = fs.listStatus(path);
        for (int i=0; i<statuses.length; i++) {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(fs.open(statuses[i].getPath())));
            String line = br.readLine();
            while (null != line) {
                data.add(Integer.valueOf(line.trim()));
                line = br.readLine();
            }
            br.close();
        }
    }

    public static class Map extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

        Collection<Integer> activeUsers;

        @Override
        protected void setup(Context context) throws IOException {
            activeUsers = new ArrayList<>();
            Job0.readFileHDFS(activeUsers,
                    new Path(context.getConfiguration().get(Main.ACTIVE_USERS_FILE_PATH)));
        }

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] item = value.toString().split(",");
            int userId = Integer.valueOf(item[0]);
            if (activeUsers.contains(userId)) {
                context.write(
                        new IntWritable(userId),
                        new IntWritable(Integer.valueOf(item[1]))
                );
            }
        }
    }


    private static class Reduce extends Reducer<IntWritable, IntWritable, IntWritable, Text> {

        // A set is used to get rid of duplicated elements
        final static List<Integer> allItems = new LinkedList<>();

        @Override
        protected void setup(Context context) throws IOException {

            Collection<Integer> aux = new LinkedHashSet<>();
            Job0.readFileHDFS(aux, new Path(context.getConfiguration().get(Main.UNIQUE_ITEMS_FILE_PATH)));
            allItems.addAll(aux);
            Collections.sort(allItems);
        }

        // Helper method to compose final results
        String composeString(Collection<Integer> c) {
            boolean isFirst = true;
            StringBuilder sb = new StringBuilder();
            for (Integer item: c) {
                if (isFirst) {
                    sb.append(item);
                    isFirst = false;
                } else {
                    sb.append("," + item);
                }
            }

            return sb.toString();
        }

        List<Integer> prepareUserProfile (Iterable<IntWritable> values) {
            List<Integer> aux = new LinkedList<>();

            for (IntWritable value: values) {
                aux.add(value.get());
            }

            return aux;
        }

        List<Integer> prepareItemsToHide (Random generator, int nItemsToRemove, List<Integer> userItems) {

            int[] indexes = generator.ints(nItemsToRemove, 0, userItems.size()-1).toArray();

            for (Integer i: indexes) {
                userItems.remove(i);
            }

            return userItems;
        }

        List<Integer> hideItems (List<Integer> items) {
            List<Integer> aux = new LinkedList<>(allItems);

            for (Integer value: items) {
                aux.remove(value);
            }

            return aux;
        }
    }

    /*
     * Standard reducer for computing recommendations
     */
    public static class ReduceNone extends Reduce {
        @Override
        protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context)
                throws IOException, InterruptedException {

            Set<Integer> aux = new LinkedHashSet<>(allItems);
            for (IntWritable value: values) {
                aux.remove(value.get());
            }

            context.write(key, new Text(composeString(aux)));
        }
    }

    public abstract static class ReduceEvaluation extends Reduce {
        Random generator;

        abstract int numberItemsToRemove (Context context, List<Integer> userItems);

        @Override
        protected void setup(Context context) throws IOException {
            super.setup(context); // Initialize common part
            generator = new Random(context.getConfiguration().getInt(Main.EVALUATION_SEED, 0));
        }

        @Override
        protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            List<Integer> userItems = prepareUserProfile(values);

            // Instead of hiding a percentage of items from user profile, hide n items.
            int nItemsToRemove = numberItemsToRemove(context, userItems);

            userItems = prepareItemsToHide(generator, nItemsToRemove, userItems);
            context.write(key, new Text(composeString(hideItems(userItems))));
        }
    }

    public static class ReducePercentage extends ReduceEvaluation {

        @Override
        int numberItemsToRemove(Context context, List<Integer> userItems) {
            return userItems.size() *
                    context.getConfiguration().getInt(Main.EVALUATION_N_VALUE, userItems.size());
        }
    }

    public static class ReduceAllButN extends ReduceEvaluation {

        @Override
        int numberItemsToRemove(Context context, List<Integer> userItems) {

            // Instead of hiding a percentage of items from user profile, hide n items.
            return context.getConfiguration().getInt(Main.EVALUATION_N_VALUE, userItems.size());
        }
    }

    public static class ReduceGivenN extends ReduceEvaluation {

        @Override
        int numberItemsToRemove(Context context, List<Integer> userItems) {
            // In this case hide (profile size - n items) from the profile
            return userItems.size() - context.getConfiguration().getInt(Main.EVALUATION_N_VALUE, userItems.size());
        }
    }
}
