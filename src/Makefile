# Makefile

JCC = javac
JVM =  java
JFLAGS = -g

default:
	 $(JCC) $(JFLAGS) *.java

run1:
	$(JVM) Raft -p 31415 -i s0.in

run2:
	$(JVM) Agent -p 31415 


clean:
	$(RM) *.class