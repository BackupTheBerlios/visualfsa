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


	
	public IntegerSet(int _size) {
		// ein Int speichert 32 Elemente
		setBits = new Vector<Integer>();
		
		int vecSize = (_size/32)+1; // einer mehr kann nicht schaden
		
		vectorSize = vecSize;
		size = _size;
		
		System.out.println("setsize: "+size+" need: "+vecSize);
		
		for (int i = 0 ; i < vecSize ; i++ ) {
			setBits.add(new Integer(0));
		}
		
	}
	


	public void insert(int i) {
		// offset berechnen um das richtige Bit zu finden
		int vecIndex = i/32, offset = i%32;
		int mask, setBit;
		int[] m = getIndexAndOffset(i);
		
		mask = 1 << m[1];
		vecIndex = m[0];				
			
		// selektiere den entsprechenden int und setze das richtige bit
		setBit = (int)setBits.elementAt(vecIndex);
		setBit = setBit | mask;
		setBits.setElementAt((Integer)setBit,vecIndex);
	}
	


	private int[] getIndexAndOffset(int k) {
		// offset und index berechnen
		int vecIndex = k/32;
		int offset = k%32;
		
		System.out.println("vecIndex "+vecIndex+" vectorSize "+vectorSize);
		
		if (k<0 || vecIndex >= vectorSize)
			throw new IllegalArgumentException(k+"");
		
		int[] result = { vecIndex, offset };
		
		return result;
	}



	public String toString() {
		int mask,cc=0;
		System.out.print("[");
		for (int i = 0 ; i < setBits.size() ; i++) {
			for (int k = 0 ; k < 32 ; k++) {
				mask = 1 << k;
				if ((mask & (int)setBits.elementAt(i))!=0) {
					System.out.print(" "+(k+(i*32)));
				}
				cc++;
			}
		}
		System.out.println(" ]");
		return "";
	}


	
	public static void main(String[] args) {
		IntegerSet a,b,c;
		
		a = new IntegerSet(32); b = new IntegerSet(12); c = new IntegerSet(20);
		a.insert(12); a.insert(31); a.insert(0); a.insert(16);
		b.insert(3); b.insert(5); b.insert(10); b.insert(10);
		c.insert(12); c.insert(0); c.insert(14); c.insert(19);
		System.out.println(a);
		System.out.println(b);
		System.out.println(c);
	}

}










