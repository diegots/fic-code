package tfg;

import java.io.File;

public class MaxUserId {


    public static final int NON_VALID_USER_ID = 0;

    private final File file;
    private LinesHandler linesHandler;


    public MaxUserId(File file, String lineContentDelimiter) {
        this.file = file;
        linesHandler = new LinesHandlerImpl(lineContentDelimiter);
    }


    Integer getMaxUserId() {

        if (linesHandler.getResults() == NON_VALID_USER_ID) {
            Utilities.processByLine(file, linesHandler);
        }

        return  linesHandler.getResults();
    }


    class LinesHandlerImpl implements LinesHandler {

        private Integer maxUserId;
        private String delimiter;


        public LinesHandlerImpl(String delimiter) {
            this.delimiter = delimiter;
            maxUserId = NON_VALID_USER_ID;
        }


        @Override
        public void handle(String line) {

            int currentUserId = Integer.valueOf((line.split(delimiter))[0]);
            if (currentUserId > maxUserId) {
                maxUserId = currentUserId;
            }
        }


        @Override
        public Integer getResults() {
            return maxUserId;
        }
    }
}
