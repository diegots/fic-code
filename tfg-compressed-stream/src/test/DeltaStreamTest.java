package test;

import main.ReadDeltaStream;
import main.WriteDeltaStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import static junit.framework.TestCase.assertEquals;


public class DeltaStreamTest {

  final static String FILE_NAME = "file_name.data";
  final static int [] a = new int[15];

  @Before
  public void fillData () {
    a[0] = 1;
    a[1] = 2;
    a[2] = 3;
    a[3] = 4;
    a[4] = 1001;
    a[5] = 6;
    a[6] = 7;
    a[7] = 8;
    a[8] = 9;
    a[9] = 1001;
    a[10] = 11;
    a[11] = 12;
    a[12] = 13;
    a[13] = 14;
    a[14] = 1001;
  }

  @Test
  public void sameTest () {
    WriteDeltaStream writeDeltaStream = null;
    try {
      writeDeltaStream = new WriteDeltaStream(new FileOutputStream(FILE_NAME));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    writeDeltaStream.writeOut(a, 0, 5);
    writeDeltaStream.writeOut(a, 5, 5);
    writeDeltaStream.writeOut(a, 10, 5);
    writeDeltaStream.close();

    ReadDeltaStream readDeltaStream = null;
    try {
      readDeltaStream = new ReadDeltaStream(new FileInputStream(FILE_NAME));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    final int count = 5;
    int[] res = new int[count];
    res = readDeltaStream.readDelta(count);
    for (int i=0; i<count; i++) {
      assertEquals(a[i], res[i]);
    }

    res = readDeltaStream.readDelta(count);
    for (int i=0; i<count; i++) {
      assertEquals(a[i+count], res[i]);
    }

    res = readDeltaStream.readDelta(count);
    for (int i=0; i<count; i++) {
      assertEquals(a[i+count*2], res[i]);
    }

    readDeltaStream.close();

  }

  @After
  public void cleanEnviroment () {
    new File(FILE_NAME).delete();
  }
}