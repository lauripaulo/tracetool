package br.ufpr.dinf.arch.jbluepill.core;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpr.dinf.arch.jbluepill.model.Instruction;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMap;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMapEntry;
import br.ufpr.dinf.arch.jbluepill.model.MemoryOperation;
import br.ufpr.dinf.arch.jbluepill.simulation.TranslateLookasideBuffer;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class TlbWorkerThread extends SimulatorWorkerThread {
	
	private static Logger logger = LogManager.getFormatterLogger("TlbWorkerThread");
	
	private ArrayList<TranslateLookasideBuffer> tlbList;
	private MemoryMap memMap;

	public TlbWorkerThread(String name, ArrayList<TranslateLookasideBuffer> tlbList, MemoryMap memMap) {
		super(name, logger);
        logger.debug(">> TlbWorkerThread(%s, size=%s, %s)", name, tlbList, memMap);
		this.tlbList = tlbList;
		this.memMap = memMap;
        logger.debug("<< TlbWorkerThread():");
	}

	@Override
	public void run() {
		logger.debug(">> run ()");
		super.run();
		setExecuting(true);
		
		for (Instruction asm : getExecutionStack()) {
    		
            MemoryMapEntry memEntry = memMap.getOwnerOfAddress(asm.getAddrAsLongValue());

            // call to simulated buffers
            handleCallToTlbList(asm.getAddrAsLongValue(), memEntry);
            
            for (MemoryOperation memOp : asm.getMemoryOperations()) {

                // call to simulated buffers
                handleCallToTlbList(memOp.getAddrAsLongValue(), memEntry);
                
            }
    	}

		logger.debug("<< run ():");
	}
	
	private void handleCallToTlbList(long address, MemoryMapEntry memEntry) {
		// create a segmented address from the address and memory map seg id.
        for (TranslateLookasideBuffer tlb : tlbList) {
        	TraceUtils.evalTLB(tlb, address, memEntry);
        }
	}

}
