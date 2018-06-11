package br.ufpr.dinf.arch.jbluepill.util;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.exception.InvalidTraceLineException;
import br.ufpr.dinf.arch.jbluepill.model.Instruction;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMapEntry;
import br.ufpr.dinf.arch.jbluepill.model.MemoryOperation;
import br.ufpr.dinf.arch.jbluepill.model.MemoryOperationType;
import br.ufpr.dinf.arch.jbluepill.simulation.Cache;
import br.ufpr.dinf.arch.jbluepill.simulation.SegmentBuffer;
import br.ufpr.dinf.arch.jbluepill.simulation.TranslateLookasideBuffer;

/**
 * utility static methods to manipulate trace file, persist data and text,
 * format values and extract info from traces.
 * 
 * @author Lauri Laux
 */
public class TraceUtils {
	
	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("TraceUtils");
	
	private static StringBuilder csvBuffer = new StringBuilder();

	public static String APP_VERSION = "v0.9.2.6";
	
	/**
	 * Inits a buffer to keep track of the simulation evolution stats in a
	 * CSV file. To be used later in a graphical representation or to work in
	 * it with a Python script of some sort. 
	 */
	public static void initStatsCsv() {
        csvBuffer.append("buffer");
        csvBuffer.append(";");
        csvBuffer.append("calls");
        csvBuffer.append(";");
        csvBuffer.append("hits");
        csvBuffer.append(";");
        csvBuffer.append("miss");
        csvBuffer.append(";");
        csvBuffer.append("added");
        csvBuffer.append(";");
        csvBuffer.append("present");
        csvBuffer.append(";");
        csvBuffer.append("removed");
        csvBuffer.append(";");
        csvBuffer.append("rogue");
        csvBuffer.append("\n");
	}
	
	/**
	 * Saves the CSV stats buffer to a file.
	 * 
	 * @param fileName the file name with full path
	 * @throws FileNotFoundException self explanatory
	 * @throws UnsupportedEncodingException self explanatory
	 */
	public static void saveStatsCsv(String fileName) throws FileNotFoundException, UnsupportedEncodingException {
	    saveToTxtFile(fileName, csvBuffer.toString());
	}
	
	/**
	 * returns a memory operation object extracted from a trace line.
	 * 
	 * @param traceLine the string with the memory operation.
	 * @param lineNumber line number inside the trace file.
	 * 
	 * @return returns a new {@link MemoryOperation} object.
	 * 
	 * @throws InvalidTraceLineException raised when an invalid trace line is found.
	 */
	public static MemoryOperation parseMemoryOperationLine (String traceLine, long lineNumber) 
			throws InvalidTraceLineException {
		MemoryOperation operation = new MemoryOperation();
		String[] parts = traceLine.split("\\,");
		try {
			if (parts[0] != null && parts[0].length() > 0) {
				if ("L".equals(parts[0].toUpperCase())) {
					operation.setType(MemoryOperationType.LOAD);
				} else if ("S".equals(parts[0].toUpperCase())) {
					operation.setType(MemoryOperationType.STORE);
				} else if ("M".equals(parts[0].toUpperCase())) {
					operation.setType(MemoryOperationType.MODIFY);
				} else {
					throw new InvalidTraceLineException("Line does not contain a memory operation. Line=" + 
							lineNumber + " content='" + traceLine + "'");
				}
			}
			operation.setAddress(parts[1]);
			operation.setAddrAsLongValue(Long.parseUnsignedLong(parts[1], 16));
			operation.setSize(Integer.parseInt(parts[2]));
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Invalid memory operation trace line! Line:" 
					+ lineNumber + ", content: " + traceLine, e);
			throw new InvalidTraceLineException("Invalid memory operation trace line! Line:" 
					+ lineNumber + ", content: " + traceLine, e);
		}
		return operation;
	}
	
	
	/**
	 * extract a new {@link Instruction} object from the traceLine param and
	 * trace file line number.
	 * 
	 * @param traceLine text line with the instruction to extract.
	 * @param lineNumber line number inside the trace line. 
	 * 
	 * @return a new {@link Instruction} object.
	 * 
	 * @throws InvalidTraceLineException raised when an invalid trace line is found.
	 */
	public static Instruction parseInstructionLine (String traceLine, long lineNumber) 
			throws InvalidTraceLineException {
//		logger.debug(">> parseInstructionLine(traceLine=%s, lineNumber=%s) ", traceLine, lineNumber);
		Instruction asm = new Instruction();
		String[] parts = traceLine.split("\\,");
		try {
			asm.setAddr(parts[1]);
			asm.setAddrAsLongValue(Long.parseUnsignedLong(parts[1], 16));
			asm.setSize(Integer.parseInt(parts[2]));
			asm.setTraceLineNumber(lineNumber);
		} catch (ArrayIndexOutOfBoundsException e) {
			logger.error("Invalid instruction trace line! Line:" 
					+ lineNumber + ", content: " + traceLine, e);
			throw new InvalidTraceLineException("Invalid instruction trace line! Line:" 
					+ lineNumber + ", content: " + traceLine, e);
		}
//		logger.debug("<< parseInstructionLine(): %s", asm);
		return asm;
	}
	
	
	/**
	 * returns an String with the unsigned long value and it's hex representation.
	 * 
	 * @param value long value to format.
	 * 
	 * @return String with the unsigned long value and it's hex representation.
	 */
	public static String getLongHexString (long value) {
		String result =  Long.toUnsignedString(value) + " (0x" + Long.toHexString(value) + ")";
		return result;
	}
	
	/**
	 * write the XML object representation of the original object to the outputFile.
	 * 
	 * @param data {@link Object} to be saved.
	 * @param outputFile the output file name to persiste.
	 * 
	 * @throws FileNotFoundException raised when the output file cannot be created.
	 */
	public static void saveWithXmlEncoder (Object obj, String outputFile) throws FileNotFoundException {
		logger.debug(">> saveWithXmlEncoder(data=%s, outputFile=%s)", obj, outputFile);
		XMLEncoder encoder;
		encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(outputFile)));
		encoder.writeObject(obj);
		encoder.flush();
		encoder.close();
		logger.debug("<< saveWithXmlEncoder()");
	}

	
	/**
	 * loads {@link AnalysisData} from the inputFile param.
	 * 
	 * @param inputFile file with the XML representation of {@link AnalysisData}
	 * 
	 * @return {@link AnalysisData} object from the disk.
	 * 
	 * @throws FileNotFoundException raised when the file cannot be loaded.
	 */
	public static Object loadWithXmlEncoder (String inputFile) throws FileNotFoundException {
		Object data = null;
		logger.debug(">> loadWithXmlEncoder(inputFile=%s)", inputFile);
		XMLDecoder decoder;
		decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(inputFile)));
		data = decoder.readObject();
		decoder.close();
		logger.debug("<< loadWithXmlEncoder(): %s", data);
		return data;
	}
	
	
	/**
	 * saves a text to a file on disk using UTF-8 encoding.
	 * 
	 * @param fileName file name to be saved.
	 * @param txt the text to save.
	 * 
	 * @throws FileNotFoundException raised when the text file cannot be created.
	 * 
	 * @throws UnsupportedEncodingException raised when the th UTF-8 encoding cannot be used. 
	 */
	public static void saveToTxtFile (String fileName, String txt) throws FileNotFoundException, UnsupportedEncodingException {
        logger.debug(">> saveToTxtFile(%s, %s)", fileName, 
                        (txt != null && txt.length() > 20 ? txt.substring(0, 20) + "..." : txt));
	    PrintWriter writer = new PrintWriter(fileName, "UTF-8");
	    writer.print(txt);
	    writer.flush();
	    writer.close();
        logger.debug("<< saveToTxtFile():");
	}
	
	
	/**
	 * transform a non segmented address from a real execution trace 
	 * to a segmented address made of a 'segment Id' and 'displacement'.
	 * 
	 * if the address has any value in the segment id area it will be used
	 * as the segment id.
	 * 
	 * <code>
	 * Example: Changeable address.
	 * 
     * refAddress: 0x000000000060AFFF
	 * refSegId..: 0x0001
	 * 
	 * return....: 0x000100000060AFFF
	 *               |--||----------|
	 *                |       |
	 *                |       +-------> displacement.
	 *                ++---------------> segment id.
     *
     *
	 * Example: Non changeable address.
     * 
     * refAddress: 0xFFFF00000060AFFF
     * refSegId..: 0x001F
     * 
     * return....: 0xFFFF00000060AFFF
     *               |--||----------|
     *                |       |
     *                |       +-------> displacement.
     *                ++---------------> segment id.
</code>
	 * 
	 * @param refAddress original non segmented address
	 * @param refSegId the segment id to be used
	 * 
	 * @return a segmented address made of the first byte as the segment id 
	 *         and the rest as displacement. 
	 */
    public static long createSegmentedAddress(long address, int segId) {
		long addressSegmented = 0L;
		String addressHex = String.format("%016X", address);
		String currAddrPrefixHex = addressHex.substring(0, 4);
		String SegIdHex = String.format("%04X", segId);
		if ("0000".equals(currAddrPrefixHex)) {
			addressHex = addressHex.substring(4, 16);
			addressHex = SegIdHex + addressHex;
			addressSegmented = Long.parseUnsignedLong(addressHex, 16);
		} else {
			addressSegmented = address;
		}
		return addressSegmented;
	}
    
    /**
     * 
     * @param address
     * @return
     */
    public static String getSegmentPrefix(long address) {
        String hexAddressPrefix = null;
        String segAddressHex = String.format("%016X", address);
        hexAddressPrefix = segAddressHex.substring(0, 4);
        return hexAddressPrefix;
    }
    
    
    /**
     * 
     * @param sb
     * @param address
     * @param memEntry
     * 
     */
    public static void evalSegmentBuffer(final SegmentBuffer sb, long address, MemoryMapEntry memEntry) {
       if (sb.findAddress(address) == null) {
            sb.addEntry(address, memEntry.getBaseAddress() * 100, memEntry.getRights(),
                            memEntry.getOwner() + " " + memEntry.getRights());
        }
    }

    /**
     * 
     * @param tlb
     * @param address
     * @param memEntry
     * 
     */
    public static void evalTLB(final TranslateLookasideBuffer tlb, long address, MemoryMapEntry memEntry) {
        if (tlb.findAddress(address) == null) {
            tlb.addEntry(address, address * 100, memEntry.getRights(),
                            memEntry.getOwner() + " " + memEntry.getRights());
        }
    }


    /**
     * returns a String with the cache results and optional cache contents.
     * 
     * @param cache
     * @param buildCacheContents
     * @return
     */
    public static String buildCacheResults (Cache cache, boolean buildCacheContents, long rogue) {
        logger.debug(">> buildCacheResults (%s, %s)", cache, buildCacheContents);
        
        StringBuilder buffer = new StringBuilder();

        long miss = cache.getCalls() - cache.getHits();
        
        buffer.append(String.format("Configuration ...........: %s %n", cache.toString()));
        
        buffer.append(String.format("Total calls: %s, ", cache.getCalls()));
        buffer.append(String.format("Total hits: %s, ", cache.getHits()));
        buffer.append(String.format("Total miss: %s %n", miss));
        
        buffer.append(String.format("Entries created: %s, ", cache.getEntriesAdded()));
        buffer.append(String.format("Entries already present: %s, ", cache.getEntriesAlreadyPresent()));
        buffer.append(String.format("Entries removed: %s %n", cache.getEntriesRemoved()));
        
        logger.debug("== calls: %s, hit: %s, miss: %s, added: %s, preset: %s, removed: %s", cache.getCalls(), cache.getHits(), miss, cache.getEntriesAdded(),
                        cache.getEntriesAlreadyPresent(), cache.getEntriesRemoved());

        if (buildCacheContents) {
            buffer.append(String.format("Final cache contents: %n"));
            for (int i=0; i < cache.getSize(); i++) {
            	String lruInfo = "";
            	for (Integer lru : cache.getLine()[i].getLruStack()) {
            		lruInfo = lruInfo + " " + lru;
            	};
                buffer.append(String.format("Line: %s  (LRU indexes =[%s]) %n", i, lruInfo));
                for (int j=0; j < cache.getAssociativity(); j++) {
                    buffer.append(String.format(">> Set %2s - %s %n", j, cache.getLine()[i].getSet()[j]));
                }
            }
            // Updates the stats *only* when we have a 'good amount' of information 
            updateCsvStats(cache, miss, rogue);
        }
        final String result = buffer.toString(); 
        logger.debug("<< buildCacheResults ():");        
        return result;
    }

    /**
     * Update te CSV stats
     * @param cache
     * @param miss
     */
    private static void updateCsvStats(Cache cache, long miss, long rogue) {
        String name = cache.toString();
        name = name.substring(0, name.indexOf(","));
        csvBuffer.append(name);
        csvBuffer.append(";");
        csvBuffer.append(cache.getCalls());
        csvBuffer.append(";");
        csvBuffer.append(cache.getHits());
        csvBuffer.append(";");
        csvBuffer.append(miss);
        csvBuffer.append(";");
        csvBuffer.append(cache.getEntriesAdded());
        csvBuffer.append(";");
        csvBuffer.append(cache.getEntriesAlreadyPresent());
        csvBuffer.append(";");
        csvBuffer.append(cache.getEntriesRemoved());
        csvBuffer.append(";");
        csvBuffer.append(rogue);
        csvBuffer.append("\n");
    }
    
    public static void decompressGzipFile(String gzipFile, String newFile) {
        logger.debug(">> decompressGzipFile(%s, %s)", gzipFile, newFile);
        try {
            FileInputStream fis = new FileInputStream(gzipFile);
            GZIPInputStream gis = new GZIPInputStream(fis);
            FileOutputStream fos = new FileOutputStream(newFile);
            byte[] buffer = new byte[16384];
            int len;
            while((len = gis.read(buffer)) != -1){
                fos.write(buffer, 0, len);
            }
            //close resources
            fos.flush();
            fos.close();
            gis.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug(">> decompressGzipFile():");
    }

    public static void compressGzipFile(String file, String gzipFile) {
        logger.debug(">> compressGzipFile(%s, %s)", gzipFile, gzipFile);
        try {
            FileInputStream fis = new FileInputStream(file);
            FileOutputStream fos = new FileOutputStream(gzipFile);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[16384];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.flush();
            gzipOS.close();
            fos.flush();
            fos.close();
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        logger.debug("<< compressGzipFile():");
    }

}