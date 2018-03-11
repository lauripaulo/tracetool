package br.ufpr.dinf.arch.jbluepill.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.exception.InvalidTraceLineException;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

/**
 * object to hold and improve the pmap memory map information.
 * 
 * The memory map file format is below.
 *
 * <code>
 *           1         2         3         4         5
 * 012345678901234567890123456789012345678901234567890
 * |||||||||||||||||||||||||||||||||||||||||||||||||||
 * 0000000000400000     72K r-x-- lsblk
 * 0000000000611000      4K r---- lsblk
 * 0000000000612000      4K rw--- lsblk
 * 0000000004000000    152K r-x-- ld-2.23.so
 * 0000000004026000      8K rw---   [ anon ]
 * </code>
 * 
 * @author Lauri Laux
 *
 */
public class MemoryMap implements Serializable {

	private static final long serialVersionUID = -6833365493746704110L;

	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("MemoryMap");
	
	private ArrayList<MemoryMapEntry> entries;
	
	private int segmentIdCounter;

    private int simulatedSegmentCounter = 0;

    public MemoryMap(ArrayList<MemoryMapEntry> entries) {
		super();
		this.entries = entries;
	}

	public MemoryMap() {
		super();
		entries = new ArrayList<MemoryMapEntry>(512);
	}

	public ArrayList<MemoryMapEntry> getEntries() {
		return entries;
	}

	public void setEntries(ArrayList<MemoryMapEntry> entries) {
		this.entries = entries;
	}
	
	
	/**
	 * returns the memory map 'segment' owner of a given address.
	 * 
	 * @param address the address to lookup.
	 * 
	 * @return {@link MemoryMap} of the owner for the address.
	 */
	public MemoryMapEntry getOwnerOfAddress (long address) {
//		logger.debug(">> getOwnerOfAddress(%s)", Long.toHexString(address));
		MemoryMapEntry entry = null;
		for (MemoryMapEntry memMapEntry : getEntries()) {
			if (address >= memMapEntry.getBaseAddress() && 
					address <= memMapEntry.getTopAddress()) 
			{
				entry = memMapEntry;
				break;
			}
		}
//		logger.debug("<< getOwnerOfAddress: %s", entry);
		return entry;
	}
	
	
    public boolean createSimulatedSegmentForAddress(long address) {
        logger.debug(">> createArtificialSegment(): " + TraceUtils.getLongHexString(address));
        int insertIndex = 0;
        MemoryMapEntry artificialSegment = null;
        MemoryMapEntry currentSegment = null;
        MemoryMapEntry nextSegment = null;
        Iterator<MemoryMapEntry> entryIterator = getEntries().iterator();
        while (entryIterator.hasNext()) {
            if (nextSegment == null) {
                currentSegment = entryIterator.next();
                // it may be the first segment?
                if (insertIndex == 0 && address < currentSegment.getBaseAddress()) {
                    long base = 0;
                    long size = currentSegment.getBaseAddress();
                    artificialSegment = createSimulatedSegment(base, size);
                    break;
                }
                if (entryIterator.hasNext()) {
                    nextSegment = entryIterator.next();
                }
            } else {
                currentSegment = nextSegment;
                nextSegment = entryIterator.next();
            }
            insertIndex ++;
            if (address > currentSegment.getTopAddress() && address < nextSegment.getBaseAddress()) {
                long base = currentSegment.getTopAddress();
                long size = nextSegment.getBaseAddress() - currentSegment.getTopAddress();
                artificialSegment = createSimulatedSegment(base, size);
                logger.info(String.format("Created for address %s new artificial segment: %s", 
                                TraceUtils.getLongHexString(address), artificialSegment));
                break;
            }
        }
        if (artificialSegment != null) {
            getEntries().add(insertIndex, artificialSegment);
            simulatedSegmentCounter ++;
        }
        logger.debug("<< createArtificialSegment: ", artificialSegment != null);
        return artificialSegment != null;
    }

    private MemoryMapEntry createSimulatedSegment(long base, long size) {
        MemoryMapEntry artificialSegment;
        // create a segment to fill the gap
        artificialSegment = new MemoryMapEntry();
        artificialSegment.setBaseAddress(base);
        artificialSegment.setSize(size) ;
        artificialSegment.setRights("----");
        artificialSegment.setOwner(String.format("[simulated #%s]", simulatedSegmentCounter));
        artificialSegment.setTopAddress(base + size);
        createSegmentId(artificialSegment);
        return artificialSegment;
    }
	
   /**
     * returns the memory map 'segment' owner of a given address.
     * 
     * @param address the address to lookup.
     * 
     * @return {@link MemoryMap} of the owner for the address.
     */
    public MemoryMapEntry updateOwnerTopAddress (long address) {
        logger.debug(">> updateOwnerTopAddress(%s)", String.format("%04X",Long.toHexString(address)));
        MemoryMapEntry memMapEntry = null;
        for (int i = 0; i < getEntries().size() - 1; i++) {
            memMapEntry = getEntries().get(i);
            MemoryMapEntry nextEntry = getEntries().get(i + 1);
            if (address >= memMapEntry.getBaseAddress() && 
                    address < nextEntry.getBaseAddress()) 
            {
                memMapEntry.setTopAddress(address);
                memMapEntry.setSize(address - memMapEntry.getBaseAddress());
                break;
            }
            logger.debug("== Base address: %s, new top address to: %s ", 
                            String.format("%04X", address),
                            String.format("%04X", memMapEntry.getTopAddress()));
        }
        logger.debug("<< updateOwnerTopAddress: %s", memMapEntry);
        return memMapEntry;
    }
	
    /**
     * reads a memory map file produced with the 'pmap' command 
     * object with all the references parsed.
	 * 
	 * @param memFile the memory map file name.
	 * 
	 * @throws FileNotFoundException raised if the memory map file cannot be open.
	 * @throws InvalidTraceLineException raised then a memory map trace line is invalid.
	 * 
	 */
	public void readMemoryMapFile(String memFile) throws FileNotFoundException {
		logger.debug(">> readMemoryMapFile(%s) ", memFile);
		File file = new File(memFile);
		Scanner scanner = new Scanner(file);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line == null || line.length() < 31 || "total".equals(line) ) {
				continue;
			} else {
				MemoryMapEntry entry = createMemMapEntryFromLine(line);
				getEntries().add(entry);
			}
		}
		scanner.close();
		logger.debug("<< readMemoryMapFile():");
	}

    public MemoryMapEntry createMemMapEntryFromLine(String line) {
        MemoryMapEntry entry = new MemoryMapEntry();
        String address = line.substring(0,  16).trim();
        String size = line.substring(16, 23).trim();
        String rights = line.substring(25, 30).trim();
        String binary = line.substring(31).trim();
        entry.setBaseAddress(Long.parseUnsignedLong(address, 16));
        entry.setSize(Long.parseLong(size) * 1024);
        entry.setRights(rights);
        entry.setOwner(binary);
        entry.setTopAddress(entry.getBaseAddress() + entry.getSize());
        createSegmentId(entry);
        return entry;
    }
	
	/**
	 * the memory map add entries in order of base address, if the
	 * first byte is zero we can add new segment id. If it is not
	 * we need to check if the id is already in the memory map and 
	 * change it;
	 * 
	 * @param entry the entry to evaluate.
	 */
	private void createSegmentId(MemoryMapEntry newEntry) {
       logger.debug(">> createSegmentId(baseAddress=%s) ", String.format("%04X", newEntry.getBaseAddress()));
       String newEntryAddrPrefix = TraceUtils.getSegmentPrefix(newEntry.getBaseAddress());
       if ("0000".equals(newEntryAddrPrefix)) {
           newEntry.setSegmentId(segmentIdCounter ++);
       } else {
           // tricky part... we need to search for this prefix in the
           // current entries to see if we are not giving the same 
           // segment id for two entries.
           for (MemoryMapEntry entry : getEntries()) {
               String entryAddrPrefix = TraceUtils.getSegmentPrefix(entry.getBaseAddress());
               if (entryAddrPrefix.equals(newEntryAddrPrefix)) {
                   // for the time being.... GIVE UP!!!
                   logger.error("Error!!! Segment IDs not sane! See memory map!");
                   logger.error("baseAddress.......:" + String.format("%16X", newEntry.getBaseAddress()));
                   logger.error("entryAddrPrefix...: " + entryAddrPrefix);
                   logger.error("newEntryAddrPrefix: " + newEntryAddrPrefix);
                   System.exit(-1);
               }
           }
           // use the current address prefix as the segment id.
           newEntry.setSegmentId(Integer.parseUnsignedInt(newEntryAddrPrefix, 16));
       }
       logger.debug("<< createSegmentId(): - (not returned: ID=%s)", String.format("%04X", newEntry.getSegmentId()));
	}

	/**
	 * update the top address of the 'segment' memory map entry with
	 * the next address base address minus one.
	 * 
	 * @deprecated not used anymore because the reasearch need the real segment sizes.
	 */
	public void updateTopAddrWithBase() {
		logger.debug(">> updateTopAddrWithBase()");
		//
		// Updates the top address with the next entry base address
		//
		MemoryMapEntry lastEntry = null;
		for (MemoryMapEntry entry : getEntries()) {
			if (lastEntry != null) {
				lastEntry.setTopAddress(entry.getBaseAddress() - 1);
		        logger.debug("Base address: %s, new top address to: %s ", 
		                        String.format("%04X", lastEntry.getBaseAddress()),
		                        String.format("%04X", lastEntry.getTopAddress()));
			}
			lastEntry = entry;
		}
		lastEntry.setTopAddress(lastEntry.getBaseAddress() + (lastEntry.getSize() * 1024));
        logger.debug("Base address: %s, new top address to: %s ", 
                        String.format("%04X", lastEntry.getBaseAddress()),
                        String.format("%04X", lastEntry.getTopAddress()));
		logger.debug(">> updateTopAddrWithBase():");
	}
	
	/**
	 * Update the top address with the base + size in bytes.
	 * 
	 */
	public void updateTopAddrWithSize() {
		logger.debug(">> updateTopAddrWithSize()");
		for (MemoryMapEntry entry : getEntries()) {
			// Size is converted from Kbytes to bytes when reading the memory map.
			long topAddr = entry.getBaseAddress() + entry.getSize();
			entry.setTopAddress(topAddr);
		}		
		logger.debug(">> updateTopAddrWithSize():");
	}
	
	
	/**
	 * returns a text representing the memory mao table in CSV format.
	 * 
	 * @return a formatted string containing the memory map table in CSV.
	 */
	public String toStringCSV () {
	    logger.debug(">> toStringCSV()");
		StringBuffer buffer = new StringBuffer();
		buffer.append("Segment ID");
		buffer.append(", ");
		buffer.append("Base Address");
		buffer.append(", ");
		buffer.append("Top Address");
		buffer.append(", ");
		buffer.append("Size");
		buffer.append(", ");
		buffer.append("Rights");
		buffer.append(", ");
		buffer.append("Owner");
		buffer.append(", ");
		buffer.append("Instructions");
		buffer.append(", ");
		buffer.append("Mem. access");
		buffer.append(String.format("%n"));
		for (MemoryMapEntry entry : getEntries()) {
			buffer.append(String.format("%04X", entry.getSegmentId()));
			buffer.append(", ");
			buffer.append("0x" + String.format("%016X",entry.getBaseAddress()));
			buffer.append(", ");
			buffer.append("0x" + String.format("%016X", entry.getTopAddress()));
			buffer.append(", ");
			buffer.append(entry.getSize());
			buffer.append(", ");
			buffer.append(entry.getRights());
			buffer.append(", ");
			buffer.append(entry.getOwner());
			buffer.append(", ");
			buffer.append(entry.getInstructionCount());
			buffer.append(", ");
			buffer.append(entry.getMemoryAccessCount());
			buffer.append(String.format("%n"));
		}
		String result = buffer.toString();
        logger.debug("<< toStringCSV():");
		return result;
	}

    public int getSegmentIdCounter() {
        return segmentIdCounter;
    }

    
    public void setSegmentIdCounter(int segmentIdCounter) {
        this.segmentIdCounter = segmentIdCounter;
    }

    public int getSimulatedSegmentCounter() {
        return simulatedSegmentCounter;
    }

    
    public void setSimulatedSegmentCounter(int simulatedSegmentCounter) {
        this.simulatedSegmentCounter = simulatedSegmentCounter;
    }

}
