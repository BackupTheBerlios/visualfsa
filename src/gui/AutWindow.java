
package src.gui;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ListIterator;
import java.util.LinkedList;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

public class AutWindow extends JLayeredPane {

    public static final int STATE_LAYER = 1;
    public static final int STATE_SIZE = 30;
    public static final int STATE_HALFSIZE = 15;
	
    private JState markedState;
    private JState edgeState;
    private boolean drawingEdge;

    public AutWindow() {
	super();
	setLayout(null);
	setDoubleBuffered(true);
	setOpaque(true);
	setBackground(Color.WHITE);
	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }


    public void addState(Point position) {
    }

    public void addState(Point position, String name) {
	JState newState;
		
	newState = new JState(name, this);
	newState.setBounds(position.x, position.y, STATE_SIZE, STATE_SIZE);
			
	add(newState);
	setLayer(newState, STATE_LAYER);
    }


    // füge bei Doppelklick einen neuen Zustand ein
    public void processMouseEvent(MouseEvent ev) {
	if (ev.getClickCount()==2 && ev.getButton()==MouseEvent.BUTTON1
	    && ev.getID()==MouseEvent.MOUSE_PRESSED) {
	    // TODO naming
	    addState(ev.getPoint(), ""+getComponentCountInLayer(STATE_LAYER));
	}
    }



    // jedem Zustand wird seine Transitionsliste entnommen und die entspr.
    // Transitionen dann gezeichnet
    public void paintComponent(Graphics g) {
	int numstate;
	JState current, endState;
	Object[] states;
	LinkedList<JState> currentList;
	ListIterator<JState> currentTarget;
	Point startLoc, endLoc;

	numstate = getComponentCountInLayer(STATE_LAYER);
	states = getComponentsInLayer(STATE_LAYER);

	for (int i = 0 ; i < numstate ; i++ ) {
	    current = (JState)states[i];
	    drawTransitions(current, g);
	    
	}
    }


    protected void drawTransitions(JState startState, Graphics g) {
	Point startLoc, endLoc;
	LinkedList<JState> transList;
	ListIterator<JState> current;
	JState endState;

	Point mp = new Point();
	int xsize,ysize;

	startLoc = startState.getLocation();
	startLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
	
	transList = startState.getTransList();
	current = transList.listIterator();

	while (current.hasNext()) {
	    endState = current.next();
	    endLoc = endState.getLocation();
	    endLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
	    
	    xsize = Math.abs(endLoc.x-startLoc.x);
	    ysize = Math.abs(endLoc.y-startLoc.y);

	    xsize = xsize / 2;
	    ysize = ysize / 2;

	    if (endLoc.x <= startLoc.x) {
		mp.x = endLoc.x + xsize;
	    }
	    else {
		mp.x = startLoc.x + xsize;
	    }

	    if (endLoc.y <= startLoc.y) {
		mp.y = endLoc.y + ysize;
	    }
	    else {
		mp.y = startLoc.y + ysize;
	    }
		
	    g.drawString("muh", mp.x, mp.y-4); 

	    g.drawLine(startLoc.x,startLoc.y,endLoc.x,endLoc.y);
	}
	
    }


    // ein Zustand ruft diese Methode auf um anzuzeigen das von ihm
    // aus das zeichnen einer neuen Transition gestartet wurde
    // wir merken uns diesen Zustand
    // ein markierter Zustand verliert diese, Markieren ist während
    // des Zeichnens generell deaktiviert
    public void startEdge(JState start) {
	drawingEdge = true;
		
	if (markedState!=null && markedState!=start) {
	    markedState.setMode(JState.MODE_NOMARK);
	    markedState = null;
	}
		
	edgeState = start;
    }

    public void endEdge(JState end) {
	Vector<Character> chars;

	chars = editTransChars("");

	if (end==edgeState) {
	    System.out.println("Schlingenfoo not implemented yet :-)");
	}
	else {
	    edgeState.insertTransition(end);
	}

	edgeState.setMode(JState.MODE_MARK);
	markedState = edgeState;

	drawingEdge = false;
    }


    private Vector<Character> editTransChars(String initial) {
	String result, ctok;
	StringTokenizer strTok;
	Vector<Character> chVec = new Vector<Character>();
	Character currChar;

	result = JOptionPane.showInputDialog(this, "Trans.-zeichen (durch Kommata getrennt)");

	if (result==null) return null;

	strTok = new StringTokenizer(result,",;");

	while (strTok.hasMoreTokens()) {
	    ctok = strTok.nextToken();
	    if (ctok.length()==1) {
		if (Character.isLetterOrDigit(ctok.charAt(0))) {
		    currChar = new Character(ctok.charAt(0));
		    if (!chVec.contains(currChar))
			chVec.add(currChar);
		}
	    }
	}
	System.out.println(chVec);
	return chVec;
     }


    public boolean isDrawingEdge() {
	return drawingEdge;
    }


    public JState getMarkedState() {
	return markedState;
    }
	
    public void setMarkedState(JState _marked) {
	markedState =  _marked;
    }

}
