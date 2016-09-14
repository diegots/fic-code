package turingmachine.engine;

import java.util.LinkedList;
import java.util.List;
import turingmachine.exception.BadMovementException;

class Tape {

    private final List tape;
    private int headPosition;
    private int steps;
    
    public Tape(String inputSyms) {
        
        // Sets the user given symbols to the tape
        tape = new LinkedList();
        
        // In case user there is no user input, use a B symbol
        if (inputSyms.length() == 0) {
            tape.add("B");
        
        // User input symbols as tape initial state
        } else {
            for (int i = 0; i < inputSyms.length(); i++) {
                String s = Character.toString(inputSyms.charAt(i));
                tape.add(s);
            }
        }
        
        // Initial values
        headPosition = 0;
        steps = 1;
    }
    
    public String read () {
        return (String) tape.get(headPosition);
    }
    
    public int getSteps () {
        return steps;
    }
    
    public void writeAndMove (String symbol, String movement) 
        throws BadMovementException {
        
        tape.add(headPosition, symbol);
        tape.remove(headPosition+1);
        
        steps++;
        
        if ("L".equals(movement)) {
            headPosition--;
            if (headPosition < 0) {
                headPosition = 0;
                tape.add(headPosition, "B");
            }
        
        } else if ("R".equals(movement)) {
            headPosition++;
            if (headPosition == tape.size()) {
                tape.add("B");
            }
        }
        
        else throw new BadMovementException();
    }
}
