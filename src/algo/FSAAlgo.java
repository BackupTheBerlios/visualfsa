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

import datastructs.WordTree;
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
     
     */
    public static synchronized Vector<String> guessLang(FSA aut, int step) throws OutOfMemoryError {
        Vector<Character> alpha;
        Vector<String> lang;
        boolean autType;
        WordTree wordGenerator;
        String currWord;
        
        autType = aut.isDeterministic();
        alpha = aut.getAlphabet();
        
        lang = new Vector<String>();
        
        // der Wortbaum ist zwar toll, erwischt aber das leere Wort nicht...
        // soviel Zeit haben wir dann aber auch noch, dieses explizit zu testen
        
        if (aut.accepts("",  true, autType)) {
            lang.add("\\epsilon");
        }
        
        // der Wortbaum bekommt bei jedem Durchlauf den n�chsten
        // Buchstaben des Eingabealphabets als Wurzel
        
        wordGenerator = new WordTree(alpha, step);
        
        for ( Iterator<Character> it = alpha.iterator(); it.hasNext(); ) {
            // setze die Wurzel des Baumes
            wordGenerator.setRootData(it.next());
            
            currWord = wordGenerator.nextWord();
            
            while (currWord!=null) {
                // pr�fe ob der automat akzeptiert
                if (aut.accepts(currWord, true,  autType)) {
                    lang.add(currWord);
                }
                currWord = wordGenerator.nextWord();
            }
            
            // Um den Baum nicht neu erzeugen zu m�ssen, werden
            // alle Knoten als unbesucht gesetzt
            wordGenerator.resetVisited();
        }
        
        return lang;
    }
    
    
    /*
     
        Automat Minimierung Stufe 1
     
        Entfernung nicht erreichbarer Zust�nde
     
     */
    
    public static FSA removeIsolatedStates(FSA aut) {
        
        // im prinzip tiefensuche, vom startzustand nicht
        // erreichbare Zust�nde fliegen raus
        
        LinkedList<Integer> stateStack;
        Vector<Integer> visitedStates;
        
        // wir nehmen uns einen Stack f�r die noch zu bearbeitenden
        // Zust�nde, sowie einen Vector der die bereits besuchten
        // Zust�nde speichert
        
        stateStack = new LinkedList<Integer>();
        visitedStates = new Vector<Integer>();
        Integer currState;
        
        currState = aut.getStartSet().getFirst();
        
        // markiere den Startzustand als besucht
        visitedStates.add(currState);
        // pushe ihn auf den Stack
        stateStack.addLast(currState);
        
        LinkedList<Transition> currTrans;
        Integer reachedState;
        
        
        do {
            
            // Zustand vom Stack holen
            currState = stateStack.getLast();
            
            // Transitionen des aktuellen Zustands holen
            currTrans = aut.getStateTransitions(currState);
            
            // iteriere �ber die Trans.liste
            for ( Transition ct : currTrans) {
                reachedState = ct.getEndState();
                // noch nicht besucht?
                if (!visitedStates.contains(reachedState)) {
                    
                    // markiere den Zustand als besucht, pushe auf den Stack
                    visitedStates.add(reachedState);
                    stateStack.addLast(reachedState);
                    break; // verlasse for
                }
            }

            // nun testen wir, ob ein neuer Zustand auf dem Stack gelandet ist,
            // ist dies derselbe Zustand wie vor der for Schleife wurde keine
            // Transition zu einem noch nicht besuchten Zustand gefunden, der
            // betreffende Zustand wird also vom Stack entfernt
            
            if (currState==stateStack.getLast())
                stateStack.removeLast();
            
        } while (!stateStack.isEmpty());
        
        // die nun in visitedStates enthaltenden Zust�nde, sind jene
        // die man vom Startzustand aus erreichen kann
        // um uns wildes Referenzengefummel zu ersparen, legen wir
        // einfach einen neuen Automaten an
       
        FSA result = new FSA();
        
        result.setName(aut.getName()+"_reduced");
        
        for ( Integer retained : visitedStates ) {
            
            // der Zustand erh�lt die Posi des urspr�ngl. automaten
            result.setPosition(retained, aut.getPosition(retained));
            
            result.setStartFlag(retained, aut.getStartFlag(retained));
            result.setFinalFlag(retained, aut.getFinalFlag(retained));
            
            // sowie alle seine Transitionen
            currTrans = aut.getStateTransitions(retained);
            
            for ( Transition ct : currTrans) {
                result.addTransition(ct.getStartState(), ct.getEndState(), ct.getChar());
            }
            
        }
        
        
        return result;
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
//        IntegerSet currentIntSet;
        
        // eingabealphabet
        Vector<Character> alpha;
        
        alpha = aut.getAlphabet();
        
        // aktuelle Transitionsliste
        LinkedList<Transition> transList;
        Transition currTrans;
        
        // der neue Zustand (==Zustandsmenge) im neuen Automaten
        IntegerSet destSet;
        int stateId; // der aktuell untersuchte (alte) Zustand
        
        // nimm einen Zustand des neuen Automaten her... (also eine Teilmenge der Potmenge)
        for ( IntegerSet currentIntSet : statePowerSet ) {
            
            // (TODO?) IntegerSet implementiert (noch) nicht Iterator
            currentSet = currentIntSet.pureElements();
            
            // eine spezielle Teilmenge der Potenzmenge, n�mlich die leere
            // Menge, wird gesondert behandelt, sie dient gewisserma�en
            // als Fangzustand
            if (currentSet.isEmpty()) {
                // f�ge f�r alle Buchstaben die Transitionen ein die beim
                // Zustand verbleibt
                stateId = statePowerSet.indexOf(currentIntSet);
                
                for ( Character cChar : alpha ) {
                    dfaResult.addTransition(stateId,stateId, cChar);
                }
                
                continue;
            }
            
            // durchlaufe f�r diesen Zustand alle Buchstaben des Eingabealphabets
            for ( Character currChar : alpha ) {
                
                // lege eine neue (zun�chst) leere Menge an, hier kommen
                // alle Zust�nde hinein die von stateId mit currChar aus erreichbar sind
                destSet = new IntegerSet();
                
                // durchlaufe die aktuelle Menge (=Zustand) ....
                for ( Iterator<Integer> setIt = currentSet.iterator(); setIt.hasNext(); ) {
                    
                    stateId = setIt.next();
                    
                    // besorge alle Transitionen dieses (alten) Zustands aus dem
                    // alten Automaten
                    transList = aut.getStateTransitions(stateId);
                    
                    if (transList==null) continue;
                    
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
        
        // bestimme den neuen Startzustand des Automaten
        // der neue Startzustand ist jene Zustandsmenge, welche alle alten
        // Startzust�nde enth�lt, die Potenzmenge wird danach durchsucht
        // wird auch in der schleife weiter unten getan
        IntegerSet oldStartSet = aut.getStartSet();
        
        // abschlie�end bestimmen wir nun noch die neuen Endzust�nde des Automaten
        // dazu nehmen wir die urspr�ngliche Endzustandsmenge und schneiden mit
        // jedem Element der Potenzmenge, ist der Schnitt nicht leer, ist der
        // entsprechende Zustand (==Menge) ein endzustand im neuen Automat
        // einfach gesagt, jede Zustandsmenge des Potenzautomaten die
        // den einen alten Endzustand enth�lt ist Endzustand.
        
        IntegerSet oldFinalSet = aut.getFinalSet();
        
        IntegerSet myClone;
        
        for ( IntegerSet currentIntSet : statePowerSet ) {
            
            // bevor wir hier rumschneiden, erst schauen ob das nicht
            // der neue Startzustand ist
            if (currentIntSet.equals(oldStartSet)) {
                dfaResult.setStartFlag(statePowerSet.indexOf(currentIntSet), true);
            }
            
            myClone = (IntegerSet)currentIntSet.clone();
            
            myClone.intersect(oldFinalSet);
            
            if (!myClone.isEmpty()) {
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
