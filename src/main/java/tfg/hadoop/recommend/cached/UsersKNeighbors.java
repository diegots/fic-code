package tfg.hadoop.recommend.cached;

import tfg.generate.Conf;

import java.util.ArrayList;
import java.util.List;

public class UsersKNeighbors {

  private final List<Integer> usersKNeighbors;

  public UsersKNeighbors(List<Integer> usersKNeighbors) {
    this.usersKNeighbors = usersKNeighbors;
  }

  public List<Integer> getNeighbors(int requestedIdx) {
    List<Integer> result = new ArrayList<>();
    System.out.println("getNeighbors requested: " + requestedIdx);

    int i=0;
    int rowIndex = 0;
    while (i<usersKNeighbors.size()) {
      if (Conf.get().USERS_ROWS_DELIMITER == usersKNeighbors.get(i)) {
        rowIndex++;
      }
      i++;
      if (rowIndex == requestedIdx) {
        break;
      }
    }

    while (usersKNeighbors.get(i) != Conf.get().USERS_ROWS_DELIMITER) {
      result.add(usersKNeighbors.get(i));
      i++;
    }

    return result;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (int i=0; i<usersKNeighbors.size(); i++) {
      if (Conf.get().USERS_ROWS_DELIMITER == usersKNeighbors.get(i)) {
        b.append("\n");
      } else {
        b.append(" " + usersKNeighbors.get(i));
      }
    }
    return b.toString();
  }
}
