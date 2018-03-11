package br.ufpr.dinf.arch.jbluepill.core;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import br.ufpr.dinf.arch.jbluepill.model.Instruction;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMap;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMapEntry;
import br.ufpr.dinf.arch.jbluepill.model.MemoryOperation;
import br.ufpr.dinf.arch.jbluepill.simulation.SegmentBuffer;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class SegmentBufferWorkerThread extends SimulatorWorkerThread {
	
	private static Logger logger = LogManager.getFormatterLogger("SegmentBufferWorkerThread");
	
	private MemoryMap memMap;
	private ArrayList<SegmentBuffer> sbList;

	public SegmentBufferWorkerThread(String name, ArrayList<SegmentBuffer> sbList, MemoryMap memMap) {
		super(name, logger);
        logger.debug(">> SegmentBufferWorkerThread(%s, size=%s, %s)", name, sbList, memMap);
		this.memMap = memMap;
		this.sbList = sbList;
        logger.debug("<< SegmentBufferWorkerThread():");
	}

	@Override
	public void run() {
		logger.debug(">> run ()");
		super.run();
		setExecuting(true);

		for (Instruction asm : getExecutionStack()) {
    		
            MemoryMapEntry memEntry = memMap.getOwnerOfAddress(asm.getAddrAsLongValue());

            // call to simulated buffers
			handleCallToSbList(asm.getAddrAsLongValue(), memEntry);
            
            for (MemoryOperation memOp : asm.getMemoryOperations()) {
                
                memEntry = memMap.getOwnerOfAddress(memOp.getAddrAsLongValue());

            	// call to simulated buffers
				handleCallToSbList(memOp.getAddrAsLongValue(), memEntry);
                
            }
    	}
    	
		setExecuting(false);
		logger.debug("<< run ():");
	}
	
	private void handleCallToSbList(long address, MemoryMapEntry memEntry) {
		// create a segmented address from the address and memory map seg id.
        long addressSegmented;
        addressSegmented = TraceUtils.createSegmentedAddress(address, memEntry.getSegmentId());
        for (SegmentBuffer sb : sbList) {
        	TraceUtils.evalSegmentBuffer(sb, addressSegmented, memEntry);
        }
	}

}
