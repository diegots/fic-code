package tfg.hadooprec.cached;

import tfg.generate.Conf;

import java.util.List;

public class UserIds {

  private final List<Integer> userIds;

  public UserIds(List<Integer> userIds) {
    this.userIds = userIds;
  }

  public Integer findId (int userIndex) {
    return userIds.get(userIndex);
  }

  public Integer findIndex (int userId) {
    return userIds.indexOf(userId);
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder();
    for (int i=0; i<userIds.size(); i++) {
      if (Conf.get().USERS_ROWS_DELIMITER == userIds.get(i)) {
        b.append("\n");
      } else {
        b.append(" " + userIds.get(i));
      }
    }
    return b.toString();
  }
}
