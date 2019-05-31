package tfg;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {

        BufferedReader reader = new BufferedReader(new FileReader(args[0]));
        long counter = 0;
        String line;
        try {

            while ((line = reader.readLine()) != null) {
                if (line.split(",").length != 3) {
                    System.out.println("NOOK at " + counter);
                    System.exit(-1);
                }
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Line: " + counter);
        }

        System.out.println("OK");
    }
}
