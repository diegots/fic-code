package p1811002;

import p1811002.utils.Utilities;

import java.io.*;
import java.util.*;

public class OrderSimilarities {

    private final String similaritiesPath;
    private final String idxPath;
    private final Messages messages;

    public OrderSimilarities(String similaritiesPath, String idxPath, Messages messages) {
        this.similaritiesPath = similaritiesPath;
        this.idxPath = idxPath;
        this.messages = messages;
    }

    public long order() {
        messages.printMessage("Start ordering");
        final long start = System.currentTimeMillis();

        // 1. read from disk
        try {
            DataElement dataElement = new DataElement(
                new FileInputStream(Conf.getConf().getSimilaritiesPath()));
            while (dataElement.hasMoreBits()) {
                messages.printDoing();
                Map<Integer, Integer> dataRow = dataElement.readRowDelta();

                // 2. order values
                Map<Integer, Integer> sortedDataRow = Utilities.sortMapByValue(dataRow);

                // 3. store indexes as coded values
                List<Integer> res = new ArrayList<>(sortedDataRow.keySet());
                res.add(Conf.getConf().getRowDelimiter());

                // TODO write ordered indexes with compression
                Utilities.writeLine(Conf.getConf().getOrderedIndexesPath(), res);


            }

            dataElement.closeStream();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        messages.printMessageln(" done.");
        return System.currentTimeMillis() - start;
    }
}