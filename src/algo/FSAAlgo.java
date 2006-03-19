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

package algo;

import java.awt.Point;
import java.util.Vector;
import java.util.LinkedList;
import java.util.Set;
import javax.swing.JDialog;

import datastructs.WordTree;
import datastructs.Transition;
import datastructs.IntegerSet;
import datastructs.FSA;

import gui.dialogs.BusyDialog;

import threads.GenericAlgoThread;

public class FSAAlgo {
    
    
    // gittergr��e bei der automatischen Positionsgenerierung f�r die Zust�nde
    private static final int POS_GEN_SIZE = 90;
    private static final int POS_OFFSET = 8;
    
    // wieviel Zust�nde in eine Reihe
    private static final int STATES_PER_ROW = 6;
    
    /*
        guessLang versucht die Sprache die ein Automat erkennt zu identifzieren.
        Dabei werden zun�chst f�r jede Wortl�nge, beginnend bei 1, bis zu einer
        fixen Grenze mit einem Baum alle W�rter �ber dem Alphabet erzeugt.
        Die Baumerzeugung bricht dabei automatisch aus Sicherheitsgr�nden
        die Erzeugung an sich ab, sowie eine gewisse Speicherbedarfgrenze
        �berschritten wurde. Die eigentliche Erzeugung der W�rter entspricht
        dann einem Durchlauf durch den Baum, beginnend bei der Wurzel bis zum
        ersten nicht als besucht markierten Knoten.
        Die Spracherkennung testet nun jedes Wort im Automat auf Akzeptanz und
        bricht das Verfahren ab, sowie eine gewisse Anzahl an W�rter erkannt wurde
        (wiederrum aus Speicherplatzgr�nden)
     
        Um das Problem zu umgehen, f�r jeden einzelnen Algorithmus ein eigenes Threadobjekt
        im cvs-tree zu haben, hier mal der vielleicht st�mperhafte versuch das Ganze
        etwas zu generalisieren. Der jeweilige Algorithmus �bergibt dabei einem einzigen
        'Ausf�hrthread' eine Instanz des GenericAlgorithm Interfaces, welches die eigentlichen
        Methoden enth�lt die zur Ausf�hrung der Arbeit wichtig sind.
     
     */
    public static GenericAlgorithm guessLang(final FSA aut) {
        
        GenericAlgorithm langAlg;
        
        langAlg = new GenericAlgorithm() {
            
            private Vector<Character> alpha;
            private boolean autType;
            private WordTree wordGenerator;
            private String currWord;
            private Vector<String> lang;
            private StringBuffer resultText;
            
            public void runAlgorithm() {
                
                autType = aut.isDeterministic();
                alpha = aut.getAlphabet();
                
                boolean exitFlag = false;
                
                lang = new Vector<String>();
                
                // der Wortbaum ist zwar toll, erwischt aber das leere Wort nicht...
                // soviel Zeit haben wir dann aber auch noch, dieses explizit zu testen
                
                if (aut.accepts("",  true, autType)) {
                    lang.add("\\epsilon");
                }
                
                // pr�fe zun�chst die W�rter der L�nge n, dann die mit L�nge n+1
                for (int wordLength = 1 ; wordLength < 11 ; wordLength++) {
                    
                    wordGenerator = new WordTree(alpha, wordLength);
                    
                    for ( Character c : alpha ) {
                        // setze die Wurzel des Baumes
                        wordGenerator.setRootData(c);
                        
                        currWord = wordGenerator.nextWord();
                        
                        while (currWord!=null && !exitFlag) {
                            // pr�fe ob der automat akzeptiert
                            if (aut.accepts(currWord, true,  autType)) {
                                if (!lang.contains(currWord)) {
                                    lang.add(currWord);
                                }
                                
                                if (lang.size() > 1000) {
                                    exitFlag = true;
                                }
                            }
                            currWord = wordGenerator.nextWord();
                        }
                        
                        // Um den Baum nicht neu erzeugen zu m�ssen, werden
                        // alle Knoten als unbesucht gesetzt
                        if (exitFlag) break;
                        wordGenerator.resetVisited();
                    }
                    
                    wordGenerator = null; System.gc();
                    if (exitFlag) break;
                }
                
                
                resultText = new StringBuffer("L = { ");
                
                for ( String s : lang ) {
                    resultText.append(s+", \n");
                }
                
                resultText.append(" }");
                lang = null; System.gc();
            }
            
            public Object getResult() { return resultText; }
            
        };
        
        return langAlg;
    }
    
    
    /*
    
        Vereinigung zweier Automaten
     
        Einfaches Spiel diesmal, die Automaten werden einfach zusammengelegt
        Wichtig ist nur, die Zust�nde des einen Automaten entsprechend um-
        zubennen...b�ses zahlengefummel das
     
     */
    public static FSA autUnion(FSA autA, FSA autB) {
        FSA resultFSA, otherClone;
        int stateMaxA = -1;
        int stateMaxB = -1;
        int offset;
        
        // bevor wir hier Referenzen kaputt haun, lieber n neuen Automaten 
        // als R�ckgabewert erstellen, sieht auch prinzipiell sauberer aus

        // der Automat mit der absolut h�heren Zustandsnr wird geclonet, ihm
        // werden dann, mit offset versehen die Zust�nde des anderen hinzugef�gt
        // um allerhand Fallunterscheidungen zu sparen, wird der jeweils andere
        // automat auch geclonet
        
        for ( Integer key : autB.getStates() ) { stateMaxB = Math.max(stateMaxB, key); }
        for ( Integer key : autA.getStates() ) { stateMaxA = Math.max(stateMaxA, key); }
        
        if (stateMaxA>=stateMaxB) {
            resultFSA = (FSA)autA.clone();
            otherClone = (FSA)autB.clone();
            offset = stateMaxA;
        }
        else {
            resultFSA = (FSA)autB.clone();
            otherClone = (FSA)autA.clone();
            offset = stateMaxB;
        }
        
        offset++;
   
        for (Integer key : otherClone.getStates() ) {
            for ( Transition t : otherClone.getStateTransitions(key) ) {
                resultFSA.addTransition(t.getStartState()+offset,
                    t.getEndState()+offset, t.getChar() );
            }
        }
        
        for (Integer state : otherClone.getStates() ) { 
            resultFSA.setStartFlag(state+offset, otherClone.isStartState(state));
            resultFSA.setFinalFlag(state+offset, otherClone.isFinalState(state));
        }
        
        resultFSA.setName(autA.getName()+"_union_"+autB.getName());
       
        // da die Positionen mit Sicherheit eh hin�ber sind... erzeuge einfach neue
        resultFSA = FSAAlgo.generatePositions(resultFSA);
        
        return resultFSA;
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
            
            // zustand hat keine transition, soll vorkommen
            // umgeht auch einen Nullpointer, f�r den Fall
            // das der Automat DFA ist, weil nur einen zustand
            // hat, dieser ist start und endzustand, aber ohne transition
            
            if (currTrans!=null) {
                
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
            
            result.setStartFlag(retained, aut.isStartState(retained));
            result.setFinalFlag(retained, aut.isFinalState(retained));
            
            // sowie alle seine Transitionen
            currTrans = aut.getStateTransitions(retained);
            
            if (currTrans!=null) {
                for ( Transition ct : currTrans) {
                    result.addTransition(ct.getStartState(), ct.getEndState(), ct.getChar());
                }
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
    public static GenericAlgorithm determ(final FSA aut) {
        GenericAlgorithm determAlgo;
        
        determAlgo = new GenericAlgorithm() {
            
            private FSA dfaResult;
            
            
            public void runAlgorithm() {
                
                dfaResult = new FSA();
                
                /* erstelle die Zustandsmenge des Automaten */
                Set<Integer> tempSet;
                IntegerSet stateSet;
                
                stateSet = new IntegerSet();
                
                for ( Integer i : aut.getStates() ) {
                    stateSet.insert(i);
                }
                
                // Potenzmenge davon berechnen
                Vector<IntegerSet> statePowerSet;
                
                //waitDlg.setCurrentStep("calculating powerset");
                statePowerSet = stateSet.getPowerset();
                
                // aktuelle teilmenge der Potenzmenge
                Vector<Integer> currentSet;
                
                // eingabealphabet
                Vector<Character> alpha;
                
                alpha = aut.getAlphabet();
                
                // aktuelle Transitionsliste
                LinkedList<Transition> transList;
                
                // der neue Zustand (==Zustandsmenge) im neuen Automaten
                IntegerSet destSet;
                int stateId;
                
                int progress,count = -1,pSetSize = statePowerSet.size();
                
                // nimm einen Zustand des neuen Automaten her... (also eine Teilmenge der Potmenge)
                for ( IntegerSet currentIntSet : statePowerSet ) {
                    
                    count++;
                    
                    progress = (count*100)/pSetSize;
                    
                    //  waitDlg.setProgress(progress);
                    //  waitDlg.setCurrentStep("checking set "+count+" of "+pSetSize);
                    
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
                        for ( Integer stId : currentSet ) {
                            
                            // besorge alle Transitionen dieses (alten) Zustands aus dem
                            // alten Automaten
                            transList = aut.getStateTransitions(stId);
                            
                            if (transList==null) continue;
                            
                            // betrachte nun die ausgehenden Transition dieses ZUstands im alten Aut.
                            for ( Transition t : transList ) {
                                
                                // gab es im alten Automaten eine Transition von stateId mit currChar
                                // irgendwo anders hin, f�ge den jeweiligen Zielzustand in die
                                // neue Zielzustandsmenge ein
                                if (t.getChar() == currChar && t.getStartState()==stId) {
                                    destSet.insert(t.getEndState());
                                    
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
                
                // ein wenig Minimierung ohne den User zu fragen ;)
                // k�nnte man als optional einbauen
                dfaResult = removeIsolatedStates(dfaResult);
                
            } // runAlgo
            
            public Object getResult() { return dfaResult; }
            
            
        }; // new GenericAlgorithm
        
        return determAlgo;
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
        
        for ( Integer i : stateSet ) {
            pos = new Point(currX, currY+hopOffSet);
            currX+=POS_GEN_SIZE;
            ++rowCount;
            hopOffSet*=-1;
            if (rowCount>=STATES_PER_ROW) {
                currX = POS_GEN_SIZE;
                currY+=POS_GEN_SIZE;
                rowCount = 1;
            }
            aut.setPosition(i, pos);
        }
        
        return aut;
    }
    
}
