package main;

import main.utils.Utilities;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LinkedMap;

import java.io.*;
import java.util.*;

public class OrderSimilarities {

  private final Messages messages;
  private StreamOut streamOut;

  public OrderSimilarities(String idxPath, Messages messages) {
    this.messages = messages;
    try {
      streamOut = new StreamOut.DeltaStreamOut(new FileOutputStream(idxPath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public long order() {
    messages.printMessage("Start ordering");
    final long start = System.currentTimeMillis();

    // 1. read from disk
    try {
      Row row = new Row(
          new FileInputStream(Conf.getConf().getSimilaritiesPath()));
      while (row.hasMoreBits()) {
        messages.printDoing();
        Map<Integer, Integer> readRow = row.readRowDelta();

        // 2. order values
        LinkedMap<Integer, Integer> sortedRow = Utilities.sortMapByValue(readRow);

        // 3. store indexes as coded values
        List<Integer> res = new ArrayList<>();

        // Insert only k elements
        int i = 0;
        MapIterator<Integer, Integer> iterator = sortedRow.mapIterator();
        while (iterator.hasNext() && i++ < Conf.getConf().getK()) {
          iterator.next();
          res.add(iterator.getKey());
        }

        res.add(Conf.getConf().getRowDelimiter());
        streamOut.write(res);
      }

      row.closeStream();
      streamOut.close();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    messages.printMessageln(" done.");
    return System.currentTimeMillis() - start;
  }
}