package turingmachine.engine;

import turingmachine.exception.BadMovementException;
import turingmachine.exception.NextTransitionException;
import turingmachine.inputdata.InputData;

public class TuringMachineImpl implements TuringMachine {

    private final MachineDescription machineDescription;
    private final Tape tape;
    private Boolean accepted;
    
    public TuringMachineImpl(InputData inputData) {
        
        // Store the Machine description
        machineDescription = new MachineDescription(inputData.getMachineDesc());
        
        // Store the user given tape symbols
        tape = new Tape(inputData.getInputSyms());
        
        // Solve the problem!
        process();
    }
    
    private void process() {
        accepted = true;
        
        // Set the machine in the initial state
        String readT = ""; 
        try {
            readT = machineDescription.getFirstTransition(); // Dest, Wr Sym, Mov
        } catch (NextTransitionException ex) {
            System.err.println("Bad machine transition");
        }
        
        // Loop until the H state is reached or a NextTransitionException is
        // raised, which means that the input can't be accepted.
        do {
            try {
                tape.writeAndMove(readT.charAt(1) + "", readT.charAt(2) + "");
                readT = machineDescription.next(readT.charAt(0) + "", tape.read());
            
            } catch (BadMovementException ex) {
                System.err.println("BadMovementException");
            
            } catch (NextTransitionException ex) {
                // TODO unde the last transition
                accepted = false;
                break;
            }
        } while (!"H".equals(readT.charAt(0) + ""));
        
        // Perform the transition to H state
        if (accepted) {
            try {
                tape.writeAndMove(readT.charAt(1) + "", readT.charAt(2) + "");
            } catch (BadMovementException ex) {
                System.err.println("BadMovementException");
            }
        }
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
    public String[] getTape() {
        return tape.getTape();
    }

    @Override
    public int getHeadPosition() {
        return tape.getHeadPosition();
    }
}
