package tfg.common.model;

import java.io.Serializable;
import java.util.*;

public class User implements Serializable {
  private final int userId;
  private final Map<Integer, Double> userProfile;

  public User(int userId, Map<Integer, Double> userProfile) {
    this.userId = userId;

    if (userProfile == null)
      this.userProfile = new HashMap<>();
    else
      this.userProfile = userProfile;
  }

  public static User getEmptyUser() {
    return new User(0, new HashMap<>());
  }

  public static boolean isEmptyUser(User user) {
    return 0 == user.getUserId() && user.userProfile.size() == 0;
  }

  public int getUserId() {
    return userId;
  }

  public Map<Integer, Double> getUserProfile() {
    return Collections.unmodifiableMap(userProfile);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    User user = (User) o;

    return userId == user.userId &&
        userProfile.equals(user.userProfile);
  }

  @Override
  public int hashCode() {
    return Objects.hash(userId, userProfile);
  }
}
