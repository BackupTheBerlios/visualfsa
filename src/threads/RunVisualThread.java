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

package threads;

import java.util.Arrays;
import java.util.Vector;
import java.util.Iterator;

import java.awt.Component;

import datastructs.FSA;
import datastructs.IntegerSet;
import gui.runvis.WordAnimation;
import gui.runvis.RunVisual;
import gui.AutWindowAnimator;
import gui.JState;

public class RunVisualThread extends Thread {
    
    private WordAnimation wordAnimator;
    private RunVisual mainClass;
    private AutWindowAnimator autAnimation;
    private FSA myAut;
    private Vector<IntegerSet> stateLog;
    private String word;
    
    public RunVisualThread(WordAnimation _wordAni, RunVisual _runVisual, AutWindowAnimator _autAni,
            FSA aut, Vector<IntegerSet> log, String _word) {
        wordAnimator = _wordAni;
        mainClass = _runVisual;
        autAnimation = _autAni;
        myAut = aut;
        stateLog = log;
        word = _word;
        
        wordAnimator.setWord(word);
    }
    
    public void run() {
        
        int wordLength = wordAnimator.getMaxStep();
        
        wordAnimator.setAnimating(true);
        
        mainClass.insertLog("Wort: "+word);
        mainClass.insertLog("Startzustand: "+stateLog.get(0)+"\n");
        
        Component[] states;
        
        states = autAnimation.getComponentsInLayer(AutWindowAnimator.STATE_LAYER);
        
        Vector<Integer> currentState;
        
        // sortiere die Zustände nach ihrer Nummer
        Arrays.sort(states);
                
        for ( int i = 0 ; i < wordLength ; i++ ) {
            
            
            mainClass.insertLog("Automat liest: "+word.charAt(i));
            
            wordAnimator.animateStep();
    
                    currentState = stateLog.get(i).pureElements();
            markStates(states, currentState, true);
            mainClass.insertLog("Zustand ist: "+currentState);
            
            // markiere die aktuellen Zustände
            
            try {
                this.sleep(1000);
            } catch (Exception e) {
                
            }
            
            // markierungen entfernen
            markStates(states, currentState, false);
    
        }
        
        wordAnimator.setAnimating(false);
        wordAnimator.repaint();
    }
    
    
    private void markStates(Component[] states, Vector<Integer> which, boolean mark) {
        JState curr;
        
        for ( Iterator<Integer> it = which.iterator(); it.hasNext(); ) {
            curr = (JState)states[it.next()];
            if (mark) {
                curr.setMode(JState.MODE_MARK);
            } else {
                curr.setMode(JState.MODE_NOMARK);
            }
        }
    }
    
    
}
