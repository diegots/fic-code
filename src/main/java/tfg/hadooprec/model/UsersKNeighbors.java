package tfg.hadooprec.model;

import tfg.common.stream.StreamIn;
import tfg.generate.Conf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class UsersKNeighbors {
  private final List<Integer> data;

  public UsersKNeighbors(InputStream inputStream) {
    data = new StreamIn.DeltaStreamIn().read(inputStream);
  }

  public List<Integer> getNeighbors(int index) {
    List<Integer> result = new ArrayList<>();

    int pos = 0;
    for (int i= 0; i<data.size(); i++) {
      if (Conf.get().USERS_ROWS_DELIMITER == data.get(i)) {
        pos++;
      }

      if (pos == index) {
        result.add(data.get(i));
      }
    }

    return result;
  }

}
