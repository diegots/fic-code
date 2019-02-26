package tfg.common.stream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import tfg.generate.Conf;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class StreamTest {
  private final static Integer[] row = {0, 1, 2, 500, 998, 999, 1000};
  private final static Integer[] firstRow = {0, 1, 2, 500, 998, 999, 1000};
  private final static Integer[] secondRow = {3, 4, 5, 501, 995, 996, 997};
  private final static Integer[] thirdRow = {6, 7, 8, 502, 992, 993, 994};

  private int[] expectedArray;
  private List<Integer> expectedList;
  private List<Integer> expectedDelmOneRow;
  private List<Integer> expectedDelmThreeRow;

  @Before
  public void setup() {
    expectedArray = new int[8];
    expectedArray[0] = 0;
    expectedArray[1] = 1;
    expectedArray[2] = 2;
    expectedArray[3] = 127;
    expectedArray[4] = 128;
    expectedArray[5] = 253;
    expectedArray[6] = 254;
    expectedArray[7] = 255;

    expectedList = new ArrayList<>();
    // 255 is the greatest possible value into a ByteArray!
    fillList(expectedList, new Integer[] {255, 254, 253, 252, 2, 1, 0}, null);

    expectedDelmOneRow = new ArrayList<>();
    fillList(expectedDelmOneRow, row, Conf.get().SIMILARITY_ROWS_DELIMITER);

    expectedDelmThreeRow = new ArrayList<>();
    fillList(expectedDelmThreeRow, firstRow, Conf.get().SIMILARITY_ROWS_DELIMITER);
    fillList(expectedDelmThreeRow, secondRow, Conf.get().SIMILARITY_ROWS_DELIMITER);
    fillList(expectedDelmThreeRow, thirdRow, Conf.get().SIMILARITY_ROWS_DELIMITER);
  }

  private void fillList(List<Integer> list, Integer[] data, Integer dlm) {

    for (int i=0; i<data.length; i++) {
      list.add(data[i]);
    }
    if (dlm != null) {
      list.add(dlm);
    }
  }

  @After
  public void clear() {
    expectedList.clear();
  }

  @Test
  public void plainStreamListTest() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.PlainStreamOut streamOut =
        new StreamOut.PlainStreamOut(outputStream);
    streamOut.write(expectedList);
    streamOut.close();

    // Reads data back
    StreamIn.PlainStreamIn streamIn = new StreamIn.PlainStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(expectedList.size(), actualList.size());
    assertEquals(expectedList, actualList);
  }

  @Test
  public void plainStreamArrayTest_part() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.PlainStreamOut streamOut =
        new StreamOut.PlainStreamOut(outputStream);
    streamOut.write(expectedArray, 1, 7);
    streamOut.close();

    // Reads data back
    StreamIn.PlainStreamIn streamIn = new StreamIn.PlainStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(7, actualList.size());
    for (int i=1; i<7; i++) {
      assertEquals(expectedArray[i], actualList.get(i-1).intValue());
    }
  }

  @Test
  public void plainStreamArrayTest_full() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.PlainStreamOut streamOut =
        new StreamOut.PlainStreamOut(outputStream);
    streamOut.write(expectedArray, 0, 8);
    streamOut.close();

    // Reads data back
    StreamIn.PlainStreamIn streamIn = new StreamIn.PlainStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(8, actualList.size());
    for (int i=0; i<=7; i++) {
      assertEquals(expectedArray[i], actualList.get(i).intValue());
    }
  }

  @Test
  public void plainStreamArrayTest_one() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.PlainStreamOut streamOut =
        new StreamOut.PlainStreamOut(outputStream);
    streamOut.write(expectedArray, 4, 1);
    streamOut.close();

    // Reads data back
    StreamIn.PlainStreamIn streamIn = new StreamIn.PlainStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(1, actualList.size());
    assertEquals(expectedArray[4], actualList.get(0).intValue());
  }

  @Test
  public void deltaStreamOutListTest() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.DeltaStreamOut streamOut =
        new StreamOut.DeltaStreamOut(outputStream);
    streamOut.write(expectedList);
    streamOut.close();

    // Reads data back
    StreamIn.DeltaStreamIn streamIn = new StreamIn.DeltaStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(expectedList, actualList);
    assertEquals(expectedList.size(), actualList.size());
  }

  @Test
  public void deltaStreamArrayTest_one() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.DeltaStreamOut streamOut =
        new StreamOut.DeltaStreamOut(outputStream);
    streamOut.write(expectedArray, 3, 1);
    streamOut.close();

    // Reads data back
    StreamIn.DeltaStreamIn streamIn = new StreamIn.DeltaStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(1, actualList.size());
    assertEquals(expectedArray[3], actualList.get(0).intValue());
  }

  @Test
  public void deltaStreamArrayTest_full() {
    // Writes data out
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    StreamOut.DeltaStreamOut streamOut =
        new StreamOut.DeltaStreamOut(outputStream);
    streamOut.write(expectedArray, 0, expectedArray.length);
    streamOut.close();

    // Reads data back
    StreamIn.DeltaStreamIn streamIn = new StreamIn.DeltaStreamIn(
        new ByteArrayInputStream(outputStream.toByteArray()));
    List<Integer> actualList = streamIn.read();
    assertEquals(expectedArray.length, actualList.size());
    for (int i=0; i<=7; i++) {
      assertEquals(expectedArray[i], actualList.get(i).intValue());
    }
  }

  @Test
  public void deltraStreamInRow() throws FileNotFoundException {
    FileOutputStream outputStream = new FileOutputStream("deltaStreamInRow.test");
    StreamOut.DeltaStreamOut streamOut =
        new StreamOut.DeltaStreamOut(outputStream);
    streamOut.write(expectedDelmThreeRow);
    streamOut.close();

    StreamIn.DeltaStreamInRow deltaStreamInRow = new StreamIn.DeltaStreamInRow(
        new FileInputStream("deltaStreamInRow.test"));
    for (int i=0; i<=2; i++) {
      List<Integer> row = deltaStreamInRow.read(i);
      assertEquals(7, row.size());
    }

    assertEquals(Arrays.asList(thirdRow), deltaStreamInRow.read(2));
    assertEquals(Arrays.asList(firstRow), deltaStreamInRow.read(0));
    assertEquals(Arrays.asList(secondRow), deltaStreamInRow.read(1));
  }
}
