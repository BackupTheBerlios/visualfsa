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
import java.util.ListIterator;
import java.util.LinkedList;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class JState extends JComponent {

    private boolean startState;
    private boolean finalState;
    private boolean marked;
    private int num;
    private AutWindow parent;
    private Color currentColor;
    private LinkedList<TransitionData> outgoingTransList;

    public static final int MODE_MARK = 1;
    public static final int MODE_NOMARK = 2;
    public static final int MODE_EDGE = 3;

    private Point initial;
    private boolean transDrawn;
    
    public JState(int _num, AutWindow _parent) {
	super();
	setDoubleBuffered(true);
	setOpaque(true);
	num = _num;
	initial = new Point();
	outgoingTransList = new LinkedList<TransitionData>();
	currentColor = VFSAGUI.options.getBackCol();
	parent = _parent;
	enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK);
    }


    public void paintComponent(Graphics g) {
	int xPos;
	String name = ""+num;

	// Inhalt
	g.setColor(currentColor);
	g.fillRect(0,0,getWidth()-1, getHeight()-1);
	
	// Rand
	if (!finalState) {
	    g.setColor(VFSAGUI.options.getLineCol());
	}
	else {
	    g.setColor(Color.RED);
	}
	g.drawRect(0,0,getWidth()-1, getHeight()-1);
	
	// Startzust�nde werden mit einem kleinen schwarzen dreieck in der oberen Ecke kenntlich gemacht
	if (startState) {
	    Polygon edge = new Polygon();
	    edge.addPoint(0,0);
	    edge.addPoint(8,0);
	    edge.addPoint(0,8);
	    g.fillPolygon(edge);
	}

	xPos = SwingUtilities.computeStringWidth(g.getFontMetrics(),name);
	
        g.setColor(VFSAGUI.options.getLineCol());
	g.drawString(name,(AutWindow.STATE_HALFSIZE)-(xPos/2),AutWindow.STATE_HALFSIZE+5);
    }

	
    protected void processMouseEvent(MouseEvent event) {
	if (event.getID()==MouseEvent.MOUSE_PRESSED) {
	    
	    // klickt der User doppelt auf einen Zustand
	    // wird der Transitionszeichenmodus aktiviert
	    // er endet solange bis der User eine Transition
	    // einzeichnet oder das Zeichnen abbricht
	    // w�hrend des Zeichnens ist der Ausgangszustand
	    // rot markiert

	    initial = event.getPoint();

	    if (parent.isDrawingEdge()) {
		parent.endEdge(this);
		return;
	    }
	    else if (!(parent.isDrawingEdge()) && event.getClickCount()==2) {
		setMode(MODE_EDGE);
		parent.startEdge(this);
		return;
	    }
	    
	    // W�hrend des Kantenzeichnens ist das Markieren nicht zul�ssig
	    if (parent.isDrawingEdge()) return;


	    // popup men�
	    if (event.getButton()==MouseEvent.BUTTON3) {
		parent.showPopup(this);
	    }
	    markMe();
	}	
    }


    // das Zeichenfenster veranlasst die Entfernung eines Zustands, deshalb
    // entfernen alle Zust�nde ihre Transitionen die zu diesem zustand f�hrten
    public void removeTransTo(JState removedState) {
	ListIterator<TransitionData> listIt;
	LinkedList<TransitionData> temp = new LinkedList<TransitionData>();
	TransitionData current;

	listIt = outgoingTransList.listIterator();

	// entfernen und gleichzeitig iterieren klappt nicht, deshalb einfach die
	// bleibenden rausfischen

	while (listIt.hasNext()) {
	    current = listIt.next();
	    if (current.getEndState()!=removedState) temp.add(current);
	}

	outgoingTransList.retainAll(temp);
    }


	
    // wenn wir nicht markiert sind, schaue beim Parent
    // Window wessen Markierung wir entfernen m�ssen
    // setze gleichzeitig unsere Markierung
    private void markMe() {
	if (!marked) {
	    setMode(MODE_MARK);
	    if (parent.getMarkedState()!=null) {
		parent.getMarkedState().setMode(MODE_NOMARK);
	    }
	    parent.setMarkedState(this);
	}
    }

    protected void processMouseMotionEvent(MouseEvent event) {
	Point newPos, currentPos;
	parent = (AutWindow)getParent();
	
	// dragging disabled solange wir eine Transition zeichnen
	if (parent.isDrawingEdge()) return;
	
	if (event.getID()==MouseEvent.MOUSE_DRAGGED) {
	    currentPos = this.getLocation();
	    newPos = new Point();
	    newPos.x = currentPos.x + event.getPoint().x - initial.x;
	    newPos.y = currentPos.y + event.getPoint().y - initial.y;
	    setLocation(newPos);
	    parent.repaint(); // arghs!
	}
    }
    


    // R�ckgabewert gibt an ob die Transition wirklich neu war
    public boolean insertTransition(JState endState, Vector<Character> autoTrans) {
	ListIterator<TransitionData> tList;
	ListIterator<Character> charIt;
	Character currChar;
	Vector<Character> chars, existing;
	TransitionData tcurr, newTrans;
	String init="";

	// pr�fe ob wir bereits eine Transition zu diesem Zustand besitzen

	tList = outgoingTransList.listIterator();

	while (tList.hasNext()) {
	    tcurr = (TransitionData)tList.next();

	    // wir haben bereits eine Transition zu diesem Zustand
	    if (endState==tcurr.getEndState()) {
		existing = tcurr.getChars();
		
		// erstelle aus den bestehenden Zeichen den initial String f�r
		// das Inputfenster
		
		charIt = existing.listIterator();
		
		while (charIt.hasNext()) {
		    init = init + ((Character)charIt.next()).toString() + ",";
		}
		
		if (autoTrans==null) {
		    chars = parent.editTransChars(init);
		}
		else {
		    chars = autoTrans;
		}
		
		// dr�ckt der User Cancel bzw gibt ung�ltige Zeichen ein �ndert
		// sich die urspr�ngliche Transition nicht
		if (chars!=null) {
		    
		    // f�ge die neuen Buchstaben hinzu
		    charIt = chars.listIterator();
		    while (charIt.hasNext()) {
			currChar = (Character)charIt.next();
			if (!existing.contains(currChar))
			    existing.add(currChar);
		    }
		}
		parent.repaint();
		return false;
	    }
	}

	// hier angekommen war der Zustand noch nicht enthalten
	if (autoTrans==null) {
	    chars = parent.editTransChars("");
	}
	else {
	    chars = autoTrans;
	}

	if (chars!=null) {
	    newTrans = new TransitionData(endState, chars);
	    outgoingTransList.add(newTrans);
	}

	parent.repaint(); // besser einzelnd neu zeichnen?
	return true;
    }


    // clone?
    protected LinkedList<TransitionData> getTransList() {
	return outgoingTransList;
    }
	
    // pr�ft ob der Zustand eine Transition zu dest hat
    public boolean hasTransTo(JState dest) {
        ListIterator<TransitionData> lIt;
        TransitionData current;
        
        lIt = outgoingTransList.listIterator();
        
        while (lIt.hasNext()) {
            current = lIt.next();
            if (current.getEndState()==dest) return true;
        }
        
        return false;
    }
    
    // get/set transDrawn
    // transDrawn gibt an ob im letzten Durchlauf von drawTransitions in AutWindow
    // die Transitionen dieses Zustands gezeichnet wurden, dadurch wird verhindert
    // das bei beidseitigen Transitionen beide Transitionen verschoben werden
    
    public boolean getTransDrawn() {
        return transDrawn;
    }
		
    public void setTransDrawn(boolean td) {
        transDrawn = td;
    }
    
    
    // (de)Markiere den Knoten als ab/ausgew�hlt
    public void setMode(int flag) {
	switch(flag) {
	    
	case MODE_MARK:
	    currentColor = VFSAGUI.options.getMarkCol();
	    marked = true;
	    repaint();
	    break;
	case MODE_NOMARK:
	    currentColor = VFSAGUI.options.getBackCol();
	    marked = false;
	    repaint();
	    break;
	case MODE_EDGE:
	    currentColor= VFSAGUI.options.getTransCol();
	    repaint();
	    
	    
	}
    }

    public void setNumber(int _num) {
	num = _num;
    }

    public int getNumber() {
	return num;
    }
    

    public void setStartState(boolean flag) {
	startState = flag;
	repaint();
    }
    
    public boolean isStartState() {
	return startState;
    }
    
    public void setFinalState(boolean flag) {
	finalState = flag;
	repaint();
    }

    public boolean isFinalState() {
	return finalState;
    }
    
    public boolean isMarked() {
	return marked;
    }
    
}
