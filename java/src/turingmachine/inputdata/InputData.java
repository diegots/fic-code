package turingmachine.inputdata;

import java.io.IOException;
import java.util.List;
import turingmachine.exception.BadInputSymsException;
import turingmachine.exception.BadInputArgsException;

public interface InputData {
    
    public void processInput (String[] args) 
        throws BadInputArgsException, BadInputSymsException, IOException;
    
    public List getMachineDesc ();
    public String getPathOutFile ();
    public String getInputSyms ();
}
