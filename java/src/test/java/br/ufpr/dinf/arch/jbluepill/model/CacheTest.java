package br.ufpr.dinf.arch.jbluepill.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.Arrays;

import org.junit.Test;

import br.ufpr.dinf.arch.jbluepill.simulation.Cache;
import br.ufpr.dinf.arch.jbluepill.simulation.CacheEntry;

public class CacheTest {

	
	@Test
	public void testFullyAssocAddEntryOk() {
	
		Cache fullyAssocCache = new Cache(1, 4) {
			@Override
			public int getIndexFromAddress(long address) {
				return 0;
			}
			@Override
			public long getTagFromAddress(long address) {
				return (address >>> 48);
			}
		};

		//   +----> tag
		// |--||----------|
		// 0000000d6840a000

		long[] addrs = {
				Long.parseLong("0000000d6840a000", 16),
				Long.parseLong("0001000d6840a000", 16),
				Long.parseLong("0002000d6840a000", 16),
				Long.parseLong("0003000d6840a000", 16),
				Long.parseLong("0004000d6840a000", 16),
				Long.parseLong("0005000d6840a000", 16),
				Long.parseLong("0006000d6840a000", 16),
				Long.parseLong("0007000d6840a000", 16)
			};
		
		fullyAssocCache.addEntry(addrs[0], 0, "r--", "test-01");
		fullyAssocCache.addEntry(addrs[1], 1, "rw-", "test-02");
		fullyAssocCache.addEntry(addrs[2], 2, "rwx", "test-03");
		fullyAssocCache.addEntry(addrs[3], 3, "-wx", "test-04");
		
		assertEquals(4, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(0, fullyAssocCache.getEntriesRemoved());
		assertEquals(0, fullyAssocCache.getHits());
		assertEquals("[3, 2, 1, 0]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		long addrBase = fullyAssocCache.getLine()[0].getSetAtIndex(0).getPhysicalAddressBase();
		assertEquals(0, addrBase);
		
		fullyAssocCache.addEntry(addrs[4], 4, "--x", "test-05");

		assertEquals(5, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(1, fullyAssocCache.getEntriesRemoved());
		assertEquals(0, fullyAssocCache.getHits());
		assertEquals("[0, 3, 2, 1]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		addrBase = fullyAssocCache.getLine()[0].getSetAtIndex(0).getPhysicalAddressBase();
		assertEquals(4, addrBase);

		fullyAssocCache.addEntry(addrs[5], 5, "---", "test-06");

		assertEquals(6, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(2, fullyAssocCache.getEntriesRemoved());
		assertEquals(0, fullyAssocCache.getHits());
		assertEquals("[1, 0, 3, 2]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		addrBase = fullyAssocCache.getLine()[0].getSetAtIndex(1).getPhysicalAddressBase();
		assertEquals(5, addrBase);

		fullyAssocCache.addEntry(addrs[6], 6, "---", "test-07");

		assertEquals(7, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(3, fullyAssocCache.getEntriesRemoved());
		assertEquals(0, fullyAssocCache.getHits());
		assertEquals("[2, 1, 0, 3]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		addrBase = fullyAssocCache.getLine()[0].getSetAtIndex(2).getPhysicalAddressBase();
		assertEquals(6, addrBase);

		fullyAssocCache.addEntry(addrs[7], 7, "---", "test-08");

		assertEquals(8, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(4, fullyAssocCache.getEntriesRemoved());
		assertEquals(0, fullyAssocCache.getHits());
		assertEquals("[3, 2, 1, 0]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		addrBase = fullyAssocCache.getLine()[0].getSetAtIndex(3).getPhysicalAddressBase();
		assertEquals(7, addrBase);
	}
	
	@Test
	public void testFullyAssocFindEntryOk() {
	
		Cache fullyAssocCache = new Cache(1, 4) {
			@Override
			public int getIndexFromAddress(long address) {
				return 0;
			}
			@Override
			public long getTagFromAddress(long address) {
				return (address >>> 48);
			}
		};

		//   +----> tag
		// |--||----------|
		// 0000000d6840a000
		
		long[] addrs = {
				Long.parseLong("0000000d6840a000", 16),
				Long.parseLong("0001000d6840a000", 16),
				Long.parseLong("0002000d6840a000", 16),
				Long.parseLong("0003000d6840a000", 16),
				Long.parseLong("0004000d6840a000", 16),
				Long.parseLong("0005000d6840a000", 16),
				Long.parseLong("0006000d6840a000", 16),
				Long.parseLong("0007000d6840a000", 16)
			};
		
		fullyAssocCache.addEntry(addrs[0], 0, "r--", "test-01");
		fullyAssocCache.addEntry(addrs[1], 1, "rw-", "test-02");
		fullyAssocCache.addEntry(addrs[2], 2, "rwx", "test-03");
		fullyAssocCache.addEntry(addrs[3], 3, "-wx", "test-04");

		CacheEntry entry = fullyAssocCache.findAddress(addrs[0]);
		assertEquals(0, entry.getPhysicalAddressBase());

		entry = fullyAssocCache.findAddress(addrs[1]);
		assertEquals(1, entry.getPhysicalAddressBase());

		entry = fullyAssocCache.findAddress(addrs[2]);
		assertEquals(2, entry.getPhysicalAddressBase());

		entry = fullyAssocCache.findAddress(addrs[3]);
		assertEquals(3, entry.getPhysicalAddressBase());

		assertEquals(4, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(0, fullyAssocCache.getEntriesRemoved());
		assertEquals(4, fullyAssocCache.getHits());
		assertEquals("[3, 2, 1, 0]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));

		fullyAssocCache.addEntry(addrs[4], 4, "-wx", "test-05");

		entry = fullyAssocCache.findAddress(addrs[4]);
		assertEquals(4, entry.getPhysicalAddressBase());
		assertEquals("[0, 3, 2, 1]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		
		entry = fullyAssocCache.findAddress(addrs[0]);
		assertNull("Entry must be null.", entry);
		
		assertEquals(5, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(1, fullyAssocCache.getEntriesRemoved());
		assertEquals(5, fullyAssocCache.getHits());
		assertEquals("[0, 3, 2, 1]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));

		entry = fullyAssocCache.findAddress(addrs[3]);
		assertEquals(5, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(1, fullyAssocCache.getEntriesRemoved());
		assertEquals(6, fullyAssocCache.getHits());
		assertEquals("[3, 0, 2, 1]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));
		
		entry = fullyAssocCache.findAddress(addrs[2]);
		assertEquals(5, fullyAssocCache.getEntriesAdded());
		assertEquals(0, fullyAssocCache.getEntriesAlreadyPresent());
		assertEquals(1, fullyAssocCache.getEntriesRemoved());
		assertEquals(7, fullyAssocCache.getHits());
		assertEquals("[2, 3, 0, 1]", 
				Arrays.toString(fullyAssocCache.getLine()[0].getLruStack().toArray()));

	}
	

	@Test
	public void testAssocCacheAddEntryOk() {
	
		Cache assocCache = new Cache(2, 2) {
			@Override
			public int getIndexFromAddress(long address) {
				int index = 0;
				// discard the initial 12 bits
				address = address >>> 48;
				index = (int) (address & 0xfL);
				if (getSize() > 1) {
				    index = index % getSize();
				} else {
				    // fully associative cache.
				    index = 0;
				}
				return index;
			}
			@Override
			public long getTagFromAddress(long address) {
				return (address >>> 48);
			}
		};

		//   +----> tag
		// |--||----------|
		// 0000000d6840a000

		long[] addrs = {
				Long.parseLong("0000000d6840a000", 16),
				Long.parseLong("0001000d6840a000", 16),
				Long.parseLong("0002000d6840a000", 16),
				Long.parseLong("0003000d6840a000", 16),
				Long.parseLong("0004000d6840a000", 16),
				Long.parseLong("0005000d6840a000", 16),
				Long.parseLong("0006000d6840a000", 16),
				Long.parseLong("0007000d6840a000", 16)
			};
		
		assocCache.addEntry(addrs[0], 0, "r--", "test-01");
		assocCache.addEntry(addrs[1], 1, "rw-", "test-02");
		assocCache.addEntry(addrs[2], 2, "rwx", "test-03");
		assocCache.addEntry(addrs[3], 3, "-wx", "test-04");
		
		assertEquals(4, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(0, assocCache.getEntriesRemoved());
		assertEquals(0, assocCache.getHits());
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));
		assertEquals(0, assocCache.getLine()[0].getSetAtIndex(0).getPhysicalAddressBase());
		assertEquals(2, assocCache.getLine()[0].getSetAtIndex(1).getPhysicalAddressBase());

		assertEquals(1, assocCache.getLine()[1].getSetAtIndex(0).getPhysicalAddressBase());
		assertEquals(3, assocCache.getLine()[1].getSetAtIndex(1).getPhysicalAddressBase());

		assocCache.addEntry(addrs[4], 4, "-wx", "test-05");
		assertEquals(5, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(1, assocCache.getEntriesRemoved());
		assertEquals(0, assocCache.getHits());
		assertEquals("[0, 1]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));

		assocCache.addEntry(addrs[5], 5, "-wx", "test-06");
		assertEquals(6, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(2, assocCache.getEntriesRemoved());
		assertEquals(0, assocCache.getHits());
		assertEquals("[0, 1]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));
		assertEquals("[0, 1]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));

	}

	
	@Test
	public void testAssocCacheFindEntryOk() {
	
		Cache assocCache = new Cache(2, 2) {
			@Override
			public int getIndexFromAddress(long address) {
				int index = 0;
				// discard the initial 12 bits
				address = address >>> 48;
				index = (int) (address & 0xfL);
				if (getSize() > 1) {
				    index = index % getSize();
				} else {
				    // fully associative cache.
				    index = 0;
				}
				return index;
			}
			@Override
			public long getTagFromAddress(long address) {
				return (address >>> 48);
			}
		};

		//   +----> tag
		// |--||----------|
		// 0000000d6840a000

		long[] addrs = {
				Long.parseLong("0000000d6840a000", 16),
				Long.parseLong("0001000d6840a000", 16),
				Long.parseLong("0002000d6840a000", 16),
				Long.parseLong("0003000d6840a000", 16),
				Long.parseLong("0004000d6840a000", 16),
				Long.parseLong("0005000d6840a000", 16),
				Long.parseLong("0006000d6840a000", 16),
				Long.parseLong("0007000d6840a000", 16)
			};
		
		assocCache.addEntry(addrs[0], 0, "r--", "test-01");
		assocCache.addEntry(addrs[1], 1, "rw-", "test-02");
		assocCache.addEntry(addrs[2], 2, "rwx", "test-03");
		assocCache.addEntry(addrs[3], 3, "-wx", "test-04");
		
		assertEquals(4, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(0, assocCache.getEntriesRemoved());
		assertEquals(0, assocCache.getHits());
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));
		assertEquals(0, assocCache.getLine()[0].getSetAtIndex(0).getPhysicalAddressBase());
		assertEquals(2, assocCache.getLine()[0].getSetAtIndex(1).getPhysicalAddressBase());

		assertEquals(1, assocCache.getLine()[1].getSetAtIndex(0).getPhysicalAddressBase());
		assertEquals(3, assocCache.getLine()[1].getSetAtIndex(1).getPhysicalAddressBase());

		assertEquals(0, assocCache.findAddress(addrs[0]).getPhysicalAddressBase());
		assertEquals("[0, 1]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));

		assertEquals(1, assocCache.findAddress(addrs[1]).getPhysicalAddressBase());
		assertEquals("[0, 1]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));
		
		assertEquals(2, assocCache.findAddress(addrs[2]).getPhysicalAddressBase());
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));

		assertEquals(3, assocCache.findAddress(addrs[3]).getPhysicalAddressBase());
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));


		assertEquals(4, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(0, assocCache.getEntriesRemoved());
		assertEquals(4, assocCache.getHits());
		
		assocCache.addEntry(addrs[4], 4, "-wx", "test-05");
		assertEquals(5, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(1, assocCache.getEntriesRemoved());
		assertEquals(4, assocCache.getHits());
		assertEquals("[0, 1]", 
				Arrays.toString(assocCache.getLine()[0].getLruStack().toArray()));
		assertEquals("[1, 0]", 
				Arrays.toString(assocCache.getLine()[1].getLruStack().toArray()));
		
		assertNull(assocCache.findAddress(addrs[0]));
		assertEquals(5, assocCache.getEntriesAdded());
		assertEquals(0, assocCache.getEntriesAlreadyPresent());
		assertEquals(1, assocCache.getEntriesRemoved());
		assertEquals(4, assocCache.getHits());

	}

}

