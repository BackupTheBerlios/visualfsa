/*

	IntegerSet.java
	28-01-2005
	
*/

package src.datastructs;

import java.util.Vector;

public class IntegerSet implements Cloneable {

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
		
		int vecSize = (_size/32)+1;
		
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
		// bei Vereinigung werden einfach die überzähligen Elemente von b
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



	// Vector mit den eigentlichen Elementen
	private Vector<Integer> contentVector() {
		Vector<Integer> result = new Vector<Integer>();
		for (int i = 0 ; i <= size ; i++ ) {
			if (this.contains(i)) {
				result.add((Integer)i);
			}
		}
		return result;
	}



	// Potenzmenge, array mit allen Teilmengen
	// astronmische Laufzeit, aber :wayne:
	public IntegerSet[] getPowerset() {

		Vector<Integer> myElements, remainElements;
		
		int cardinality; 
		
		int step, stepcount; // schrittweite, wieviele Elemente halten wir fest?
		int current, elem; // derzeitges Element das mit den festen Elementen kombiniert wird
		IntegerSet remain, fixed, newTM, empty, single;
		Vector<Vector> result;
		Vector<IntegerSet> tmSet;
		
		// Zur Vereinfachung wird die Menge zunächst auf ihre eigentlichen Elemente
		// reduziert, um ewiges Gesuche und contains-geteste zu sparen

		myElements = contentVector();
		cardinality = myElements.size();

		// Arrayindex n enthält die n-elementigen Teilmengen
		// hinzu kommt, die leere Menge
		result = new Vector<Vector>();
		
		empty = new IntegerSet(size);
		
		tmSet = new Vector<IntegerSet>(); tmSet.add(empty);
		result.add(tmSet);
		
		tmSet = new Vector<IntegerSet>();
		
		// die einelementigen Mengen kann man per Hand berechnen
		for (int i = 0 ; i < cardinality ; i++ ) {
			single = new IntegerSet(size);
			single.insert(myElements.elementAt(i));
			tmSet.add(single);
		}

		result.add(tmSet);

		for (step = 1 ; step < (cardinality-1) ; step++ ) {
		
			// für die step-elementigen mengen füge den vector ein
			tmSet = new Vector<IntegerSet>();
		
			for (current = 0 ; current < cardinality ; current++ ) {
				
				// halte current fix
				fixed = new IntegerSet(size);
				fixed.insert(myElements.elementAt(current));
				
				stepcount = 1;
				elem = current+1;
	
				// packe step-viele elemente dazu			
				while (stepcount < step) {
					if (elem>=cardinality) elem = 0;
					
					fixed.insert(myElements.elementAt(elem));
					stepcount++;
					elem++;
				}
	
	
				// bilde den rest (also this minus fixed)
				remain = (IntegerSet)this.clone();
				remain.minus(fixed);
				remainElements = remain.contentVector();
				
				// kombiniere jedes Element von remain nun mit fixed
				// die dabei entstehende Menge ist eine neue Teilmenge
				for (int k = 0 ; k < remainElements.size() ; k++) {
					newTM = (IntegerSet)fixed.clone();
					newTM.insert(remainElements.elementAt(k));
					if (!tmSet.contains(newTM))
						tmSet.add(newTM);
				}
				
				// in den entsprechend teilmengen vektor damit
				

			}
			result.add(tmSet);
				
		}
		
		// die gesamte Menge
		tmSet = new Vector<IntegerSet>(); tmSet.add((IntegerSet)this.clone());
		result.add(tmSet);
		
		int cc = 0;

		for (int i = 0 ; i < result.size() ; i++ ) {
			cc += result.elementAt(i).size();
			System.out.println(result.elementAt(i));
			System.out.println();
		}
		
		System.out.println(cc+" Teilmengen");

		return null;

	}


	// this und b sind gleich wenn sie dieselben Elemente enthalten
	// die referenzgleichheit wird nicht geprüft
	public boolean equals(Object other) {
		Vector<Integer> bVector;
		IntegerSet b;
		int m, aBits, bBits;
		
		if (other instanceof IntegerSet) {

			b = (IntegerSet)other;
			bVector = b.getVector();


			m = Math.min(bVector.size(), setBits.size());
			
			for (int i = 0 ; i < m ; i++ ) {
				bBits = bVector.elementAt(i);
				aBits = setBits.elementAt(i);
				
				if (aBits!=bBits) return false;
			}
			
			// hat eines der Sets mehr bit-ints
			// wird geprüft ob diese alle null sind
			if (bVector.size()>setBits.size()) {
				for (int i = setBits.size() ; i < bVector.size() ; i++) {
					if (bVector.elementAt(i)!=0) return false;
				}
				return true;
			}
			else if (setBits.size()>bVector.size()) {
				for (int i = bVector.size() ; i < setBits.size() ; i++) {
					if (setBits.elementAt(i)!=0) return false;
				}
				return true;
			}
			else {
				return true;
			}
		}
		return false;	
	}


	public String toString() {
		int mask;
		String stringRep;
		
		stringRep = "[";
		for (int i = 0 ; i < setBits.size() ; i++) {
			for (int k = 0 ; k < 32 ; k++) {
				mask = 1 << k;
				if ((mask & (int)setBits.elementAt(i))!=0) {
					stringRep += " "+(k+(i*32));
				}
			}
		}
		stringRep += " ]";
		return stringRep;
	}


	public Object clone() {
		IntegerSet c;
		Vector<Integer> newVec = new Vector<Integer>(setBits);
		c = new IntegerSet(size);
		c.setVector(newVec);
		return c;		
	}

	// liefert eine den int-Vectors
	private Vector<Integer> getVector() {
		return (setBits);
	}


	private void setVector(Vector<Integer> vec) {
		setBits = vec;
	}
	
	public static void main(String[] args) {
		IntegerSet[] mengen;
		IntegerSet M,N;
		int max, msize;
		int a,b;
		
		// STRESSTESTING!!! :-)
		
		M = new IntegerSet(9);
		
		M.insert(0);
		M.insert(1);
		M.insert(2);
		M.insert(3);
		M.insert(4);
		M.insert(5);
		M.insert(6);

		
		M.getPowerset();
		
		
/* 		mengen = new IntegerSet[100];
		
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
		}  */
		
	}

}










