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

package gui;

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
    
    public void setChars(Vector<Character> _ch) {
        chars = _ch;
    }
    
    public String toString() {
        return chars.toString()+" -> "+endState.getNumber();
    }

}
