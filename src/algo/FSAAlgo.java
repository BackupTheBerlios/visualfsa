/*
 * FSAAlgo.java
 *
 * 04-09-04
 *
 * Klasse mit diversen statischen Methode die Algorithmen auf endl.
 * Automaten realisieren ;-)
 *
 * bisher da: nfa -> dfa
 */

package src.algo;

import src.datastructs.FSA;
import src.datastructs.Transition;

import java.util.*;

public class FSAAlgo {
    
    public FSAAlgo() {
    }
    
    /* NFA -> DFA, erster Ansatz */
    public static FSA determine(FSA aut) {
        HashSet[] powerset;
        Integer[] elems;
        int states, ssCount;
        FSA result;
        
        states = aut.getStateCount();
        
        // SubSetCount..
        ssCount = 1 << states;
        
        result = new FSA(ssCount);
        
        // **debug**
        //System.out.println(" fsa states: "+states+" subsets: "+ssCount);
        
        powerset = new HashSet[ssCount];
        elems = new Integer[states];
        
        // irgendwie hübscher als die in der Schleife unten ständig
        // neu zu erzeugen...
        for (int i = 0 ; i < states ; i++ ) {
            elems[i] = new Integer(i);
        }
        
        // potenzmenge basteln
        // !!!! mit mehr als 31 states geht das hier schief!!!
        for (int i = 0 ; i < ssCount ; i++) {
            powerset[i] = new HashSet();
            for (int j = 0 ; j < states ; j++) {
                if ( (i&(1<<j))!=0 )
                    powerset[i].add(elems[j]);
            }
        }
        
        
        // **debug**
        //for (int i = 0 ; i < ssCount ; i++ ) {
        //    System.out.println(powerset[i]);
        //}
        
        // die eigentliche Determinisierung... im Prinzip recht geradlinig
        // für jedes Element der Potenzmenge, werden dessen enthaltene Zustände
        // durchlaufen und die von dort erreichbaren Zustände bilden am Ende
        // insgesamt (als Menge) den neuen Zustand in der Transition (jeweils
        // für alle Buchstaben des Arbeitsalphabets)

        HashSet alpha;
        Iterator alphaIt, setIt;
        int currentState, destState;
        char currentChar;
        LinkedList transList;
        ListIterator tlIt;
        Transition currentTrans;
        
        alpha = aut.getAlphabet();
        
        for (int i = 0 ; i < ssCount ; i++ ) {

            alphaIt = alpha.iterator();
            
            // für jedes Zeichen im Arbeitsalphabet...
            while (alphaIt.hasNext()) {
                currentChar = ((Character)alphaIt.next()).charValue();

                setIt = powerset[i].iterator();
                
                destState = 0;
                
                // ...prüfe die erreichbaren Zustände...
                // ..für jedes Element der Zustandsmenge
                while (setIt.hasNext()) {
                    
                    currentState = ((Integer)setIt.next()).intValue();
                    
                    transList = aut.getTransitions(currentState);
                    tlIt = transList.listIterator();
                    
                    while (tlIt.hasNext()) {
                        currentTrans = (Transition)tlIt.next();
                        // kommen wir mit dem aktuellen Zeichen wohin?
                        if (currentTrans.getChar()==currentChar) {         
                            // die Sache mit 31.... 
                            destState = destState | (1<<currentTrans.getEndState());
                        }
                    }
                }
                
                result.addTransition(i,destState,currentChar);
            
            }
            
        }
        
        return result;
    }
    
    
    
}
