package br.ufpr.dinf.arch.jbluepill.simulation;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Represents a cache set with a number of {@link CacheEntry} configurable 
 * representing the associativity of the cache.
 * 
 * @author Lauri Laux
 *
 */
public class CacheSet implements Serializable {

    private static final long serialVersionUID = -7761747522605635982L;
    
    private int associativity;
	private CacheEntry set[];
	
	LinkedList<Integer> lruStack;

   public CacheSet() {
   }

	public CacheSet(int associativity) {
		this.associativity = associativity;
		this.set = new CacheEntry[associativity];
		lruStack = new LinkedList<Integer>();
	}

	/**
	 * Returns the {@link CacheEntry} at the given address
	 * 
	 * @param index the index to get;
	 * @return {@link CacheEntry} at the given address
	 */
	public CacheEntry getSetAtIndex(int index) {
		CacheEntry entry = null;
		if (index >= set.length) {

		} else {
			entry = set[index];
		}
		return entry;
	}

	public int getAssociativity() {
		return associativity;
	}

	public void setAssociativity(int associativity) {
		this.associativity = associativity;
	}

	public CacheEntry[] getSet() {
		return set;
	}

	public void setSet(CacheEntry[] set) {
		this.set = set;
	}

	public LinkedList<Integer> getLruStack() {
		return lruStack;
	}

	public void setLruStack(LinkedList<Integer> lruStack) {
		this.lruStack = lruStack;
	}

	@Override
	public String toString() {
		return "CacheSet [associativity=" + associativity 
				+ ", lruStack=" + lruStack.toString()
				+ ", set=" + Arrays.toString(set) + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + associativity;
		result = prime * result + Arrays.hashCode(set);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CacheSet other = (CacheSet) obj;
		if (associativity != other.associativity)
			return false;
		if (!Arrays.equals(set, other.set))
			return false;
		return true;
	}
}
