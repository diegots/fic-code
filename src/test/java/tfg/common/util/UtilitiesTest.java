package tfg.common.util;

import org.junit.Test;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.assertEquals;

public class UtilitiesTest {

  @Test
  public void sortMapByValueTest() {
    Map<Integer, Integer> map01 = new HashMap<>();
    map01.put(0, 100);
    map01.put(1, 99);
    map01.put(2, 101);
    map01.put(3, 98);
    map01.put(4, 102);

    Map<Integer, Integer> map01Sorted = Utilities.sortMapByValue(map01);
    List<Integer> sortedValues = new ArrayList<>(map01.values());
    Collections.sort(sortedValues);
    Collections.reverse(sortedValues);
    assertEquals(sortedValues, new ArrayList<>(map01Sorted.values()));
  }

  @Test
  public void mapToFromFileTest() {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    Map<Integer, Integer> expected = new LinkedHashMap<>();

    int seed = 23;
    int bound = 1000000;
    Random random = new Random(seed);
    for (int i=0; i<10; i++) {
      expected.put(i, random.nextInt(bound));
    }
    Utilities.mapToFile(outputStream, expected);
    ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
    Map<Integer, Integer> actual = Utilities.mapFromFile(inputStream);
    assertEquals(expected, actual);
  }

  @Test
  public void writeLineTest() throws IOException {
    String expected = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Donec "
        + "libero ipsum, pharetra non urna ut, euismod varius nibh. Mauris imperdiet quam leo, "
        + "a semper neque cursus sed. Aenean enim lacus, malesuada ut dignissim sit amet, "
        + "auctor a magna. Nulla mollis quis eros sed porta. Aenean id viverra risus. Cras "
        + "mollis sodales nulla in dapibus. Sed tincidunt vehicula sapien quis molestie. Cras "
        + "ipsum massa, tempor vitae aliquam quis, dignissim in metus. Maecenas in neque vitae "
        + "leo auctor egestas. Donec pulvinar eros dolor, a ultrices risus tristique quis. "
        + "Aenean at tortor et ex venenatis sollicitudin a non libero.";

    String destFile = "writeLineTest.txt";
    Utilities.writeLine(destFile, expected);
    BufferedReader reader = new BufferedReader(new FileReader(destFile));

    String actual;
    while ((actual = reader.readLine()) != null) {
      assertEquals(expected, actual);
    }
    reader.close();

    Utilities.deleteFile(Paths.get(destFile));
  }

  @Test
  public void writeListToFileAsDeltaTest() {

    String destFile = "writeListToFileAsDeltaTest.txt";

    List<Integer> expected = new LinkedList<>();

    int seed = 23;
    int bound = 1000000;
    Random random = new Random(seed);
    for (int i=0; i<10; i++) {
      expected.add(random.nextInt(bound));
    }

    Utilities.writeListToFileAsDeltas(destFile, expected);
    List<Integer> actual = Utilities.readFileToListAsDeltas(destFile);
    assertEquals(expected, actual);

    Utilities.deleteFile(Paths.get(destFile));
  }
}