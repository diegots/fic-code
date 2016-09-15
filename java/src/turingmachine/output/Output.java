package turingmachine.output;

public class Output {

    boolean accept;
    int steps;
    String outFilePath;
    
    public Output(boolean accept, int steps, String outFilePath) {
        this.accept = accept;
        this.steps = steps;
        this.outFilePath = outFilePath;
    }
    
    private void acceptMsg () {
        
        System.out.print("Accept: ");
        if (accept)
            System.out.println("yes");
        else
            System.out.println("no");
    }
    
    private void stepsDone () {
        System.out.println("Steps: " + steps);
    }
    
    public void finalTape (String tape) {
        
    }
    
    public void showResults () {
        acceptMsg();
        stepsDone();
    }
}
