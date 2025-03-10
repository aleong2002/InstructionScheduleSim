import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

public class PipelineSimulator {
    private List<Instruction> dispatchList; //Dispatch Queue
    private List<Instruction> issueList; //Scheduling Queue
    private List<Instruction> executeList; //Functional Units
    private RegisterFile registerFile;
    private FakeROB fakeROB;
    private List<Instruction> instructionsToRemove;
    private int N, S;
    private final int maxDispatchSize = 2 * N;
    int fetchCount;
    int issueCount;
	int schedulingCount;
	int dispatchCount;
	int tag;
	int cycle;
    int[] registerFileTags = new int[128];

    public PipelineSimulator(String[] args) {
        S = Integer.parseInt(args[1]);
        N = Integer.parseInt(args[0]);
        this.dispatchList = new ArrayList<>();
        this.issueList = new ArrayList<>(S); //args[1] 
        this.executeList = new ArrayList<>(N + 1); // args[0] + 1
        this.registerFile = new RegisterFile();
        this.fakeROB = new FakeROB();
        this.instructionsToRemove = new ArrayList<>();

        this.issueCount = 0;
		this.schedulingCount = 0;
		this.dispatchCount = 0;
		this.fetchCount = N;
		this.tag = 0;
		this.cycle = 0;
    }

    // FakeRetire()

    // Execute()
    public void Execute() {
        instructionsToRemove.clear();

        for (Instruction instruction : executeList) {
            if (instruction.getRemainingCycles() == 1) {
                instructionsToRemove.add(instruction);
                instruction.advanceState();
                // matching tags? update dest register file
                if (instruction.getDest() != -1 && registerFileTags[instruction.getDest()] == instruction.getTag()) {
                    registerFile.markReady(instruction.getDest());
                }
                // waking up dependent instructions
                for (Instruction entry : fakeROB.entries) {
                    if (entry.isSrc1Tagged && entry.getSrc1() == instruction.getTag()) {
                        entry.setSrc1Ready();
                    }
                    if (entry.isSrc2Tagged && entry.getSrc2() == instruction.getTag()) {
                        entry.setSrc2Ready();
                    }
                }

            } else {
                instruction.decrementCycle();
            }

        }
        executeList.removeAll(instructionsToRemove);
    }

    // Issue()

    // Dispatch()
    public void Dispatch() {
        instructionsToRemove.clear();
        for (Instruction instruction : dispatchList) {
            if (instruction.getState() == Instruction.State.ID && schedulingCount < S) {
                instructionsToRemove.add(instruction);
                dispatchCount--;
                issueList.add(instruction);
                schedulingCount++;
                instruction.advanceState(); // set state to IS

                if (instruction.getSrc1() != -1) {
                    if (registerFile.isReady(instruction.getSrc1())) {
                        instruction.setSrc1Ready();
                    }
                    else {
                        instruction.setSrc1NotReady();
                        instruction.setSrc1(registerFileTags[instruction.getSrc1()]);
                        instruction.isSrc1Tagged = true;
                    }
                }
    
                if (instruction.getSrc2() != -1) {
                    if (registerFile.isReady(instruction.getSrc2())) {
                        instruction.setSrc2Ready();
                    }
                    else {
                        instruction.setSrc2NotReady();
                        instruction.setSrc2(registerFileTags[instruction.getSrc2()]);
                        instruction.isSrc2Tagged = true;
                    }
                }
    
                if (instruction.getDest() != -1) {
                    registerFileTags[instruction.getDest()] = instruction.getTag();
                    registerFile.markNotReady(instruction.getDest());
                }
    
            }
            if (instruction.getState() == Instruction.State.IF) {
                instruction.advanceState(); // set state to ID
            }
        }
        dispatchList.removeAll(instructionsToRemove);
    }

    //Fetch()

    // AdvanceCycle()
    public void AdvanceCycle() {
        for (Instruction instruction : fakeROB.entries) {
            switch (instruction.getState()) {
                case IF:
                    if (instruction.IFCycle == -1) {
                        instruction.IFCycle = cycle;
                    }
                    instruction.IFTime++;
                    break;
                case ID:
                    if (instruction.IDCycle == -1) {
                        instruction.IDCycle = cycle;
                    }
                    instruction.IDTime++;
                    break;
                case IS:
                    if (instruction.ISCycle == -1) {
                        instruction.ISCycle = cycle;
                    }
                    instruction.ISTime++;
                    break;
                case EX:
                    if (instruction.EXCycle == -1) {
                        instruction.EXCycle = cycle;
                    }
                    instruction.EXTime++;
                    break;
                case WB:
                    if (instruction.WBCycle == -1) {
                        instruction.WBCycle = cycle;
                    }
                    instruction.WBTime++;
                    break;
                default:
                    break;
            }
        }
        cycle++;
    }
    public static void main(String[] args) {
        PipelineSimulator simulator = new PipelineSimulator(args);
        try {
            do {
            //simulator.FakeRetire();
            simulator.Execute();
            //simulator.Issue();
            //simulator.Dispatch();
            //simulator.Fetch();
            //simulator.AdvanceCycle();
        } while (simulator.fakeROB.entries.size() > 0 || scanner.hasNext());
		} catch (FileNotFoundException e) {
			throw new FileNotFoundException("Trace file does not exist"); 
		}

		simulator.cycle--;
    }
}
