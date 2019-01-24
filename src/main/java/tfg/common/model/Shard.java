package tfg.common.model;

import java.util.Iterator;
import java.util.List;

public class Shard {

  private final List<User> users;
  private User lastUserLookedUp;

  public Shard(List<User> users) {
    this.users = users;
    lastUserLookedUp = User.getEmptyUser();
  }

  private User findUser(int userId) {

    if (userId == lastUserLookedUp.getUserId()) {
      return lastUserLookedUp;
    }

    Iterator<User> iterator = users.iterator();
    while (iterator.hasNext()) {

      User user = iterator.next();
      if (userId == user.getUserId()) {
        return lastUserLookedUp = user;
      }
    }

    return lastUserLookedUp = User.getEmptyUser();
  }

  public boolean contains (int userId) {
    return !User.isEmptyUser(findUser(userId));
  }

  public User getUser (int userId) {
    return findUser(userId);
  }
}
