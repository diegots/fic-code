package tfg.hadoop.recommend;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.junit.Test;
import org.mockito.Mockito;
import tfg.common.stream.StreamIn;
import tfg.generate.Conf;
import tfg.generate.model.FrequencyTable;
import tfg.hadoop.recommend.cached.UserIds;
import tfg.hadoop.recommend.cached.UsersKNeighbors;
import tfg.hadoop.recommend.model.ActiveUser;
import tfg.hadoop.recommend.model.PairWritable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;

public class JoinDataTest {
  @Test
  public void mapperTest_threeShards() throws IOException, InterruptedException {
    Job1.Map.Context contextMock = mock(Job1.Map.Context.class);
    Job1.Map mapper = new Job1.Map();

    Configuration conf = new Configuration();
    conf.setInt(Main.SHARDS_NUMBER, 3);
    when(contextMock.getConfiguration()).thenReturn(conf);

    Text value = new Text("1\t8,7,3");
    mapper.map(null, value, contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new ActiveUser(value));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new ActiveUser(value));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(2), new ActiveUser(value));

    value = new Text("100\t4,7,1,4");
    mapper.map(null, value, contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new ActiveUser(value));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(1), new ActiveUser(value));
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(2), new ActiveUser(value));
  }

  @Test
  public void mapperTest_oneShards() throws IOException, InterruptedException {
    Job1.Map.Context contextMock = mock(Job1.Map.Context.class);
    Job1.Map mapper = new Job1.Map();

    Configuration conf = new Configuration();
    conf.setInt(Main.SHARDS_NUMBER, 1);
    when(contextMock.getConfiguration()).thenReturn(conf);

    Text value = new Text("1\t8,7,3");
    mapper.map(null, value, contextMock);
    verify(contextMock, Mockito.times(1))
        .write(new IntWritable(0), new ActiveUser(value));
  }

  @Test
  public void reducerTest_normal() throws IOException, InterruptedException {

    int shardId = 0;

    Job1.Reduce.Context contextMock = mock(Job1.Reduce.Context.class);
    Job1.Reduce reducer = new Job1.Reduce();

    Configuration conf = new Configuration();
    conf.setInt(Main.SHARDS_NUMBER, 3);
    when(contextMock.getConfiguration()).thenReturn(conf);

    reducer.userIds = new UserIds(prepareUserIds());
    reducer.usersKNeighbors = new UsersKNeighbors(prepareUsersKNeighbors());
    reducer.frequencyTable = new FrequencyTable(prepareFrequencyTable());

    List<Integer> similarities = new ArrayList<>();
    similarities.add(0, 0); // reasigned values
    similarities.add(1, 2);
    similarities.add(2, 5);
    similarities.add(3, 1);
    similarities.add(4, 4);
    reducer.similarities = mock(StreamIn.DeltraStreamInRow.class);
    when(reducer.similarities.read(0)).thenReturn(similarities);
    reducer.shard = prepareShard(shardId);

    ActiveUser activeUser = new ActiveUser(new Text("1\t8,7,3"));
    List<ActiveUser> values = new ArrayList<>();
    values.add(activeUser);

    reducer.reduce(new IntWritable(shardId), values, contextMock);
    verify(contextMock, Mockito.times(1))
        .write(
            new IntWritable(1),
            new PairWritable(
                new IntWritable(7), new DoubleWritable(1.548)));
    verify(contextMock, Mockito.times(1))
        .write(
            new IntWritable(1),
            new PairWritable(
                new IntWritable(8), new DoubleWritable(2.3220)));
  }

  private Map<Integer, Map<Integer, Double>> prepareShard(int shardId) {
    Map<Integer, Map<Integer, Double>> res = new HashMap<>();
    if (shardId == 0) {
      Map<Integer, Double> aux = new HashMap<>();
      aux.put(7, 3.0);
      aux.put(1, 4.0);
      aux.put(2, 3.5);
      aux.put(4, 3.0);
      aux.put(8, 4.5);
      res.put(3,aux);
    }
    return res;
  }

  private List<Integer> prepareUsersKNeighbors() {
    final List<Integer> userIds = new ArrayList<>();
    userIds.add(0);
    userIds.add(3);
    userIds.add(2);
    userIds.add(4);
    userIds.add(1);
    userIds.add(Conf.USERS_ROWS_DELIMITER);
    userIds.add(1);
    userIds.add(4);
    userIds.add(3);
    userIds.add(2);
    userIds.add(0);
    userIds.add(Conf.USERS_ROWS_DELIMITER);
    userIds.add(2);
    userIds.add(4);
    userIds.add(3);
    userIds.add(1);
    userIds.add(0);
    userIds.add(Conf.USERS_ROWS_DELIMITER);
    userIds.add(3);
    userIds.add(0);
    userIds.add(1);
    userIds.add(2);
    userIds.add(4);
    userIds.add(Conf.USERS_ROWS_DELIMITER);
    userIds.add(4);
    userIds.add(1);
    userIds.add(2);
    userIds.add(0);
    userIds.add(3);
    userIds.add(Conf.USERS_ROWS_DELIMITER);
    return userIds;
  }

  private List<Integer> prepareUserIds() {
    final List<Integer> userIds = new ArrayList<>();
    for (int i=1; i<=8; i++) {
      userIds.add(i);
    }
    return userIds;
  }

  private List<Integer> prepareFrequencyTable() {
    final List<Integer> frequencyTableData = new ArrayList<>();
    for (int i=0; i<=1000; i++) {
      frequencyTableData.add(0);
    }
    frequencyTableData.set(379, 2);
    frequencyTableData.set(389, 2);
    frequencyTableData.set(504, 2);
    frequencyTableData.set(516, 2);
    frequencyTableData.set(557, 2);
    frequencyTableData.set(592, 2);
    frequencyTableData.set(595, 2);
    frequencyTableData.set(603, 2);
    frequencyTableData.set(813, 4);
    frequencyTableData.set(1000, 5);
    return frequencyTableData;
  }

}

