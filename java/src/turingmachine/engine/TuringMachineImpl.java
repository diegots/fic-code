package turingmachine.engine;

import java.util.List;
import turingmachine.inputdata.InputData;

public class TuringMachineImpl implements TuringMachine {

    private final MachineDescription machineDescription;
    private Tape tape;
    private String currentState;
    private Boolean accepted;
    
    private String inputSyms;
    
    public TuringMachineImpl(InputData inputData) {
        
        // Store the Machine description
        machineDescription = new MachineDescription(inputData.getMachineDesc());
        
        // Store the user given tape symbols
        tape = new Tape(inputData.getInputSyms());
        
        // Process the tape with the data received
        accepted = tape.proccessTape();
    }
    
    @Override
    public int getSteps() {
        return tape.getSteps();
    }

    @Override
    public Boolean isAccepted() {
        return accepted;
    }

    @Override
    public String getTape() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
