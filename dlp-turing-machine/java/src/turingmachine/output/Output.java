package turingmachine.output;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Output {

    boolean accept;
    int steps;
    Path outFilePath;
    
    public Output(boolean accept, int steps, String outFilePath) {
        this.accept = accept;
        this.steps = steps;
        
        if ("".equals(outFilePath))
            this.outFilePath = null;
        else 
            this.outFilePath = 
                FileSystems.getDefault().getPath(outFilePath).toAbsolutePath();
    }
    
    private void acceptMsg () {
        
        System.out.print("Accept: ");
        if (accept)
            System.out.println("yes");
        else
            System.out.println("no");
    }
    
    private void stepsDone () {
        System.out.println("Steps: " + steps);
    }
    
    public void writeOutTape (String[] tape, int headPosition) {

        if (outFilePath == null) // Not out file was given
            return;
        
        List lines = new ArrayList();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < headPosition; i++)
            sb.append(tape[i]);

        lines.add(sb); // First line
        sb = new StringBuilder(); // Clear the StringBuilder

        for (int i = headPosition; i < tape.length; i++)
            sb.append(tape[i]);

        lines.add(sb); // Second line
        
        try {            
            Files.write(outFilePath, lines, Charset.forName("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void showResults (String[] tape, int headPosition) {

        acceptMsg();
        stepsDone();
        
//        System.out.print("tape: ");
//        for (int i = 0; i < tape.length; i++) 
//            System.out.print(tape[i]);
//      
//        System.out.println("\nheadPosition: " + headPosition);
        
        writeOutTape(tape, headPosition);
    }
}
