package turingmachine.engine;

import java.util.Iterator;
import java.util.List;
import turingmachine.exception.NextTransitionException;

public class MachineDescription {

    private List machineDesc;
    
    public MachineDescription(List machineDesc) {
        // TODO make a deep copy of the list and not a pointer ref
        this.machineDesc = machineDesc;
    }

    public String next (String origState, String readSym) 
        throws NextTransitionException {
        
        Iterator ite = machineDesc.iterator();
        while (ite.hasNext()) {
            String next = (String) ite.next();
            Character fst = next.charAt(0);
            Character thrd = next.charAt(2);
            
            if (fst.equals(origState.charAt(0)) 
                && thrd.equals(readSym.charAt(0)))
            
            return 
                Character.toString(next.charAt(1))
                + Character.toString(next.charAt(3))
                + Character.toString(next.charAt(4));
        }
        
        /* In case that no valid transition is found, raise and exception.
         * Is spected to always be able to make a transition in a any valid
         * Turing Machine graph. */
        throw new NextTransitionException();
    }
        
}
