package br.ufpr.dinf.arch.jbluepill.core;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.TraceTool;

public class SocketConnectionServer extends Thread {
	
	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("SocketConnectionServer");

	private int port;
	private ServerSocket listener;
	private int clientNumber;
	private ArrayList<ConnectionHandler> threadList;
	private TraceTool traceTool;
	

	public SocketConnectionServer(int port, TraceTool traceTool) {
		super("SocketConnectionServer");
		this.port = port;
		this.traceTool = traceTool;
	}
	
	public void signalEndOfConnectionHandler() {
		logger.debug(">> signalEndOfConnectionHandler()");
		synchronized (traceTool) {
			try {
				clientNumber --;
				Thread.sleep(200);
				if (clientNumber <= 0) {
					killConnections();
				}
			} catch (InterruptedException e) {
				logger.error("Thread error!", e);
				System.exit(102);
			}
		}
		logger.debug("<< signalEndOfConnectionHandler()");
	}

	@Override
	public void run() {
		logger.debug(">> run()");
		System.out.println("Server socket thread started.");
        try {
    		listener = new ServerSocket(port);
    		threadList = new ArrayList<ConnectionHandler>();
        	createConnectionHandler();
            while (clientNumber >= 0) {
            	createConnectionHandler();
                // See if we have threads running.
                Thread.sleep(200);
            }
        	System.out.println("NO MORE CLIENTS CONNECTED!!!!");
			listener.close();
        } catch (SocketException e) {
			logger.debug(String.format("Socket exception (probably closed!). Message: %s", clientNumber, e));
        	System.out.println("Server socket closed!");
        } catch (IOException e) {
			logger.error("IO error!", e);
			System.exit(101);
        } catch (InterruptedException e) {
			logger.error("Thread error!", e);
			System.exit(101);
		}
		System.out.println("Server socket thread ended.");
		logger.debug("<< run()");
	}

	private void createConnectionHandler() throws IOException {
		logger.debug(">> createConnectionHandler()");
		ConnectionHandler thread = new ConnectionHandler(listener.accept(), clientNumber, this);
		threadList.add(thread);
		thread.start();
		clientNumber ++;
		logger.debug(">> createConnectionHandler()");
	}
	
	public void println(String line) throws IOException {
		// do not log the method. It needs to be real fast.
		synchronized (traceTool) {
			traceTool.processLine(line);
		}
	}
	
	public long getInstructionCounter() {
		long counter = 0L;
		synchronized (traceTool) {
			counter = traceTool.getInstructionCounter();
		}
		return counter;
	}

	public void killConnections() {
		logger.debug(">> killConnections()");
		try {
			listener.close();
		} catch (IOException e) {
			logger.error("IO error!", e);
			System.exit(102);
		}
		logger.debug("<< killConnections()");
	}
	

}