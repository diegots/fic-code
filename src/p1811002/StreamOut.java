package p1811002;

import it.unimi.dsi.io.OutputBitStream;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public interface StreamOut {
  void write(int [] a, int start, int count);
  void write(List<Integer> data);
  void close();

  class DeltaStreamOut implements StreamOut {

    private final OutputBitStream outputBitStream;

    public DeltaStreamOut(OutputStream outputStream) {
      outputBitStream = new OutputBitStream(outputStream);
    }

    public void write(int[] a, int start, int count) {

      for (int i = start; i < start + count; i++) {
        try {
          outputBitStream.writeDelta(a[i]);

        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public void write(List<Integer> data) {
      Iterator<Integer> iterator = data.iterator();
      while (iterator.hasNext()) {
        try {
          outputBitStream.writeDelta(iterator.next());
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    public void close() {
      try {
        outputBitStream.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
