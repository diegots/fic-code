package tfg.generate.engine;

import tfg.common.stream.StreamOut;
import tfg.generate.Conf;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public class ProccessRows {

  public long process(RowTask rowTask, StreamOut streamOut) {
    Conf.get().getMessages().printMessage("Start " + rowTask.getTaskName());
    final long start = System.currentTimeMillis();

    // 1. readSeekable from disk
    try {
      Row row = new Row(
          new FileInputStream(Conf.get().getEncodedSimMatPath()));

      List<Integer> computedValues = null;
      while (row.hasMoreBits()) {
        Conf.get().getMessages().printDoing();
        Map<Integer, Integer> readRow = row.readRowDelta();

        // Do something with this row
        computedValues = rowTask.doTheTask(readRow);

        // Store it
        streamOut.write(computedValues);
      }

      streamOut.close();
      row.closeStream();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    Conf.get().getMessages().printMessageln(" done.");
    return System.currentTimeMillis() - start;
  }

}
