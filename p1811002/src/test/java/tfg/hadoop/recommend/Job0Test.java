package tfg.hadoop.recommend;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class Job0Test {
  @Test
  public void mapperTest_noActiveUsers() throws IOException, InterruptedException {

    Job0.Map.Context contextMock = mock(Job0.Map.Context.class);
    Job0.Map mapper = new Job0.Map();
    mapper.activeUsers  = new ArrayList<>();

    mapper.map(null, new Text("1,924,3.5,1094785598"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);

    mapper.map(null, new Text("1,924,3.5,1094785598"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);
  }
  @Test
  public void mapperTest_header() throws IOException, InterruptedException {

    Job0.Map.Context contextMock = mock(Job0.Map.Context.class);
    Job0.Map mapper = new Job0.Map();
    mapper.activeUsers  = new ArrayList<>();

    mapper.map(null, new Text("userId,movieId,rating,timestamp"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);

  }

  @Test
  public void mapperTest_normal() throws IOException, InterruptedException {

    Job0.Map.Context contextMock = mock(Job0.Map.Context.class);
    Job0.Map mapper = new Job0.Map();
    mapper.activeUsers  = new ArrayList<>();
    mapper.activeUsers.add(47);
    mapper.activeUsers.add(16);
    mapper.activeUsers.add(967);

    mapper.map(null, new Text("16,924,3.5,1094785598"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(16), new IntWritable(924));

    mapper.map(null, new Text("16,1266,4.0,1112485371"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(16), new IntWritable(1266));

    mapper.map(null, new Text("16,589,3.5,1112485557"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(16), new IntWritable(589));

    mapper.map(null, new Text("1,296,4.0,1112484767"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);

    mapper.map(null, new Text("967,1371,2.5,1260759135"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(967), new IntWritable(1371));
  }

  @Test
  public void reducerTest_normal() throws IOException, InterruptedException {

    Job0.Reduce.Context contextMock = mock(Job0.Reduce.Context.class);
    Job0.Reduce reducer = new Job0.Reduce();

    //reducer.allItems = new LinkedHashSet<>();
    reducer.allItems.add(924);
    reducer.allItems.add(1266);
    reducer.allItems.add(589);
    reducer.allItems.add(678);
    reducer.allItems.add(46);
    reducer.allItems.add(61);

    List<IntWritable> values = new ArrayList<>();
    values.add(new IntWritable(924));
    values.add(new IntWritable(1266));
    values.add(new IntWritable(589));
    reducer.reduce(new IntWritable(16), values, contextMock); // userId, userId rated values
    verify(contextMock, Mockito.times(1)).write(new IntWritable(16), new Text("678,46,61"));

    values = new ArrayList<>();
    reducer.allItems.add(678);
    reducer.allItems.add(61);
    values.add(new IntWritable(678));
    values.add(new IntWritable(61));
    reducer.reduce(new IntWritable(67), values, contextMock); // userId, userId rated values
    verify(contextMock, Mockito.times(1)).write(new IntWritable(67), new Text("924,1266,589,46"));
  }

  @Test
  public void reducerTest_empty() throws IOException, InterruptedException {
    Job0.Reduce.Context contextMock = mock(Job0.Reduce.Context.class);
    Job0.Reduce reducer = new Job0.Reduce();

    //reducer.allItems = new LinkedHashSet<>();
    reducer.allItems.add(924);
    reducer.allItems.add(1266);
    reducer.allItems.add(589);
    reducer.allItems.add(678);
    reducer.allItems.add(46);
    reducer.allItems.add(61);

    List<IntWritable> values = new ArrayList<>();
    values.add(new IntWritable(61));
    values.add(new IntWritable(46));
    values.add(new IntWritable(678));
    values.add(new IntWritable(589));
    values.add(new IntWritable(1266));
    values.add(new IntWritable(924));
    reducer.reduce(new IntWritable(16), values, contextMock); // userId, userId rated values
    verify(contextMock, Mockito.times(1)).write(new IntWritable(16), new Text(""));
  }
}