package br.ufpr.dinf.arch.jbluepill.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.exception.InvalidTraceLineException;
import br.ufpr.dinf.arch.jbluepill.model.BinaryReference;
import br.ufpr.dinf.arch.jbluepill.model.Instruction;
import br.ufpr.dinf.arch.jbluepill.model.InstructionObserver;
import br.ufpr.dinf.arch.jbluepill.model.MemoryOperation;
import br.ufpr.dinf.arch.jbluepill.model.TraceLineType;
import br.ufpr.dinf.arch.jbluepill.util.TraceReader;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class TraceWorker {

	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("TraceWorker");

	private TraceReader reader;
	private long instructions = 1L;
	private long memOps = 0L;

	public TraceWorker(TraceReader reader) {
		this.reader = reader;
	}
	
	public void processTrace (InstructionObserver observer) throws InvalidTraceLineException {
		logger.debug(">> processTrace()");
		
		Instruction asm = null;
				
		try {
			
			// Lets reads until the exceptions telling us that there is no more lines
			// to read is raised. It may not be the best approach, but to maintain
			// the model of one Instruction object with memory operations inside
			// this is the best read loop for now.
			while (true) {
				
				final String traceLine = reader.readNextLine();
				final String lineStart = traceLine.length() > 0 ? traceLine.trim().substring(0, 1) : "";
				
				if (lineStart.equals(TraceLineType.INSTRUCTION.getPrefix())) {
					
					if (asm != null) {
						instructions ++;
						observer.instructionReaded(asm);
					}
					asm = TraceUtils.parseInstructionLine(traceLine, reader.getLineNumber());
//					logger.debug("New instruction: %s", asm);
					
				} else if (lineStart.equals(TraceLineType.LOAD.getPrefix()) 
					|| lineStart.equals(TraceLineType.MODIFY.getPrefix())
					|| lineStart.equals(TraceLineType.STORE.getPrefix())
					) {
					
					MemoryOperation memop;
					memop = TraceUtils.parseMemoryOperationLine(traceLine, reader.getLineNumber());
					asm.getMemoryOperations().addLast(memop);
					memOps ++;
//					logger.debug("New memory operation: %s", memop);
					
				} else {
					// End of instructions on file?
					logger.info("getAllObjectReferences() - Skipping line: %s", traceLine);
				}
								
			}
		} catch (NoSuchElementException e) {
			// last element?
			if (asm != null) {
				instructions ++;
				observer.instructionReaded(asm);
			}
			logger.debug("Instructions: %s", instructions);
			logger.debug("Mem. Ops....: %s", memOps);
		}
		
		
		logger.debug("<< processTrace()");
	}

	/**
	 * return an {@link ArrayList} of all the objects (binaries) the trace has.
	 */
	public HashMap<String, BinaryReference> getAllObjectReferences() throws InvalidTraceLineException {
		logger.debug(">> getAllObjectReferences()");
		
		HashMap<String, BinaryReference> refMap = new HashMap<String, BinaryReference>();
		BinaryReference ref;
		String traceLine;
		
		traceLine = reader.getCurrentLine();
		Instruction asm = null;
		
		while (reader.hasNextLine()) {
						
			if (traceLine.startsWith("==")) {
				// End of instructions on file?
				logger.debug("getAllObjectReferences() - Skipping comment: %s", traceLine);
				continue;
			}
			
			if (traceLine.startsWith(TraceLineType.INSTRUCTION.getPrefix())) {
				asm = TraceUtils.parseInstructionLine(traceLine, reader.getLineNumber());
				
				logger.debug("New instruction: %s", asm);
				
				// update binary reference
				if (refMap.containsKey(asm.getBinary())) {
					ref = refMap.get(asm.getBinary());
				} else {
					ref = createObjReferenceFromInstruction(asm);
					ref.setFileSize(getFileSize(ref.getFileName()));
					refMap.put(asm.getBinary(), ref);
					logger.info("Added objref: %s", ref);
				}
				ref.setInstructionsCalled(ref.getInstructionsCalled() + 1);
				
			} else {
				MemoryOperation memop;
				memop = TraceUtils.parseMemoryOperationLine(traceLine, reader.getLineNumber());
				asm.getMemoryOperations().addLast(memop);
				logger.debug("New memory operation: %s", memop);
			}
			
			traceLine = reader.readNextLine();
			
		}
		logger.debug("<< getAllObjectReferences()");
		return refMap;
	}
	
	
	/**
	 * @param asm
	 * @return
	 */
	private BinaryReference createObjReferenceFromInstruction(Instruction asm) {
		logger.trace(">> createObjReferenceFromInstruction (asm=%s)", asm);
		BinaryReference ref = new BinaryReference();
		ref.setFileName(asm.getBinary());
		logger.trace("<< createObjReferenceFromInstruction (): %s", ref);
		return ref;
	}

	public long getFileSize(String filePath) {
		logger.trace(">> getFileSize(filePath=%s)", filePath);
		long size = 0L;
		File binFile = new File(filePath);
		if (binFile.exists() && binFile.isFile()) {
			size = binFile.length();
		}
		logger.trace("<< getFileSize(): %s", size);
		return size;
	}

	public long getInstructions() {
		return instructions;
	}


	public long getMemOps() {
		return memOps;
	}

}