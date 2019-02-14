package tfg.common.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

public class User implements Serializable {
  private final int userId;
  private final Map<Integer, Double> userProfile;

  public User(int userId, Map<Integer, Double> userProfile) {
    this.userId = userId;
    this.userProfile = userProfile;
  }

  public static User getEmptyUser() {
    return new User(0, null);
  }

  public static boolean isEmptyUser(User user) {
    return 0 == user.getUserId() && null == user.userProfile;
  }

  public int getUserId() {
    return userId;
  }

  public Map<Integer, Double> getUserProfile() {
    return Collections.unmodifiableMap(userProfile);
  }
}
