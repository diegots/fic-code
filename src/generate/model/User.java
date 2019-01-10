package generate.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;

// TODO Utilize user instead of Map<Integer, Map<Integer, Double>>
public class User implements Serializable {
  private final int userId;
  private final Map<Integer, Double> userProfile;

  public User(int userId, Map<Integer, Double> userProfile) {
    this.userId = userId;
    this.userProfile = userProfile;
  }

  public int getUserId() {
    return userId;
  }

  public Map<Integer, Double> getUserProfile() {
    return Collections.unmodifiableMap(userProfile);
  }
}
