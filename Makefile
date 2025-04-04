# Makefile for compiling a Java program sim.java.  
# javac will compile program's dependencies
JAVA = java
JAVAC = javac
RUN = run
MAIN = sim
CFLAGS = -deprecation

sim_cache:
	$(JAVAC) $(CFLAGS) sim.java

# removes .class files
clean:
	rm -f *.class
