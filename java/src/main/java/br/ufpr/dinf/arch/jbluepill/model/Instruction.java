/**
 * 
 */
package br.ufpr.dinf.arch.jbluepill.model;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * Model for a single instruction from valgrind lackey tool (modified), it has
 * 
 * Example of trace line:
 * <code>
 * I ,   4004cd2, 4, /lib/x86_64-linux-gnu/ld-2.23.so
 *  S,   4226b58, 8
 * I ,   4004cd6, 4, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004cda, 3, /lib/x86_64-linux-gnu/ld-2.23.so
 *  L,   4225f90, 8
 * I ,   4004cdd, 3, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004ce0, 2, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004ce2, 3, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004ce5, 6, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004ceb, 7, /lib/x86_64-linux-gnu/ld-2.23.so
 *  L,   4226a38, 8
 * I ,   4004cf2, 3, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004cf5, 2, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004cf7, 4, /lib/x86_64-linux-gnu/ld-2.23.so
 *  M,   4225e88, 8
 * I ,   4004cfb, 7, /lib/x86_64-linux-gnu/ld-2.23.so
 *  L,   4226a30, 8
 * I ,   4004d02, 3, /lib/x86_64-linux-gnu/ld-2.23.so
 * I ,   4004d05, 2, /lib/x86_64-linux-gnu/ld-2.23.so
 * </<code>
 * 
 * @author Lauri Laux
 *
 */
public class Instruction implements Serializable {

	private static final long serialVersionUID = -1611748615139402846L;
	
	private String addr;
	private int size;
	private String binary;
	private long traceLineNumber;
	private long addrAsLongValue;
	private LinkedList<MemoryOperation> memoryOperations;

	public Instruction() {
		super();
		memoryOperations = new LinkedList<MemoryOperation>();
	}

	public Instruction(String addr, int size, String binary, long traceLineNumber) {
		super();
		this.addr = addr;
		this.size = size;
		this.binary = binary;
		this.traceLineNumber = traceLineNumber;
		this.addrAsLongValue = Long.parseUnsignedLong(addr, 16);
		memoryOperations = new LinkedList<MemoryOperation>();
	}

	public String getAddr() {
		return addr;
	}

	public void setAddr(String addr) {
		this.addr = addr;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getBinary() {
		return binary;
	}

	public void setBinary(String binary) {
		this.binary = binary;
	}

	public long getTraceLineNumber() {
		return traceLineNumber;
	}

	public void setTraceLineNumber(long traceLineNumber) {
		this.traceLineNumber = traceLineNumber;
	}

	public long getAddrAsLongValue() {
		return addrAsLongValue;
	}

	public void setAddrAsLongValue(long addrAsLongValue) {
		this.addrAsLongValue = addrAsLongValue;
	}

	public LinkedList<MemoryOperation> getMemoryOperations() {
		return memoryOperations;
	}

	public void setMemoryOperations(LinkedList<MemoryOperation> memoryOperations) {
		this.memoryOperations = memoryOperations;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((addr == null) ? 0 : addr.hashCode());
		result = prime * result + ((binary == null) ? 0 : binary.hashCode());
		result = prime * result + size;
		result = prime * result + (int) (traceLineNumber ^ (traceLineNumber >>> 32));
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
		Instruction other = (Instruction) obj;
		if (addr == null) {
			if (other.addr != null)
				return false;
		} else if (!addr.equals(other.addr))
			return false;
		if (binary == null) {
			if (other.binary != null)
				return false;
		} else if (!binary.equals(other.binary))
			return false;
		if (size != other.size)
			return false;
		if (traceLineNumber != other.traceLineNumber)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Instruction [addr=" + addr + ", size=" + size + ", binary=" + binary + ", traceLineNumber="
				+ traceLineNumber + ", addrAsLongValue=" + addrAsLongValue + ", memoryOperations=" + memoryOperations
				+ "]";
	}

}
