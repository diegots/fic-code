package p1811002;

import it.unimi.dsi.io.InputBitStream;
import org.apache.commons.collections4.map.HashedMap;
import p1811002.utils.Utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class DataElement {

  private final InputBitStream bitStream;

  public DataElement(InputStream stream) {
    Utilities.checkNull(stream);
    bitStream = new InputBitStream(stream);
  }

  public Map<Integer, Integer> readRowDelta () {

    Map<Integer, Integer> res = new HashedMap<>();
    int value = 0;
    int i = 0;
    try {
      while (bitStream.available() > 0) {
        if (Conf.getConf().getRowDelimiter() != value) {
          res.put(i++, bitStream.readDelta());
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
