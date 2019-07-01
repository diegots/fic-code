package tfg.evaluation;

import java.io.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TextFileReader {

    public static void readLines(File file, ReadFileInterface readFile) {
        try {

            String line;
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                readFile.doSomething(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    interface ReadFileInterface {

        void doSomething(String line);


        class ReadFileInterfaceImpl implements ReadFileInterface {

            private final Map<Integer, List<Integer>> recommendations;


            public ReadFileInterfaceImpl() {
                recommendations = new HashMap<>();
            }


            public Map<Integer, List<Integer>> getRecommendations() {
                return Collections.unmodifiableMap(recommendations);
            }


            @Override
            public void doSomething(String line) {
                

            }
        }
    }
}

