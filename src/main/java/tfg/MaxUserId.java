package tfg;

import java.io.*;

public class MaxUserId {

    final File file;
    ProcessFile processFile;


    public MaxUserId(File file, String fileDelimiter) {
        this.file = file;
        processFile = new ProcessFileImpl(fileDelimiter);
    }


    Integer getMaxUserId() {

        if (processFile.getResultsAfterFile() == null) {
            Utilities.processByLine(file, processFile);
        }

        return  processFile.getResultsAfterFile();
    }


    class ProcessFileImpl implements ProcessFile {

        private Integer maxUserId;
        private String delimiter;


        public ProcessFileImpl(String delimiter) {
            this.delimiter = delimiter;
            maxUserId = 0;
        }


        @Override
        public void treatLine(String line) {

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
