package main;

import it.unimi.dsi.io.InputBitStream;

import java.io.IOException;
import java.io.InputStream;

public class ReadDeltaStream {

  private final InputBitStream inputBitStream;

  public ReadDeltaStream(InputStream inputStream) {
    inputBitStream = new InputBitStream(inputStream);
  }

  public int [] readDelta(int count) {

    int [] res = new int[count];

    for (int i=0; i<count; i++) {
      try {
        res[i] = inputBitStream.readDelta();
      } catch (IOException e) {
        e.printStackTrace();
      }

      //System.out.println("Read value: " + res[i]);
    }

    return res;
  }

  public void close () {
    try {
      inputBitStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
