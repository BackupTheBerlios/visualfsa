/*
 *  FSA.java - 21-08-2004
 *
 *  Implementierung eines endlichen Automaten
 *
 *  fester Startzustand (0)
*/

package datastructs;

import java.util.*;

public class FSA
{

    private int maxState;
    private LinkedList[] table; // Transitionstabelle
    private boolean[] finalStates; // welche Zustände sind Endzustände

    /* Konstruktoren */
    public FSA(int numOfStates) 
    {
	maxState = numOfStates;
	
	table = new LinkedList[maxState];
	finalStates = new boolean[maxState];
       
	for (int i = 0 ; i < maxState ; i++ )
	    table[i] = new LinkedList();
	
    }
    
    public FSA(FSA aut)
    {
	// TODO
    }
    


    /* fügt dem Automat eine Transition hinzu */
    public void addTransition(int ss, int es, char c)
    {
	Transition newTrans;
	LinkedList transList;
		
	// nur Buchstaben und Ziffern sind als Transitionzeichen erlaubt
	if (!Character.isLetter(c) && !Character.isDigit(c))
	    throw new IllegalArgumentException("ungültiges Transitionszeichen: "+c);

	if (isValidState(ss) && isValidState(es)) {
	    newTrans = new Transition(ss,es,c);
	    // Referenz auf die Transitionsliste dieses Zustands holen
	    transList = table[ss];
	    if (!transList.contains(newTrans))
		transList.add(newTrans);
	}
	else {
	    throw new IllegalArgumentException("ungültiger Zustand: "+ss+"/"+es);
	}
    }

    /* Zustand als endzustand markieren */
    public void setFinalState(int state, boolean flag)
    {
	if (!isValidState(state)) {
	    throw new IllegalArgumentException("ungültiger Zustand: "+state);
	}
	else {
	    finalStates[state] = flag;
	}
    }
    

    /* Worderkennung */
    public boolean accepts(String word)
    {
	// je nach Art (DFA/NFA) die passende Methode wählen
	if (isDeterministic()) {
	    dfaAccepts(word);
	}
	else {
	    // TODO
	}
	return true;
    }
    

    /* prüft ob dieser Automat deterministisch ist */
    public boolean isDeterministic()
    {
	HashSet alphabet = (HashSet)getAlphabet();
	HashSet alphacopy;
	ListIterator it;
	Character currentChar;
	
	// für jeden Zustand wird eine Kopie des Arbeitsalphabet angelegt
        // alle Transitionszeichen eines Zustandes werden aus dieser entfernt
        // am Ende des Durchlaufs muss die Kopie leer sein 
	
	for (int i = 0 ; i < maxState ; i++) {
	    alphacopy = new HashSet(alphabet);
	    it = table[i].listIterator();
	    
	    while (it.hasNext()) {
		currentChar = new Character(((Transition)it.next()).getChar());
		if (alphacopy.contains(currentChar)) {
		    alphacopy.remove(currentChar);
		}
		else {
		    // dieses Zeichen wurde mehrfach bedient -> NFA
		    return false;
		}
	    }
	    // nicht alle Zeichen wurden bedient -> NFA
	    if (!alphacopy.isEmpty()) return false;
	}
	return true;
    }
    

    /* erzeugt eine mehrzeilige String-Repräsentation diese Automaten */
    public String toString()
    {
	String stringRep = new String();
	ListIterator it;

        // arbeitsalphabet rein
	stringRep = stringRep + getAlphabet().toString() + "\n";
	for (int i = 0 ; i < maxState ; i++) {
	    it = table[i].listIterator();
	    while (it.hasNext()) {
                // jede Transition rein 
                stringRep = stringRep + ((Transition)it.next()).toString() + "\n";
	    }
	}
        // am Ende noch die Info ob DFA/NFA
        stringRep = stringRep + (isDeterministic() ? "DFA" : "NFA");
	return stringRep;
    }


    /* Worterkennung für DFAs */
    private boolean dfaAccepts(String word)
    {
	LinkedList currentTransList;
	Transition t;
	char[] charArray;
	int nextState = -1;
        ListIterator it;
        
	charArray = word.toCharArray();
       	currentTransList = table[0];
        
        for (int i = 0 ; i < charArray.length ; i++) {
            it = currentTransList.listIterator();
            nextState = -1;
            while (it.hasNext()) {
                t = (Transition)it.next();
                if (t.getChar()==charArray[i]) {
                    nextState = t.getEndState();
                    break; // weiter zum nächsten Zeichen
                }
            }
            // tritt dieser Fall auf muss das Wort Zeichen enthalten
            // haben die gar nicht im Arbeitsalphabet enthalten waren
            if (nextState==-1) return false;
        }
	return finalStates[nextState];
    }
    


    /* Rangecheck für Zustände */
    private boolean isValidState(int state)
    {
	return (state>=0 && state<maxState);
    }
    
    /* Arbeitsalphabet des Automaten bestimmen */
    private Collection getAlphabet()
    {
	HashSet alpha = new HashSet();
	ListIterator it;
        
        // alle auftauchenden Transitionszeichen werden einfach blind in das
        // Set getan, welches Doppelte automatisch ausfiltert
	for (int i = 0 ; i < maxState ; i++ ) {
	    it = table[i].listIterator();
	    while (it.hasNext()) {
		alpha.add(new Character(((Transition)it.next()).getChar()));
	    }
	}
	return alpha;
    }
    

}
