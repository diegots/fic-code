package main;

import it.unimi.dsi.io.OutputBitStream;

import java.io.IOException;
import java.io.OutputStream;

public class WriteDeltaStream {

  private final OutputBitStream outputBitStream;

  public WriteDeltaStream(OutputStream outputStream) {
    outputBitStream = new OutputBitStream(outputStream);
  }

  public void writeOut (int [] a, int start, int count) {

    for (int i=start; i<start+count; i++) {
      try {
        outputBitStream.writeDelta(a[i]);
        //System.out.println("Wrote: " + a[i] + " in " + written + " bytes");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void close () {
    try {
      outputBitStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
