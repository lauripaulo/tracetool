
package br.ufpr.dinf.arch.jbluepill.simulation;

import java.io.Serializable;

/**
 * Base cache class to be used by TLB and Segment Buffer that provides base methods t
 * find and create cache entries. It also provides information about the number of
 * entries added, removed and number of entries already in the cache when trying to
 * create a new entry
 *  
 * @author Lauri Laux
 *
 */
public abstract class Cache implements Serializable {

    private static final long serialVersionUID = -3028950018769251310L;

    private int size;
    private int associativity;
    private long hits;
    private long calls;
    private long entriesAdded;
    private long entriesRemoved;
    private long entriesAlreadyPresent;
    private CacheSet[] line;

    public Cache() {
    }

    /**
     * creates a cache with lines equals the size params and each line will have
     * a number of sets equals the associativity.
     * 
     * @param size
     *            number of cache array lines
     * @param associativity
     *            number of sets in each array line.
     */
    public Cache(int size, int associativity) {
        this.size = size;
        this.associativity = associativity;
        this.line = new CacheSet[size];
        for (int i = 0; i < line.length; i++) {
            line[i] = new CacheSet(associativity);
        }
    }

    /**
     * return a line (index) from a given address using a hash function
     * (generally 'Addres mod Size'), the base implementation discards the first
     * 12 bytes and uses the next 4 bits as the hash.
     * 
     * This method must be override this method to change the hash function to
     * suit you needs.
     * 
     * @param address
     *            the address to hash and get the index.
     * 
     * @return the index to be used;
     */
    public abstract int getIndexFromAddress(long address);

    /**
     * returns the address shifted to the right at the first tag bit.
     * 
     * This method must be override this method to change the tag inital bit to
     * suit you needs.
     * 
     * @param address
     *            to get the tag from.
     * 
     * @return address shifted to the right at the first tag bit.
     */
    public abstract 
    long getTagFromAddress(long address);

    /**
     * Add a new segment to the segment cache;
     * 
     * @param address
     *            the address to add
     * @param physicalAddress
     *            the physical base address to translate.
     * @param rights
     *            (ASID) information.
     * 
     */
    public void addEntry(long address, long physicalAddress, String rights, String entryName) {
//        logger.debug(">> addEntry (%s, %s, %s, %s)", Long.toHexString(address), Long.toHexString(physicalAddress),
//                        rights, entryName);
        
        CacheEntry entry = null;
        int lruIndex = 0;

        long tag = getTagFromAddress(address);
        int index = getIndexFromAddress(address);
        CacheSet set = line[index];
     
        for (int i = 0; i < set.getSet().length; i++) {
            // install address in the first invalid position.
            if (set.getSet()[i] == null || !set.getSet()[i].isValid()) {
//                logger.debug("Found empty cache entry at index=%s set=%s", index, i);
                set.getSet()[i] = new CacheEntry();
                entry = set.getSet()[i];
                
                // LRU update. Make the entry index the top of the stack
                set.getLruStack().remove(new Integer(i));
                set.getLruStack().addFirst(new Integer(i));
                
                break;
            } else {
                if (tag == set.getSet()[i].getTag() && set.getSet()[i].isValid()) {
                    // Found the same entry. It is already in the cache
                    entriesAlreadyPresent ++;
//                    logger.debug("Entry already in the cache at line=%s, set=%s, entry=%s", index, i, set.getSet()[i]);
//                    logger.debug("<< addEntry ():");
                    return;
                }
            }
        }
        
        // if no free entries found use the LRU index.
        if (entry == null) {
        	
            // LRU update. Remove the botton of the stack
        	lruIndex = set.getLruStack().removeLast();
            
        	entriesRemoved ++;
            entry = set.getSet()[lruIndex];
//            logger.debug("LRU will be used at index=%s, set=%s, old entry=%s", index, lruIndex, entry);
            
            // Now the entry that we will add below is the newest one and need to be
            // the first index of the LRU stack.
            set.getLruStack().addFirst(lruIndex);
        }
        
        // add the new entry.
        entry.setAsid(rights);
        entry.setPhysicalAddressBase(physicalAddress);
        entry.setEntryName(entryName);
        entry.setTag(tag);
        entry.setValid(true);
        entriesAdded++;

        // logger.info("Cache entry added at line=%s, entry=%s", index, entry);

//        logger.debug("<< addEntry ():");
    }
  
    /**
     * look for a cached entry that corresponds to the address param.
     * 
     * @param address to find the segment related.
     * 
     * @return the {@link CacheEntry} with the segment information.
     */
    public CacheEntry findAddress (long address) {
//        logger.debug(">> findAddress (%s)", Long.toHexString(address));

        CacheEntry entry = null;
        long tag = getTagFromAddress(address);
        int index = getIndexFromAddress(address);
        CacheSet set = line[index];
        
        calls ++;

        for (int i = 0; i < set.getSet().length; i++) {
            if (set.getSet()[i] != null && set.getSet()[i].isValid() && set.getSet()[i].getTag() == tag) {
                entry = set.getSet()[i];
//                logger.debug("Entry found at index=%s, set=%s, old entry=%s", index, i, entry);
                hits ++;
                
                // LRU update. Make the entry index the top of the stack
                set.getLruStack().remove(new Integer(i));
                set.getLruStack().addFirst(new Integer(i));
                
                break;
            }
        }
        
//        if (entry == null) {
//            logger.debug("Nothing found for address=0x%s", Long.toHexString(address));
//        }
        
//        logger.debug("<< findAddress ():");
        return entry;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getAssociativity() {
        return associativity;
    }

    public void setAssociativity(int associativity) {
        this.associativity = associativity;
    }

    public CacheSet[] getLine() {
        return line;
    }

    public void setLine(CacheSet[] line) {
        this.line = line;
    }

    public long getHits() {
        return hits;
    }

    public void setHits(long hits) {
        this.hits = hits;
    }

    public long getCalls() {
        return calls;
    }

    public void setCalls(long calls) {
        this.calls = calls;
    }

    public long getEntriesAdded() {
        return entriesAdded;
    }

    public void setEtriesAdded(long segmentsAdded) {
        this.entriesAdded = segmentsAdded;
    }

    public long getEntriesRemoved() {
        return entriesRemoved;
    }

    public void setEntriesRemoved(long segmentsDroped) {
        this.entriesRemoved = segmentsDroped;
    }

    
    public long getEntriesAlreadyPresent() {
        return entriesAlreadyPresent;
    }

    public void setEntriesAlreadyPresent(long segmentsAlreadyPresent) {
        this.entriesAlreadyPresent = segmentsAlreadyPresent;
    }
    
}
