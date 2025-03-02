import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
    private Map<Integer, Boolean> readyFlags; // Tracks if a register is ready

    public RegisterFile(int numRegisters) {
        this.readyFlags = new HashMap<>();
        for (int i = 0; i < numRegisters; i++) {
            readyFlags.put(i, false); // All registers start as "not ready"
        }
    }

    public void markReady(int register) {
        if (readyFlags.containsKey(register)) {
            readyFlags.put(register, true); // Mark the register as "ready"
        } else {
            throw new IllegalArgumentException("Invalid register ID: " + register);
        }
    }

    public boolean isReady(int register) {
        return readyFlags.getOrDefault(register, false);
    }
}
