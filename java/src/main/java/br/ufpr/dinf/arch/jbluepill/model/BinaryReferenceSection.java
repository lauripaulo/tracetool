package br.ufpr.dinf.arch.jbluepill.model;

import java.io.Serializable;

import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

/**
 * section inside a ELF binary.
 * 
 * @author Lauri Laux
 *
 */
public class BinaryReferenceSection implements Serializable {

	private static final long serialVersionUID = 26010171497287674L;

	private long size;
	private long address;
	private long align;
	private long entrySize;
	private long flags;
	private long offset;
	private long count;

	public BinaryReferenceSection() {
	}
	
	public BinaryReferenceSection(long size, long address, long align, long entrySize, long flags, long offset,
			long count) {
		super();
		this.size = size;
		this.address = address;
		this.align = align;
		this.entrySize = entrySize;
		this.flags = flags;
		this.offset = offset;
		this.count = count;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getAddress() {
		return address;
	}

	public void setAddress(long address) {
		this.address = address;
	}

	public long getAlign() {
		return align;
	}

	public void setAlign(long align) {
		this.align = align;
	}

	public long getEntrySize() {
		return entrySize;
	}

	public void setEntrySize(long entrySize) {
		this.entrySize = entrySize;
	}

	public long getFlags() {
		return flags;
	}

	public void setFlags(long flags) {
		this.flags = flags;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return "ObjectReferenceSection [size=" + TraceUtils.getLongHexString(size) + 
				", address=" + TraceUtils.getLongHexString(address) + 
				", align=" + TraceUtils.getLongHexString(align) + 
				", entrySize=" + TraceUtils.getLongHexString(entrySize) + 
				", flags=" + TraceUtils.getLongHexString(flags) + 
				", offset=" + TraceUtils.getLongHexString(offset) + 
				", count=" + TraceUtils.getLongHexString(count) + 
				"]";
	}

}