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

package src.datastructs;

public class Transition
{
    
    private int startState, endState;
    private char ch;


    public Transition(int ss, int es, char c)
    {
	// da der Automat selbst das Überprüfen von Zeichen und Zuständen vornimmt
        // kann das mas hier so machen 
	ch = c;
	startState = ss;
	endState = es;
    }

    public int getStartState()
    {
	return startState;
    }
    
    public int getEndState()
    {
	return endState;
    }


    public char getChar()
    {
	return ch;
    }


    /* equals ist nötig damit diverse .contains Methoden der java api funzen */
    public boolean equals(Object o)
    {
	Transition t;
	if (o instanceof Transition) {
	    t = (Transition)o;
	    return (t.ch==ch && t.startState==startState && t.endState==endState);
	}
	else {
	    return false;
	}
    }


    /* für die toString Methode von FSA.java */
    public String toString()
    {
	return ("("+ startState + " -> " + endState + " : " + ch + ")"); 
    }

}
