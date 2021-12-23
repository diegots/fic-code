package tfg.generate.active_users;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteFile {

    private BufferedWriter bufferedWriter;


    public WriteFile(String file) {
        try {
            this.bufferedWriter = new BufferedWriter(new FileWriter(file, true));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void write(String line) {
        try {
            bufferedWriter.append(line + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void closeFile() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
