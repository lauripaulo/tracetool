package br.ufpr.dinf.arch.jbluepill.model;

/**
 * callback model interface to receive notification of 
 * intruction events.
 * 
 * @author Lauri Laux
 *
 */
public interface InstructionObserver {
	
	/**
	 * receives notification when a new instruction  is readed from trace file. 
	 * 
	 * @param asm
	 */
	public void instructionReaded (Instruction asm);

}
