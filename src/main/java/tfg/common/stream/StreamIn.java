package tfg.common.stream;

import it.unimi.dsi.io.InputBitStream;
import tfg.generate.Conf;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to readSeekable data stored in a stream.
 */
public interface StreamIn {

  /**
   * Reads bytes from the input stream.
   */
  List<Integer> read();

  /**
   * Considers the stream as Delta encoded.
   */
  class DeltaStreamIn implements StreamIn {

    private final InputBitStream bitStream;

    public DeltaStreamIn(InputStream inputStream) {
      this.bitStream  = new InputBitStream(inputStream);
    }

    @Override
    public List<Integer> read() {
      List<Integer> res = new ArrayList<>();

      try {
        while (bitStream.hasNext()) {
          res.add(bitStream.readDelta());
        }
      } catch (EOFException e) {
        // This is not an exception, but the signal that the input data was readSeekable completelly
      } catch (IOException e) {
        e.printStackTrace();
      }

      return res;
    }
  }

  class PlainStreamIn implements StreamIn {
    private final InputStream inputStream;

    public PlainStreamIn(InputStream inputStream) {
      this.inputStream  = inputStream;
    }

    @Override
    public List<Integer> read() {
      List<Integer> res = new ArrayList<>();

      int value;
      while (true) {
        try {
          if ((value = inputStream.read()) == -1) {
            break;
          }
          res.add(value);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      return res;
    }
  }

  class DeltaStreamInRow implements StreamIn {

    private final FileInputStream fileInputStream;
    private final InputBitStream bitStream;


    public DeltaStreamInRow(FileInputStream fileInputStream) {
      this.fileInputStream = fileInputStream;
      this.bitStream  = new InputBitStream(fileInputStream);
    }

    public List<Integer> read(int requestedIdx) {

      List<Integer> similaritiesRow = new ArrayList<>();

      // Initialize state
      reset();

      int similarity;
      int actualRowIdx = 0;
      while (bitStream.hasNext()) {
        try {
          similarity = bitStream.readDelta();
          if (Conf.get().SIMILARITY_ROWS_DELIMITER == similarity) {
            actualRowIdx++;
          } else if (actualRowIdx == requestedIdx) {
            similaritiesRow.add(similarity);
          }

          if (actualRowIdx > requestedIdx) {
            break;
          }

        } catch (EOFException e) {
          // End Of File
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      return similaritiesRow;
    }

    @Override
    public List<Integer> read() {
      return null;
    }

    public void reset () {
      bitStream.flush();
      try {
        fileInputStream.getChannel().position(0);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
