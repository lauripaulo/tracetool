package br.ufpr.dinf.arch.jbluepill;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.core.SegmentBufferWorkerThread;
import br.ufpr.dinf.arch.jbluepill.core.TlbWorkerThread;
import br.ufpr.dinf.arch.jbluepill.core.TraceWorker;
import br.ufpr.dinf.arch.jbluepill.exception.InvalidTraceLineException;
import br.ufpr.dinf.arch.jbluepill.model.Instruction;
import br.ufpr.dinf.arch.jbluepill.model.InstructionObserver;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMap;
import br.ufpr.dinf.arch.jbluepill.model.MemoryMapEntry;
import br.ufpr.dinf.arch.jbluepill.model.MemoryOperation;
import br.ufpr.dinf.arch.jbluepill.simulation.SegmentBuffer;
import br.ufpr.dinf.arch.jbluepill.simulation.TranslateLookasideBuffer;
import br.ufpr.dinf.arch.jbluepill.util.TraceReader;
import br.ufpr.dinf.arch.jbluepill.util.TraceToolState;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class TraceToolSimulatorLimit {
	
	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("TraceToolSimulator");
	public static final int BUFFER_SIZE = 250000;
	
	private String xmlFile;
	private String csvFile;
	private String resultsFile;
	private String memoryMapFile;

	private TraceToolState state = new TraceToolState();

//	private ArrayList<SegmentBuffer> sbList;
	private ArrayList<TranslateLookasideBuffer> tlbList;
	private ArrayList<Instruction> rogueMemRefsList;

	private ConcurrentLinkedQueue<Instruction> asmBuffer = new ConcurrentLinkedQueue<Instruction>();

	private long instructionCount = 0L;
    private long memOps = 0L;

	private long bufferCount;
	
	private MemoryMap memMap;
	
	private long startingTime;

	public TraceToolSimulatorLimit() {
		rogueMemRefsList = new ArrayList<Instruction>();

//		sbList = new ArrayList<SegmentBuffer>();
		tlbList = new ArrayList<TranslateLookasideBuffer>();

//		final SegmentBuffer sb32 = new SegmentBuffer(1,  32);
//		final SegmentBuffer sb64 = new SegmentBuffer(1,  64);
//		final SegmentBuffer sb16x8 = new SegmentBuffer(16, 8);
//		sbList.add(sb32);
//		sbList.add(sb64);
//		sbList.add(sb16x8);

		final TranslateLookasideBuffer tlb256 = new TranslateLookasideBuffer(1, 256);
		final TranslateLookasideBuffer tlb1024 = new TranslateLookasideBuffer(1, 1024);
        tlbList.add(tlb256);
        tlbList.add(tlb1024);
        
        startingTime = System.currentTimeMillis();
        TraceUtils.initStatsCsv();
	}
	
	public void start() throws InvalidTraceLineException, IOException {
		logger.debug(">> start()");
		
		state = (TraceToolState) TraceUtils.loadWithXmlEncoder(xmlFile);
		memMap = generateMemoryMap(memoryMapFile);
		state.setMemoryMap(memMap);
//		state.setSbList(sbList);
		state.setTlbList(tlbList);
		
		if (state.isTraceFilesCompressed() && state.isNonCompressedFilesRemoved()) {
			
			System.out.println("Trace files are compressed.");
			
			// only zip files, decompress to a temp file
			// process it and remove
			for (String zipFile : state.getCompressedTraceFiles()) {
				
			    state.setCurrentSimulationFile(state.getCompressedTraceFiles().indexOf(zipFile));
				String zipFilePath = state.getPathToFiles() + File.separator + zipFile;
				String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + "tracetool-" + UUID.randomUUID() + "-tempfile.txt";
				
				System.out.println("Extrating file: " + zipFilePath + "...");
				TraceUtils.decompressGzipFile(zipFilePath, tempFilePath);
				
				processTraceFile(tempFilePath);

				try {
				    File tmp = new File (tempFilePath);
                    Files.delete(tmp.toPath());
                } catch (IOException e) {
                    logger.warn("Cannot remove temp file: %s", tempFilePath); 
                    System.out.println("Cannot remove temp file " + tempFilePath + "!!!!");
                } finally {
                    logger.debug("File removed: %s", tempFilePath); 
                    System.out.println("Temp file deleted: "  + tempFilePath);
				}
				saveResults(false);
			}
			
		} if (!state.isNonCompressedFilesRemoved()) {
			
			// iterate over the file list, processing each one until the end
			for (String traceFile : state.getTraceFiles()) {
                state.setCurrentSimulationFile(state.getTraceFiles().indexOf(traceFile));
			    String traceFilePath = state.getPathToFiles() + File.separator + traceFile;
				System.out.println("Trace file to process.: " + traceFilePath + "...");
				processTraceFile(traceFilePath);
				saveResults(false);
			}
			
		}
		
		saveResults(true);

        System.out.println("\nDone!");
        

		logger.debug(">> start():");
	}

    private void saveResults(boolean isFinal) throws FileNotFoundException, UnsupportedEncodingException {
		logger.debug(">> saveResults(%s)", isFinal);
        StringBuffer buffer = new StringBuffer();
        
		long seconds = TimeUnit.SECONDS.convert((System.currentTimeMillis() - startingTime), 
				TimeUnit.MILLISECONDS);
		
		buffer.append(String.format("%s%n%n", getProgressDetails()));
        
        buffer.append(String.format("Path do trace folder...........: %s %n", state.getPathToFiles()));
        buffer.append(String.format("TraceTool state file...........: %s %n", xmlFile));
        buffer.append(String.format("Instructions processed.........: %s %n", instructionCount));
        buffer.append(String.format("Memory operations processed....: %s %n", memOps));
        buffer.append(String.format("Time in seconds to finish......: %s seconds %n", seconds));
        
        logger.debug("Instructions processed: %s, Memory operations processed: %s", instructionCount, memOps);

        for (TranslateLookasideBuffer tlb : tlbList) {
        	buffer.append(String.format("%n%s%n", 
        	                TraceUtils.buildCacheResults(tlb, true, rogueMemRefsList.size())));
        }
        
//        for (SegmentBuffer sb : sbList) {
//        	buffer.append(String.format("%n%s%n", 
//        	                TraceUtils.buildCacheResults(sb, true, rogueMemRefsList.size())));
//        }       
       
        String result = buffer.toString();
        
        // System.out.println(result);

        System.out.println("Saving results to: " + getResultsFile());
        if (isFinal) {
            System.out.println(result);
            TraceUtils.saveToTxtFile(getResultsFile(), result);
            TraceUtils.saveStatsCsv("stats-" + getResultsFile() + ".csv");
        } else {
            TraceUtils.saveToTxtFile(getResultsFile() + "-" + instructionCount + ".txt", result);
            TraceUtils.saveStatsCsv("stats-" + getResultsFile() + ".csv");
        }
        
        TraceUtils.saveToTxtFile(getCsvFile() + "-" + instructionCount + ".csv", this.memMap.toStringCSV());

        logger.debug("<< saveResults():");
    }
	
	public void processTraceFile(String traceFile) throws InvalidTraceLineException, IOException {
		logger.debug(">> processTraceFile(%s)", traceFile);
		
		TraceReader reader = new TraceReader(traceFile);
		TraceWorker worker = new TraceWorker(reader);
		
		System.out.println("\nWorking...\n");
		
        worker.processTrace(new InstructionObserver() {
        	
            public void instructionReaded(Instruction asm) {

        		if (bufferCount == BUFFER_SIZE) {
        			processBufferMultiThread(asmBuffer);
        			asmBuffer = new ConcurrentLinkedQueue<Instruction>();
        			bufferCount = 0;
        		}
        		
                if (validateMemRefs(asm)) {
        			asmBuffer.add(asm);
            		bufferCount ++;    
                }
            }

            private boolean validateMemRefs(Instruction asm) {
	    		boolean isReferenceOk = true;        		
				MemoryMapEntry memEntry;
                
        		memEntry = memMap.getOwnerOfAddress(asm.getAddrAsLongValue());
                if (memEntry == null) {
                    if (!memMap.createSimulatedSegmentForAddress(asm.getAddrAsLongValue())) {
                     	handleRogueMemReference(asm, memEntry);
                     	isReferenceOk = false;
                    }
                } else {
            		// catch a rogue reference here?
                    for (MemoryOperation memOp : asm.getMemoryOperations()) {
                    	 memEntry = memMap.getOwnerOfAddress(memOp.getAddrAsLongValue());
                         if (memEntry == null) {
                             if (!memMap.createSimulatedSegmentForAddress(memOp.getAddrAsLongValue())) {
                             	handleRogueMemReference(asm, memEntry);
                             	isReferenceOk = false;
                             	break;
                             }
                         }
                    }
                }
                return isReferenceOk;
			}
            
        	
            private void handleRogueMemReference(Instruction asm, MemoryMapEntry memEntry) {
        		String logMessage = String.format("Rogue memory reference! Asm=%s, Entry=%s",
        				asm, memEntry); 
        		//Add to Rogue memory reference list
        		rogueMemRefsList.add(asm);
        		logger.warn(logMessage);
        		//System.out.println(logMessage);
        	}
            

        });
        
		System.out.println("\nEnd of log file.");
		if (asmBuffer.size() > 0) {
			System.out.println("Waiting for instruction buffer to complete...");
			processBufferMultiThread(asmBuffer);
		}
		System.out.println("\nDone.\n");
        logProgress();

        state.setRogueInstructionsList(rogueMemRefsList);
		state.setCurrentSimulationLine(reader.getLineNumber());
		TraceUtils.saveWithXmlEncoder(state, xmlFile + "-runtime.xml");
		
		// Reset buffer state
		asmBuffer = new ConcurrentLinkedQueue<Instruction>();
		
		reader.close();
		
		logger.debug("<< processTraceFile():");
	}


	private void processBufferMultiThread(ConcurrentLinkedQueue<Instruction> asmBuffer) {
    	logger.debug(">> processBufferMultiThread(size=%s)", asmBuffer.size());
		// Multi thread.

		TlbWorkerThread tlbThread;
//		SegmentBufferWorkerThread sbThread;

//		sbThread = new SegmentBufferWorkerThread("SBs thread", sbList, state.getMemoryMap());
//		sbThread.setExecutionStack(asmBuffer);
//		sbThread.start();
		
		tlbThread = new TlbWorkerThread("TLBs thread", tlbList, state.getMemoryMap());
		tlbThread.setExecutionStack(asmBuffer);
		tlbThread.start();
		
		// while the threads are working let's update the memory map with
		// instruction and data access statistics
		for (Instruction asm : asmBuffer) {
			MemoryMapEntry memEntry = memMap.getOwnerOfAddress(asm.getAddrAsLongValue());
			if (memEntry != null) {
				memEntry.setInstructionCount(memEntry.getInstructionCount() + 1);
				instructionCount ++;
			}
		    for (MemoryOperation memOp : asm.getMemoryOperations()) {
				if (memEntry != null) {
					memEntry = memMap.getOwnerOfAddress(memOp.getAddrAsLongValue());
		        	memEntry.setMemoryAccessCount(memEntry.getMemoryAccessCount() + 1);
		        	memOps ++;
				}
		    }
		}
        System.gc();
        System.out.print("+");		
		
		try {
			tlbThread.join();
		    System.out.print(".");
//			sbThread.join();
		    System.out.print(".");
		} catch (InterruptedException e) {
			System.out.println("Multi thread problem.");
			System.out.println(e.getMessage() + "\n");
			logger.error(e);
			System.exit(9);
		}
    	logger.debug("<< processBufferMultiThread():");
	}

	private void logProgress() {
    	logger.debug(">> logProgress()");
		if (state.getTotalInstructionCount() > 0) {
	        String progress = getProgressDetails();
	        System.out.println();
			System.out.println(progress);
		}
		System.out.println("----------------------------------------------------------------------\n");
//        for (SegmentBuffer sb : sbList) {
//        	System.out.println(String.format("%s", TraceUtils.buildCacheResults(sb, false, rogueMemRefsList.size())));
//        }
        for (TranslateLookasideBuffer tlb : tlbList) {
        	System.out.println(String.format("%s", TraceUtils.buildCacheResults(tlb, false, rogueMemRefsList.size())));
        }
    	logger.debug("<< logProgress()");
	}

    private String getProgressDetails() {
    	logger.debug(">> getProgressDetails()");
        double ratio;
        long seconds = TimeUnit.SECONDS.convert((System.currentTimeMillis() - startingTime), TimeUnit.MILLISECONDS);
        final double count = instructionCount;
        ratio = (count / state.getTotalInstructionCount()) * 100;
        String progress = String.format("Instructions readed: %s ( %3.2f %% ) - Rogue: %s - Sim. segs. created: %s - Time: %s seconds. ", 
        		instructionCount, ratio, rogueMemRefsList.size(), memMap.getSimulatedSegmentCounter(), seconds);
    	logger.debug("<< getProgressDetails(): %s", progress);
        return progress;
    }
	
	public MemoryMap generateMemoryMap(String memoryMapFile) throws FileNotFoundException {
    	logger.debug(">> generateMemoryMap(%s)", memoryMapFile);
		MemoryMap map = new MemoryMap();
		map.readMemoryMapFile(memoryMapFile);
		//map.updateTopAddrWithBase();
		map.updateTopAddrWithSize();
    	logger.debug("<< generateMemoryMap(): %s", map);
		return map;
	}

	public static void main(String[] args)  {
		System.out.println("TraceToolSimulator " + TraceUtils.APP_VERSION);
		System.out.println("-----------------------------");
		System.out.println("Reads trace information from TraceTool XML file");
		System.out.println("");
		System.out.println("Usage: TraceToolSimulator --xmlfile:<file> --memmap:<file> --csvexport:<csvfile> --results:<filename>");
		System.out.println();
		TraceToolSimulatorLimit tool = new TraceToolSimulatorLimit();
		try {
			for (int i=0; i < args.length; i++) {
				
				if (args[i].startsWith("--xmlfile:")) {
					
					String xml = args[i].substring(10, args[i].length());
					tool.setXmlFile(xml);
					System.out.println("XML trace tool file ....: " + xml);
					logger.debug("XML trace tool file: %s", xml);
					
				} else if (args[i].startsWith("--csvexport:")) {
					
					String csvexport = args[i].substring(12, args[i].length());
					tool.setCsvFile(csvexport);
					System.out.println("Export to CSV file......: " + csvexport);
                    logger.debug("Export to CSV file: %s", csvexport);
					
				} else if (args[i].startsWith("--results:")) {
					
					String results = args[i].substring(10, args[i].length());
					tool.setResultsFile(results);
					System.out.println("Results file name ......: " + results);
                    logger.debug("Results file name: %s", results);
					
				} else if (args[i].startsWith("--memmap:")) {
					
					String memmap = args[i].substring(9, args[i].length());
					tool.setMemoryMapFile(memmap);
					System.out.println("Memory map file.........: " + memmap);
                    logger.debug("Memory map file: %s", memmap);
					
				}
				
			}
			if (tool.getCsvFile() == null || tool.getCsvFile().length() == 0) {
				System.out.println("Param --csvexport missing. See usage.");
                logger.debug("Param --csvexport missing.");
				System.exit(2);
			}
			if (tool.getMemoryMapFile() == null || tool.getMemoryMapFile().length() == 0) {
				System.out.println("Param --memmap missing. See usage.");
                logger.debug("Param --memmap missing.");
				System.exit(3);
			}
			if (tool.getResultsFile() == null || tool.getResultsFile().length() == 0) {
				System.out.println("Param --results missing. See usage.");
                logger.debug("Param --results missing.");
				System.exit(4);
			}
			if (tool.getXmlFile() == null || tool.getXmlFile().length() == 0) {
				System.out.println("Param --xmlfile missing. See usage.");
                logger.debug("Param --xmlfile missing.");
				System.exit(5);
			}
			try {
				tool.start();
			} catch (InvalidTraceLineException e) {
				System.out.println("Invalid trace line found. Aborting.");
				System.out.println(e.getMessage() + "\n");
				logger.error(e);
				System.exit(6);
			} catch (FileNotFoundException e) {
				System.out.println("File not found. Aborting.");
				System.out.println(e.getMessage() + "\n");
				logger.error(e);
				System.exit(7);
			} catch (UnsupportedEncodingException e) {
				System.out.println("File encoding problem.");
				System.out.println(e.getMessage() + "\n");
				logger.error(e);
				System.exit(8);
            } catch (IOException e) {
                System.out.println("I/O Exception.");
                System.out.println(e.getMessage() + "\n");
                logger.error(e);
                System.exit(9);
			}
			System.exit(0);
		} catch (NumberFormatException e) {
			System.out.println("Wrong --max param. See usage.\n");
			e.printStackTrace();
			logger.error(e);
			System.exit(9);
		}
	}

	public String getXmlFile() {
		return xmlFile;
	}

	public void setXmlFile(String xmlFile) {
		this.xmlFile = xmlFile;
	}

	public String getCsvFile() {
		return csvFile;
	}

	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}

	public String getResultsFile() {
		return resultsFile;
	}

	public void setResultsFile(String resultsFile) {
		this.resultsFile = resultsFile;
	}

	public String getMemoryMapFile() {
		return memoryMapFile;
	}

	public void setMemoryMapFile(String memoryMapFile) {
		this.memoryMapFile = memoryMapFile;
	}

}
