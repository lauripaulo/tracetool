package br.ufpr.dinf.arch.jbluepill.core;

import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.logging.log4j.Logger;

import br.ufpr.dinf.arch.jbluepill.model.Instruction;

public class SimulatorWorkerThread extends Thread {
	
	private Logger logger;
	
	private static Object syncPoint = new Object();
	private long instructionCount;
	private long memOpsCount;
	private ConcurrentLinkedQueue<Instruction> executionStack;
	private boolean isExecuting;

	public SimulatorWorkerThread(String name, Logger logger) {
		super(name);
		this.logger = logger;
	}

	public void addInstruction(Instruction asm) {
		logger.debug(">> addInstruction (%s)", asm);
		executionStack.add(asm);
		logger.debug("<< addInstruction ():", asm);
	}

	public Instruction getNextInstruction() {
		logger.debug(">> getNextInstruction ()");
		Instruction i = executionStack.poll();
		logger.debug("<< getNextInstruction (): %s", i);
		return i;
	}

	public boolean isExecuting() {
		logger.debug(">> isExecuting ()");
		logger.debug("<< isExecuting (): %s", isExecuting);
		return isExecuting;
	}

	public void setExecuting(boolean isExecuting) {
		logger.debug(">> setExecuting (%s)", isExecuting);
		this.isExecuting = isExecuting;
		logger.debug("<< setExecuting ():");
	}

	public ConcurrentLinkedQueue<Instruction> getExecutionStack() {
		return executionStack;
	}

	public static Object getSyncPoint() {
		return syncPoint;
	}

	public static void setSyncPoint(Object syncPoint) {
		SimulatorWorkerThread.syncPoint = syncPoint;
	}

	public long getInstructionCount() {
		logger.debug(">> getInstructionCount ()");
		logger.debug("<< getInstructionCount (): %s", instructionCount);
		return instructionCount;
	}
	
	public void incrementInstructions() {
		logger.debug(">> incrementInstructions ()");
		this.instructionCount ++;
		logger.debug("<< incrementInstructions ():");
	}

	public long getMemOpsCount() {
		logger.debug(">> getMemOpsCount ()");
		logger.debug("<< getMemOpsCount (): %s", memOpsCount);
		return memOpsCount;
	}

	public void incrementMemOps() {
		logger.debug(">> incrementMemOps ()");
		this.memOpsCount ++;
		logger.debug("<< incrementMemOps ():");
	}

	public void setExecutionStack(ConcurrentLinkedQueue<Instruction> executionStack) {
		this.executionStack = executionStack;
	}


}