package main.dataset;


import main.utils.Utilities;
import org.apache.commons.collections4.map.LinkedMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FrequencyTable {

  final private List<Integer> frequencyTable;

  public FrequencyTable(List<Integer> unsortedFrequencyTable) {

    LinkedMap<Integer, Integer> aux = new LinkedMap<>();
    for (int i = 0; i<unsortedFrequencyTable.size(); i++) {
      aux.put(i, unsortedFrequencyTable.get(i));
    }

    LinkedMap<Integer, Integer> sortedFreqTable = Utilities.sortMapByValue(aux);

    frequencyTable = new ArrayList<>(sortedFreqTable.asList());
    Collections.reverse(frequencyTable);
  }

  public int encodeValue (int rawValue) {
    return frequencyTable.indexOf(rawValue);
  }

  public int decodeValue (int codedValue) {
    return frequencyTable.get(codedValue);
  }
}
