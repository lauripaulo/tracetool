
package br.ufpr.dinf.arch.jbluepill;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.core.SocketConnectionServer;
import br.ufpr.dinf.arch.jbluepill.exception.InvalidTraceLineException;
import br.ufpr.dinf.arch.jbluepill.util.CompressHelpThread;
import br.ufpr.dinf.arch.jbluepill.util.TraceReader;
import br.ufpr.dinf.arch.jbluepill.util.TraceToolState;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class TraceTool implements TraceToolModel {

    private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("TraceTool");

    // 10 Billion
    public static long INSTRUCTION_LIMIT = 10000000000L;

    // For tests only
    // public static long INSTRUCTION_LIMIT = 10000L;

    private String objXmlFileName;
    private String outputTraceFileName;
    private long maxInstructionsInTraceFile;
    private String workingMode;
    private boolean compressFiles = false;
    private boolean removeNonCompressedFiles = false;
    private String readFromFileName;

    private long instructionCounter = 0L;
    private long instructionsInFileCounter = 0L;
    private long fileNumberCounter = 0L;

    private TraceReader reader;
    private FileWriter fileWriter;
    private BufferedWriter fileBufferWriter;
    private PrintWriter fileOutWriter;
    private String currentLogFile;

    private LinkedList<CompressHelpThread> threadList = new LinkedList<CompressHelpThread>();
    private TraceToolState state = new TraceToolState();

    public TraceTool() {
    }

    /*
     * I 04000cc0,3 I 04000cc3,5 S ffefffeb8,8 I 04004c00,1 S ffefffeb0,8
     * 
     */

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#start()
     */
    public void start() throws InvalidTraceLineException, InterruptedException {
        logger.debug(">> start()");

        try {
            createNewOutputFile();
            state.setTraceFilesCompressed(compressFiles);
            state.setPathToFiles(System.getProperty("user.dir"));
            state.setNonCompressedFilesRemoved(isRemoveNonCompressedFiles());

            if ("file".equals(workingMode) || "stdin".equals(workingMode)) {
                handleTraceReader();
            } else if ("socket".equals(workingMode)) {
                handleSocketConnection();
            }
        } catch (FileNotFoundException e) {
            logger.debug("File not found. Message: ", e);
            System.out.println("File not found: " + e.getMessage());
        } catch (IOException e) {
            logger.debug("I/O Error. Message: ", e);
            System.out.println("File not found: " + e.getMessage());
        }

        System.out.println("Done!");

        logger.debug("<< start():");
    }

    private void waitForZipThread() throws InterruptedException {
        logger.debug(">> waitForZipThread()");

        if (compressFiles) {
            // wait for compress thread...
            System.out.println("Wait for compress threads to finish...");
            for (CompressHelpThread thread : threadList) {
                thread.join();
            }
        }
        logger.debug("<< waitForZipThread()");
    }

    private void handleTraceReader() throws IOException, InterruptedException {
        logger.debug(">> handleTraceReader()");

        initTraceReader();
        while (reader.hasNextLine()) {
            String line = reader.readNextLine();
            processLine(line);
        }
        waitForZipThread();
        savestate();

        logger.debug(">> handleTraceReader()");
    }

    private void handleSocketConnection() throws IOException, InterruptedException {
        logger.debug(">> handleSocketConnection()");

        SocketConnectionServer server = new SocketConnectionServer(1500, this);
        System.out.println("Waiting socket connection...");
        server.start();

        try {
            server.join();
        } catch (InterruptedException e) {
            logger.error("Thread error!", e);
            System.exit(1);
        }

        System.out.println();
        System.out.println("Instructions processed: " + instructionCounter);
        state.setTotalInstructionCount(instructionCounter);
        closeCurrentOutputFile();
        waitForZipThread();
        savestate();

        logger.debug("<< handleSocketConnection()");
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#processLine(java.lang.String)
     */
    public void processLine(String line) throws IOException {
        // do not log the method. It needs to be real fast.
        StringBuffer buffer = new StringBuffer();
        if (line.startsWith("(")) {
            line = line.substring(line.indexOf(' ') + 1, line.length());
        }
        if (line.startsWith("I  ")) {
            // instruction found
            buffer.append("I,");
            instructionCounter++;
            instructionsInFileCounter++;
        } else if (line.startsWith(" L ")) {
            // load found
            buffer.append("L,");

        } else if (line.startsWith(" S ")) {
            // store found
            buffer.append("S,");

        } else if (line.startsWith(" M ")) {
            // modify found
            buffer.append("M,");
        } else {
            // discard lines...
            System.out.println("Ignoring line: " + line);
            logger.debug("== Ignoring line: " + line);
            state.getIgnoredLines().addLast(line);
            return;
        }
        String addressAndSize = line.substring(3, line.length());
        if (addressAndSize.length() > 0 && addressAndSize.indexOf(',') != 0) {
            buffer.append(addressAndSize);
        } else {
            System.out.println("Ignoring line: " + line);
            logger.debug("== Ignoring line: " + line);
            state.getIgnoredLines().addLast(line);
            return;
        }
        // check if file is full
        if (instructionsInFileCounter == maxInstructionsInTraceFile) {
            instructionsInFileCounter = 0;
            fileNumberCounter++;
            closeCurrentOutputFile();
            System.out.println("Instructions so far: " + instructionCounter);
            System.out.println("Last line readed: " + line);
            createNewOutputFile();
            savestate();
        }
        fileOutWriter.println(buffer.toString());
    }

    private void savestate() {
        logger.debug(">> savestate()");
        try {
            TraceUtils.saveWithXmlEncoder(state, getObjXmlFileName());
        } catch (FileNotFoundException e) {
            System.out.println("Error writing state XML object.");
            System.out.println("Error: " + e.getMessage());
            System.exit(2);
        }
        logger.debug("<< savestate()");
    }

    private void createNewOutputFile() throws IOException {
        logger.debug(">> createNewOutputFile()");

        currentLogFile = String.format("%s-trace-%05d.txt", outputTraceFileName, fileNumberCounter);
        fileWriter = new FileWriter(currentLogFile, true);
        fileBufferWriter = new BufferedWriter(fileWriter);
        fileOutWriter = new PrintWriter(fileBufferWriter);
        System.out.println("Trace file created: " + currentLogFile);
        state.getTraceFiles().add(currentLogFile);

        logger.debug("<< createNewOutputFile():");
    }

    private void closeCurrentOutputFile() throws IOException {
        logger.debug(">> closeCurrentOutputFile()");
        fileOutWriter.flush();
        fileBufferWriter.flush();
        fileWriter.flush();
        fileOutWriter.close();
        fileBufferWriter.close();
        fileWriter.close();
        // compress files
        if (compressFiles) {
            CompressHelpThread gzipThread;
            gzipThread = new CompressHelpThread(currentLogFile, currentLogFile + ".zip", isRemoveNonCompressedFiles());
            threadList.addFirst(gzipThread);
            gzipThread.start();
            state.getCompressedTraceFiles().add(currentLogFile + ".zip");
            System.out.println("Zip thread started...");
        }

        logger.debug("<< closeCurrentOutputFile()");
    }

    private void initTraceReader() throws FileNotFoundException {
        logger.debug(">> initTraceReader()");

        if ("stdin".equals(workingMode)) {
            reader = new TraceReader();
        } else {
            reader = new TraceReader(readFromFileName);
        }

        logger.debug("<< initTraceReader()");
    }

    public static void main(String[] args) {
        System.out.println("TraceTool " + TraceUtils.APP_VERSION);
        System.out.println("--------------------");
        System.out.println("");
        System.out.println("Usage: TraceTool --mode:stdin|socket|file --compress --readfile:<filename> "
                        + "--max:<lines> --outputfile:<filename> --xmldata:<filename>");
        System.out.println();
        TraceTool tool = new TraceTool();
        try {
            for (int i = 0; i < args.length; i++) {

                if (args[i].startsWith("--max:")) {

                    Long maxLines = Long.parseLong(args[i].substring(6, args[i].length()));
                    tool.setMaxInstructionsInTraceFile(maxLines);
                    System.out.println("Max lines in output file: " + maxLines);

                } else if (args[i].startsWith("--outputfile:")) {

                    String outfile = args[i].substring(13, args[i].length());
                    tool.setOutputTraceFileName(outfile);
                    System.out.println("Output file name .......: " + outfile);

                } else if (args[i].startsWith("--xmldata:")) {

                    String xmlfile = args[i].substring(10, args[i].length());
                    tool.setObjXmlFileName(xmlfile);
                    System.out.println("XML object file name ...: " + xmlfile);

                } else if (args[i].startsWith("--mode:")) {

                    String mode = args[i].substring(7, args[i].length());
                    System.out.println("Working mode:...........: " + mode);
                    tool.setWorkingMode(mode);

                } else if (args[i].startsWith("--readfile:")) {

                    String readfile = args[i].substring(11, args[i].length());
                    tool.setReadFromFileName(readfile);
                    System.out.println("Read from ..............: " + readfile);

                } else if (args[i].startsWith("--compress")) {

                    tool.setCompressFiles(true);
                    tool.setRemoveNonCompressedFiles(true);
                    System.out.println("Compress files..........: " + tool.isCompressFiles());
                    System.out.println("Delete original files...: " + tool.isRemoveNonCompressedFiles());

                }

            }
            if (tool.getWorkingMode() == null) {
                System.out.println("Param --mode:stdin|socket|file missing. See usage.");
                System.exit(3);
            }
            if (!"stdin".equals(tool.getWorkingMode()) && !"file".equals(tool.workingMode)
                            && !"socket".equals(tool.getWorkingMode())) {
                System.out.println("Param --mode:stdin|socket|file is wrong. See usage. Unknow mode: "
                                + tool.getWorkingMode());
                System.exit(4);
            }
            if ("file".equals(tool.getWorkingMode())) {
                if (tool.getOutputTraceFileName() == null || tool.getOutputTraceFileName().length() == 0) {
                    System.out.println("Param --outputfile missing. See usage.");
                    System.exit(5);
                }
            }
            if (tool.getMaxInstructionsInTraceFile() == 0) {
                System.out.println("Param --max missing. Assuming 500.000 instructions.");
                tool.setMaxInstructionsInTraceFile(500000);
            }
            if (tool.getObjXmlFileName() == null || tool.getObjXmlFileName().length() == 0) {
                System.out.println("Param --xmldata missing. See usage.");
                System.exit(6);
            }
            tool.start();
            System.exit(9);
        } catch (NumberFormatException e) {
            System.out.println("Wrong --max param. See usage.\n");
        } catch (InvalidTraceLineException e) {
            System.out.println("Invalid trace line found. Aborting.");
            System.out.println(e.getMessage() + "\n");
            System.exit(7);
        } catch (InterruptedException e) {
            System.out.println("Thread problems. Aborting.");
            System.out.println(e.getMessage() + "\n");
            System.exit(8);
        }
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#getObjXmlFileName()
     */
    public String getObjXmlFileName() {
        return objXmlFileName;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setObjXmlFileName(java.lang.String)
     */
    public void setObjXmlFileName(String objXmlFileName) {
        this.objXmlFileName = objXmlFileName;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#getOutputTraceFileName()
     */
    public String getOutputTraceFileName() {
        return outputTraceFileName;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setOutputTraceFileName(java.lang.String)
     */
    public void setOutputTraceFileName(String outputTraceFileName) {
        this.outputTraceFileName = outputTraceFileName;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#getMaxInstructionsInTraceFile()
     */
    public long getMaxInstructionsInTraceFile() {
        return maxInstructionsInTraceFile;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setMaxInstructionsInTraceFile(long)
     */
    public void setMaxInstructionsInTraceFile(long maxInstructionsInTraceFile) {
        this.maxInstructionsInTraceFile = maxInstructionsInTraceFile;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#getReadFromFileName()
     */
    public String getReadFromFileName() {
        return readFromFileName;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setReadFromFileName(java.lang.String)
     */
    public void setReadFromFileName(String readFromFileName) {
        this.readFromFileName = readFromFileName;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#isCompressFiles()
     */
    public boolean isCompressFiles() {
        return compressFiles;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setCompressFiles(boolean)
     */
    public void setCompressFiles(boolean compressFiles) {
        this.compressFiles = compressFiles;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#isRemoveNonCompressedFiles()
     */
    public boolean isRemoveNonCompressedFiles() {
        return removeNonCompressedFiles;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setRemoveNonCompressedFiles(boolean)
     */
    public void setRemoveNonCompressedFiles(boolean removeNonCompressedFiles) {
        this.removeNonCompressedFiles = removeNonCompressedFiles;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#getWorkingMode()
     */
    public String getWorkingMode() {
        return workingMode;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setWorkingMode(java.lang.String)
     */
    public void setWorkingMode(String workingMode) {
        this.workingMode = workingMode;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#getInstructionCounter()
     */
    public long getInstructionCounter() {
        return instructionCounter;
    }

    /* (non-Javadoc)
     * @see br.ufpr.dinf.arch.jbluepill.TraceToolModel#setInstructionCounter(long)
     */
    public void setInstructionCounter(long instructionCounter) {
        this.instructionCounter = instructionCounter;
    }

}
