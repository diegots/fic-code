package tfg.hadoop.recommend;

import tfg.hadoop.util.Util;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Gets non rated items for every active user
 */
public class Job0 {
  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    List<Integer> activeUsers;

    @Override
    protected void setup(Context context) throws IOException {
      activeUsers = new ArrayList<>();
      FileSystem fs = FileSystem.get(new Configuration());
      FileStatus[] statuses = fs.listStatus(new Path(
          context.getConfiguration().get(Main.ACTIVE_USERS_FILE_PATH)));
      for (int i=0; i<statuses.length; i++) {
        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(statuses[i].getPath())));
        String line = br.readLine();
        while (null != line) {
          activeUsers.add(Integer.valueOf(line.trim()));
          line = br.readLine();
        }
        br.close();
      }
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

      if (!Util.containsHeader(value.toString())) {
        String [] item = value.toString().split(",");
        int userId = Integer.valueOf(item[0]);
        if (activeUsers.contains(userId)) {
          context.write(
              new IntWritable(userId),
              new IntWritable(Integer.valueOf(item[1]))
          );
        }
      }
    }
  }

  /**
   * Removes rated items from all items list for every active user
   */
  public static class Reduce
      extends Reducer<IntWritable, IntWritable, IntWritable, Text> {

    final static Set<Integer> allItems = new LinkedHashSet<>();

    @Override
    protected void setup(Context context) throws IOException {
      FileSystem fs = FileSystem.get(new Configuration());
      FileStatus[] statuses = fs.listStatus(new Path(context.getConfiguration().get(Main.UNIQUE_ITEMS_FILE_PATH)));
      for (int i=0; i<statuses.length; i++) {
        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(statuses[i].getPath())));
        String line = br.readLine();
        while (null != line) {
          System.err.println("-> [allItems] Adding item: " + line.trim());
          allItems.add(Integer.valueOf(line.trim()));
          line = br.readLine();
        }
        br.close();
      }
    }

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
      Set<Integer> aux = new LinkedHashSet(allItems);
      System.err.println("--> UserId: " + key.get());
      for (IntWritable value: values) {
        System.err.println("-> [aux] Removing itemId: " + value.get());
        aux.remove(value.get());
      }
      System.err.println("-> [aux] Size: " + aux.size());
      boolean isFirst = true;
      StringBuilder b = new StringBuilder();
      for (Integer item: aux) {
        if (isFirst) {
          b.append(item);
          isFirst = false;
        } else {
          b.append("," + item);
        }
      }
      System.err.println("-> Non rated items: " + b.toString());
      context.write(key, new Text(b.toString()));
    }
  }
}
