package turingmachine.engine;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import turingmachine.exception.BadMovementException;
import turingmachine.exception.NextTransitionException;
import turingmachine.inputdata.InputData;

public class TuringMachineImpl implements TuringMachine {

    private final MachineDescription machineDescription;
    private final Tape tape;
    private Boolean accepted;
    
    private String inputSyms;
    
    public TuringMachineImpl(InputData inputData) {
        
        // Store the Machine description
        machineDescription = new MachineDescription(inputData.getMachineDesc());
        
        // Store the user given tape symbols
        tape = new Tape(inputData.getInputSyms());
        
        // Process the tape with the data received
        //accepted = tape.proccessTape();
        process();
    }
    
    private void process() {
        String firstT = ""; 
        try {
            firstT = machineDescription.getFirstTransition(); // Dest, Wr Sym, Mov
        } catch (NextTransitionException ex) {
            System.err.println("Bad machine transition");
        }
        String readSym = tape.read();
        String readT = firstT;
        
        while (!"H".equals(readT.charAt(0) + "")) { // final state is H

            try {
                tape.writeAndMove(readT.charAt(1) + "", readT.charAt(2) + "");
                readT = machineDescription.next(readT.charAt(0) + "", tape.read());
            
            } catch (BadMovementException ex) {
                System.err.println("BadMovementException");
            } catch (NextTransitionException ex) {
                System.err.println("readT" + readT);
                System.err.println("NextTransitionException");
            }
        }
        System.out.println("Finishing process. Steps: " + getSteps());
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
