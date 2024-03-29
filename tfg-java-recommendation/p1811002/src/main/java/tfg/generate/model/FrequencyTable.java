package tfg.generate.model;

import org.apache.commons.collections4.map.LinkedMap;
import tfg.common.util.Utilities;

import java.util.List;

public class FrequencyTable {

  final private List<Integer> frequencyTable;

  public FrequencyTable(List<Integer> unsortedFrequencyTable) {

    LinkedMap<Integer, Integer> aux = new LinkedMap<>();
    for (int i = 0; i<unsortedFrequencyTable.size(); i++) {
      aux.put(i, unsortedFrequencyTable.get(i));
    }

    frequencyTable = Utilities.sortMapByValue(aux).asList();
  }

  public int encodeValue (int rawValue) {
    return frequencyTable.indexOf(rawValue);
  }

  public int decodeValue (int codedValue) {
    return frequencyTable.get(codedValue);
  }
}
