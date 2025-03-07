import java.util.ArrayList;
import java.util.List;

public class FakeROB {
    List<Instruction> entries;
    private final int capacity = 1024;

    public FakeROB() {
        this.entries = new ArrayList<Instruction>(capacity);
    }

    public boolean isFull() {
        return entries.size() == capacity;
    }

    public boolean isEmpty() {
        return entries.isEmpty();
    }

    public void addInstruction(Instruction instruction) {
        if (isFull()) {
            throw new IllegalStateException("FakeROB is full");
        }
        entries.add(instruction);
    }

    public Instruction getOldest() {
        if (isEmpty()) throw new IllegalStateException("FakeROB is empty");
        return entries.get(0);
    }

    public void removeOldest() {
        if (isEmpty()) throw new IllegalStateException("FakeROB is empty");
        entries.remove(0);
    }

}

// when fetch from trace file fakeROB.addInstruction(instruction);
// removing from ROB if (!fakeROB.isEmpty() && fakeROB.getOldest().isInWB()) {
//    fakeROB.removeOldest();
//}
//update in WB fakeROB.getOldest().setInWB(true); // Mark as written back
