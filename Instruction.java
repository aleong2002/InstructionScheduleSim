
public class Instruction {
    public enum State { IF, ID, IS, EX, WB };
    private static int globalCounter = 0;
    
    private State state;
    private int tag;

    private String PC;
    private int optype;
    private int dest;
    private int src1;
    private int src2;

    public Instruction(String PC, int optype, int dest, int src1, int src2) {
        this.state = State.IF;
        this.tag = globalCounter++;
        this.PC = PC;
        this.optype = optype;
        this.dest = dest;
        this.src1 = src1;
        this.src2 = src2;
    }
}
