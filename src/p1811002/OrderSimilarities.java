package p1811002;

import p1811002.utils.Utilities;

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
            DataElement dataElement = new DataElement(
                new FileInputStream(Conf.getConf().getSimilaritiesPath()));
            while (dataElement.hasMoreBits()) {
                messages.printDoing();
                Map<Integer, Integer> dataRow = dataElement.readRowDelta();

                // 2. order values
                Map<Integer, Integer> sortedDataRow = Utilities.sortMapByValue(dataRow);

                Iterator<Integer> it = sortedDataRow.keySet().iterator();
                while (it.hasNext())
                    System.out.println("Id: " + it.next());

                // 3. store indexes as coded values
                List<Integer> res = new ArrayList<>(sortedDataRow.keySet());
                res.add(Conf.getConf().getRowDelimiter());
                streamOut.write(res);

            }

            dataElement.closeStream();
            streamOut.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        }

        messages.printMessageln(" done.");
        return System.currentTimeMillis() - start;
    }
}