/*
 *  FSA.java - 21-08-2004
 *
 *  Implementierung eines endlichen Automaten
 *  (finite state automaton)
 *
 */

package src.datastructs;


import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.awt.Point;

public class FSA {

    private LinkedHashMap<Integer,LinkedList<Transition>> transitionTable;
    private LinkedHashMap<Integer,Point> posTable; // koor der Zustände
    
    private IntegerSet finalStateSet;
    private int globalStart;

    private String name;
    
    public FSA() {
	transitionTable = new LinkedHashMap<Integer,LinkedList<Transition>>();
	posTable = new LinkedHashMap<Integer,Point>();
	finalStateSet = new IntegerSet();
    }    
    
    /* diverse erweiterungen für die gui anbindung */

    public Set getStates() {
	return posTable.keySet();
    }

    public Point getPosition(Integer state) {
	return posTable.get(state);
    }

    public void setPosition(Integer state, Point p) {
	posTable.put(state, p);
    }
	
    /* 	addTransition, füge eine Transition in den Automaten ein 
	Rückgabewert gibt an ob die Transitio wirklich neu war */
    public boolean addTransition(int startState, int finalState, char transChar) {
	
	LinkedList<Transition> transList, endList;
	Integer key = (Integer)startState;
	Integer endKey = (Integer)finalState;
	Transition newTransition;
	
	// prüfe alles auf seine Richtigkeit
	if (startState<0 || finalState<0 || !(Character.isLetterOrDigit(transChar))) {
	    throw new IllegalArgumentException(startState+","+transChar+","+finalState);
	}
	
	newTransition = new Transition(startState, finalState, transChar);	
	
	// enthält die Transitionstabelle noch keine Liste für diesen
	// Zustand dann füge sie ein
		
	/* erzeuge auch eine möglicherweise noch nicht vorhandene Transitions-
	   liste für den Endzustand */
	if (!transitionTable.containsKey(endKey)) {
	    endList = new LinkedList<Transition>();
	    transitionTable.put(endKey,endList);
	}
		
	if (transitionTable.containsKey(key)) {
	    transList = transitionTable.get(key);
	    if (transList.contains(newTransition)) {
		return false;
	    }
	    else {
		transList.add(newTransition);
		return true;
	    }
	}
	else {
	    transList = new LinkedList<Transition>();
	    transList.add(newTransition);
	    transitionTable.put(key, transList);
	    return true;
	}

    }
	

    // setzt den Anfangszustand des Automaten
    public void setStartState(int q0) {
	globalStart = q0;
    }

	
	
    // akzeptiert der Automat das Wort word
    public boolean accepts(String word) {
	if (this.isDeterministic()) {
	    return this.dfaAccepts(word);
	}
	else {
	    return this.nfaAccepts(word);
	}
    }
	

    // der Startzustand bei nfa ist die menge die nur
    // den Startzustand enthält
    private boolean nfaAccepts(String w) {
	IntegerSet startSet;
	IntegerSet endSet;
	ListIterator<Integer> finalSetIt;
		
	startSet = new IntegerSet();
	startSet.insert(globalStart);
	endSet = nfaDelta(startSet, w);
	// schaue nun, ob die Endzustandsmenge einen akzeptierenden Zustand
	// enthält, schön das wir dafür unser IntSet haben
	System.out.println(endSet);
	endSet.intersect(finalStateSet);
	return (!endSet.isEmpty());
    }

	
    private boolean dfaAccepts(String w) {
	Integer reachedState;		
	reachedState = dfaDelta(globalStart, w);
	return (finalStateSet.contains(reachedState));
    }
	

    /* Rekursion, die [n] - siehe Rekursion :-)
       (Worterkennung DFA)
	
       Wir betrachten nur totale Transitionsfunktionen
		
       q - Zustand, ax Wort über Sigma*
       delta : Q x Sigma* -> Q
       delta(q,epsilon) = q
       delta(q,ax) = delta(delta(q,a),x)
		
       mit ax = a_1 ... a_n
		
       q_i+1 = delta(q_i,a_i+1) für i aus 1...n-1
		
       Zustandsfolge q0,...,qn ist Lauf vom Automaten (A) für ax 
       ist qn aus F, folgt ax in L(A).
		
       dfaDelta implementiert genau das Verhalten oben beschriebener
       Funktion delta
    */		
    private Integer dfaDelta(Integer state, CharSequence ax) {
	ListIterator<Transition> transIt;
	LinkedList<Transition> transList;
	Transition current;
	char a;
	CharSequence newax;
		
	// wenn das Wort leer ist, sind wir fertig
	// ansonsten spalte das erste Zeichen ab
	if (ax.length()==0) {
	    return state;
	}
	else {
	    a  = ax.charAt(0);
	}
		
	transList = transitionTable.get((Integer)state);
		
	transIt = transList.listIterator();
		
	while (transIt.hasNext()) {
	    current = (Transition)transIt.next();
			
	    // eine passende Transititon gefunden 
	    if (current.getChar()==a) {
		if (ax.length()==1) {
		    return current.getEndState();
		}
				
		newax = ax.subSequence(1,ax.length());
		return dfaDelta(current.getEndState(), newax);
	    }
	}
		
	return state;
    }
	
	
    /*
      nfaDelta arbeitet analog zu dfaDelta, nur mit dem
      Unterschied das es auf Mengen von Zuständen operiert.
      Ist das Ende der Eingabe erreicht und es befindet
      sich ein akzeptierender Zustand in der letzten Zustands
      menge so existiert ein akzeptierender Lauf, also ist w in L(A)
    */
    private IntegerSet nfaDelta(IntegerSet stateSet, CharSequence ax) {
	ListIterator<Transition> transIt;
	ListIterator<Integer> setIt;
	LinkedList<Transition> transList;
	Vector<Integer> setVec;
	IntegerSet newStateSet;
	Transition current;
	char a;
	CharSequence newax;

	newStateSet = new IntegerSet();

	// extrahiere das aktuelle Zeichen
	if (ax.length()==0) {
	    return stateSet;
	}
	else {
	    a  = ax.charAt(0);
	}
		
	// hole die Elemente der aktuellen Zustandsmenge
	setVec = stateSet.pureElements();
	setIt = setVec.listIterator();
		
	// für jeden Zustand in der Menge...
	while (setIt.hasNext()) {
		
	    // hole seine Transitionenliste
	    transList = transitionTable.get((Integer)setIt.next());
	    transIt = transList.listIterator();
		
	    // durchlaufe seine Transition und prüfe ob man mit a
	    // einen anderen Zustand erreichen würde
	    while (transIt.hasNext()) {
		current = (Transition)transIt.next();
			
		// eine passende Transititon gefunden 
		if (current.getChar()==a) {
		    newStateSet.insert(current.getEndState());
		}
	    }
	}
		
	// nun da wir alle Elemente der neuen Zustandsmenge haben...
	// prüfe ob wir fertig sind...
	if (ax.length()==1) {
	    return newStateSet;
	}
		
	// oder bilde das verbleibende Restwort		
	newax = ax.subSequence(1,ax.length());
	return nfaDelta(newStateSet, newax);						
    }
	
	
	
    /* teste ob der Automat deterministisch ist */
    public boolean isDeterministic() {
	Vector<Character> alpha, alphaCopy;
	Collection<LinkedList<Transition>> coll;
	Iterator<LinkedList<Transition>> tableIt;
	ListIterator transIt;
	LinkedList<Transition> currList;
	Character curr;	
	Transition t;	
			
	coll = transitionTable.values();
	tableIt = coll.iterator();

	// Bestimme zunächst das Arbeitsalphabet
	alpha = this.getAlphabet();
		
		
	// Prüfe nun, ob jeder Zustand mit seinen Transitionen alle
	// Zeichen des Arbeitsalphabets bedient
		
	while (tableIt.hasNext()) {
	    currList = (LinkedList<Transition>)tableIt.next();
	    transIt = currList.listIterator();
		
	    alphaCopy = null;
	    alphaCopy = new Vector<Character>(alpha);
		
	    while (transIt.hasNext()) {
		t = (Transition)transIt.next();
		curr = (Character)t.getChar();
		if (alphaCopy.contains(curr)) {
		    alphaCopy.remove(curr);
		}
		else {
		    // das Zeichen wird gar nicht bedient oder wurde schon bedient
		    // -> nicht deterministisch
		    return false;
		}
	    }
			
	    // ist das Alphabet nach dem Durchlauf != leer, wurden Zeichen nicht bedient
	    // -> nicht deterministisch
	    if (!alphaCopy.isEmpty()) return false;
	}
	return true;
    }
	
	
    public Vector<Character> getAlphabet() {
	Collection<LinkedList<Transition>> coll;
	Iterator<LinkedList<Transition>> tableIt;
	ListIterator<Transition> transIt;
	LinkedList<Transition> currList;
	Vector<Character> res = new Vector<Character>();
	Character curr;	
	Transition t;	
			
	coll = transitionTable.values();
	tableIt = coll.iterator();
		
	while (tableIt.hasNext()) {
	    currList = (LinkedList<Transition>)tableIt.next();
	    transIt = currList.listIterator();
			
	    while (transIt.hasNext()) {
		t = (Transition)transIt.next();
		curr = (Character)t.getChar();
		if (!res.contains(curr)) res.add(curr);
	    }		
	}
	return res;
    }
	
	
    /* setze den Endzustands-flag für einen zustand */
    public void setFinalFlag(int st, boolean flag) {
	if (!flag && finalStateSet.contains(st)) {
	    finalStateSet.remove(st);
	}
	else if (flag && !finalStateSet.contains(st)) {
	    finalStateSet.insert(st);
	}
    }
	

    public void setName(String _name) {
	name = _name;
    }

    public String getName() {
	return name;
    }
    
    public String toString() {
	return transitionTable.toString();
    }
	
	
}
