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
    private final int maxDispatchSize = 2 * N; // 2N
    private int fetchBandwidth;
    int issueCount;
	int schedulingCount;
	int dispatchCount;
	int tag;
	int cycle;
    int[] registerFileTags = new int[128];
    ///boolean[] isRegisterFileReady = new boolean[128];

    public PipelineSimulator(int N, int S) {

        this.dispatchList = new ArrayList<>();
        this.issueList = new ArrayList<>(S);
        this.executeList = new ArrayList<>(N + 1);
        this.registerFile = new RegisterFile();
        this.fakeROB = new FakeROB();
        this.instructionsToRemove = new ArrayList<>();

        this.issueCount = 0;
		this.schedulingCount = 0;
		this.dispatchCount = 0;
		this.fetchBandwidth = N;
		this.tag = 0;
		this.cycle = 0;
    }

    // FakeRetire()
    public void FakeRetire() {
        instructionsToRemove.clear();
        for (Instruction entry : fakeROB.entries) {
            if (entry.getState() == Instruction.State.WB) {
                System.out.println(entry..getTag() + " fu{" + entry.getOpType() + "} src{" + entry.getSrc1() + ","
                + entry.getSrc2() + "} dst{" + entry.getDest() + "} IF{" + entry.IFCycle + "," + entry.IFDuration 
				+ "} ID{" + entry.IDCycle + "," + entry.IDDuration + "} IS{" + entry.ISCycle + "," + entry.ISDuration 
				+ "} EX{" + entry.EXCycle + "," + entry.EXDuration + "} WB{" + entry.WBCycle + "," + entry.WBDuration + "}");
						
				instructionsToRemove.add(entry);
            }
            else { break; }
        }
        (fakeROB.entries).removeAll(instructionsToRemove);
    }

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
    public void Issue() {
        issueCount = N + 1;
        instructionsToRemove.clear();

        for (Instruction instruction : issueList) {
            if (instruction.isReady() && issueCount > 0) {
                instructionsToRemove.add(instruction);
                executeList.add(instruction);
                instruction.advanceState();
                schedulingCount--;
                issueCount--;
            }
        }
        issueList.removeAll(instructionsToRemove);
    }

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
    public void Fetch() {
        while (fetchBandwidth > 0 && dispatchCount < maxDispatchSize && scanner.hasNext()) {
            // scanner = new Instruction();
            fakeROB.addInstruction(instruction);
            dispatchList.append(instruction);
            dispatchCount++;
            tag++;
            fetchBandwidth--;
        }
    }
   
    // AdvanceCycle()
    public void AdvanceCycle() {
        for (Instruction instruction : fakeROB.entries) {
            switch (instruction.getState()) {
                case IF:
                    if (instruction.IFCycle == -1) {
                        instruction.IFCycle = cycle;
                    }
                    instruction.IFDuration++;
                    break;
                case ID:
                    if (instruction.IDCycle == -1) {
                        instruction.IDCycle = cycle;
                    }
                    instruction.IDDuration++;
                    break;
                case IS:
                    if (instruction.ISCycle == -1) {
                        instruction.ISCycle = cycle;
                    }
                    instruction.ISDuration++;
                    break;
                case EX:
                    if (instruction.EXCycle == -1) {
                        instruction.EXCycle = cycle;
                    }
                    instruction.EXDuration++;
                    break;
                case WB:
                    if (instruction.WBCycle == -1) {
                        instruction.WBCycle = cycle;
                    }
                    instruction.WBDuration++;
                    break;
                default:
                    break;
            }
        }
        cycle++;
    }
    public static void main(String[] args) {
        do {
            FakeRetire();
            Execute();
            Issue();
            Dispatch();
            Fetch();
            AdvanceCycle()
        } while (fakeROB.entries.size() > 0 || scanner.hasNext());
		} catch (FileNotFoundException e) {
			System.out.println("Trace File <" + traceFile + "> not found.");
			System.exit(1); 
		}

		cycle--;
		
		System.out.println("number of instructions = " + tag);

		System.out.println("number of cycles       = " + cycle);

		System.out.println("IPC                    = " + String.format("%.5f", (Math.round((float)tag / cycle * 100000) / 100000.0)));
    }
}
