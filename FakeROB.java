public class FakeROB {
    private ROBEntry[] entries;
    private int head;
    private int tail;
    private int size;
    private final int capacity = 1024;

    public FakeROB(int capacity) {
        this.entries = new ROBEntry[capacity];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void addInstruction(Instruction instruction) {
        if (isFull()) {
            throw new IllegalStateException("FakeROB is full");
        }
        entries[tail] = new ROBEntry(instruction);
        tail = (tail + 1) % capacity;
        size++;
    }

    public ROBEntry getOldest() {
        if (isEmpty()) throw new IllegalStateException("FakeROB is empty");
        return entries[head];
    }

    public void removeOldest() {
        if (isEmpty()) throw new IllegalStateException("FakeROB is empty");
        entries[head] = null;
        head = (head + 1) % capacity;
        size--;
    }
}

// when fetch from trace file fakeROB.addInstruction(instruction);
// removing from ROB if (!fakeROB.isEmpty() && fakeROB.getOldest().isInWB()) {
//    fakeROB.removeOldest();
//}
//update in WB fakeROB.getOldest().setInWB(true); // Mark as written back
