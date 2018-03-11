package br.ufpr.dinf.arch.jbluepill.model;

import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class VirtualAddress {

	public long originalAddress = 0L;
	public long base = 0;
	public long displacement = 0L;

	public VirtualAddress(long originalAddress, long base, long displacement) {
		super();
		this.originalAddress = originalAddress;
		this.base = base;
		this.displacement = displacement;
	}

	public long getOriginalAddress() {
		return originalAddress;
	}

	public void setOriginalAddress(long originalAddress) {
		this.originalAddress = originalAddress;
	}

	public long getBase() {
		return base;
	}

	public void setBase(long base) {
		this.base = base;
	}

	public long getDisplacement() {
		return displacement;
	}

	public void setDisplacement(long displacement) {
		this.displacement = displacement;
	}

	@Override
	public String toString() {
		return "VirtualAddress [originalAddress=" + TraceUtils.getLongHexString(originalAddress) + 
				", base=" + TraceUtils.getLongHexString(base) + 
				", displacement=" + TraceUtils.getLongHexString(displacement) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (base ^ (base >>> 32));
		result = prime * result + (int) (displacement ^ (displacement >>> 32));
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
		VirtualAddress other = (VirtualAddress) obj;
		if (base != other.base)
			return false;
		if (displacement != other.displacement)
			return false;
		return true;
	}

}
