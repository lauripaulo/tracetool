package br.ufpr.dinf.arch.jbluepill.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

import org.apache.logging.log4j.LogManager;

public class CompressHelpThread extends Thread {
    
    private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("CompressHelpThread");

	private String fileName;
	private String gzipFileName;
	private boolean removeOriginalFile = false;
	private boolean isRunning = false;

	public CompressHelpThread(String fileName, String gzipFileName, boolean removeOriginalFile) {
		super();
		this.fileName = fileName;
		this.gzipFileName = gzipFileName;
		this.removeOriginalFile = removeOriginalFile;
	}

	@Override
	public void run() {
	    logger.debug(">> run()");
		super.run();
		isRunning = true;
		System.out.println("Compressing file: " + fileName);
        try {
            FileInputStream fis = new FileInputStream(fileName);
            FileOutputStream fos = new FileOutputStream(gzipFileName);
            GZIPOutputStream gzipOS = new GZIPOutputStream(fos);
            byte[] buffer = new byte[1024];
            int len;
            while((len=fis.read(buffer)) != -1){
                gzipOS.write(buffer, 0, len);
            }
            //close resources
            gzipOS.close();
            fos.close();
            fis.close();
    		System.out.println("GZip file created: " + gzipFileName);
    		logger.debug("== GZip file created: " + gzipFileName);
            if (removeOriginalFile) {
            	File target = new File(fileName);
            	target.delete();
        		System.out.println("File removed: " + fileName);
        		logger.debug("== Trace file removed: " + fileName);;
            }
        } catch (IOException e) {
            System.out.println("Error compressing file.");
            System.out.println("Error: " + e.getMessage());
            logger.debug("== Error compressing file. Error: %s", e);
        }
        isRunning = false;
        logger.debug("<< run()");
	}

	public String getFileName() {
		return fileName;
	}

	public String getGzipFileName() {
		return gzipFileName;
	}

	public boolean isRemoveOriginalFile() {
		return removeOriginalFile;
	}

	public boolean isRunning() {
		return isRunning;
	}

}
