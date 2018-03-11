package br.ufpr.dinf.arch.jbluepill.simulation;

import java.security.InvalidParameterException;

import org.apache.logging.log4j.LogManager;

import br.ufpr.dinf.arch.jbluepill.exception.SegmentBufferMissException;
import br.ufpr.dinf.arch.jbluepill.exception.SegmentBufferOverflowException;
import br.ufpr.dinf.arch.jbluepill.exception.SegmentWiredOverflow;
import br.ufpr.dinf.arch.jbluepill.model.SegmentInfo;
import br.ufpr.dinf.arch.jbluepill.model.VirtualAddress;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

/**
 * Fully associative Segment Buffer
 * 
 * @author Lauri Laux
 * 
 * @deprecated used just for historical reasons and to support new buffers 
 * development.
 *
 */
public class FullyAssociativeSegmentBuffer {

	private static org.apache.logging.log4j.Logger logger = LogManager.getFormatterLogger("SegmentBuffer");

	private int capacity;
	private int wired;
	private int random;

	private String bufferName;

	private long bufferHits = 0L;
	private long addressRequests = 0L;
	private long bufferLineReplacements = 0L;

	private SegmentInfo[] buffer;

	public FullyAssociativeSegmentBuffer(String name, int capacity, int wired) {
		this.capacity = capacity;
		this.wired = wired;
		this.random = wired;
		this.bufferName = name;
		this.buffer = new SegmentInfo[capacity];
		// random goes from 'wired' to 'capacity'
		this.random = wired;
	}

	public void addWiredSegment(SegmentInfo segment, int index) throws SegmentWiredOverflow {
		logger.debug(">> addWiredSegment(%s, %s)", segment, index);
		if (index > (wired -1)) {
			throw new SegmentWiredOverflow("Index bigger than wired register.");
		}
		if (index < 0) {
			throw new InvalidParameterException("Index must be greater than zero.");
		}
		// Add the segment directly in the index defined by index
		if (buffer[index] == null) {
			buffer[index] = segment;
			logger.debug("New segment at line: %s", buffer[index], index);
		} else {
			long bufferVSN = buffer[index].getSegtVSN();
			if (bufferVSN != segment.getSegtVSN()) {
				logger.debug("Segment %s replaced by %s. Buffer line: %s", buffer[index], segment, index);
				buffer[index] = segment;
				bufferLineReplacements++;
			}
		}
		logger.debug("<< addWiredSegment()");
	}

	public void addSegment(SegmentInfo segment) {
		logger.debug(">> addSegment(%s)", segment);
		// find the line containing the segment based on
		// its VSN (Virtual Segment Buffer). If
		// the line is empty of the VSN is not the same
		// a new segment is installed.
		// Or the random line is choosen but the random pointer
		int line = nextFreeBufferLine();
		if (buffer[line] == null) {
			logger.debug("New segment %s created! Buffer line: %s", buffer[line], segment, line);
			buffer[line] = segment;
		} else {
			long bufferVSN = buffer[line].getSegtVSN();
			if (bufferVSN != segment.getSegtVSN()) {
				logger.debug("Segment %s replaced by %s. Buffer line: %s", buffer[line], segment, line);
				buffer[line] = segment;
				bufferLineReplacements++;
			}
		}
		logger.debug("<< addSegment()");
	}

	public long translateAddressRequests(VirtualAddress address)
			throws SegmentBufferOverflowException, SegmentBufferMissException {
		logger.debug(">> translateAddressRequests(%s)", address);
		addressRequests++;
		long physicalAddress = Long.MAX_VALUE;
		// find the buffer with the virtual address base
		// and compare it with the segment VSN.
		for (int line = 0; line < capacity; line++) {
			if (buffer[line] != null) {
				long bufferVSN = buffer[line].getSegtVSN();
				// the address base is equal to the address base
				// the segment in the buffer is what we are looking for.
				if (bufferVSN == address.getBase()) {
					logger.debug("Segment buffer hit!");
					bufferHits++;
					// to find the physical address we get the
					// segment physical start address and the
					// virtual address displacement and add them
					physicalAddress = buffer[line].getSegPSbase() + address.displacement;
					// the physical address cannot be greater than
					// the segment physical limit. Let's ensure that.
					if (physicalAddress > buffer[line].getSegPSlimit()) {
						throw new SegmentBufferOverflowException("Segment limit overflow!");
					}
					break;
				}
			}
		}
		if (physicalAddress == Long.MAX_VALUE) {
			throw new SegmentBufferMissException("Segment not found in buffer.");
		}
		logger.debug("<< translateAddressRequests(): %s", TraceUtils.getLongHexString(physicalAddress));
		return physicalAddress;
	}

	/**
	 * get a new buffer line to write segment into buffer overwrite it to change
	 * behavior.
	 * 
	 * @return
	 */
	public int nextFreeBufferLine() {
		logger.debug(">> nextFreeBufferLine()");
		int line = getRndLine();
		logger.debug("<< nextFreeBufferLine(): %s", line);
		return line;
	}

	private int getRndLine() {
		logger.debug(">> getRndLine()");
		random++;
		if (random == capacity) {
			random = wired;
		}
		logger.debug("<< getRndLine(): %s", random);
		return random;
	}

	public int getWired() {
		return wired;
	}

	public void setWired(int wired) {
		this.wired = wired;
	}

	public int getCapacity() {
		return capacity;
	}

	public String getBufferName() {
		return bufferName;
	}

	public long getBufferHits() {
		return bufferHits;
	}

	public long getAddressRequests() {
		return addressRequests;
	}

	public long getBufferLineReplacements() {
		return bufferLineReplacements;
	}

	public SegmentInfo[] getBuffer() {
		return buffer;
	}

	public int getRandom() {
		return random;
	}

}
