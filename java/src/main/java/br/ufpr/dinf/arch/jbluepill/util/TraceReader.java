package br.ufpr.dinf.arch.jbluepill.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.model.Instruction;

/**
 * Reads a trace file line by line as a text string or directly as an
 * {@link Instruction} validated class.
 * 
 * @author Lauri Laux
 */
public class TraceReader {

	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("TraceReader");

	private File file;
	private Scanner scanner;
	private String line;
	private long lineNumber;
	private BufferedReader bufferedReader;

	public File getFile() {
		logger.trace(">> getFile()");
		logger.trace("<< getFile(): s", file);
		return file;
	}

	public long getLineNumber() {
		logger.trace(">> getgetLineNumber()");
		logger.trace("<< getLineNumber() %s", lineNumber);
		return lineNumber;
	}

	/**
	 * Create a new Scanner object which will read the data
	 * from the file passed in. To check if there are more
	 * line to read from it we check by calling the
	 * scanner.hasNextLine() method. We then read line one
	 * by one till all lines is read.
	 *
	 * @param traceFile
	 * @throws FileNotFoundException
	 */
	public TraceReader(String traceFile) throws FileNotFoundException {
		file = new File(traceFile);
		int bufferSize = 1024 * 512;
		bufferedReader = new BufferedReader(new FileReader(traceFile), bufferSize);
		scanner = new Scanner(bufferedReader);
	}
	
	/**
	 * close the file reader and scanner
	 * 
	 * @throws IOException 
	 */
	public void close() throws IOException {
	    scanner.close();
	    bufferedReader.close();
	}

	/**
	 * 
	 * Create a new Scanner object which will read the data
	 * from stdin (standard input).
	 *  
	 * To check if there are more line to read from it we 
	 * check by calling the scanner.hasNextLine() method. 
	 * We then read line one by one till all lines is read.
	 */
	public TraceReader() {
		scanner = new Scanner(System.in);
	}

	public boolean hasNextLine() {
		logger.trace(">> hasNextLine()");
		boolean result = scanner.hasNextLine();
		logger.trace("<< hasNextLine(): %s", result);
		return result;
	}

	public String readNextLine() {
		logger.trace(">> readNextLine()");
		line = scanner.nextLine();
		lineNumber++;
		logger.trace("<< readNextLine(): %s", line);
		return line;
	}

	public String getCurrentLine() {
		logger.trace(">> getCurrentLine()");
		logger.trace("<< getCurrentLine(): %s", line);
		return line;
	}

	/**
	 * will skip the 6 initial lines with valgring info and will set the current
	 * and point the {@link #readNextLine()} to the first instruction.
	 */
	public void skipHeader() {
		logger.trace(">> skipHeader()");
		while (hasNextLine()) {
			String line = readNextLine();
			if (!line.startsWith("==")) {
				break;
			}
			logger.info("Skiping header: %s ", line);
		}
		logger.trace("<< skipHeader()");
	}

}
