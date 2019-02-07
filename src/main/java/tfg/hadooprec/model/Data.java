package tfg.hadooprec.model;

import tfg.common.stream.StreamIn;

import java.io.InputStream;
import java.util.List;

public class Data {

  private List<Integer> data;

  public Data(InputStream inputStream) {
    data = new StreamIn.DeltaStreamIn().read(inputStream);
  }

  public int findIndex (int userId) {
    return data.indexOf(userId);
  }

  public int getId(int index) {
    if (null != data.get(index)) {
      return data.get(index);
    }

    return -1;
  }
}
