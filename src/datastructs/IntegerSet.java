/*

	IntegerSet.java
	28-01-2005
	
*/

package src.datastructs;

import java.util.Vector;

public class IntegerSet {

	private int size; // fixe Mengengroesse
	private int vectorSize;
	private Vector<Integer> setBits;

	private final int MODE_INTERSECT = 1, MODE_UNITE = 2,
						MODE_MINUS = 3;


	// Konstruktor
	// Die Kapazität von _size-vielen Elementen wird garantiert, das Set
	// kann aber de-facto immer etwas mehr an Elementen aufnehmen, da
	// die 32 Bit des letzten int's in der Regel nie voll genutzt werden
	public IntegerSet(int _size) {
		// ein Int speichert 32 Elemente
		setBits = new Vector<Integer>();
		
		int vecSize = (_size/32)+1; // einer mehr kann nicht schaden
		
		vectorSize = vecSize;
		size = _size;
		
		for (int i = 0 ; i < vecSize ; i++ ) {
			setBits.add(new Integer(0));
		}
		
	}
	


	public void insert(int i) {
		// offset berechnen um das richtige Bit zu finden
		int vecIndex;
		int mask, setBit;
		int[] m = getIndexAndOffset(i);
		
		mask = 1 << m[1];
		vecIndex = m[0];				
			
		// selektiere den entsprechenden int und setze das richtige bit
		setBit = (int)setBits.elementAt(vecIndex);
		setBit = setBit | mask;
		setBits.setElementAt((Integer)setBit,vecIndex);
	}
	

	// test auf enthalten sein
	public boolean contains(int i) {
		int mask, vecIndex, setBit;
		
		int[] m = getIndexAndOffset(i);
		
		mask = 1 << m[1];
		vecIndex = m[0];
		setBit = (int)setBits.elementAt(vecIndex);
		return ((setBit&mask)!=0);
	}


	private int[] getIndexAndOffset(int k) {
		// offset und index berechnen
		int vecIndex = k/32;
		int offset = k%32;
		
		if (k<0 || vecIndex >= vectorSize)
			throw new IllegalArgumentException(k+"");
		
		int[] result = { vecIndex, offset };
		
		return result;
	}


	// vereinige unsere Menge mit b
	// Mengenvereinigung läuft geringfügig anders als intersect, da
	// die Menge u.U. vergrößert werden muss
	public void unite(IntegerSet b) {
		doSetOp(b, MODE_UNITE);
	}

	// schneide unsere Menge mit b
	public void intersect(IntegerSet b) {
		doSetOp(b, MODE_INTERSECT);
	}
	
	// Mengendifferenz this = this ohne b
	public void minus(IntegerSet b) {
		doSetOp(b, MODE_MINUS);
	}

	// schneide unsere Menge mit b
	private void doSetOp(IntegerSet b, int mode) {
		int maxSize, currentB,currentA, res;
		Vector<Integer> bVector;
		
		// informationen von b holen
		bVector = b.getVector();
		// bestimme Iterationsobergrenze
		maxSize = Math.min(bVector.size(), vectorSize);
		
		for (int i = 0 ; i < maxSize ; i++) {
			// verunde beide ints und schreibe das
			// ergebnis in die Menge zurück
			currentA = (int)setBits.elementAt(i);
			currentB = (int)bVector.elementAt(i);
			switch(mode) {
				case MODE_INTERSECT:
					res = currentA & currentB;
					setBits.setElementAt((Integer)(res),i);
					break;
				case MODE_UNITE:
					res = currentA | currentB;
					setBits.setElementAt((Integer)(res),i);
					break;
				case MODE_MINUS:
					res = currentA & (~currentB);
					setBits.setElementAt((Integer)(res),i);
					break;
			}
		}
		
		// hatte unsere Menge mehr bit-ints als b, setze diese 0
		// für den Durchschnittfall
		// bei Vereinigung werd einfach die überzähligen Elemente von b
		// eingefügt.
		if (mode==MODE_INTERSECT) {
			for (int i = bVector.size() ; i < vectorSize ; i++) {
				setBits.setElementAt(0,i);
			}
		}
		else if (mode==MODE_UNITE) {
			if (bVector.size()>vectorSize) {
				for (int i = vectorSize ; i < bVector.size() ; i++) {
					setBits.add(bVector.elementAt(i));
				}
			}
		}

	}



	// Potenzmenge, array mit allen Teilmengen
	public IntegerSet[] getPowerset() {
		return null;
	}

	public String toString() {
		int mask;
		System.out.print("[");
		for (int i = 0 ; i < setBits.size() ; i++) {
			for (int k = 0 ; k < 32 ; k++) {
				mask = 1 << k;
				if ((mask & (int)setBits.elementAt(i))!=0) {
					System.out.print(" "+(k+(i*32)));
				}
			}
		}
		System.out.println(" ]");
		return "";
	}


	// liefert eine Kopie des int-Vectors
	private Vector<Integer> getVector() {
		return (setBits);
	}

	
	public static void main(String[] args) {
		IntegerSet[] mengen;
		IntegerSet M,N;
		int max, msize;
		int a,b;
		
		// STRESSTESTING!!! :-)
		
		/* M = new IntegerSet(20);
		N = new IntegerSet(40);
		
		M.insert(10);
		M.insert(5);
		M.insert(15);
		M.insert(2);
		
		N.insert(2);
		N.insert(1);
		N.insert(19);
		N.insert(15);
		
		System.out.println(M); System.out.println(N);
		M.minus(N);
		System.out.println(M);
		 */
		mengen = new IntegerSet[100];
		
		for (int i = 0 ; i < 100 ; i++) {
			msize = ((int)(Math.random()*200));
			mengen[i] = new IntegerSet(msize);
			for (int k = 0 ; k < 30 ; k++) {
				mengen[i].insert(((int)(Math.random()*msize)));
			}
		}
		
		for (int k = 0 ; k < 200 ; k++ ) {
			a = (int)(Math.random()*100);
			b = (int)(Math.random()*100);
			mengen[a].minus(mengen[b]);		
		} 
		
	}

}










