# Dynamic Instruction Scheduling Simulator
Machine Problem for UCF's CDA 5106

## Project Description
We have constructed a simulator for an out-of-order superscalar processor based on Tomasulo's algorithm that fetches, dispatches, and issues N instructions per cycle. Perfect caches and perfect branch prediction are assumed. The simulator is compiled and run on Linux machine.

## Simulator Inputs
The simulator reads a trace file in the following format:

`<PC> <operation type> <dest reg #> <src1 reg #> <src2 reg #>`

`<PC> <operation type> <dest reg #> <src1 reg #> <src2 reg #> ...`

 where
- `<PC>` is the program counter of the instruction (in hex).
- `<operation type>` is either “0”, “1”, or “2”.
- `<dest reg#>` is the destination register of the instruction. If it is \-1, then the instruction does not have a destination register (for example, a conditional branch instruction). Otherwise, it is between 0 and 127.
- `<src1 reg #>` is the first source register of the instruction. If it is \-1, then the instruction does not have a first source register. Otherwise, it is between 0 and 127.
- `<src2 reg #>` is the second source register of the instruction. If it is \-1, then the instruction does not have a second source register. Otherwise, it is between 0 and 127.

## Simulator Output
The simulator outputs the following measurements after completion of the run:
1. Total number of instructions in the trace.
2. Total number of cycles to finish the program.
3. Average number of instructions completed per cycle (IPC).

The simulator also outputs the timing information for every instruction in the trace.

## Validation Requirements
Sample simulation outputs are provided, and they are called “validation runs”. To confirm the correctness of the simulator follow these steps for each validation run:
1. Redirect the console output of your simulator to a temporary file. This
can be achieved by placing `> your_output_file` after the simulator
command.
2. Test whether or not your outputs match properly, by running this linux
command: `diff -i -w <your_output_file> <posted_output_file>`

## To Run Simulator
Provided is a Makefile that automatically compiles the simulator. This Makefile creates a simulator named "sim". 
To run the simulator, input the command-line arguments as follows:

```sim <S> <N> <tracefile>```

where `<S>` is the Scheduling Queue size, `<N>` is the peak fetch and dispatch rate, and `<tracefile>` is the filename of the input trace. Issue rate will be `<N + 1>`.
Simulator will print output to the console but can also be redirected to file name (see Validation Requirements #1).

To 'make clean' the simulator, type `make clean` in command-line to remove object (.o) files and simulator executable.