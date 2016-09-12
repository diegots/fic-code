package turingmachine.inputdata;

public interface InputData {
    public void processInput (String[] args);
    
    public String getMachineDesc ();
    public String getPathOutFile ();
    public String getInputSyms ();
}
