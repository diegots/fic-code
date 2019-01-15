package common.stream;

import it.unimi.dsi.io.OutputBitStream;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

public interface StreamOut {
  void write(int [] a, int start, int count);
  void write(List<Integer> data);
  void close();

  /** Avoids any kind of writing in case engine result is going to be kept in memory */
  class Memory implements StreamOut {

    final private List<Integer> exportData;
    private List<Integer> currentData;

    public Memory(List<Integer> data) {
      this.exportData = data;
    }

    @Override
    public void write(int[] a, int start, int count) {}

    @Override
    public void write(List<Integer> data) {
      currentData = data;
    }

    @Override
    public void close() {
      exportData.addAll(currentData);
    }
  }

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

  static StreamOut createDeltaStreamOut(String pathToFile) {
    StreamOut streamOut = null;
    try {
      streamOut = new StreamOut.DeltaStreamOut(new FileOutputStream(pathToFile));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return streamOut;
  }
}
