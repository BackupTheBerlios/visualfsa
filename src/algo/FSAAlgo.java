/*
  Copyright 2005 Mathias Lichtner
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
