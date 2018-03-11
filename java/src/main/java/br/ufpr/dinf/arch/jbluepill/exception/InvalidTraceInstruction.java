/**
 * 
 */
package br.ufpr.dinf.arch.jbluepill.exception;

/**
 * Raised when and instruction has a unexpected value.
 * 
 * @author Lauri Laux
 */
public class InvalidTraceInstruction extends Exception {

	private static final long serialVersionUID = -6452570442977755682L;

	public InvalidTraceInstruction() {
		super();
	}

	public InvalidTraceInstruction(String arg0) {
		super(arg0);
	}

	public InvalidTraceInstruction(String message, Throwable cause) {
		super(message, cause);
	}
	
	
}
