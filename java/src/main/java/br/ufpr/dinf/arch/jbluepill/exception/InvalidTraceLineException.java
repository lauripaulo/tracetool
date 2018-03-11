package br.ufpr.dinf.arch.jbluepill.exception;

/**
 * Raised when a trace line (from Valgrind) is invalid.
 * 
 * @author Lauri Laux
 */
public class InvalidTraceLineException extends Exception {

	private static final long serialVersionUID = -8151786053625729L;

	public InvalidTraceLineException() {
	}

	public InvalidTraceLineException(String message) {
		super(message);
	}

	public InvalidTraceLineException(String message, Throwable cause) {
		super(message, cause);
	}
	
}