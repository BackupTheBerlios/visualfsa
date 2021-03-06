/*
  Copyright 2005, 2006 Mathias Lichtner
  mlic at informatik.uni-kiel.de
 
  This file is part of visualfsa.
 
  visualfsa is free software; you can redistribute it and/or modify it under the terms
  of the GNU General Public License as published by the Free Software Foundation;
  either version 2 of the License, or (at your option) any later version.
 
  visualfsa is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
  See the GNU General Public License for more details.
 
  You should have received a copy of the GNU General Public License along with visualfsa;
  if not, write to the Free Software Foundation, Inc.,
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package datastructs;


import java.io.Serializable;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;
import java.util.Collection;
import java.util.Iterator;
import java.util.ListIterator;
import java.awt.Point;

public class FSA implements Serializable, Cloneable {
    
    private LinkedHashMap<Integer,LinkedList<Transition>> transitionTable;
    private LinkedHashMap<Integer,Point> posTable; // koor der Zust�nde
    
    private IntegerSet finalStateSet, startStateSet;
    
    // L�ufe protokollieren? default - nein
    private boolean logRun;
    private Vector<IntegerSet> lastLog;
    
    private String name;
    
    public FSA() {
        transitionTable = new LinkedHashMap<Integer,LinkedList<Transition>>();
        posTable = new LinkedHashMap<Integer,Point>();
        finalStateSet = new IntegerSet();
        startStateSet = new IntegerSet();
        logRun = false;
    }
    
    
    public void setLog(boolean _log) {
        logRun = _log;
    }
    
    public boolean getLog() {
        return logRun;
    }
    
    public Vector<IntegerSet> getLastLog() {
        return lastLog;
    }
    
    /* diverse erweiterungen f�r die gui anbindung */
    
    public Set<Integer> getStates() {
        return posTable.keySet();
    }
    
    public Set<Integer> getNonGuiStates() {
        return transitionTable.keySet();
    }
    
    public Point getPosition(Integer state) {
        return posTable.get(state);
    }
    
    public void setPosition(Integer state, Point p) {
        posTable.put(state, p);
    }
    
    // jeder Endzustand wird nicht Endzustand, jeder nicht Endzustand
    // wird Endzustand, der automat erkennt fortan also das Komplement seiner
    // (ehemaligen) Sprache
    public void invertStates() {
        Set<Integer> stateSet;
        
        stateSet = this.getStates();
        
        for (Integer s : stateSet) {
            if (finalStateSet.contains(s)) {
                finalStateSet.remove(s);
            } else {
                finalStateSet.insert(s);
            }
        }
    }
    
    // pr�ft ob der Automat mindestens eine Transition hat, bei den Mengenoperation
    // gibt es Probleme mit Automaten die nur aus Zust�nden bestehen, aber keine
    // Transitionen haben
    public boolean hasTransitions() {
        LinkedList<Transition> tl;
        for (Integer key : transitionTable.keySet()) {
            tl = transitionTable.get(key);
            if (tl!=null && tl.size()>=0) return true;
        }
        return false;
    }
    
    
    public LinkedList<Transition> getStateTransitions(int k) {
        return transitionTable.get(k);
    }
    
    /* 	addTransition, f�ge eine Transition in den Automaten ein
        R�ckgabewert gibt an ob die Transitio wirklich neu war */
    public boolean addTransition(int startState, int finalState, char transChar) {
        
        LinkedList<Transition> transList, endList;
        Integer key = (Integer)startState;
        Integer endKey = (Integer)finalState;
        Transition newTransition;
        
        // pr�fe alles auf seine Richtigkeit
        if (startState<0 || finalState<0 || !(Character.isLetterOrDigit(transChar))) {
            throw new IllegalArgumentException(startState+","+transChar+","+finalState);
        }
        
        newTransition = new Transition(startState, finalState, transChar);
        
        // enth�lt die Transitionstabelle noch keine Liste f�r diesen
        // Zustand dann f�ge sie ein
        
        /* erzeuge auch eine m�glicherweise noch nicht vorhandene Transitions-
           liste f�r den Endzustand */
        if (!transitionTable.containsKey(endKey)) {
            endList = new LinkedList<Transition>();
            transitionTable.put(endKey,endList);
        }
        
        if (transitionTable.containsKey(key)) {
            transList = transitionTable.get(key);
            if (transList.contains(newTransition)) {
                return false;
            } else {
                transList.add(newTransition);
                return true;
            }
        } else {
            transList = new LinkedList<Transition>();
            transList.add(newTransition);
            transitionTable.put(key, transList);
            return true;
        }
        
    }
    
    
    // akzeptiert der Automat das Wort word
    // wegen Performancezwecken, wie zb in FSAAlog wo accepts viele hunderte
    // bis tausende Male aufgerufen wird, kann der isDeterm... Test �bersprungen
    // werden, solange sichergestellt wird das der Automat nicht ver�ndert wurde
    // isDFA gibt an ob es sich um einen DFA handelt
    public boolean accepts(String word, boolean override, boolean isDFA) {
        boolean autType;
        
        if (override)
            autType = isDFA;
        else
            autType = this.isDeterministic();
        
        lastLog = null;
        
        // sollen wir loggen?
        if (logRun) {
            lastLog = new Vector<IntegerSet>();
        }
        
        if (autType) {
            return this.dfaAccepts(word);
        } else {
            return this.nfaAccepts(word);
        }
        
    }
    
    
    // der Startzustand bei nfa ist die menge die nur
    // den Startzustand enth�lt
    private boolean nfaAccepts(String w) {
        IntegerSet startSet;
        IntegerSet endSet;
        ListIterator<Integer> finalSetIt;
        
        startSet = startStateSet;
        endSet = nfaDelta(startSet, w);
        // schaue nun, ob die Endzustandsmenge einen akzeptierenden Zustand
        // enth�lt, sch�n das wir daf�r unser IntSet haben
        
        // nfaDelta liefert uU zb die Originalreferenz auf das StartStateSet
        // zur�ck, deshalb hier clone
        endSet = (IntegerSet)endSet.clone();
        
        if (logRun) {
            lastLog.add(endSet);
        }
        
        endSet.intersect(finalStateSet);
        return (!endSet.isEmpty());
    }
    
    private boolean dfaAccepts(String w) {
        Integer reachedState;
        // da gepr�ft wurde ob der Automat deterministisch ist, muss
        // gelten das das Startzustandsset nur ein Element enth�lt
        // welches getFirst findet (dabei liefert IntegerSet.getFirst
        // das kleinste element in der Menge
        reachedState = dfaDelta(startStateSet.getFirst(), w);
        
        if (reachedState == -1) return false;
        
        if (logRun) {
            IntegerSet stateSet;
            stateSet = new IntegerSet();
            stateSet.insert(reachedState);
            lastLog.add(stateSet);
        }
        
        return (finalStateSet.contains(reachedState));
    }
    
    
    /* Rekursion, die [n] - siehe Rekursion :-)
       (Worterkennung DFA)
     
       Wir betrachten nur totale Transitionsfunktionen
     
       q - Zustand, ax Wort �ber Sigma*
       delta : Q x Sigma* -> Q
       delta(q,epsilon) = q
       delta(q,ax) = delta(delta(q,a),x)
     
       mit ax = a_1 ... a_n
     
       q_i+1 = delta(q_i,a_i+1) f�r i aus 1...n-1
     
       Zustandsfolge q0,...,qn ist Lauf vom Automaten (A) f�r ax
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
        
        if (logRun) {
            IntegerSet stateSet;
            stateSet = new IntegerSet();
            stateSet.insert(state);
            lastLog.add(stateSet);
        }
        
        // wenn das Wort leer ist, sind wir fertig
        // ansonsten spalte das erste Zeichen ab
        if (ax.length()==0) {
            return state;
        } else {
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
        
        return -1;
    }
    
    
    /*
      nfaDelta arbeitet analog zu dfaDelta, nur mit dem
      Unterschied das es auf Mengen von Zust�nden operiert.
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
        
        // log
        // speichere einfach f�r den aktuellen Buchstaben die Zustandsmenge
        if (logRun) {
            lastLog.add(stateSet);
        }
        
        newStateSet = new IntegerSet();
        
        // extrahiere das aktuelle Zeichen
        if (ax.length()==0) {
            return stateSet;
        } else {
            a  = ax.charAt(0);
        }
        
        // hole die Elemente der aktuellen Zustandsmenge
        setVec = stateSet.pureElements();
        setIt = setVec.listIterator();
        
        // f�r jeden Zustand in der Menge...
        while (setIt.hasNext()) {
            
            // hole seine Transitionenliste
            transList = transitionTable.get((Integer)setIt.next());
            transIt = transList.listIterator();
            
            // durchlaufe seine Transition und pr�fe ob man mit a
            // einen anderen Zustand erreichen w�rde
            while (transIt.hasNext()) {
                current = (Transition)transIt.next();
                
                // eine passende Transititon gefunden
                if (current.getChar()==a) {
                    newStateSet.insert(current.getEndState());
                }
            }
        }
        
        // nun da wir alle Elemente der neuen Zustandsmenge haben...
        // pr�fe ob wir fertig sind...
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
        
        // Bestimme zun�chst das Arbeitsalphabet
        alpha = this.getAlphabet();
        
        
        // pr�fe zun�chst ob wir mehr als einen Startzustand haben
        if (startStateSet.cardinality()!=1) return false;
        
        // Pr�fe nun, ob jeder Zustand mit seinen Transitionen alle
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
                } else {
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
    
    
    /* setze den Endzustands-flag f�r einen zustand */
    public void setFinalFlag(int st, boolean flag) {
        if (!flag && finalStateSet.contains(st)) {
            finalStateSet.remove(st);
        } else if (flag && !finalStateSet.contains(st)) {
            finalStateSet.insert(st);
        }
    }
    
    /* setze den Startzustands-Flag f�r einen Zustand */
    public void setStartFlag(int st, boolean flag) {
        if (!flag && startStateSet.contains(st)) {
            startStateSet.remove(st);
        } else if (flag && !startStateSet.contains(st)) {
            startStateSet.insert(st);
        }
    }
    
    public boolean isFinalState(int st) {
        return finalStateSet.contains(st);
    }
    
    public boolean isStartState(int st) {
        return startStateSet.contains(st);
    }
    
    // erzeugt eine ausf�hrliche textuelle Beschreibung des Automaten
    public String infoString() {
        StringBuffer str = new StringBuffer();
        Collection<LinkedList<Transition>> coll;
        Iterator<LinkedList<Transition>> tableIt;
        ListIterator<Transition> listIt;
        
        str.append("Name: "+name+"\n");
        str.append("Type: "+(this.isDeterministic() ? "DFA\n" : "NFA\n"));
        str.append("Start-states: "+startStateSet+"\n");
        str.append("Final-states: "+finalStateSet+"\n");
        str.append("Alphabet: "+getAlphabet()+"\n");
        
        str.append("Transitions:\n");
        
        coll = transitionTable.values();
        tableIt = coll.iterator();
        
        while (tableIt.hasNext()) {
            listIt = ((LinkedList<Transition>)tableIt.next()).listIterator();
            while (listIt.hasNext()) {
                str.append(((Transition)listIt.next())+"\n");
            }
        }
        
        return str.toString();
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
    
    public IntegerSet getFinalSet() {
        return finalStateSet;
    }
    
    public IntegerSet getStartSet() {
        return startStateSet;
    }
    
    // tiefe Kopie des Automaten erstellen
    public Object clone() {
        FSA copy;
        LinkedList<Transition> currentTL;
        
        copy = new FSA();
        
        for (Integer key : transitionTable.keySet()) {
            // die Transitionsliste des aktuellen Zustands(schluessel) aus der Map ziehen
            currentTL = transitionTable.get(key);
            
            copy.setPosition(key, this.getPosition(key)); // o.O
            
            // wenn != leer, dr�ber laufen und dem neuen automaten hinzuf�gen
            if (currentTL!=null) {
                for ( Transition ct : currentTL) {
                    copy.addTransition(ct.getStartState(), ct.getEndState(), ct.getChar());
                }
            }
        }
        
        // Startzust�nde und Endzust�nde kopieren
        copy.startStateSet = (IntegerSet)this.startStateSet.clone();
        copy.finalStateSet = (IntegerSet)this.finalStateSet.clone();
        
        copy.setName(name);
        
        return copy;
    }
    
}
