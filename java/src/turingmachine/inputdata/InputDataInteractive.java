package turingmachine.inputdata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import turingmachine.exception.BadInputArgs;

public class InputDataInteractive implements InputData {

    private String machineDesc;
    private String pathOutFile;
    private String inputSyms;
    
    @Override
    public void processInput(String[] args) {
        try {
            parseCommandLineArgs(args);
            // System.out.println("machineDesk: " + machineDesc + ", pathOutFile: " + pathOutFile);
            
            requestInputSyms();
            // System.out.println("inputSyms: " + inputSyms);
        } catch (BadInputArgs bia) {
            return;
        }
    }

    @Override
    public String getMachineDesc() {
        return machineDesc;
    }

    @Override
    public String getPathOutFile() {
        return pathOutFile;
    }

    @Override
    public String getInputSyms() {
        return inputSyms;
    }
    
    private void parseCommandLineArgs (String[] args) throws BadInputArgs {
        // TODO add some more checks on input args
        
        if (args.length == 1) { // only machine description
            machineDesc = args[0];
            
        } else if (args.length == 2) { // machine desc and outFile path
            machineDesc = args[0];
            pathOutFile = args[1]; 
            
        } else throw new BadInputArgs();
    }
    
    private void requestInputSyms () {
        
        String syms = "";
        
        System.out.print("Input: ");
        
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            syms = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(InputDataInteractive.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        inputSyms = syms;
     }
    
}
