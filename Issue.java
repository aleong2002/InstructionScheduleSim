import java.util.*;

public class Issue {
    private int issueRate;

    public Issue(int issueRate) {
        this.issueRate = issueRate;
    }

    public void issue(List<Instruction> issueList, List<Instruction> executeList) {
        List<Instruction> readyInstructions = new ArrayList<>();
        for (Instruction instr : issueList) {
            if (instr.isReady()) {
                readyInstructions.add(instr);
            }
        }

        readyInstructions.sort(Comparator.comparingInt(Instruction::getTag));

        int issuedCount = 0;
        // getting all ready instructions
        for (Instruction instr : readyInstructions) {
            if (issuedCount >= issueRate) {
                break;
            }
            // decrementing the scheduling queue entry
            issueList.remove(instr);
            // state from IS to EX
            instr.setState(InstructionState.EX);
            // setting execution timer based on operation type
            instr.setExecutionTimer(instr.getLatency());

            executeList.add(instr);

            issuedCount++;
        }
    }
}
