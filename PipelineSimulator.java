import java.util.ArrayList;
import java.util.List;

public class PipelineSimulator {
    private List<Instruction> dispatchList; //Dispatch Queue
    private List<Instruction> issueList; //Scheduling Queue
    private List<Instruction> executeList; //Functional Units

    private final int maxDispatchSize; // 2N

    public PipelineSimulator(int N, int S) {
        this.maxDispatchSize = 2 * N;
        this.dispatchList = new ArrayList<>();
        this.issueList = new ArrayList<>(S);
        this.executeList = new ArrayList<>(N + 1);
    }

    // FakeRetire()

    // Execute()
    public void Execute() {
        List<Instruction> completed = new ArrayList<>();

        for (Instruction instruction : executeList) {
            instruction.decrementLatency();
            if(instruction.executionCheck()){  // assuming latency = 0 means it is complete, not sure if it goes here??
                completed.add(instruction);
            }
        }

        for (Instruction instruction : completed) {
            // remove from execute_list
            executeList.remove(instruction);

            // set to WB
            instruction.updateInstructionState(instruction);

            // Update the register file: Mark destination register as ready
            /* if (instruction.getPhysicalDestReg() != null) {
                registerFile.markReady(instruction.getPhysicalDestReg()); // Example method to set ready flag
            }

            // Wake up dependent instructions
            wakeUpDependentInstructions(instruction); */

        }
    }

    // Issue()

    // Dispatch()

    //Fetch()

    // AdvanceCycle()
}
