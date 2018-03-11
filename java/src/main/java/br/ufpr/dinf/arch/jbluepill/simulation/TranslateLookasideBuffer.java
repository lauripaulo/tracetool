package br.ufpr.dinf.arch.jbluepill.simulation;

public class TranslateLookasideBuffer extends Cache {

    private static final long serialVersionUID = -8254240033928628505L;

    public TranslateLookasideBuffer() {
    }

    public TranslateLookasideBuffer(int size, int associativity) {
        super(size, associativity);
    }
                   
    // ‭0001 0000 0000 0000‬
    @Override
    public int getIndexFromAddress(long address) {
        int index = 0;
        if (getSize() > 1) {
            // discard the initial 13 bits (4k page size)
            long indexAndTag = address >>> 12;
            // keep the first 4 bits and discard the rest.
            index = ((int) (indexAndTag & 0xf)) % getSize();
        } else {
            // fully associative cache.
            index = 0;
        }
        return index;
    }

    @Override
    public long getTagFromAddress(long address) {
        return address >>> 16;
    }

    @Override
    public String toString() {
        String text = new String();
        int entries = getSize() * getAssociativity();
        if (getSize() > 1) {
            text = "TLB with " + entries + " entries, configured with " + getSize() + " lines and " + getAssociativity() + " ways set-associative (LRU).";
        } else {
            text = "TLB with " + entries + " entries, fully associative (LRU).";
        }
        return text;
    }
}
