package tfg;

import java.io.*;

public class MaxUserId {

    final File file;
    LinesHandler linesHandler;


    public MaxUserId(File file, String lineContentDelimiter) {
        this.file = file;
        linesHandler = new LinesHandlerImpl(lineContentDelimiter);
    }


    Integer getMaxUserId() {

        if (linesHandler.getResultsAfterFile() == null) {
            Utilities.processByLine(file, linesHandler);
        }

        return  linesHandler.getResultsAfterFile();
    }


    class LinesHandlerImpl implements LinesHandler {

        private Integer maxUserId;
        private String delimiter;


        public LinesHandlerImpl(String delimiter) {
            this.delimiter = delimiter;
            maxUserId = 0;
        }


        @Override
        public void handle(String line) {

            int currentUserId = Integer.valueOf((line.split(delimiter))[0]);
            if (currentUserId > maxUserId) {
                maxUserId = currentUserId;
            }
        }


        @Override
        public Integer getResultsAfterFile() {
            return maxUserId;
        }
    }
}
