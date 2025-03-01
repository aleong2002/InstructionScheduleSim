public class ROBEntry {
    private Instruction instruction;
    private boolean isInWB;

    public ROBEntry(Instruction instruction) {
        this.instruction = instruction;
        this.isInWB = false;
    }

    public Instruction getInstruction() {
        return instruction;
    }

    public boolean isInWB(){  // might have to be changed later
        return isInWB;
    }

    public void setInWB(boolean inWB) { // might have to be changed later
        isInWB = inWB;
        instruction.setState(Instruction.State.WB);
    }
}
