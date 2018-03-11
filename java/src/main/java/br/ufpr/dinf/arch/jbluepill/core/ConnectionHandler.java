package br.ufpr.dinf.arch.jbluepill.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.TraceTool;

public class ConnectionHandler extends Thread {

	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("ConnectionHandler");

	private int clientNumber;
	private Socket socket;
	private SocketConnectionServer worker;

	public ConnectionHandler(Socket socket, int clientNumber, SocketConnectionServer worker) {
		super("ConnectionHandler#" + clientNumber);
		this.clientNumber = clientNumber;
		this.socket = socket;
		this.worker = worker;
	}

	@Override
	public synchronized void run() {
		logger.debug(">> run() - clientNumber: " + clientNumber);
		System.out.println(String.format("Connection handler %s started.", clientNumber));
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()), 65536);
			while (socket.isConnected() && !socket.isClosed()) {
				String input = in.readLine();
				if (input == null || input.length() == 0) {
					break;
				}
				worker.println(input);
				// limit trace instruction count to the instruction limit.
				if (worker.getInstructionCounter() > TraceTool.INSTRUCTION_LIMIT) {
					final String instructionMessage = String.format(
							"Instruction limit reached! Processsed %s instructions.", worker.getInstructionCounter());
					System.out.println(instructionMessage);
					logger.debug(instructionMessage);
					break;
				}
			}
		} catch (IOException e) {
			logger.error(String.format("IO error in client %s!", clientNumber), e);
            System.out.println(String.format("IO error in client %s! Error: %s", clientNumber, e));
            System.exit(100);
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
    			logger.error(String.format("IO error in client %s!", clientNumber), e);
                System.out.println("Couldn't close a socket, what's going on?");
                System.out.println("Error:" + e);
            }
            System.out.println("Connection with client" + clientNumber + " closed");
        }	
		worker.signalEndOfConnectionHandler();
		logger.debug("<< run()");
	}

}
