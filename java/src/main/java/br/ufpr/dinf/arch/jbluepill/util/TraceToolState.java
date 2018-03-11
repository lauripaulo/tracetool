package br.ufpr.dinf.arch.jbluepill.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import br.ufpr.dinf.arch.jbluepill.model.Instruction;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMap;
import br.ufpr.dinf.arch.jbluepill.simulation.SegmentBuffer;
import br.ufpr.dinf.arch.jbluepill.simulation.TranslateLookasideBuffer;

public class TraceToolState implements Serializable {

	private static final long serialVersionUID = -1356230763802219954L;

	private ArrayList<String> traceFiles = new ArrayList<String>();
	private ArrayList<String> compressedTraceFiles = new ArrayList<String>();
	private long totalInstructionCount;
	private boolean isTraceFilesCompressed;
	private String pathToFiles;
	private boolean nonCompressedFilesRemoved;
	private LinkedList<String> ignoredLines = new LinkedList<String>();
	private ArrayList<SegmentBuffer> sbList = new ArrayList<SegmentBuffer>();
	private ArrayList<TranslateLookasideBuffer> tlbList = new ArrayList<TranslateLookasideBuffer>();
	private ArrayList<Instruction> rogueInstructionsList = new ArrayList<Instruction>();
	private int currentSimulationFile;
	private long currentSimulationLine;
	
	
	public ArrayList<Instruction> getRogueInstructionsList() {
		return rogueInstructionsList;
	}

	public void setRogueInstructionsList(ArrayList<Instruction> rogueInstructionsList) {
		this.rogueInstructionsList = rogueInstructionsList;
	}
    
    public long getCurrentSimulationLine() {
        return currentSimulationLine;
    }
    
    public void setCurrentSimulationLine(long currentSimulationLine) {
        this.currentSimulationLine = currentSimulationLine;
    }



    public int getCurrentSimulationFile() {
        return currentSimulationFile;
    }


    
    public void setCurrentSimulationFile(int currentSimulationFile) {
        this.currentSimulationFile = currentSimulationFile;
    }


    public ArrayList<SegmentBuffer> getSbList() {
        return sbList;
    }

    
    public void setSbList(ArrayList<SegmentBuffer> sbList) {
        this.sbList = sbList;
    }

    
    public ArrayList<TranslateLookasideBuffer> getTlbList() {
        return tlbList;
    }

    
    public void setTlbList(ArrayList<TranslateLookasideBuffer> tlbList) {
        this.tlbList = tlbList;
    }

    private MemoryMap memoryMap = new MemoryMap();

	public ArrayList<String> getTraceFiles() {
		return traceFiles;
	}

	public void setTraceFiles(ArrayList<String> traceFiles) {
		this.traceFiles = traceFiles;
	}

	public ArrayList<String> getCompressedTraceFiles() {
		return compressedTraceFiles;
	}

	public void setCompressedTraceFiles(ArrayList<String> compressedTraceFiles) {
		this.compressedTraceFiles = compressedTraceFiles;
	}

	public long getTotalInstructionCount() {
		return totalInstructionCount;
	}

	public void setTotalInstructionCount(long totalInstructionCount) {
		this.totalInstructionCount = totalInstructionCount;
	}

	public boolean isTraceFilesCompressed() {
		return isTraceFilesCompressed;
	}

	public void setTraceFilesCompressed(boolean isTraceFilesCompressed) {
		this.isTraceFilesCompressed = isTraceFilesCompressed;
	}

	public String getPathToFiles() {
		return pathToFiles;
	}

	public void setPathToFiles(String pathToFiles) {
		this.pathToFiles = pathToFiles;
	}

	public boolean isNonCompressedFilesRemoved() {
		return nonCompressedFilesRemoved;
	}

	public void setNonCompressedFilesRemoved(boolean nonCompressedFilesRemoved) {
		this.nonCompressedFilesRemoved = nonCompressedFilesRemoved;
	}

	public LinkedList<String> getIgnoredLines() {
		return ignoredLines;
	}

	public void setIgnoredLines(LinkedList<String> ignoredLines) {
		this.ignoredLines = ignoredLines;
	}

	public MemoryMap getMemoryMap() {
		return memoryMap;
	}

	public void setMemoryMap(MemoryMap memoryMap) {
		this.memoryMap = memoryMap;
	}

}
