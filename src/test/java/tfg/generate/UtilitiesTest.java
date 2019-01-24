package tfg.generate;

import org.junit.Test;
import tfg.common.util.Utilities;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class UtilitiesTest {

  @Test
  public void objectToFile() {

    Map<Integer, Integer> readData = null;
    List<Integer> data = new ArrayList<>();
    Random random = new Random(20);
    for (int i=0; i<100; i++) {
      data.add(random.nextInt());
    }

    final String PATH = "objectToFile";
    try {

      FileOutputStream streamOut = new FileOutputStream(PATH, true);
      Utilities.objectToFile(streamOut, data);
      streamOut.close();

      FileInputStream streamIn = new FileInputStream(PATH);
      readData = Utilities.objectFromFile(streamIn);
      streamIn.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println("Size: " + readData.size());
    for (int i=0; i<data.size(); i++) {
      assertEquals(data.get(i), readData.get(i));
    }
  }

  @Test
  public void objectFromFile() {
  }
}