/*
  TransitionData.java
  speichert die relevanten Infos einer graphischen Transition
*/

package src.gui;

import java.util.Vector;

public class TransitionData {

    private JState endState;
    private Vector<Character> chars;

    public TransitionData(JState _endState, Vector<Character> _chars) {
	endState = _endState;
	chars = _chars;
    }

    public JState getEndState() {
	return endState;
    }

    public Vector<Character> getChars() {
	return chars;
    }

}
