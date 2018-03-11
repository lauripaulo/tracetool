package br.ufpr.dinf.arch.jbluepill.model;

import java.io.Serializable;

/**
 * Memory map entry.
 * 
 * @author Lauri Laux
 *
 */
public class MemoryMapEntry implements Serializable {

	private static final long serialVersionUID = -8923290132236587079L;

	private long baseAddress;
	private long size;
	private long topAddress;
	private String rights;
	private String owner;
	private long instructionCount;
	private long memoryAccessCount; 
	private int segmentId;
	
    public MemoryMapEntry() {
		super();
	}

	public MemoryMapEntry(long baseAddress, long size, String rights, String owner) {
		super();
		this.baseAddress = baseAddress;
		this.size = size;
		this.rights = rights;
		this.owner = owner;
		this.topAddress = baseAddress + (size * 1024);
		this.instructionCount = 0L;
		this.memoryAccessCount = 0L;
	}

	public long getBaseAddress() {
		return baseAddress;
	}

	public void setBaseAddress(long baseAddress) {
		this.baseAddress = baseAddress;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public String getRights() {
		return rights;
	}

	public void setRights(String rights) {
		this.rights = rights;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public long getTopAddress () {
		return topAddress;
	}
	
//	@Override
//	public String toString() {
//		return "MemoryMapEntry [segmentId=0x" + String.format("%04X", segmentId) 
//		+ ", baseAddress=0x" + String.format("%016X", baseAddress) 
//		+ ", topAddress=0x" + String.format("%016X", getTopAddress()) 
//        + ", size=" + size
//		+ ", rights=" + rights + ", instructionCount=" + instructionCount
//		+ ", memoryAccessCount=" + memoryAccessCount 
//		+ ", owner=" + owner + "]";
//	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (baseAddress ^ (baseAddress >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MemoryMapEntry other = (MemoryMapEntry) obj;
		if (baseAddress != other.baseAddress)
			return false;
		return true;
	}

	public long getInstructionCount() {
		return instructionCount;
	}

	public void setInstructionCount(long instructionCount) {
		this.instructionCount = instructionCount;
	}

	public long getMemoryAccessCount() {
		return memoryAccessCount;
	}

	public void setMemoryAccessCount(long memoryAccessCount) {
		this.memoryAccessCount = memoryAccessCount;
	}

	public void setTopAddress(long topAddress) {
		this.topAddress = topAddress;
	}

    public int getSegmentId() {
        return segmentId;
    }
    
    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
    }
	
}
