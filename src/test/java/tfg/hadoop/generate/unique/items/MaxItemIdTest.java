package tfg.hadoop.generate.unique.items;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class MaxItemIdTest {

  @Test
  public void MapperTest_header() throws IOException, InterruptedException {
    MaxItemId.Map.Context contextMock = mock(MaxItemId.Map.Context.class);
    MaxItemId.Map mapper = new MaxItemId.Map();
    mapper.map(null, new Text("userId,movieId,rating,timestamp"), contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);
  }

  @Test
  public void MapperTest_normal() throws IOException, InterruptedException {

    MaxItemId.Map.Context contextMock = mock(MaxItemId.Map.Context.class);
    MaxItemId.Map mapper = new MaxItemId.Map();

    mapper.map(null, new Text("1,31,2.5,1260759144"), contextMock);
    mapper.map(null, new Text("1,1129,2.0,1260759185"), contextMock);
    mapper.map(null, new Text("1,1061,3.0,1260759182"), contextMock);

    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new IntWritable(31));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new IntWritable(1129));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new IntWritable(1061));
  }

  @Test
  public void MapperTest_repeated() throws IOException, InterruptedException {
    MaxItemId.Map.Context contextMock = mock(MaxItemId.Map.Context.class);
    MaxItemId.Map mapper = new MaxItemId.Map();

    mapper.map(null, new Text("1,47,3.5,1112484727"), contextMock);
    mapper.map(null, new Text("1,47,3.5,1112484727"), contextMock);
    mapper.map(null, new Text("1,47,3.5,1112484727"), contextMock);

    verify(contextMock, Mockito.times(3))
        .write(new IntWritable(1), new IntWritable(47));
  }

  @Test
  public void ReducerTest_repeated() throws IOException, InterruptedException {
    MaxItemId.Reduce.Context contextMock = mock(MaxItemId.Reduce.Context.class);
    MaxItemId.Reduce reducer = new MaxItemId.Reduce();
    List<IntWritable> values = new ArrayList<>();
    values.add(new IntWritable(1029));
    values.add(new IntWritable(1029));
    values.add(new IntWritable(1029));
    values.add(new IntWritable(1029));
    reducer.reduce(null, values, contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1029), NullWritable.get());
  }

  @Test
  public void ReducerTest_empty() throws IOException, InterruptedException {
    MaxItemId.Reduce.Context contextMock = mock(MaxItemId.Reduce.Context.class);
    MaxItemId.Reduce reducer = new MaxItemId.Reduce();
    List<IntWritable> values = new ArrayList<>();
    reducer.reduce(null, values, contextMock);
    verify(contextMock, Mockito.times(0))
        .write(null, null);
  }

  @Test
  public void ReducerTest_normal() throws IOException, InterruptedException {
    MaxItemId.Reduce.Context contextMock = mock(MaxItemId.Reduce.Context.class);
    MaxItemId.Reduce reducer = new MaxItemId.Reduce();
    List<IntWritable> values = new ArrayList<>();
    values.add(new IntWritable(1029));
    values.add(new IntWritable(47));
    values.add(new IntWritable(1172));
    values.add(new IntWritable(31));
    reducer.reduce(null, values, contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1172), NullWritable.get());
  }
}
