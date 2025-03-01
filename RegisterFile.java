import java.util.HashMap;
import java.util.Map;

public class RegisterFile {
    private Map<Integer, Boolean> readyFlags; // Tracks if a register is ready

    public RegisterFile() {
        this.readyFlags = new HashMap<>();
    }

    public void markReady(int register) {
        readyFlags.put(register, true);
    }

    public boolean isReady(int register) {
        return readyFlags.getOrDefault(register, false);
    }
}
