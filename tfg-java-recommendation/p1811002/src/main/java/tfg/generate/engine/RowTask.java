package tfg.generate.engine;

import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LinkedMap;
import tfg.common.util.Utilities;
import tfg.generate.Conf;
import tfg.generate.model.FrequencyTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface RowTask {

  List<Integer> doTheTask (Map<Integer, Integer> row);
  String getTaskName ();

  class Order implements RowTask {

    static final String TASK_NAME = "Ordering";

    @Override
    public List<Integer> doTheTask(Map<Integer, Integer> row) {

      // Sort values
      LinkedMap<Integer, Integer> sortedRow = Utilities.sortMapByValue(row);

      // Store indexes as coded values but insert only the k-top elements
      List<Integer> result = new ArrayList<>();
      int i = 0;
      MapIterator<Integer, Integer> iterator = sortedRow.mapIterator();

      while (iterator.hasNext() && i++ < Conf.get().getK()) {
        iterator.next();
        result.add(iterator.getKey());
//        System.err.println("    Idx: " + iterator.getKey()
//            + ", value: " + iterator.getValue());
      }

      // Add row delimiter
      result.add(Conf.get().USERS_ROWS_DELIMITER);

      return result;
    }

    @Override
    public String getTaskName() {
      return TASK_NAME;
    }
  }

  class FrequencyCompute implements RowTask {

    static final String TASK_NAME = "Computing frequency";
    private final List<Integer> frequencies;

    public FrequencyCompute() {

      // Initialize frquencies List
      frequencies = new ArrayList<>();
      for (int i = 0; i<= Conf.get().SIMILARITY_MAX_VALUE; i++) {
        frequencies.add(0);
      }

    }

    @Override
    public List<Integer> doTheTask(Map<Integer, Integer> row) {

      Iterator<Integer> rowIterator = row.keySet().iterator();
      while (rowIterator.hasNext()) {
        int similarity = row.get(rowIterator.next());
        Integer count = frequencies.get(similarity);
        count++;
        frequencies.set(similarity, count);

      }
      return frequencies;
    }

    @Override
    public String getTaskName() {
      return TASK_NAME;
    }
  }

  class ReassignIds implements RowTask {

    static final String TASK_NAME = "Reassign Ids";
    final FrequencyTable frequencyTable;

    public ReassignIds(FrequencyTable frequencyTable) {
      this.frequencyTable = frequencyTable;
    }

    @Override
    public List<Integer> doTheTask(Map<Integer, Integer> row) {

      List<Integer> result = new ArrayList<>();

      // Receive a fresh readSeekable row
      LinkedMap<Integer, Integer> orderedMap = null;
      if (row instanceof LinkedMap) {
        orderedMap = (LinkedMap<Integer, Integer>) row;
      }

      MapIterator<Integer, Integer> iterator = orderedMap.mapIterator();
      while (iterator.hasNext()) {
        iterator.next();
        result.add(frequencyTable.encodeValue(iterator.getValue()));
      }

      // Add row delimiter
      result.add(Conf.get().SIMILARITY_ROWS_DELIMITER);

      return result;
    }

    @Override
    public String getTaskName() {
      return TASK_NAME;
    }
  }
}
