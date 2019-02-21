package tfg.hadoop.recommend;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import org.mockito.Mockito;
import tfg.hadoop.recommend.model.TripleWritable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class Job2Test {
  @Test
  public void mapperTest_normal() throws IOException, InterruptedException {
    int shardsNumber = 3;
    Configuration conf = new Configuration();
    conf.setInt(Main.SHARDS_NUMBER, shardsNumber);
    Job2.Map.Context contextMock = mock(Job2.Map.Context.class);
    when(contextMock.getConfiguration()).thenReturn(conf);

    Job2.Map mapper = new Job2.Map();
    mapper.map(null, new Text("4\t8\t2.6639999999999997"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(
            new IntWritable(4%shardsNumber),
            new TripleWritable(
                new IntWritable(4),
                new IntWritable(8),
                new DoubleWritable(2.6639999999999997)));
    mapper.map(null, new Text("5\t1\t1.945"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(
            new IntWritable(5%shardsNumber),
            new TripleWritable(
                new IntWritable(5),
                new IntWritable(1),
                new DoubleWritable(1.945)));
  }

  @Test
  public void reducerTest_normal() throws IOException, InterruptedException {
    Configuration conf = new Configuration();
    conf.setInt(Main.DECIMAL_PLACES, 3);
    conf.setInt(Main.RECS_NUMBER, 5);
    Job2.Reduce.Context contextMock = mock(Job2.Reduce.Context.class);
    when(contextMock.getConfiguration()).thenReturn(conf);

    Job2.Reduce reducer = new Job2.Reduce();

    reducer.reduce(new IntWritable(0), prepareValues(), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(3), new Text("[5:8.298, 3:5.197, 6:4.432]"));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new Text("[7:7.129, 8:5.854, 3:3.911]"));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(4), new Text("[8:6.6, 2:5.254, 3:4.531]"));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(2), new Text("[1:6.34, 2:5.96, 6:3.896]"));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(5), new Text("[7:7.006, 1:5.869, 6:3.572]"));
  }

  private List<TripleWritable> prepareValues() {

    List<TripleWritable> values = new ArrayList<>();
    String[] datas = {"4\t8\t2.6639999999999997",
        "4\t2\t2.072",
        "1\t8\t2.322",
        "1\t7\t1.548",
        "2\t1\t2.228",
        "2\t2\t1.9495000000000002",
        "5\t7\t1.809",
        "5\t1\t2.412",
        "3\t6\t2.368",
        "3\t5\t2.368",
        "3\t6\t2.064",
        "3\t5\t1.29",
        "5\t6\t2.016",
        "5\t1\t1.512",
        "5\t7\t1.945",
        "5\t6\t1.556",
        "5\t1\t1.945",
        "4\t2\t1.626",
        "2\t6\t2.38",
        "2\t1\t2.9749999999999996",
        "2\t6\t1.516",
        "2\t1\t1.137",
        "2\t2\t0.758",
        "1\t7\t4.0649999999999995",
        "5\t7\t3.252",
        "4\t8\t2.38",
        "4\t3\t2.9749999999999996",
        "4\t8\t1.556",
        "4\t3\t1.556",
        "4\t2\t1.556",
        "1\t8\t2.016",
        "1\t3\t2.016",
        "1\t8\t1.516",
        "1\t7\t1.516",
        "1\t3\t1.895",
        "3\t5\t2.412",
        "3\t3\t2.412",
        "3\t5\t2.228",
        "3\t3\t2.785",
        "2\t2\t3.252"};
    for (String data: datas) {
      String[] splitted = data.split("\t");
      values.add(
          new TripleWritable(
              new IntWritable(Integer.valueOf(splitted[0])),
              new IntWritable(Integer.valueOf(splitted[1])),
              new DoubleWritable(Double.valueOf(splitted[2]))));
    }
    return values;
  }
}
