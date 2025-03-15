import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

public class PipelineSimulator {
    public PipelineSimulator(String[] args) throws IOException {
        // command line arguments
        int S = Integer.parseInt(args[0]);
        int N = Integer.parseInt(args[1]);
        String traceFile = args[2];

        // int variables
        int tag = 0;
        int cycle = 0;
        int maxDispatchSize = 2 * N;
        int schedulingCount = 0;
        int dispatchCount = 0;

        // list objects
        int[] RFTags = new int[128];
        FakeROB fakeROB = new FakeROB();
        RegisterFile rFile = new RegisterFile();
        List<Instruction> dispatchList = new ArrayList<>(); // Dispatch Queue
        List<Instruction> issueList = new ArrayList<>(S); // Scheduling Queue
        List<Instruction> executeList = new ArrayList<>(N + 1); // Functional Units
        List<Instruction> removalList = new ArrayList<>(); // reusable list of instructions to remove
        
        Scanner s = new Scanner(new File(traceFile));
        do {
            // FakeRetire()
            removalList.clear();
            for (Instruction entry : fakeROB.entries) {
                if (entry.getState() == Instruction.State.WB) {
                    System.out.println(
                        entry.getTag() + " fu{" + entry.getOpType() + "} src{" + entry.getOrigSrc1()
                        + "," + entry.getOrigSrc2() + "} dst{" + entry.getDest() + "} IF{" + entry.getIFCycle()
                        + "," + entry.getIFTime() + "} ID{" + entry.getIDCycle() + "," + entry.getIDTime()
                        + "} IS{" + entry.getISCycle() + "," + entry.getISTime() + "} EX{" + entry.getEXCycle()
                        + "," + entry.getEXTime() + "} WB{" + entry.getWBCycle() + "," + entry.getWBTime() + "}"
                    );
                    removalList.add(entry);
                } else { break; }
            }
            (fakeROB.entries).removeAll(removalList);

            // Execute()
            removalList.clear();
            for (Instruction instr : executeList) {
                if (instr.getRemainingCycles() == 1) {
                    removalList.add(instr);
                    instr.advanceState();
                    // matching tags? update dest register file
                    if (instr.getDest() != -1 && RFTags[instr.getDest()] == instr.getTag() ) { rFile.markReady(instr.getDest()); }
                    // waking up dependent instructions
                    for (Instruction entry : fakeROB.entries) {
                        if (entry.getSrc1() == instr.getTag() && entry.getIsSrc1Tagged() ) { entry.setSrc1Ready(); }
                        if (entry.getSrc2() == instr.getTag() && entry.getIsSrc2Tagged()) { entry.setSrc2Ready(); }
                    }
                } else { instr.decrementCycle(); }
            }
            executeList.removeAll(removalList);

            // Issue()
            int issueCount = N + 1;
            removalList.clear();
            for (Instruction instr : issueList) {
                if (instr.isReady() && issueCount > 0) {
                    schedulingCount--;
                    issueCount--;
                    removalList.add(instr);
                    executeList.add(instr);
                    instr.advanceState();
                }
            }
            issueList.removeAll(removalList);

            // Dispatch()
            removalList.clear();
            for (Instruction instr : dispatchList) {
                if (instr.getState() == Instruction.State.ID && schedulingCount < S) {
                    schedulingCount++;
                    dispatchCount--;
                    removalList.add(instr);
                    issueList.add(instr);
                    // transition from ID to IS
                    instr.advanceState();
                    if (instr.getSrc1() != -1) {
                        if (rFile.isReady(instr.getSrc1())) { instr.setSrc1Ready(); }
                        else {
                            instr.setSrc1NotReady();
                            instr.setSrc1(RFTags[instr.getSrc1()]);
                            instr.setIsSrc1Tagged(true);
                        }
                    }
                    if (instr.getSrc2() != -1) {
                        if (rFile.isReady(instr.getSrc2())) { instr.setSrc2Ready(); } 
                        else {
                            instr.setSrc2NotReady();
                            instr.setSrc2(RFTags[instr.getSrc2()]);
                            instr.setIsSrc2Tagged(true);
                        }
                    }
                    if (instr.getDest() != -1) {
                        RFTags[instr.getDest()] = instr.getTag();
                        rFile.markNotReady(instr.getDest());
                    }
                }
                // transistion from IF to ID to model 1 cycle latency for IF
                if (instr.getState() == Instruction.State.IF) { instr.advanceState(); }
            }
            // remove instructions in ID state
            dispatchList.removeAll(removalList);

            // load instructions to fakeROB in N batches
            int fetchCount = N;
            while (fetchCount > 0 && dispatchCount < maxDispatchSize && s.hasNext()) {
                String PC = s.next();
                int optype = s.nextInt();
                int dest = s.nextInt();
                int src1 = s.nextInt();
                int src2 = s.nextInt();
                Instruction instr = new Instruction(PC, optype, dest, src1, src2);
                fetchCount--;
                tag++;
                fakeROB.addInstruction(instr);
                dispatchList.add(instr);
                dispatchCount++;
            }

            for (Instruction instr : fakeROB.entries) {
                switch (instr.getState()) {
                    case IF:
                        if (instr.getIFCycle() == -1) { instr.setIFCycle(cycle); }
                        instr.setIFTime(instr.getIFTime() + 1);
                        break;
                    case ID:
                        if (instr.getIDCycle() == -1) { instr.setIDCycle(cycle); }
                        instr.setIDTime(instr.getIDTime() + 1);
                        break;
                    case IS:
                        if (instr.getISCycle() == -1) { instr.setISCycle(cycle); }
                        instr.setISTime(instr.getISTime() + 1);
                        break;
                    case EX:
                        if (instr.getEXCycle() == -1) { instr.setEXCycle(cycle); }
                        instr.setEXTime(instr.getEXTime() + 1);
                        break;
                    case WB:
                        if (instr.getWBCycle() == -1) {
                            instr.setWBCycle(cycle);
                            instr.setWBTime(1);
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("No valid instr state");
                }
            }
            cycle++;
        } while (!fakeROB.isEmpty() || s.hasNext()); // advancecycle()
        cycle--;
        System.out.println(String.format("%-25s= %d", "number of instructions", tag));
        System.out.println(String.format("%-25s= %d", "number of cycles", cycle));
        System.out.println(String.format("%-25s= %.5f", "IPC", (float) tag / cycle));
        s.close();    
    }
}
