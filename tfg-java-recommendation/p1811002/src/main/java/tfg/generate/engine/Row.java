package tfg.generate.engine;

import it.unimi.dsi.io.InputBitStream;
import org.apache.commons.collections4.map.LinkedMap;
import tfg.common.util.Utilities;
import tfg.generate.Conf;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Row {

  private final InputBitStream bitStream;
  private boolean hashMoreBits = true;

  public Row(InputStream stream) {
    Utilities.checkNull(stream);
    bitStream = new InputBitStream(stream);
  }

  public Map<Integer, Integer> readRowDelta () {

    Map<Integer, Integer> res = new LinkedMap<>();
    int value;
    int i = 0;
    try {
      while (bitStream.available() > 0) {
        value = bitStream.readDelta();
        if (Conf.get().SIMILARITY_ROWS_DELIMITER != value) {
          res.put(i++, value);
        } else {
          break;
        }
      }
    } catch (EOFException e) {
      // End of file, not an exception
      hashMoreBits = false;
    } catch (IOException e) {
      e.printStackTrace();
    }

    return res;
  }

  public boolean hasMoreBits () {
    return hashMoreBits;
  }

  public void closeStream () {
    try {
      bitStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
