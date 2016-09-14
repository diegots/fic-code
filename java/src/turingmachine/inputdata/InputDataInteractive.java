package turingmachine.inputdata;

import turingmachine.exception.BadInputSymsException;
import turingmachine.exception.BadInputArgsException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InputDataInteractive implements InputData {

    private List machineDesc;
    private String pathOutFile = "";
    private String inputSyms = "";
    
    @Override
    public void processInput(String[] args) 
        throws BadInputArgsException, BadInputSymsException, IOException {
        
            parseCommandLineArgs(args);
            // System.out.println("machineDesk: " + machineDesc + ", pathOutFile: " + pathOutFile);
            
            requestInputSyms();
            // System.out.println("inputSyms: " + inputSyms);
    }

    @Override
    public List getMachineDesc() {
        
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
    
    private void parseCommandLineArgs (String[] args) 
            throws BadInputArgsException, IOException {
        // TODO add some more checks on input args
        
        if (args.length == 1) { // only machine description
            
            machineDesc = parseDesc(args[0]);
            
        } else if (args.length == 2) { // machine desc and outFile path
            machineDesc = parseDesc(args[0]);
            pathOutFile = args[1]; 
            
        } else throw new BadInputArgsException();
    }
    
    private List parseDesc (String path) throws IOException {
        
        String dir = "../" + path.split("/")[0];
        String file = path.split("/")[1];
        
        Path filePath = FileSystems.getDefault().getPath(dir, file).toAbsolutePath();
        Scanner scanner = new Scanner(filePath);
        List arc = new ArrayList();
        while (scanner.hasNext()) 
            arc.add(scanner.nextLine());
            
        return arc;
    }
    
    private void requestInputSyms () throws BadInputSymsException {
        
        String syms = "";
        
        System.out.print("Input: ");
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            syms = br.readLine();
        } catch (IOException ex) {
            throw new BadInputSymsException();
        }
        
        inputSyms = syms;
     }
    
}
