/*
 *
 * Transition.java
 *
 * Repr�sentiert eine einzelne Transition bestehend aus Start- und Endzustand
 * sowie dem -Transitionszeichen-
 *
*/

package datastructs;

public class Transition
{
    
    private int startState, endState;
    private char ch;


    public Transition(int ss, int es, char c)
    {
	// da der Automat selbst das �berpr�fen von Zeichen und Zust�nden vornimmt
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


    /* equals ist n�tig damit diverse .contains Methoden der java api funzen */
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


    /* f�r die toString Methode von FSA.java */
    public String toString()
    {
	return ("("+ startState + " -> " + endState + " : " + ch + ")"); 
    }

}
