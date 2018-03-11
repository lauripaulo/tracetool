package br.ufpr.dinf.arch.jbluepill.model;

/**
 * enum with the memory operation types STORE, LOAD and MODIFY.
 * 
 * @author Lauri Laux
 *
 */
public enum MemoryOperationType {
	STORE("STORE"), 
	LOAD("LOAD"), 
	MODIFY("MODIFY"); 

	private String name;

	private MemoryOperationType(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}

	public String getName() {
		return this.name;
	}
	
}