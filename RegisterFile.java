import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
    private Map<Integer, Boolean> readyFlags; // Tracks if a register is ready

    public RegisterFile() {
        this.readyFlags = new HashMap<>();
        for (int i = 0; i < 128; i++) {
            readyFlags.put(i, true); // All registers start as "ready"
        }
    }

    public void markReady(int register) {
        if (readyFlags.containsKey(register)) {
            readyFlags.put(register, true); // Mark the register as "ready"
        } else {
            throw new IllegalArgumentException("Invalid register ID: " + register);
        }
    }

    public void markNotReady(int register) {
        if (readyFlags.containsKey(register)) {
            readyFlags.put(register, false); // Mark the register as "not ready"
        } else {
            throw new IllegalArgumentException("Invalid register ID: " + register);
        }
    }

    public boolean isReady(int register) { return readyFlags.getOrDefault(register, false); }
}
