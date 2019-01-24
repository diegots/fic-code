package tfg.common.stream;

import it.unimi.dsi.io.InputBitStream;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to read data stored in a stream.
 */
public interface StreamIn {

  /**
   * Reads bytes from the input stream.
   */
  List<Integer> read(InputStream inputStream);

  /**
   * Considers the stream as Delta encoded.
   */
  class DeltaStreamIn implements StreamIn {

    @Override
    public List<Integer> read(InputStream inputStream) {
      List<Integer> res = new ArrayList<>();

      InputBitStream bitStream = new InputBitStream(inputStream);

      try {
        while (bitStream.hasNext()) {
          res.add(bitStream.readDelta());
        }
        bitStream.close();

      } catch (EOFException e) {
        // This is not an exception, but the signal that the input data was read completelly
      } catch (IOException e) {
        e.printStackTrace();
      }

      return res;
    }

  }
}
