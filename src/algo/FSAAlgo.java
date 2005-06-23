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

import java.awt.Point;
import java.util.*;


public class FSAAlgo {
    
    
    // gittergröße bei der automatischen Positionsgenerierung für die Zustände
    private static final int POS_GEN_SIZE = 90;
    private static final int POS_OFFSET = 8;
    
    // wieviel Zustände in eine Reihe
    private static final int STATES_PER_ROW = 6;
    
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
    
        NFA: Aut = (A,S,si,delta,F)
        mit S Zustandsmenge, A Eingabealphabet, si Startzustand aus S
        delta die Transitionsrelation, F Endzustandsmenge (Teilmenge S)
    
        äquivalenter DFA:
    
        DetAut = (A,Pot(S),{si},delta',F')
    
        mit delta'(Q,a) = { s' | es ex. s aus Q ^ (s,a,s') ist in delta }
        F' = { Q aus Pot(S) | Q n F != emptySet }
    
        Die neuen Endzustände des entstehenden Automaten sind also all jene
        Teilmengen der Potenzmenge seiner Zustände in denen sich alte
        Endzustände befinden. Der neue Startzustand ist die Menge die nur
        den alten Startzustand enthält.
    
        Der Zielzustand für einen Zustand (Teil der Potenzmenge) und ein Zeichen
        stellt sich als die Menge aller Zustände dar, die man im alten NFA
        mit diesem Zeichen von den einzelnen Zustände erreicht hätte.
    
        (wirre Erklärung, noch viel wirrerer Code) :-)
    
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
        
        // aktuelle teilmenge der Potenzmenge
        Vector<Integer> currentSet;
        IntegerSet currentIntSet;
        
        // eingabealphabet
        Vector<Character> alpha;
        Character currChar;
        
        alpha = aut.getAlphabet();
        
        // aktuelle Transitionsliste
        LinkedList<Transition> transList;
        Transition currTrans;
        
        // der neue Zustand (==Zustandsmenge) im neuen Automaten
        IntegerSet destSet;
        int stateId; // der aktuell untersuchte (alte) Zustand
        
        // nimm einen Zustand des neuen Automaten her... (also eine Teilmenge der Potmenge)
        for ( Iterator<IntegerSet> psIt = statePowerSet.iterator(); psIt.hasNext();) {
            
            currentIntSet = psIt.next();
            
            // (TODO?) IntegerSet implementiert (noch) nicht Iterator
            currentSet = currentIntSet.pureElements();
            
            System.out.println("Untersuche Zustandsmenge "+currentSet);
            
            // eine spezielle Teilmenge der Potenzmenge, nämlich die leere
            // Menge, wird gesondert behandelt, sie dient gewissermaßen
            // als Fangzustand
            if (currentSet.isEmpty()) {
                // füge für alle Buchstaben die Transitionen ein die beim
                // Zustand verbleibt
                stateId = statePowerSet.indexOf(currentIntSet);
                
                for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                    dfaResult.addTransition(stateId,stateId, alphaIt.next());
                }
            }
            
            // identifiziere die einelementige Menge die den alten Startzustand enthält
            // Annahme ist natürlich das es sich um DFAs handelt, die nur einen Startzustand
            // haben dürfen
            if (currentIntSet.cardinality()==1 && aut.isStartState(currentIntSet.getFirst())) {
                System.out.println("  --> "+currentIntSet+" ist das StartStateSet");
                dfaResult.setStartFlag(statePowerSet.indexOf(currentIntSet), true);
            }
            
            for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                
                
                  currChar = alphaIt.next();
                  // lege eine neue (zunächst) leere Menge an, hier kommen
                  // alle Zustände hinein die von stateId mit currChar aus erreichbar sind
                  destSet = new IntegerSet();
                
                // durchlaufe die aktuelle Menge (=Zustand) ....
                for ( Iterator<Integer> setIt = currentSet.iterator(); setIt.hasNext(); ) {
                    
                    stateId = setIt.next();
                    
                    System.out.println("Betrachte "+stateId+" aus "+currentSet);
                    
                    // besorge alle Transitionen dieses (alten) Zustands aus dem
                    // alten Automaten
                    transList = aut.getStateTransitions(stateId);
                    
                    // durchlaufe für diesen Zustand alle Buchstaben des Eingabealphabets
                    
                    
                  
                    
                  
                    
                    // betrachte nun die ausgehenden Transition dieses ZUstands im alten Aut.
                    for ( Iterator<Transition> transIt = transList.iterator(); transIt.hasNext(); ) {
                        
                        currTrans = transIt.next();
                        
                        // gab es im alten Automaten eine Transition von stateId mit currChar
                        // irgendwo anders hin, füge den jeweiligen Zielzustand in die
                        // neue Zielzustandsmenge ein
                        if (currTrans.getChar() == currChar && currTrans.getStartState()==stateId) {
                            System.out.println("passende transition "+currTrans+" für "+stateId+
                                    "aus "+currentIntSet);
                            destSet.insert(currTrans.getEndState());
                            
                        }
                        
                    }
                    
                    System.out.println(currentIntSet+" erreicht mit "+currChar+" "+destSet);
                    
                    // über den Index im Vector der Potenzmenge werden die einzelnen
                    // Menge eindeutig identifiziert, hier wird die endgültige Transition eingefügt
                 
                    
                }
                  
                     dfaResult.addTransition(statePowerSet.indexOf(currentIntSet),
                            statePowerSet.indexOf(destSet),
                            currChar);
            }
            
        }
        
        
        // abschließend bestimmen wir nun noch die neuen Endzustände des Automaten
        // dazu nehmen wir die ursprüngliche Endzustandsmenge und schneiden mit
        // jedem Element der Potenzmenge, ist der Schnitt nicht leer, ist der
        // entsprechende Zustand (==Menge) ein endzustand im neuen Automat
        // einfach gesagt, jede Zustandsmenge des Potenzautomaten die
        // den einen alten Endzustand enthält ist Endzustand.
        
        IntegerSet oldFinalSet = aut.getFinalSet();
        
        for ( Iterator<IntegerSet> it = statePowerSet.iterator(); it.hasNext(); ) {
            currentIntSet = it.next();
            currentIntSet.intersect(oldFinalSet);
            if (!currentIntSet.isEmpty()) {
                dfaResult.setFinalFlag(statePowerSet.indexOf(currentIntSet),  true);
            }
        }
        
        // TODO
        dfaResult.setName("myName");
        
        // erzeuge die noch nicht vorhandenen Pixelpositionen der Zustände
        dfaResult = generatePositions(dfaResult);
        
        // TODO
        // die Determinisierung erzeugt viel redundanten / anderweitigen
        // Unsinn (= sinnlose Zustände und Transitionen)
        // eine nachgelagerte Minimierung wäre sinnvoll
        
        return dfaResult;
    }
    
    
    
    private static FSA generatePositions(FSA aut) {
        Set<Integer> stateSet;
        
        // hier müssen die internen Zustände herhalten, da der Automat noch gar kein
        // keySet für seine Zustandspositionen hat (er wurde ja nicht von der gui erzeugt)
        stateSet = aut.getNonGuiStates();
        
        int rowCount = 1;
        int currX = POS_GEN_SIZE;
        int currY = POS_GEN_SIZE;
        
        int hopOffSet = POS_OFFSET;
        
        Point pos;
        
        for ( Iterator<Integer> it = stateSet.iterator(); it.hasNext(); ) {
            pos = new Point(currX, currY+hopOffSet);
            currX+=POS_GEN_SIZE;
            ++rowCount;
            hopOffSet*=-1;
            if (rowCount>=STATES_PER_ROW) {
                currX = POS_GEN_SIZE;
                currY+=POS_GEN_SIZE;
                rowCount = 1;
            }
            aut.setPosition(it.next(), pos);
        }
        
        return aut;
    }
    
}
