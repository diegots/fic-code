package main.engine;

import main.Conf;
import main.stream.StreamOut;
import main.utils.Messages;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class ProccessRows {

  private final Messages messages;

  public ProccessRows(Messages messages) {
    this.messages = messages;
  }

  public long process(RowTask rowTask, StreamOut streamOut) {
    messages.printMessage("Start " + rowTask.getTaskName());
    final long start = System.currentTimeMillis();

    // 1. read from disk
    try {
      Row row = new Row(
          new FileInputStream(Conf.getConf().getSimilaritiesPath()));
      while (row.hasMoreBits()) {
        messages.printDoing();
        Map<Integer, Integer> readRow = row.readRowDelta();

        // Do something with this row
        List<Integer> computedValues = rowTask.doTheTask(readRow);

        // Store it
        streamOut.write(computedValues);
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
