package main;

import main.utils.Utilities;
import org.apache.commons.collections4.MapIterator;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public interface RowTask {

  List<Integer> doTheTask (Map<Integer, Integer> row);

  class Order implements RowTask {

    @Override
    public List<Integer> doTheTask(Map<Integer, Integer> row) {

      // 2. process values
      LinkedMap<Integer, Integer> sortedRow = Utilities.sortMapByValue(row);

      // Store indexes as coded values but insert only the k-top elements
      List<Integer> result = new ArrayList<>();
      int i = 0;
      MapIterator<Integer, Integer> iterator = sortedRow.mapIterator();
      while (iterator.hasNext() && i++ < Conf.getConf().getK()) {
        iterator.next();
        result.add(iterator.getKey());
      }
      return result;
    }
  }

  class FrequencyCompute implements RowTask {

    private final List<Integer> frequencies = new ArrayList<>();

    @Override
    public List<Integer> doTheTask(Map<Integer, Integer> row) {

      Iterator<Integer> rowIterator = row.keySet().iterator();
      while (rowIterator.hasNext()) {
        int rowValue = row.get(rowIterator.next());

        Integer count = frequencies.get(rowValue);
        if (null == count) {
          frequencies.add(rowValue, 1);
        } else {
          frequencies.add(rowValue, count++);
        }
      }
      return frequencies;
    }
  }

  class ReassignIds implements RowTask {

    @Override
    public List<Integer> doTheTask(Map<Integer, Integer> row) {
      return null;
    }
  }
}
