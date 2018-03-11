package br.ufpr.dinf.arch.jbluepill.model;

import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class SegmentInfo {

	private long segtVSN;
	private String segASID;
	private long segPSbase;
	private long segPSlimit;
	private String segName;
	private long segVirtualBase;
	private long segVirtualLimit;

	public long getSegtVSN() {
		return segtVSN;
	}

	public void setSegtVSN(long segtVSN) {
		this.segtVSN = segtVSN;
	}

	public String getSegASID() {
		return segASID;
	}

	public void setSegASID(String segtASID) {
		this.segASID = segtASID;
	}

	public long getSegPSbase() {
		return segPSbase;
	}

	public void setSegPSbase(long segPSbase) {
		this.segPSbase = segPSbase;
	}

	public long getSegPSlimit() {
		return segPSlimit;
	}

	public void setSegPSlimit(long segPSlimit) {
		this.segPSlimit = segPSlimit;
	}

	public String getSegName() {
		return segName;
	}

	public void setSegName(String segName) {
		this.segName = segName;
	}

	@Override
	public String toString() {
		return "SegmentInfo [segtVSN=" + segtVSN + 
				", segtASID=" + segASID.toString() + 
				", segPSbase=" + TraceUtils.getLongHexString(segPSbase) +
				", segPSlimit=" + TraceUtils.getLongHexString(segPSlimit) + 
				", segVirtualBase=" + TraceUtils.getLongHexString(segVirtualBase) + 
				", segVirtualLimit=" + TraceUtils.getLongHexString(segVirtualLimit) + 
				", segName=" + segName + 
				"]";
	}

	public long getSegVirtualBase() {
		return segVirtualBase;
	}

	public void setSegVirtualBase(long segVirtualBase) {
		this.segVirtualBase = segVirtualBase;
	}

	public long getSegVirtualLimit() {
		return segVirtualLimit;
	}

	public void setSegVirtualLimit(long segVirtualLimit) {
		this.segVirtualLimit = segVirtualLimit;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((segName == null) ? 0 : segName.hashCode());
		result = prime * result + (int) (segPSbase ^ (segPSbase >>> 32));
		result = prime * result + (int) (segPSlimit ^ (segPSlimit >>> 32));
		result = prime * result + (int) (segVirtualBase ^ (segVirtualBase >>> 32));
		result = prime * result + (int) (segVirtualLimit ^ (segVirtualLimit >>> 32));
		result = prime * result + (int) (segtVSN ^ (segtVSN >>> 32));
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
		SegmentInfo other = (SegmentInfo) obj;
		if (segName == null) {
			if (other.segName != null)
				return false;
		} else if (!segName.equals(other.segName))
			return false;
		if (segPSbase != other.segPSbase)
			return false;
		if (segPSlimit != other.segPSlimit)
			return false;
		if (segVirtualBase != other.segVirtualBase)
			return false;
		if (segVirtualLimit != other.segVirtualLimit)
			return false;
		if (segtVSN != other.segtVSN)
			return false;
		return true;
	}

	
}
