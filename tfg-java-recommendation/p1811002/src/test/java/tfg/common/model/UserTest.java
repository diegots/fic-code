package tfg.common.model;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class UserTest {

  @Test
  public void emptyUserTest() {

    User u0 = User.getEmptyUser();
    User u1 = new User(0, new HashMap<>());

    Map<Integer, Double> userProfile = new HashMap<>();
    userProfile.put(5,2.0);
    userProfile.put(6,3.5);
    userProfile.put(7,4.0);
    User u2 = new User(123, userProfile);

    assertEquals(u0, u1);
    assertNotEquals(u0, u2);
    assertTrue(User.isEmptyUser(u0));
    assertTrue(User.isEmptyUser(u1));
    assertFalse(User.isEmptyUser(u2));
    assertTrue(u0.getUserId() == 0);
    assertTrue(u0.getUserProfile().size() == 0);
  }

  @Test
  public void getUserProfileTest() {
    Map<Integer, Double> userProfileA = new HashMap<>();
    userProfileA.put(5,2.0);
    userProfileA.put(6,3.5);
    userProfileA.put(7,4.0);
    User u0 = new User(123, userProfileA);

    assertEquals(userProfileA, u0.getUserProfile());
  }

  @Test
  public void getUserId() {
    User u0 = new User(101, new HashMap<>());
    assertEquals(101, u0.getUserId());
  }
}
