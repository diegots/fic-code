package main;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

public class ProccessRows {

  private final Messages messages;
  private StreamOut streamOut;

  public ProccessRows(String destPath, Messages messages) {
    this.messages = messages;
    try {
      streamOut = new StreamOut.DeltaStreamOut(new FileOutputStream(destPath));
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }

  public long process(RowTask rowTask) {
    messages.printMessage("Start ordering");
    final long start = System.currentTimeMillis();

    // 1. read from disk
    try {
      Row row = new Row(
          new FileInputStream(Conf.getConf().getSimilaritiesPath()));
      while (row.hasMoreBits()) {
        messages.printDoing();
        Map<Integer, Integer> readRow = row.readRowDelta();

        // Do something with this row
        List<Integer> newRow = rowTask.doTheTask(readRow);

        newRow.add(Conf.getConf().getRowDelimiter());
        streamOut.write(newRow);
      }

      streamOut.close();
      row.closeStream();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    messages.printMessageln(" done.");
    return System.currentTimeMillis() - start;
  }

}
