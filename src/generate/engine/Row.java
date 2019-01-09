package similarity.engine;

import it.unimi.dsi.io.InputBitStream;
import similarity.Conf;
import similarity.utils.Utilities;
import org.apache.commons.collections4.map.LinkedMap;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class Row {

  private final InputBitStream bitStream;

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
        if (Conf.getConf().getRowDelimiter() != value) {
          res.put(i++, value);
        } else {
          break;
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return res;
  }

  public boolean hasMoreBits () {
    boolean result = false;
    try {
      result = bitStream.available() > 0;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return result;
  }

  public void closeStream () {
    try {
      bitStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
