package turingmachine.engine;

public interface TuringMachine {
    
    public int getSteps();
    public Boolean isAccepted();
    public String getTape();
}
