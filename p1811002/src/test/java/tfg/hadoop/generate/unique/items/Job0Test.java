package tfg.hadoop.generate.unique.items;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static tfg.hadoop.generate.unique.items.Main.MAX_ITEMID;

public class Job0Test {

  @Test
  public void MapperTest_header() throws IOException, InterruptedException {
    final int numReducers = 3;
    final int maxItemId = 8;

    Configuration configuration = new Configuration();
    configuration.setInt(MAX_ITEMID, maxItemId);
    Job0.Map.Context contextMock = mock(Job0.Map.Context.class);
    when(contextMock.getNumReduceTasks()).thenReturn(numReducers);
    when(contextMock.getConfiguration()).thenReturn(configuration);

    Job0.Map mapper = new Job0.Map();
    mapper.setup(contextMock);

    mapper.map(null, new Text("userId,movieId,rating,timestamp"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);
  }

  @Test
  public void MapperTest_normal() throws IOException, InterruptedException {

    final int numReducers = 3;
    final int maxItemId = 8;

    Configuration configuration = new Configuration();
    configuration.setInt(MAX_ITEMID, maxItemId);
    Job0.Map.Context contextMock = mock(Job0.Map.Context.class);
    when(contextMock.getNumReduceTasks()).thenReturn(numReducers);
    when(contextMock.getConfiguration()).thenReturn(configuration);

    Job0.Map mapper = new Job0.Map();
    mapper.setup(contextMock);

    mapper.map(null, new Text("1,1,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new IntWritable(1)); // reducerId, itemId

    mapper.map(null, new Text("1,2,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new IntWritable(1));

    mapper.map(null, new Text("1,3,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new IntWritable(1));

    mapper.map(null, new Text("1,4,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new IntWritable(4));

    mapper.map(null, new Text("1,5,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new IntWritable(5));

    mapper.map(null, new Text("1,6,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new IntWritable(6));

    mapper.map(null, new Text("1,7,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(2), new IntWritable(7));

    mapper.map(null, new Text("1,8,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(2), new IntWritable(8));

    mapper.map(null, new Text("1,9,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(2), new IntWritable(9));

    mapper.map(null, new Text("1,9,1,111"), contextMock);
    verify(contextMock, Mockito.times(2))
        .write(new IntWritable(2), new IntWritable(9));

    mapper.map(null, new Text("1,0,1,111"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);

    mapper.map(null, new Text("1,10,1,111"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);
  }

  @Test
  public void MapperTest_oneReducer () throws IOException, InterruptedException {

    final int numReducers = 1;
    final int maxItemId = 8;

    Configuration configuration = new Configuration();
    configuration.setInt(MAX_ITEMID, maxItemId);
    Job0.Map.Context contextMock = mock(Job0.Map.Context.class);
    when(contextMock.getNumReduceTasks()).thenReturn(numReducers);
    when(contextMock.getConfiguration()).thenReturn(configuration);

    Job0.Map mapper = new Job0.Map();
    mapper.setup(contextMock);

    mapper.map(null, new Text("1,1,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new IntWritable(1)); // reducerId, itemId

    mapper.map(null, new Text("1,3,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new IntWritable(3)); // reducerId, itemId

    mapper.map(null, new Text("1,8,1,111"), contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new IntWritable(8)); // reducerId, itemId
  }

  @Test
  public void ReducerTest_normal() throws IOException, InterruptedException {

    Job0.Reduce.Context contextMock = mock(Job0.Reduce.Context.class);
    Job0.Reduce reducer = new Job0.Reduce();
    List<IntWritable> values = new ArrayList<>();
    values.add(new IntWritable(1));
    values.add(new IntWritable(2));
    values.add(new IntWritable(3));
    values.add(new IntWritable(123));

    reducer.reduce(null, values, contextMock);
    verify(contextMock, Mockito.times(1)).write(new IntWritable(1), NullWritable.get());
    verify(contextMock, Mockito.times(1)).write(new IntWritable(2), NullWritable.get());
    verify(contextMock, Mockito.times(1)).write(new IntWritable(3), NullWritable.get());
    verify(contextMock, Mockito.times(1)).write(new IntWritable(123), NullWritable.get());
  }

  @Test
  public void ReducerTest_empty() throws IOException, InterruptedException {
    Job0.Reduce.Context contextMock = mock(Job0.Reduce.Context.class);
    Job0.Reduce reducer = new Job0.Reduce();
    List<IntWritable> values = new ArrayList<>();

    reducer.reduce(null, values, contextMock);
    verify(contextMock, Mockito.times(0)).write(null, null);
  }

  @Test
  public void ReducerTest_repeated() throws IOException, InterruptedException {
    Job0.Reduce.Context contextMock = mock(Job0.Reduce.Context.class);
    Job0.Reduce reducer = new Job0.Reduce();
    List<IntWritable> values = new ArrayList<>();
    values.add(new IntWritable(1));
    values.add(new IntWritable(1));
    values.add(new IntWritable(1));
    values.add(new IntWritable(1));

    values.add(new IntWritable(5));

    values.add(new IntWritable(7));
    values.add(new IntWritable(7));
    values.add(new IntWritable(7));

    values.add(new IntWritable(5));

    reducer.reduce(null, values, contextMock);
    verify(contextMock, Mockito.times(1)).write(new IntWritable(1), NullWritable.get());
    verify(contextMock, Mockito.times(1)).write(new IntWritable(5), NullWritable.get());
    verify(contextMock, Mockito.times(1)).write(new IntWritable(7), NullWritable.get());
  }
}
