package br.ufpr.dinf.arch.jbluepill.simulation;

public class SegmentBuffer extends Cache {

    private static final long serialVersionUID = 2706241970290909594L;


    public SegmentBuffer() {
    }

    public SegmentBuffer(int size, int associativity) {
		super(size, associativity);
	}


	/**
	 * return a line (index) from a given address using
	 * a hash function (generally 'Addres mod Size'), the
	 * base implementation discards the first 12 bytes
	 * and uses the next 4 bits as the hash.
	 * 
	 * This method must be override this method to change
	 * the hash function to suit you needs.
	 * 
	 * @param address the address to hash and get the index.
	 * 
	 * @return the index to be used;
	 */
	@Override
    public int getIndexFromAddress(long address) {
        int index = 0;
        if (getSize() > 1) {
            // discard the initial 48 bits
            long segId = address >>> 48;
            index = ((int) (segId & 0xfL)) % getSize();
        } else {
            // fully associative cache.
            index = 0;
        }
        return index;
    }

	/**
	 * returns the address shifted to the right at the first tag
	 * bit.
	 * 
	 * This method must be override this method to change
	 * the tag inital bit to suit you needs.
	 * 
	 * @param address to get the tag from.
	 * 
	 * @return address shifted to the right at the first tag bit.
	 */
	@Override
	public long getTagFromAddress (long address) {
		return (address >>> 48);
	}


    @Override
    public String toString() {
        String text = new String();
        int entries = getSize() * getAssociativity();
        if (getSize() > 1) {
            text = "SB with " + entries + " entries, configured with " + getSize() + " lines and " + getAssociativity() + " ways set-associative (LRU).";
        } else {
            text = "SB with " + entries + " entries, fully associative (LRU).";
        }
        return text;
    }

}
