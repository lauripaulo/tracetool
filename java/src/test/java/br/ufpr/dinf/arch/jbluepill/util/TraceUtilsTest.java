package br.ufpr.dinf.arch.jbluepill.util;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import static org.junit.Assert.*;

public class TraceUtilsTest {

	@Rule
	public ExpectedException badTraceLineException = ExpectedException.none();
	
	@Test
	public void testCreateSegmentAddress() {
	    long address = Long.parseUnsignedLong("000000000060AFFF", 16);
	    int segId = Integer.parseUnsignedInt("0001", 16);
	    long result = 0L;
	    result = TraceUtils.createSegmentedAddress(address, segId);
	    String resultInHex = String.format("%016X", result);
	    assertEquals("000100000060AFFF", resultInHex);
	}

    @Test
    public void testCreateSegmentAddressPrefixPreset() {
        long address = Long.parseUnsignedLong("00FF00000060AFFF", 16);
        int segId = Integer.parseUnsignedInt("0001", 16);
        long result = 0L;
        result = TraceUtils.createSegmentedAddress(address, segId);
        String resultInHex = String.format("%016X", result);
        assertEquals("00FF00000060AFFF", resultInHex);
    }

    @Test
    public void testGetSegmentPrefix () {
        long address = Long.parseUnsignedLong("00FF00000060AFFF", 16);
        String result = TraceUtils.getSegmentPrefix(address);
        assertEquals("00FF", result);
        
    }
    
//	@Test
//	public void testParseTraceLine() {
//		String traceLine = "F,04000cc0,3,/lib/x86_64-linux-gnu/ld-2.23.so";
//		try {
//			Instruction asm = TraceUtils.parseInstructionLine(traceLine, 5);
//			assertEquals("Unexpected instruction type", InstructionType.FETCH, asm.getType());
//			assertEquals("Wrong address parsed", "04000cc0", asm.getAddr());
//			assertEquals("Wrong instruction type", 3, asm.getSize(), 0);
//			assertEquals("Wrong object name (binary referecen)", asm.getObjname(), "/lib/x86_64-linux-gnu/ld-2.23.so");
//			assertEquals("Wrong address as long value", asm.getAddrAsLongValue(), 67112128L, 0);
//		} catch (InvalidTraceLineException e) {
//			fail();
//		}
//	}
//	
//	@Test
//	public void testParseTraceBadLine() throws InvalidTraceLineException {
//		badTraceLineException.expect(InvalidTraceLineException.class);
//		badTraceLineException.expectMessage("Trace line do not have 4 fields. "
//				+ "Line=5 content='F04000cc0,3,/lib/x86_64-linux-gnu/ld-2.23.so'");
//		String traceLine = "F04000cc0,3,/lib/x86_64-linux-gnu/ld-2.23.so";
//		TraceUtils.parseInstructionLine(traceLine, 5);
//	}
//
//	@Test
//	public void testParseTraceBadFetch() throws InvalidTraceLineException {
//		badTraceLineException.expect(InvalidTraceLineException.class);
//		badTraceLineException.expectMessage("Unknow instruction type. "
//				+ "Line=5 content='A,04000cc0,3,/lib/x86_64-linux-gnu/ld-2.23.so'");
//		String traceLine = "A,04000cc0,3,/lib/x86_64-linux-gnu/ld-2.23.so";
//		TraceUtils.parseInstructionLine(traceLine, 5);
//	}
//
//	@Test
//	public void testParseTraceBadAddressStr() throws InvalidTraceLineException {
//		badTraceLineException.expect(InvalidTraceLineException.class);
//		badTraceLineException.expectMessage("Address is null or invalid. "
//				+ "Line=5 content='F,,3,/lib/x86_64-linux-gnu/ld-2.23.so'");
//		String traceLine = "F,,3,/lib/x86_64-linux-gnu/ld-2.23.so";
//		TraceUtils.parseInstructionLine(traceLine, 5);
//	}
//
//	@Test
//	public void testParseTraceBadAddressHex() throws InvalidTraceLineException {
//		badTraceLineException.expect(InvalidTraceLineException.class);
//		badTraceLineException.expectMessage("Address is not a valid hex value. "
//				+ "Line=5 content='F,04000xc0,3,/lib/x86_64-linux-gnu/ld-2.23.so'");
//		String traceLine = "F,04000xc0,3,/lib/x86_64-linux-gnu/ld-2.23.so";
//		TraceUtils.parseInstructionLine(traceLine, 5);
//	}
//
//	@Test
//	public void testParseTraceBadSizeStr() throws InvalidTraceLineException {
//		badTraceLineException.expect(InvalidTraceLineException.class);
//		badTraceLineException.expectMessage("Size is null or empty. "
//				+ "Line=5 content='F,04000cc0,,/lib/x86_64-linux-gnu/ld-2.23.so'");
//		String traceLine = "F,04000cc0,,/lib/x86_64-linux-gnu/ld-2.23.so";
//		TraceUtils.parseInstructionLine(traceLine, 5);
//	}
//
//	@Test
//	public void testParseTraceBadSizeValue() throws InvalidTraceLineException {
//		badTraceLineException.expect(InvalidTraceLineException.class);
//		badTraceLineException.expectMessage("Size is not an integer value. "
//				+ "Line=5 content='F,04000cc0,a,/lib/x86_64-linux-gnu/ld-2.23.so'");
//		String traceLine = "F,04000cc0,a,/lib/x86_64-linux-gnu/ld-2.23.so";
//		TraceUtils.parseInstructionLine(traceLine, 5);
//	}
//	
//	@Test
//	public void testGetVirtualAddress() {
//		long address = Long.parseLong("1111FFFFFFFFFFFF", 16);
//		VirtualAddress vAddress = TraceUtils.convertToVirtualAddress(address, 16);
//		assertEquals(Long.parseLong("FFFFFFFFFFFF", 16), vAddress.getBase());
//		assertEquals(Long.parseLong("1111", 16), vAddress.getDisplacement());
//		assertEquals(address, vAddress.originalAddress);
//
//		address = Long.parseLong("1111FFFFFFFFFFFF", 16);
//		vAddress = TraceUtils.convertToVirtualAddress(address, 8);
//		assertEquals(Long.parseLong("11FFFFFFFFFFFF", 16), vAddress.getBase());
//		assertEquals(Long.parseLong("11", 16), vAddress.getDisplacement());
//		assertEquals(address, vAddress.originalAddress);
//
//		address = Long.parseLong("111111111111FFFF", 16);
//		vAddress = TraceUtils.convertToVirtualAddress(address, 48);
//		assertEquals(Long.parseLong("FFFF", 16), vAddress.getBase());
//		assertEquals(Long.parseLong("111111111111", 16), vAddress.getDisplacement());
//		assertEquals(address, vAddress.originalAddress);
//	}
//	
//	@Test
//	public void testGetVirtualAddressNegativeDisplacement() throws InvalidParameterException {
//		badTraceLineException.expect(InvalidParameterException.class);
//		badTraceLineException.expectMessage("Displacement size must be greater than 0 and lesser than 64.");
//		long address = Long.parseLong("1111FFFFFFFFFFFF", 16);
//		TraceUtils.convertToVirtualAddress(address, -13423424234234L);
//		
//	}
//
//
//	@Test
//	public void testGetVirtualAddressZeroDisplacement() throws InvalidParameterException {
//		badTraceLineException.expect(InvalidParameterException.class);
//		badTraceLineException.expectMessage("Displacement size must be greater than 0 and lesser than 64.");
//		long address = Long.parseLong("1111FFFFFFFFFFFF", 16);
//		TraceUtils.convertToVirtualAddress(address, 0);
//		
//	}
	
}
