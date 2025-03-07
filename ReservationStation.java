public class ReservationStation {
    private boolean busy;
    private int tag;
    private int optype;
    private Integer destreg, src1reg, src2reg;
    private boolean src1Ready, src2Ready; // op ready flags
    private int src1val, src2val;
    private int remainingCycles;

    public ReservationStation() {
        this.busy = false; //start as free
        this.tag = -1; // default invalid tag
    }

    // Allocate an instruction to this reservation station
    public void allocate(int tag, int optype, Integer destReg, Integer srcReg1, boolean srcReg1Ready, Integer srcReg2, boolean srcReg2Ready) {
        this.busy = true;
        this.tag = tag;
        this.optype = optype;
        this.destreg = destReg;
        this.src1reg = srcReg1;
        this.src1Ready = srcReg1Ready;
        this.src2reg = srcReg2;
        this.src2Ready = srcReg2Ready;
    }

    // check if ready to execute
    public boolean isReady() { return src1Ready && src2Ready; }

    // notify this station that an operand is ready
    public void notifyOperandReady(int reg, int value) {
        if (src1reg != null && src1reg.equals(reg)) {
            src1Ready = true;
            src1val = value;
        }
        if (src2reg != null && src2reg.equals(reg)) {
            src2Ready = true;
            src2val = value;
        }
    }

    // check if execute is complete
    public boolean isExecutionComplete() { return remainingCycles == 0; }

    // Clear this station after execution
    public void clear() {
        this.busy = false;
        this.tag = -1;
        this.optype = 0;
        this.destreg = null;
        this.src1reg = null;
        this.src2reg = null;
        this.src1Ready = false;
        this.src2Ready = false;
        this.src1val = 0;
        this.src2val = 0;
        this.remainingCycles = 0;
    }

    // Set execution latency
    public void setExecutionLatency(int cycles) { this.remainingCycles = cycles; }
}

// stores information about the source opernads' optype, source and destination registers, and readiness status
// checks whether source operands are available and ready for execution
// listens to ubdates from CDB to wake up when its operands are ready
// keeps track of execution progress i.e. latency cycles for FUs
