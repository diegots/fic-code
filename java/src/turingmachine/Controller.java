package turingmachine;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import turingmachine.engine.TuringMachine;
import turingmachine.engine.TuringMachineImpl;
import turingmachine.exception.BadInputArgsException;
import turingmachine.exception.BadInputSymsException;
import turingmachine.inputdata.InputData;
import turingmachine.inputdata.InputDataInteractive;

public class Controller {

    private InputData inputData;
    private TuringMachine tm;

    public void run(String[] args) {
        
        inputData = new InputDataInteractive();
        
        try {
            inputData.processInput(args);
        } catch (BadInputArgsException ex) {
            System.err.println("BadInputArgsException");
            
        } catch (BadInputSymsException ex ) {
            System.err.println("BadInputSymsException");
        
        } catch (IOException ex) {
            System.err.println("IOException");
        }
        
        // First get the user input and use it to initiate the Turing Machine.
        tm = new TuringMachineImpl(inputData);
        
        // Run the Machine
        
        // Get results
        
        
    }

    
    
}
