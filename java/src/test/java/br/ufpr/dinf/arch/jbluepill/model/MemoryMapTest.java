package br.ufpr.dinf.arch.jbluepill.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.util.Arrays;

import org.junit.Test;

import br.ufpr.dinf.arch.jbluepill.simulation.Cache;
import br.ufpr.dinf.arch.jbluepill.simulation.CacheEntry;
import br.ufpr.dinf.arch.jbluepill.util.TraceUtils;

public class MemoryMapTest {

    
    @Test
    public void testCreateArtificialSegment() throws FileNotFoundException {
        MemoryMap map = new MemoryMap();
        map.getEntries().add(map.createMemMapEntryFromLine("0000000000108000    156K r-x-- firefox")); 
        map.getEntries().add(map.createMemMapEntryFromLine("000000000032e000      8K rw--- firefox"));  
        map.getEntries().add(map.createMemMapEntryFromLine("0000000004000000    152K r-x-- ld-2.23.so"));  
        map.getEntries().add(map.createMemMapEntryFromLine("0000000004026000      8K rw---   [ anon ]")); 
        map.getEntries().add(map.createMemMapEntryFromLine("0000000004028000    160K r---- ld.so.cache"));  
        map.getEntries().add(map.createMemMapEntryFromLine("0000000004225000      2K rw--- ld-2.23.so")); 
        map.getEntries().add(map.createMemMapEntryFromLine("0000000004227000      4K rw---   [ anon ]")); 
        map.getEntries().add(map.createMemMapEntryFromLine("0000000004228000      4K rwx--   [ anon ]")); 
        
        assertEquals(8, map.getEntries().size());
        
        map.updateTopAddrWithSize();
        
        // create 1st artificial
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000000330055", 16));
        assertNotNull(map.getOwnerOfAddress(Long.parseUnsignedLong("0000000000330055", 16)));
        assertNotNull(map.getOwnerOfAddress(Long.parseUnsignedLong("0000000000330095", 16)));
        
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000000108000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("000000000032e000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004000000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004026000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004028000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004225000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004227000", 16));
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004228000", 16));
        
        assertEquals(9, map.getEntries().size());
        
        
        // create 2nd artificial
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000004226500", 16));
        assertNotNull(map.getOwnerOfAddress(Long.parseUnsignedLong("0000000004226500", 16)));
        
        assertEquals(10, map.getEntries().size());
        
        // Create a segment at the beginning.
        
        map.createSimulatedSegmentForAddress(Long.parseUnsignedLong("0000000000008000", 16));

        //
        System.out.println(map.toStringCSV());
        
    }
    
    
}