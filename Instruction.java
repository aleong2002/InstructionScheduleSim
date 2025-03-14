public class Instruction {
    // instruction properties
    public enum State { IF, ID, IS, EX, WB };
    private static int globalCounter = 0;
    private State state;
    private String PC;
    private int tag; // assign increasing nums for order of instrs. ex: 0, 1, 2 ...
    private int optype; // "0" =  1 cycle latency, "1" = 2, "2" = 5
    private int dest; // 0 to 127 - need to check? 128 registers
    private int src1;
    private int src2;
    private int origsrc1;
    private int origsrc2;
    private int remainingCycles; // derived from optype - latency

    //check values
    private boolean src1Ready = false;
    private boolean src2Ready = false;
    private boolean isSrc1Tagged;
    private boolean isSrc2Tagged;

    // input: 2b6420 0 -1 29 14
    // output: 0 fu{0} src{29,14} dst{-1} IF{0,1} ID{1,1} IS{2,1} EX{3,1} WB{4,1}
    private int IFCycle;
    private int IFTime;
    private int IDCycle;
    private int IDTime;
    private int ISCycle;
    private int ISTime;
    private int EXCycle;
    private int EXTime;
    private int WBCycle;
    private int WBTime;

    public Instruction(String PC, int optype, int dest, int src1, int src2) {
        this.state = State.IF;
        this.tag = globalCounter++;
        this.PC = PC;
        this.optype = optype;
        this.dest = dest; // if -1, then null
        this.src1 = src1;
        this.src2 = src2;
        this.origsrc1 = src1;
        this.origsrc2 = src2;
        this.isSrc1Tagged = false;
        this.isSrc2Tagged = false;
        this.remainingCycles = getLatency();
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

    // getters
    public String getPC() { return PC; }
    public int getTag() { return tag; }
    public State getState() { return state; }
    public int getRemainingCycles() { return remainingCycles; }
    public int getDest() { return dest; }
    public int getSrc1() { return src1; }
    public int getSrc2() { return src2; }
    public int getOrigSrc1() { return origsrc1; }
    public int getOrigSrc2() { return origsrc2; }
    public int getOpType() { return optype;}
    public boolean getIsSrc1Tagged() { return isSrc1Tagged; }
    public boolean getIsSrc2Tagged() { return isSrc2Tagged; }
    public int getIFCycle() { return IFCycle; }
    public int getIFTime() { return IFTime; }
    public int getIDCycle() { return IDCycle; }
    public int getIDTime() { return IDTime; }
    public int getISCycle() { return ISCycle; }
    public int getISTime() { return ISTime; }
    public int getEXCycle() { return EXCycle; }
    public int getEXTime() { return EXTime; }
    public int getWBCycle() { return WBCycle; }
    public int getWBTime() { return WBTime; }
    private int getLatency() {
        return switch (optype) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 5;
            default -> throw new IllegalArgumentException("optype invalid");
        };
    }

    // setters
    public void setTag(int tag) { this.tag = tag; }
    public void setState(State state) { this.state = state; }
    public void setDest(int dest) { this.dest = dest; }
    public void setSrc1(int src1) { this.src1 = src1; }
    public void setSrc2(int src2) { this.src2 = src2; }
    public void setSrc1Ready() { src1Ready = true; }
    public void setSrc2Ready() { src2Ready = true; }
    public void setSrc1NotReady() {src1Ready = false;}
    public void setSrc2NotReady() {src2Ready = false;}
    public void setIsSrc1Tagged(boolean bool) { isSrc1Tagged = bool; }
    public void setIsSrc2Tagged(boolean bool) { isSrc2Tagged = bool; }
    public void setIFCycle(int cycle) { IFCycle = cycle; }
    public void setIFTime(int time) { IFTime = time; }
    public void setIDCycle(int cycle) { IDCycle = cycle; }
    public void setIDTime(int time) { IDTime = time; }
    public void setISCycle(int cycle) { ISCycle = cycle; }
    public void setISTime(int time) { ISTime = time; }
    public void setEXCycle(int cycle) { EXCycle = cycle; }
    public void setEXTime(int time) { EXTime = time; }
    public void setWBCycle(int cycle) { WBCycle = cycle; }
    public void setWBTime(int time) { WBTime = time; }

    // decrement cycle
    public void decrementCycle() { remainingCycles--; }  

    // checks
    public boolean isExecutionComplete() { return remainingCycles == 0; }
    public boolean isReady(){
        return (src1 == -1 || (src1 != -1 && src1Ready)) && (src2 == -1 || (src2 != -1 && src2Ready));
    }  

    // advance instruction state
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