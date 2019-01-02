package p1811002;

import it.unimi.dsi.io.OutputBitStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public class WriteDeltaStream {

  private final OutputBitStream outputBitStream;

  public WriteDeltaStream(OutputStream outputStream) {
    outputBitStream = new OutputBitStream(outputStream);
  }

  public void writeOut (int [] a, int start, int count) {

    for (int i=start; i<start+count; i++) {
      try {
        outputBitStream.writeDelta(a[i]);

      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public void writeOut (List<Integer> data) {
    Iterator<Integer> iterator = data.iterator();
    while (iterator.hasNext()) {
      try {
        outputBitStream.writeDelta(iterator.next());
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
