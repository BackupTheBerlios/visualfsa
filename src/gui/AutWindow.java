
package src.gui;

import src.datastructs.FSA;

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

    private static final int MIN_STATE_DISTANCE = 55;
	
    private JState markedState;
    private JState edgeState;
    private boolean drawingEdge;

    private VFSAGUI topLevel;
    private StatePopup statePopup;

    public AutWindow(VFSAGUI _topLevel) {
	super();
	topLevel = _topLevel;
	setLayout(null);
	setDoubleBuffered(true);
	setOpaque(true);
	setBackground(Color.WHITE);
	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	statePopup = new StatePopup();
    }


    public void addState(Point position) {
	JState newState;
		
	newState = new JState(getComponentCountInLayer(STATE_LAYER), this);
	newState.setBounds(position.x, position.y, STATE_SIZE, STATE_SIZE);
			
	add(newState);
	setLayer(newState, STATE_LAYER);
    }


    // entferne den Zustand which
    public void removeState(JState which) {
	JState curr;
	Object[] states;

	// um nur die Zustände ausser diesem zu erwischen verschiebe ihn kurzzeitig in
	// einen anderen Layer
	setLayer(which, STATE_LAYER+1);
	states = getComponentsInLayer(STATE_LAYER);

	// jeder andere Zustand entfernt nun seine ausgehenden Transitionen zu diesem
	// Zustand
	for (int i = 0 ; i < states.length ; i++ ) {
	    curr = (JState)states[i];
	    curr.removeTransTo(which);
	    if (curr.getNumber()>which.getNumber()) curr.setNumber(curr.getNumber()-1);
	}

	if (markedState==which)
	    markedState = null;

	remove(which);
	which = null;
	repaint();
    }



    // füge bei Doppelklick einen neuen Zustand ein
    public void processMouseEvent(MouseEvent ev) {
	System.out.println(toFSA());
	if (ev.getClickCount()==2 && ev.getButton()==MouseEvent.BUTTON1
	    && ev.getID()==MouseEvent.MOUSE_PRESSED) {
	    addState(ev.getPoint());
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

    public void showPopup(JState who) {
	if (!statePopup.isVisible()) {
	    statePopup.handlePopupEvent(who);	
	}
    }


    protected void drawTransitions(JState startState, Graphics g) {
	Point startLoc, endLoc;
	LinkedList<TransitionData> transList;
	ListIterator<TransitionData> current;
	JState endState;
	TransitionData currTrans;

	Point mp = new Point(), endPoint;
	int xsize,ysize;

	// dreiecksseiten
	double ankath, hypo, gegenkath;
	double winkel, cosine, sine;
	int schnittx, schnitty;

	startLoc = startState.getLocation();
	startLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
	
	transList = startState.getTransList();
	current = transList.listIterator();

	while (current.hasNext()) {
	    currTrans = current.next();
	    endState = currTrans.getEndState();
	    // aktueller Zielzustand, linke obere Ecke, Mittelpunkt berechnen
	    endLoc = endState.getLocation();
	    endLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
	    

	    // für die Beschriftung mit den Transitionszeichen die Mitte der Linie bestimmen
	    xsize = Math.abs(endLoc.x-startLoc.x);
	    ysize = Math.abs(endLoc.y-startLoc.y);

	    xsize = xsize / 2;
	    ysize = ysize / 2;

	    // mit der Transition als Hypotenuse bildet
	    // das ganze ein rechtwinkliges Dreieck, berechne die fehlenden Seiten
	    ankath = Math.abs(startLoc.x-endLoc.x);
	    gegenkath = Math.abs(startLoc.y-endLoc.y);
	    hypo = Math.sqrt(ankath*ankath + gegenkath*gegenkath);

	    // zu kurze Transition werden ausgeblendet
	    if (hypo<MIN_STATE_DISTANCE) continue;

	    // berechne nun den Winkel der zw. Hypo und Ankath. am Zielzustand
	    if (hypo!=0)
		winkel = gegenkath/hypo;
	    else
		winkel = 0;

	    winkel = Math.asin(winkel);

	    // um den Zielzustand befindet sich ein imaginärer Kreis, dessen Schnittpunkt
	    // mit der Hypotenuse ist gesucht
	    cosine = Math.cos(winkel);
	    sine = Math.sin(winkel);

	    // cos alpha = u/r -> u = cos alpha * r
	    // u ist die x koor des Schnittpunkts
	    // analog für y

	    schnittx = (int)(cosine*30);
	    schnitty = (int)(sine*30);

	    endPoint = new Point(endLoc);

	    // abhängig wie die Zustände zueinander liegen wird dieser Punkt nun
	    // auf den Mittelpunkt draufaddiert/subtrahiert

	    if (endLoc.x <= startLoc.x) {
		mp.x = endLoc.x + xsize;
		endPoint.x += schnittx;
	    }
	    else {
		mp.x = startLoc.x + xsize;
		endPoint.x -= schnittx;
	    }

	    if (endLoc.y <= startLoc.y) {
		mp.y = endLoc.y + ysize;
		endPoint.y += schnitty;
	    }
	    else {
		mp.y = startLoc.y + ysize;
		endPoint.y -= schnitty;
	    }

	    g.drawString(currTrans.getChars().toString(), mp.x, mp.y-4); 
	    g.drawLine(startLoc.x,startLoc.y,endPoint.x,endPoint.y);
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


    // der User beendet das Zeichnen einer neuen Transition
    public void endEdge(JState end) {
	Vector<Character> autoTrans;

	if (end==edgeState) {
	    System.out.println("Schlingenfoo not implemented yet :-)");
	}
	else {
	    // prüfe ob der Nutzer das Autotransitions feature nutzt
	    autoTrans = parseTransChars(topLevel.getAutoTransition());
	    edgeState.insertTransition(end, autoTrans);
	}

	edgeState.setMode(JState.MODE_MARK);
	markedState = edgeState;

	drawingEdge = false;
    }


    // editTransChars, zeigt ein Dialogfeld in dem der Benutzer die
    // Zeichen einer Transition editieren kann
    // gültige Zeichen sind Buchstaben und Zahlen
    // der Rückgabewert 'null' zeigt an das der Benutzer Cancel gedrückt
    // oder keine (bzw. keine gültigen) Zeichen eingegeben hat
    protected Vector<Character> editTransChars(String initial) {
	String result;

	result = JOptionPane.showInputDialog(this, "Trans.-zeichen (durch Kommata getrennt)", initial);

	if (result==null || result.length()==0) return null;

	return parseTransChars(result);
    }


    // wandelt die gui infos in einen FSA um
    private FSA toFSA() {
	Object[] states;
	ListIterator<TransitionData> tData;
	ListIterator<Character> tChar;
        FSA result = new FSA();
	JState current;
	TransitionData cTrans;
	int fromState, toState;

	states = getComponentsInLayer(STATE_LAYER);

	for (int i = 0 ; i < states.length ; i++ ) {
	    // nimm einen Zustand her
	    current = (JState)states[i];
	    fromState = current.getNumber();
	    // durchlaufe seine Transitionen und füge diese in den Aut. ein
	    tData = current.getTransList().listIterator();
	    while (tData.hasNext()) {
		cTrans = tData.next();
		toState = cTrans.getEndState().getNumber();

		tChar = cTrans.getChars().listIterator();
		while (tChar.hasNext()) {
		    result.addTransition(fromState, toState, tChar.next().charValue());
		}
		
	    }
	}
	return result;
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


    private Vector<Character> parseTransChars(String inp) {
	StringTokenizer strTok;
	String ctok;
	Character currChar;
	Vector<Character> chVec = new Vector<Character>();

	strTok = new StringTokenizer(inp,",; ");

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
	// nur ungültige Zeichen
	if (chVec.isEmpty()) return null;
	return chVec;
    }

}
