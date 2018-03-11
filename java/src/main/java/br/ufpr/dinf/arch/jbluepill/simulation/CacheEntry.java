package br.ufpr.dinf.arch.jbluepill.simulation;

import java.io.Serializable;

/**
 * Its an entry in the cache representing a page or segment mapping,
 * implemented as a simple java object with gets/sets of it's properties.
 * 
 * Provides {@link #equals(Object)}, {@link #toString()} and {@link #hashCode()}
 * methods making this class usable with collections, and other objects, that
 * requires this override methods.
 * 
 * @author Lauri Laux
 *
 */
public class CacheEntry implements Serializable {

    private static final long serialVersionUID = 3674173796625698761L;
    
    private long tag;
	private boolean valid;
	private long physicalAddressBase;
	private String asid;
	private String entryName;

    public CacheEntry() {
        super();
    }

    public CacheEntry(long tag, boolean valid, long physicalAddressBase, String asid, String segmentId) {
		super();
		this.tag = tag;
		this.valid = valid;
		this.physicalAddressBase = physicalAddressBase;
		this.asid = asid;
		this.entryName = segmentId;
	}

	public long getTag() {
		return tag;
	}

	public void setTag(long tag) {
		this.tag = tag;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public long getPhysicalAddressBase() {
		return physicalAddressBase;
	}

	public void setPhysicalAddressBase(long physicalAddressBase) {
		this.physicalAddressBase = physicalAddressBase;
	}

	@Override
	public String toString() {
		return "CacheEntry [tag=0x" + Long.toHexString(tag) 
		        + ", valid=" + valid 
		        + ", entryName='" + entryName + "'" 
				+ "]";
	}

	public String getAsid() {
		return asid;
	}

	public void setAsid(String asid) {
		this.asid = asid;
	}

	public String getEntryName() {
		return entryName;
	}

	public void setEntryName(String segmentId) {
		this.entryName = segmentId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 167;
		result = prime * result + (int) (tag ^ (tag >>> 32));
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
		CacheEntry other = (CacheEntry) obj;
		if (tag != other.tag)
			return false;
		return true;
	}
}
