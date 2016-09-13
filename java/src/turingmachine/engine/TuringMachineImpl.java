package turingmachine.engine;

import turingmachine.inputdata.InputData;

public class TuringMachineImpl implements TuringMachine {

    private MachineDescription machineDescription;
    private String machineDesc;
    private String inputSyms;
    
    public TuringMachineImpl(InputData inputData) {
        
        machineDescription = new MachineDescription(inputData.getMachineDesc());
        
        // inputSyms = inputData.getInputSyms();
        
        // proccess();
    }
    
    private void proccess () {
        
    }

    @Override
    public String getSteps() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String isAccepted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTape() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
