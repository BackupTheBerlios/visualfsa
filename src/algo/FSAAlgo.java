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

package algo;

import datastructs.Transition;
import datastructs.IntegerSet;
import datastructs.FSA;

import java.util.*;


public class FSAAlgo {
    
    
    /*
        guessLang versucht die Sprache die ein Automat erkennt zu identifzieren,
        dabei werden aus dem Eingabealphabet alle Wörter gebildet deren Länge
        kleiner 'step' ist (da die Sprachen potentiell unendlich sind (sein können))
        Mit jedem erzeugten Wort wird aut.accepts aufgerufen
     
        Errors werfen ist keine gute Praxis :-)
     */
    public static synchronized HashSet<String> guessLang(FSA aut, int step) throws OutOfMemoryError {
        Vector<Character> alpha;
        
        alpha = aut.getAlphabet();
        
        Vector<String> words;
        Vector<String> newWords;
        HashSet<String> lang;
        
        words = new Vector<String>();
        String newWord;
        lang = new HashSet<String>();
        String curr;
        
        boolean autType = aut.isDeterministic();
        
        /*
            words ist die Menge aller Strings aus denen wir noch neue
            Kombinationen bilden können, anfangs wird jeder einzelne Buchstabe
            des Eingabealphabets dort hineingetan
         */
        for ( Iterator<Character> it = alpha.iterator(); it.hasNext(); ) {
            words.add(it.next().toString());
        }
        
        for (int i = 0 ; i < step ; i++ ) {
            
            /* teste die aktuelle Wortliste */
            
            for ( Iterator<String> strIt = words.iterator(); strIt.hasNext(); ) {
                curr = strIt.next();
                
                /* da der Automat während des Sprachtests nicht verändert
                   wird kann der in FSA.java eingebaute Determinismustest
                   übersprungen werden
                 */
                
                if (aut.accepts(curr, true, autType)) {
                    lang.add(curr);
                }
            }
            
            newWords = new Vector<String>();
            
            /* hinter jedes Wort der aktuellen Wortliste wird jeder Buchstabe
               des Eingabealphabets gehängt, die neu enstandenen Wörter ersetzen
               die alten wodurch nie alle Wörter im Speicher gehalten werden müssen
             */
            
            for ( Iterator<String> strIt = words.iterator(); strIt.hasNext(); ) {
                
                newWord = strIt.next();
                
                
                for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                    newWords.add(newWord+alphaIt.next().toString());
                    
                }
                
            }
            
            words.clear();
            words.addAll(newWords);
            
        }
        
        return lang;
    }
    
   /*
        Determinisierung per Potenzmengenkonstruktion
        erste Implementierung
    */
    public static FSA determ(FSA aut) {
        FSA dfaResult = new FSA();
        
        /* erstelle die Zustandsmenge des Automaten */
        Set<Integer> tempSet;
        IntegerSet stateSet;
        
        stateSet = new IntegerSet();
        
        tempSet = aut.getStates();
        
        for ( Iterator<Integer> it = tempSet.iterator(); it.hasNext(); ) {
            stateSet.insert(it.next());
        }
        
        // Potenzmenge davon berechnen
        Vector<IntegerSet> statePowerSet;
        
        statePowerSet = stateSet.getPowerset();
        
        // aktuelle teilmenge der Potmge
        Vector<Integer> currentSet;
        IntegerSet currentIntSet;
        
        // eingabealphabet
        Vector<Character> alpha;
        Character currChar;
        
        alpha = aut.getAlphabet();
        
        // aktuelle Transitionsliste
        LinkedList<Transition> transList;
        Transition currTrans;
        
        IntegerSet destSet;
        
        // nimm einen Zustand des neuen Automaten her
        for ( Iterator<IntegerSet> psIt = statePowerSet.iterator(); psIt.hasNext();) {
            
            currentIntSet = psIt.next();
            
            currentSet = currentIntSet.pureElements();
            
            System.out.println("current set "+currentSet);
            
            // durchlaufe die aktuelle Menge (=Zustand)
            // und teste für den aktuellen Buchstaben welchen Zustand wir damit erreichen
            for ( Iterator<Integer> setIt = currentSet.iterator(); setIt.hasNext(); ) {
                
                // besorge alle Transitionen dieses Zustands aus dem
                // alten Automaten
                transList = aut.getStateTransitions(setIt.next());
                
                // durchlaufe für diesen Zustand alle Buchstaben des Eingabealphabets

                destSet = new IntegerSet();
                
                for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                    
                    currChar = alphaIt.next();
                    
                    

                    
                    for ( Iterator<Transition> transIt = transList.iterator(); transIt.hasNext(); ) {
                        
                        currTrans = transIt.next();
                        
                        if (currTrans.getChar() == currChar) {
                            System.out.println(currentSet+" geht mit "+currChar+" nach "+currTrans.getEndState());
                            destSet.insert(currTrans.getEndState());
                        }
                        
                    }
                    
                    dfaResult.addTransition(statePowerSet.indexOf(currentIntSet),
                                            statePowerSet.indexOf(destSet),
                                            currChar);
                    
                }
            }
            
        }
        
        
       dfaResult.setName("myName");
        
        
        return dfaResult;
    }
    
    
    
}
