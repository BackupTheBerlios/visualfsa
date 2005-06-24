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
    
    
    // gittergr��e bei der automatischen Positionsgenerierung f�r die Zust�nde
    private static final int POS_GEN_SIZE = 90;
    private static final int POS_OFFSET = 8;
    
    // wieviel Zust�nde in eine Reihe
    private static final int STATES_PER_ROW = 6;
    
    /*
        guessLang versucht die Sprache die ein Automat erkennt zu identifzieren,
        dabei werden aus dem Eingabealphabet alle W�rter gebildet deren L�nge
        kleiner 'step' ist (da die Sprachen potentiell unendlich sind (sein k�nnen))
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
            Kombinationen bilden k�nnen, anfangs wird jeder einzelne Buchstabe
            des Eingabealphabets dort hineingetan
         */
        for ( Iterator<Character> it = alpha.iterator(); it.hasNext(); ) {
            words.add(it.next().toString());
        }
        
        for (int i = 0 ; i < step ; i++ ) {
            
            /* teste die aktuelle Wortliste */
            
            for ( Iterator<String> strIt = words.iterator(); strIt.hasNext(); ) {
                curr = strIt.next();
                
                /* da der Automat w�hrend des Sprachtests nicht ver�ndert
                   wird kann der in FSA.java eingebaute Determinismustest
                   �bersprungen werden
                 */
                
                if (aut.accepts(curr, true, autType)) {
                    lang.add(curr);
                }
            }
            
            newWords = new Vector<String>();
            
            /* hinter jedes Wort der aktuellen Wortliste wird jeder Buchstabe
               des Eingabealphabets geh�ngt, die neu enstandenen W�rter ersetzen
               die alten wodurch nie alle W�rter im Speicher gehalten werden m�ssen
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
    
        �quivalenter DFA:
    
        DetAut = (A,Pot(S),{si},delta',F')
    
        mit delta'(Q,a) = { s' | es ex. s aus Q ^ (s,a,s') ist in delta }
        F' = { Q aus Pot(S) | Q n F != emptySet }
    
        Die neuen Endzust�nde des entstehenden Automaten sind also all jene
        Teilmengen der Potenzmenge seiner Zust�nde in denen sich alte
        Endzust�nde befinden. Der neue Startzustand ist die Menge die nur
        den alten Startzustand enth�lt.
    
        Der Zielzustand f�r einen Zustand (Teil der Potenzmenge) und ein Zeichen
        stellt sich als die Menge aller Zust�nde dar, die man im alten NFA
        mit diesem Zeichen von den einzelnen Zust�nde erreicht h�tte.
    
        (wirre Erkl�rung, noch viel wirrerer Code) :-)
    
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
            
            // eine spezielle Teilmenge der Potenzmenge, n�mlich die leere
            // Menge, wird gesondert behandelt, sie dient gewisserma�en
            // als Fangzustand
            if (currentSet.isEmpty()) {
                // f�ge f�r alle Buchstaben die Transitionen ein die beim
                // Zustand verbleibt
                stateId = statePowerSet.indexOf(currentIntSet);
                
                for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                    dfaResult.addTransition(stateId,stateId, alphaIt.next());
                }
            }
            
            // identifiziere die einelementige Menge die den alten Startzustand enth�lt
            // Annahme ist nat�rlich das es sich um DFAs handelt, die nur einen Startzustand
            // haben d�rfen
            if (currentIntSet.cardinality()==1 && aut.isStartState(currentIntSet.getFirst())) {
                dfaResult.setStartFlag(statePowerSet.indexOf(currentIntSet), true);
            }
            
            // durchlaufe f�r diesen Zustand alle Buchstaben des Eingabealphabets
            for ( Iterator<Character> alphaIt = alpha.iterator(); alphaIt.hasNext(); ) {
                
                currChar = alphaIt.next();
                // lege eine neue (zun�chst) leere Menge an, hier kommen
                // alle Zust�nde hinein die von stateId mit currChar aus erreichbar sind
                destSet = new IntegerSet();
                
                // durchlaufe die aktuelle Menge (=Zustand) ....
                for ( Iterator<Integer> setIt = currentSet.iterator(); setIt.hasNext(); ) {
                    
                    stateId = setIt.next();
                    
                    // besorge alle Transitionen dieses (alten) Zustands aus dem
                    // alten Automaten
                    transList = aut.getStateTransitions(stateId);
                    
                    // betrachte nun die ausgehenden Transition dieses ZUstands im alten Aut.
                    for ( Iterator<Transition> transIt = transList.iterator(); transIt.hasNext(); ) {
                        
                        currTrans = transIt.next();
                        
                        // gab es im alten Automaten eine Transition von stateId mit currChar
                        // irgendwo anders hin, f�ge den jeweiligen Zielzustand in die
                        // neue Zielzustandsmenge ein
                        if (currTrans.getChar() == currChar && currTrans.getStartState()==stateId) {
                            destSet.insert(currTrans.getEndState());
                            
                        }
                        
                    }
                    
                    // �ber den Index im Vector der Potenzmenge werden die einzelnen
                    // Menge eindeutig identifiziert, hier wird die endg�ltige Transition eingef�gt
                    
                }
                
                dfaResult.addTransition(statePowerSet.indexOf(currentIntSet),
                        statePowerSet.indexOf(destSet),
                        currChar);
            }
            
        }
        
        
        // abschlie�end bestimmen wir nun noch die neuen Endzust�nde des Automaten
        // dazu nehmen wir die urspr�ngliche Endzustandsmenge und schneiden mit
        // jedem Element der Potenzmenge, ist der Schnitt nicht leer, ist der
        // entsprechende Zustand (==Menge) ein endzustand im neuen Automat
        // einfach gesagt, jede Zustandsmenge des Potenzautomaten die
        // den einen alten Endzustand enth�lt ist Endzustand.
        
        IntegerSet oldFinalSet = aut.getFinalSet();
        
        for ( Iterator<IntegerSet> it = statePowerSet.iterator(); it.hasNext(); ) {
            currentIntSet = it.next();
            currentIntSet.intersect(oldFinalSet);
            if (!currentIntSet.isEmpty()) {
                dfaResult.setFinalFlag(statePowerSet.indexOf(currentIntSet),  true);
            }
        }
        
        // erzeuge die noch nicht vorhandenen Pixelpositionen der Zust�nde
        dfaResult = generatePositions(dfaResult);
        
        // TODO
        // die Determinisierung erzeugt viel redundanten / anderweitigen
        // Unsinn (= sinnlose Zust�nde und Transitionen)
        // eine nachgelagerte Minimierung w�re sinnvoll
        
        return dfaResult;
    }
    
    
    
    private static FSA generatePositions(FSA aut) {
        Set<Integer> stateSet;
        
        // hier m�ssen die internen Zust�nde herhalten, da der Automat noch gar kein
        // keySet f�r seine Zustandspositionen hat (er wurde ja nicht von der gui erzeugt)
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
