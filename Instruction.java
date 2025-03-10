
public class Instruction {
    public enum State { IF, ID, IS, EX, WB };
    private static int globalCounter = 0;
    
    private State state;
    private int tag; // assign increasing nums for order of instrs. ex: 0, 1, 2 ...
    private String PC;
    private int optype; // "0" =  1 cycle latency, "1" = 2, "2" = 5
    private int dest; // 0 to 127 - need to check?
    private int src1;
    private int src2;
    private int remainingCycles; // derived from optype - latency
    private boolean src1Ready = false;
    private boolean src2Ready = false;
    boolean isSrc1Tagged;
    boolean isSrc2Tagged;

    // for output:
    int IFCycle;
    int IFTime;
    int IDCycle;
    int IDTime;
    int ISCycle;
    int ISTime;
    int EXCycle;
    int EXTime;
    int WBCycle;
    int WBTime;

    // input ex: 2b6420 0 -1 29 14
    // output: 0 fu{0} src{29,14} dst{-1} IF{0,1} ID{1,1} IS{2,1} EX{3,1} WB{4,1}

    public Instruction(String PC, int optype, int dest, int src1, int src2) {
        this.state = State.IF;
        this.tag = globalCounter++;
        this.PC = PC;
        this.optype = optype;
        this.dest = dest; // if -1, then null
        this.src1 = src1;
        this.src2 = src2;
        this.remainingCycles = getLatency();
        
        this.isSrc1Tagged = false;
        this.isSrc2Tagged = false;

        this.IFCycle = -1;
        this.IFTime = 0;
        this.IDCycle = -1;
        this.IDTime = 0;
        this.ISCycle = -1;
        this.ISTime = 0;
        this.EXCycle = -1;
        this.EXTime = 0;
        this.WBCycle = -1;
        this.WBTime = 0;
    }

    public int getTag() { return tag; }
    public void setTag(int tag) { this.tag = tag; }
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public int getRemainingCycles() { return remainingCycles; }
    public void decrementCycle() { remainingCycles--; }
    public boolean isExecutionComplete() { return remainingCycles == 0; }
    public boolean isReady(){
        return (src1 == -1 || (src1 != -1 && src1Ready)) && (src2 == -1 || (src2 != -1 && src2Ready));
    }    
    public void setSrc1Ready() { src1Ready = true; }
    public void setSrc2Ready() { src2Ready = true; }
    public void setSrc1NotReady() {src1Ready = false;}
    public void setSrc2NotReady() {src2Ready = false;}
    public int getDest() { return dest; }
    public int getSrc1() { return src1; }
    public int getSrc2() { return src2; }
    public int getOpType() { return optype;}
    public void setDest(int dest) { this.dest = dest; }
    public void setSrc1(int src1) { this.src1 = src1; }
    public void setSrc2(int src2) { this.src2 = src2; }

    private int getLatency() {
        return switch (optype) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 5;
            default -> 1; // Default latency
        };
    }

    public void advanceState() {
        switch (state) {
            case IF -> state = State.ID;
            case ID -> state = State.IS;
            case IS -> state = State.EX;
            case EX -> state = State.WB;
            case WB -> {} // Do nothing - final state
        }
    }

}