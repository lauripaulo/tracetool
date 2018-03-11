package br.ufpr.dinf.arch.jbluepill.util;

import java.util.Date;
import java.util.HashMap;

import br.ufpr.dinf.arch.jbluepill.model.BinaryReference;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMap;


/**
 * used to persist all information related to the pre-processing of a
 * program execution from Valgrind customized Lackey tool.
 * 
 * It contains a binary reference map with info regarding the binary
 * ELF information of trace references.
 * 
 * It also contains the memory map object with the parsed info
 * extracted from the text pmap tool file.
 * 
 * @author lauri
 *
 */
public class AnalysisData {

	private String traceFilePath;
	private Date binaryRefsDate = new Date();
	private MemoryMap memoryMap = new MemoryMap();
	private HashMap<String, BinaryReference> refMap = new HashMap<String, BinaryReference>();
	private long instructionCount;

	public String getTraceFilePath() {
		return traceFilePath;
	}

	public void setTraceFilePath(String traceFilePath) {
		this.traceFilePath = traceFilePath;
	}

	public Date getBinaryRefsDate() {
		return binaryRefsDate;
	}

	public void setBinaryRefsDate(Date binaryRefsDate) {
		this.binaryRefsDate = binaryRefsDate;
	}

	public MemoryMap getMemoryMap() {
		return memoryMap;
	}

	public void setMemoryMap(MemoryMap memoryMap) {
		this.memoryMap = memoryMap;
	}

	public HashMap<String, BinaryReference> getRefMap() {
		return refMap;
	}

	public void setRefMap(HashMap<String, BinaryReference> refMap) {
		this.refMap = refMap;
	}

	public long getInstructionCount() {
		return instructionCount;
	}

	public void setInstructionCount(long instructionCount) {
		this.instructionCount = instructionCount;
	}
}
