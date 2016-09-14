package turingmachine.engine;

import java.util.LinkedList;
import java.util.List;

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
        
        // Start from the first character on the tape without any step
        headPosition = 0;
        steps = 0;
    }

    Boolean proccessTape () {
        return null;
    }
    
    int getSteps() {
        return steps;
    }
}
