package tfg;

import java.io.*;

public class Utilities {

    /**
     * Process a text file line by line. ProcessFile object is in charge of
     * manage the line contents, store intermediate values, etc.
     * @param file to be read from.
     * @param processFile manages read lines
     */
    public static void processByLine(File file, ProcessFile processFile) {
        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader(file));

            while ((line = br.readLine()) != null) {
                processFile.treatLine(line);
            }

            br.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
