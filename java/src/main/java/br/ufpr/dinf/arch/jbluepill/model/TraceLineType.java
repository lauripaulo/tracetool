/**
 * 
 */
package br.ufpr.dinf.arch.jbluepill.model;

/**
 * instruction types in the trace file types.
 * 
 * @author Lauri Laux
 *
 */
public enum TraceLineType
{
	INSTRUCTION("INSTRUCTION", "I"),
	LOAD("LOAD", "L"),
	STORE("STORE", "S"),
	MODIFY("MODIFY", "M");
	
	private String name, prefix;
	
    /**
     * @param text
     */
    private TraceLineType(final String name, String prefix) {
        this.name = name;
        this.prefix = prefix;
    }

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return name;
    }
    
    public String getPrefix() {
    	return this.prefix;
    }
}
