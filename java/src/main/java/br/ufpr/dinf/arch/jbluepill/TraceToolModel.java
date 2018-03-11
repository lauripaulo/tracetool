
package br.ufpr.dinf.arch.jbluepill;

import java.io.IOException;

import br.ufpr.dinf.arch.jbluepill.exception.InvalidTraceLineException;

public interface TraceToolModel {

    void start() throws InvalidTraceLineException, InterruptedException;

    void processLine(String line) throws IOException;

    String getObjXmlFileName();

    void setObjXmlFileName(String objXmlFileName);

    String getOutputTraceFileName();

    void setOutputTraceFileName(String outputTraceFileName);

    long getMaxInstructionsInTraceFile();

    void setMaxInstructionsInTraceFile(long maxInstructionsInTraceFile);

    String getReadFromFileName();

    void setReadFromFileName(String readFromFileName);

    boolean isCompressFiles();

    void setCompressFiles(boolean compressFiles);

    boolean isRemoveNonCompressedFiles();

    void setRemoveNonCompressedFiles(boolean removeNonCompressedFiles);

    String getWorkingMode();

    void setWorkingMode(String workingMode);

    long getInstructionCounter();

    void setInstructionCounter(long instructionCounter);

}