package turingmachine.controller;

import turingmachine.inputdata.InputData;
import turingmachine.inputdata.InputDataInteractive;

public class Controller {

    private InputData inputData;

    public void run(String[] args) {
        
        inputData = new InputDataInteractive();
        inputData.processInput(args);
        
    }

    
    
}
