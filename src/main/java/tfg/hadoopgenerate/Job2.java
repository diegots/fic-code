package tfg.hadoopgenerate;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Job2 {

  public static class Map
      extends Mapper<LongWritable, Text, IntWritable, IntWritable> {

    final List<Integer> activeUsers = new ArrayList<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {

      BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(
          new File("active-user.csv"))));
      String line = br.readLine();
      while (null != line) {
        activeUsers.add(Integer.valueOf(line.trim()));
        line = br.readLine();
      }
      br.close();
    }

    @Override
    protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {

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

  public static class Reduce
      extends Reducer<IntWritable, IntWritable, IntWritable, Text> {

    final Set<Integer> allItems = new LinkedHashSet<>();

    @Override
    protected void setup(Context context) throws IOException, InterruptedException {
      FileSystem fs = FileSystem.get(new Configuration());
      FileStatus[] statuses = fs.listStatus(new Path(context.getConfiguration().get(Main.ALL_ITEMS_PATH)));
      for (int i=0; i<statuses.length; i++) {
        BufferedReader br = new BufferedReader(new InputStreamReader(fs.open(statuses[i].getPath())));
        String line = br.readLine();
        while (null != line) {
          allItems.add(Integer.valueOf(line.trim()));
          line = br.readLine();
        }
        br.close();
      }
    }

    @Override
    protected void reduce(IntWritable key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {

      Set<Integer> aux = new LinkedHashSet(allItems);
      for (IntWritable value: values) {
        aux.remove(value.get());
      }

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

      context.write(key, new Text(b.toString()));
    }
  }
}
