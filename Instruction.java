
public class Instruction {
    public enum State { IF, ID, IS, EX, WB };
    private static int globalCounter = 0;
    
    private State state;
    private int tag; // assign increasing nums for order of instrs. ex: 0, 1, 2 ...
    private int latency; // derived from optype
    private int PC; // need to decode hex val
    private String optype; // "0" =  1 cycle latency, "1" = 2, "2" = 5
    private int dest; // 0 to 127 - need to check?
    private int src1;
    private int src2;

    // input ex: 2b6420 0 -1 29 14
    // output: 0 fu{0} src{29,14} dst{-1} IF{0,1} ID{1,1} IS{2,1} EX{3,1} WB{4,1}

    public Instruction(int PC, String optype, int dest, int src1, int src2) {
        this.state = State.IF;
        this.tag = globalCounter++;
        this.PC = PC;
        this.optype = optype;
        this.dest = dest == -1 ? null : dest; // if -1, then null
        this.src1 = src1 == -1 ? null : src1;
        this.src2 = src2 == -1 ? null : src2;

        switch (optype) {
            case "0": 
                this.latency = 1; break;
            case "1":
                this.latency = 2; break;
            case "2":
                this.latency = 5; break;
            default:
                throw new IllegalArgumentException("Invalid operation type");
        }
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void updateInstructionState(Instruction instruction) {
        switch (instruction.getState()) {
            case IF:
                instruction.setState(State.ID); // Transition from IF to ID
                break;
            case ID:
                instruction.setState(State.IS); // Transition from ID to IS
                break;
            case IS:
                instruction.setState(State.EX); // Transition from IS to EX
                break;
            case EX:
                instruction.setState(State.WB); // Transition from EX to WB
                break;
            case WB:
                System.out.println("Instruction has completed Write Back.");
                break;
        }
    }
    
    public int getLatency() {
        return latency;
    }

    public void decrementLatency() {
        if (latency > 0) {
            latency--;
        }
    }

    public boolean executionCheck() {
        return latency == 0;
    }
}
