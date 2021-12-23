package tfg.hadoop.util;

public class Util {
  public static boolean containsHeader(String line) {
    return line.contains("userId")
        || line.contains("movieId")
        || line.contains("rating")
        || line.contains("timestamp");
  }
}
