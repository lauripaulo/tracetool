package br.ufpr.dinf.arch.jbluepill.model;

public enum SegmentType {
	CODE("CODE"), 
	DATA("DATA"), 
	STACK("STACK"); 

	private String name;

	/**
	 * @param text
	 */
	private SegmentType(final String name) {
		this.name = name;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return this.name;
	}

}
