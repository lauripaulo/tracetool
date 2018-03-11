package br.ufpr.dinf.arch.jbluepill.model;

import java.io.Serializable;

/**
 * memory operation representing a LOAD, STORE or MODIFY.
 * 
 * @author Lauri Laux
 *
 */
public class MemoryOperation implements Serializable {

	private static final long serialVersionUID = -8832222426595621985L;
	private MemoryOperationType type;
	private long addressAsLongValue;
	private String address;
	private int size;

	public MemoryOperation() {
		super();
	}

	public MemoryOperation(MemoryOperationType type, String address, int size) {
		super();
		this.type = type;
		this.address = address;
		this.addressAsLongValue = Long.parseUnsignedLong(address, 16);
		this.size = size;
	}

	public MemoryOperationType getType() {
		return type;
	}

	public void setType(MemoryOperationType type) {
		this.type = type;
	}

	public long getAddrAsLongValue() {
		return addressAsLongValue;
	}

	public void setAddrAsLongValue(long addressAsLongValue) {
		this.addressAsLongValue = addressAsLongValue;
		this.address = Long.toHexString(addressAsLongValue);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
		this.addressAsLongValue = Long.parseLong(address, 16);
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	@Override
	public String toString() {
		return "MemoryOperation [type=" + type 
				+ ", addressAsLongValue=" + addressAsLongValue 
				+ ", address=" + address
				+ ", size=" + size 
				+ "]";
	}

}
