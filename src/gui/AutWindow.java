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

import datastructs.Transition;
import datastructs.FSA;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ListIterator;
import java.util.LinkedList;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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

    private String currentName; // name des angezeigten Automaten
    private VFSAGUI topLevel;
    private StatePopup statePopup;

    private Color lineColour, charColour;
      
    public AutWindow(VFSAGUI _topLevel) {
	super();
	topLevel = _topLevel;
	setLayout(null);
	setDoubleBuffered(true);
	setOpaque(true);
	setBackground(VFSAGUI.options.getBackCol());
	enableEvents(AWTEvent.MOUSE_EVENT_MASK);
	statePopup = new StatePopup(_topLevel);                
    }


    public JState addState(Point position) {
	JState newState;
		
	newState = new JState(getComponentCountInLayer(STATE_LAYER), this);
	newState.setBounds(position.x, position.y, STATE_SIZE, STATE_SIZE);
			
	add(newState);
	setLayer(newState, STATE_LAYER);
	return newState;
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
            current.setTransDrawn(false);
	}
        
        // wir holen uns bereits hier die Farben aus dem Optionsobjekt
        // um unnötigen Methodenaufruf-Overhead in drawTransitions zu vermeiden
        charColour = VFSAGUI.options.getCharCol();
        lineColour = VFSAGUI.options.getLineCol();
        
        for (int i = 0 ; i < numstate ; i++ ) {
            current = (JState)states[i];
            drawTransitions(current, g);
            current.setTransDrawn(true);
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
        Polygon spitze;
        
        // für die Pfeilspitzen
        double width = 5.0, length = 20.0;
              
	Point mp = new Point(), endPoint;
	int xsize,ysize;

	// dreiecksseiten
	double ankath, hypo, gegenkath;
	double winkel, cosine, sine;
	int schnittx, schnitty;

	transList = startState.getTransList();
	current = transList.listIterator();

	while (current.hasNext()) {
	    currTrans = current.next();
	    endState = currTrans.getEndState();

	    // Transition ist eine Schlinge
	    if (endState==startState) {
                startLoc = startState.getLocation();	
                startLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
                // zeichne einen  'dreiviertel bogen'
                g.setColor(lineColour);
		g.drawArc(startLoc.x-STATE_SIZE, startLoc.y-STATE_SIZE, STATE_SIZE, STATE_SIZE, 0, 270);
		// die Transititionszeichen werden oberhalb des
		// Kreisbogens dargestellt
		mp.x = startLoc.x-STATE_SIZE;
		mp.y = startLoc.y-STATE_SIZE-10;
		g.setColor(charColour);
                g.drawString(currTrans.getChars().toString(), mp.x, mp.y);
		continue;
	    }
	    
            
            
	    // aktueller Zielzustand, linke obere Ecke, Mittelpunkt berechnen
	    endLoc = endState.getLocation();
	    
            // hat der aktuelle Zielzustand auch eine Transition zu uns,
            // wird eine der beiden Transition leicht nach unten verschoben
            // wurden die Transitionen des Zielzustands noch nicht gezeichnet
            // wird unsere Transition zu ihm verschoben, der Zielzustand
            // seinerseits wird seine dann nicht verschieben
        
            startLoc = startState.getLocation();
                        
            if (endState.hasTransTo(startState) && endState.getTransDrawn()) {
                startLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE-12);
                endLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE-12);
            }
            else {
                startLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
                endLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
            }
	    

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

	    schnittx = (int)(cosine*25);
	    schnitty = (int)(sine*25);

	    endPoint = new Point(endLoc);

            
	    // abhängig wie die Zustände zueinander liegen wird dieser Punkt nun
	    // auf den Mittelpunkt draufaddiert/subtrahiert

            // TODO --- das geht besser :)
            
                       
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

            g.setColor(charColour);
            g.drawString(currTrans.getChars().toString(), mp.x, mp.y-4); 
	    g.setColor(lineColour);
            g.drawLine(startLoc.x,startLoc.y,endPoint.x,endPoint.y);
            
            
            // nicht 100% sauber, Pfeilspitze
            double e_x, e_y;
            
            Point a, b, c;
            
            e_x = (endPoint.x - startLoc.x) / hypo;
            e_y = (endPoint.y - startLoc.y) / hypo;
           
            a = new Point(); b = new Point(); c = new Point();
            
            a.x = endPoint.x - (int) Math.round(length/2*e_x);
            a.y = endPoint.y - (int) Math.round(length/2*e_y);
            
            spitze = new Polygon();
            
            spitze.addPoint(endPoint.x, endPoint.y);
            
            spitze.addPoint(a.x - (int) Math.round(width*e_y),
                            a.y + (int) Math.round(width*e_x));
            
            spitze.addPoint(a.x + (int) Math.round(width*e_y),
                            a.y - (int) Math.round(width*e_x));
            
            
            g.fillPolygon(spitze);
                      
	    
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

	// prüfe ob der Nutzer das Autotransitions feature nutzt
	autoTrans = parseTransChars(topLevel.getAutoTransition());
	    edgeState.insertTransition(end, autoTrans);

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

    // erzeugt aus einem FSA Objekt die GUI Darstellung
    public void insertAut(FSA aut) {
	Iterator stateIt;
	Integer currentStateNum;
	JState currentState, destState;
	LinkedHashMap<Integer,JState> stateMap = new LinkedHashMap<Integer,JState>();
	LinkedList<Transition> currTransList;
	ListIterator<Transition> transIt;
	Transition currTrans;
	Vector<Character> transChars;

	// Zeichenfenster zurücksetzen
	this.removeAll();
	markedState = null;

	stateIt = aut.getStates().iterator();

	while (stateIt.hasNext()) {
	    currentStateNum = (Integer)stateIt.next();
	    currentState = addState(aut.getPosition(currentStateNum));

	    currentState.setFinalState(aut.isFinalState(currentStateNum));
	    currentState.setStartState(aut.isStartState(currentStateNum));

	    // Zuordnung Referenz JState nach Zustandsnummer merken
	    // damit man gleich die Transitionen einfacher zuordnen/einfüge kann
	    stateMap.put(currentStateNum, currentState);
	}

	stateIt = stateMap.keySet().iterator();

	while (stateIt.hasNext()) {
	    currentStateNum = (Integer)stateIt.next();
	    currTransList = aut.getStateTransitions(currentStateNum);
	    
	    if (currTransList!=null) {

		transIt = currTransList.listIterator();
	    
		// Referenz für diese Nummer holen
		currentState = stateMap.get(currentStateNum);
		
		while (transIt.hasNext()) {
		    currTrans = transIt.next();
		    destState = stateMap.get(currTrans.getEndState());
		    transChars = null; // gc
		    transChars = new Vector<Character>();
		    transChars.add(currTrans.getChar());
		    currentState.insertTransition(destState, transChars);
		}
	    }
	}

	currentName = aut.getName();

	repaint();
    }

    // wandelt die gui infos in einen FSA um
    public FSA toFSA() {
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
	    // speichere seine Position
	    fromState = current.getNumber();
	    if (current.isFinalState()) result.setFinalFlag(fromState, true);
	    if (current.isStartState()) result.setStartFlag(fromState, true);
	    result.setPosition((Integer)fromState, current.getLocation());
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
	result.setName(currentName);
	return result;
    }


    public String getCurrentName() {
	return currentName;
    }

    public void setCurrentName(String _n) {
	currentName = _n;
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


    public static Vector<Character> parseTransChars(String inp) {
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
