/*

	IntegerSet.java
	01-Feb-2005
	
	Simple Mengenimplementierung, ein BigInteger repräsentiert
	durch den Status seines n-ten Bits ob n in der Menge
	ist oder nicht.
	
*/


package src.datastructs;

import java.math.BigInteger;
import java.util.Vector;

public class IntegerSet {

	private BigInteger content;
	private int cardinality;
	
	// de-facto beliebig viele Elemente speicherbar	
	public IntegerSet() {
		content = new BigInteger("0");
		cardinality = 0;
	}


	/* einfügen, entfernen und testen in O(1), nice ;) */
	
	public boolean contains(int i) {
		return content.testBit(i);
	}
	
	public void insert(int i) {
		content = content.setBit(i);
	}
	
	// entfernen nicht-vorhandener elemente ist kein fehler
	public void remove(int i) {
		content = content.clearBit(i);
	}
	
	public boolean isEmpty() {
		return (content.equals(BigInteger.ZERO));
	}

	// Mächtigkeit
	public int cardinality() {
		int maxIt = content.bitLength(), card = 0;
		for (int i = 0 ; i < maxIt ; i++) {
			if (content.testBit(i)) card++;
		}
		return card;
	}
	
	// vereinigung, durchschnitt, komplement
	public void unite(IntegerSet b) {
		content = content.or(b.content);
	}
	
	public void intersect(IntegerSet b) {
		content = content.and(b.content);
	}
	
	public void minus(IntegerSet b) {
		content = content.andNot(b.content);
	}
	
	
	/* breche die Menge auf ihre eigentlichen Elemente runter
		und packe diese in einen Vector */
	public Vector<Integer> pureElements() {
		int maxIt = content.bitLength();
		Vector<Integer> result = new Vector<Integer>();
		
		for (int i = 0 ; i < maxIt ; i++) {
			if (content.testBit(i))
				result.add((Integer)i);
		}	
	
		return result;
	}
	
	
	
	// Potenzmenge
	// die Mächtigkeit der Menge wird als int angenommen, wäre diese auch Bigint
	// ist das ganze absolut nicht mehr sinnig (|m| = n => |pot(m)| = 2^n)
	public IntegerSet[] getPowerset() {
		BigInteger subSetCount, idx, help;
		int i, card = this.cardinality();
		
		// wieviele Teilmengen haben wir?
		subSetCount = new BigInteger("1");
		subSetCount = subSetCount.shiftLeft(card);
		
		System.out.println(subSetCount+" teilmengen");
		
		idx = new BigInteger("0");
		
		// simulierte for schleife mit BigInts
		
		do {
			idx = idx.add(BigInteger.ONE);
			
			for (i = 0 ; i < card ; i++ ) {
				help = BigInteger.ONE.shiftLeft(i);
				help = help.and(idx);
				if (!help.equals(BigInteger.ZERO)) {
		
				}				
			}

		} while (!(idx.equals(subSetCount)));
		
		return null;
	}
	
	public boolean equals(Object b) {
		IntegerSet other;
		if (b instanceof IntegerSet) {
			other = (IntegerSet)b;
			return (content.equals(other.content));
		}
		return false;
	}

	
	// hauptsächlich zu testzwecken
	public String toString() {
		StringBuffer stringRep = new StringBuffer();
		stringRep.append("[ ");
		int maxIt = content.bitLength();
		
		for (int i = 0 ; i < maxIt ; i++) {
			if (content.testBit(i)) {
				stringRep.append(i+" ");
			}
		}
		stringRep.append("]");
		return (stringRep.toString());
	}	

}
