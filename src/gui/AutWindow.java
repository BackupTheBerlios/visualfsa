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

package gui;

import datastructs.Transition;
import datastructs.FSA;

import java.util.LinkedHashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ListIterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.BasicStroke;
import java.awt.Stroke;
import java.awt.RenderingHints;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.AffineTransform;

public class AutWindow extends JLayeredPane {
    
    public static final int STATE_LAYER = 1;
    public static final int STATE_SIZE = 30;
    public static final int GRID_SIZE = 40;
    public static final int STATE_HALFSIZE = 15;
    
    private static final double PIHALF = Math.PI/2.0;
    private static final int MIN_STATE_DISTANCE = 55;
    
    private JState markedState;
    private JState edgeState;
    private boolean drawingEdge;
    private boolean grid;
    
    private String currentName; // name des angezeigten Automaten
    private VFSAGUI topLevel;
    private StatePopup statePopup;
    
    private Polygon startTriangle, vertex;
    private BasicStroke linePen;
    private Color lineColour, charColour, bgCol;
    
    private boolean staticWindow;
    
    private Point initialPoint, lastPos;
    private Polygon stateShape;
    
    private boolean dragging;
    private JState draggedState;
    
    public AutWindow(VFSAGUI _topLevel, boolean _st) {
        super();
        topLevel = _topLevel;
        setLayout(null);
        setDoubleBuffered(true);
        setOpaque(true);
        
        stateShape = new Polygon();
        
        stateShape.addPoint(0,0); stateShape.addPoint(0, STATE_SIZE-1);
        stateShape.addPoint(STATE_SIZE-1, STATE_SIZE-1); stateShape.addPoint(STATE_SIZE-1,0);
        
        linePen = new BasicStroke(1.5f);
        
        staticWindow = _st;
        
        bgCol = topLevel.options.getColorValueForKey("BACKGROUND_COLOR", Color.WHITE);
        
        setBackground(bgCol);
        
        if (!staticWindow) {
            enableEvents(AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
            statePopup = new StatePopup(_topLevel);
        }
        
        startTriangle = new Polygon();
        startTriangle.addPoint(0,0);
        startTriangle.addPoint(8,0);
        startTriangle.addPoint(0,8);
        
        vertex = new Polygon();
        vertex.addPoint(0,0);
        vertex.addPoint(4,10);
        vertex.addPoint(-4, 10);
        
        
        initialPoint = new Point();
        lastPos = new Point();
    }
    
    public JState addState(Point position) {
        JState newState;
    
        newState = new JState(getComponentCountInLayer(STATE_LAYER), this);
        
        newState.setBounds(position.x, position.y, STATE_SIZE, STATE_SIZE);
        
        add(newState);
        setLayer(newState, STATE_LAYER);
        return newState;
    }
    
    
    public Polygon getStartTriangle() {
        return startTriangle;
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
    
    public void setStatePoint(Point p, Point statePos) {
        initialPoint.x = p.x;
        initialPoint.y = p.y;
        lastPos.x =  statePos.x;
        lastPos.y = statePos.y;
        drawStateShape(lastPos, true);
        lastPos.translate(initialPoint.x, initialPoint.y);
    }
    
    public void setDragging(boolean b, JState which) {
        dragging = b;
        draggedState = which;
    }
    
    public boolean isDragging() {
        return dragging;
    }
    
    // füge bei Doppelklick einen neuen Zustand ein
    protected void processMouseEvent(MouseEvent ev) {
        
        if (ev.getClickCount()==2 && ev.getButton()==MouseEvent.BUTTON1
                && ev.getID()==MouseEvent.MOUSE_PRESSED) {
            addState(ev.getPoint());
        }
        
        if (dragging && draggedState!=null && ev.getID()==MouseEvent.MOUSE_RELEASED) {
            dragging = false;
            drawStateShape(lastPos, false);
            lastPos.translate( -initialPoint.x, -initialPoint.y);
            
            if (grid) {
                lastPos.x = lastPos.x - (lastPos.x % GRID_SIZE);
                lastPos.y = lastPos.y - (lastPos.y % GRID_SIZE);
                if (lastPos.x < 1) { lastPos.x = 1; }
                if (lastPos.y < 1) { lastPos.y = 1; }
            }
            
            draggedState.setLocation(lastPos);
            repaint();
        }
        
    }
    
    public void processMouseMotionEvent(MouseEvent ev) {
        if (!dragging || isShowingPopup()) return;
        Point p = new Point(ev.getPoint());
        //p.x -= initialPoint.x;
        //p.y -= initialPoint.y;
        drawStateShape(p, true);
    }
    
    
    private void drawStateShape(Point newPos, boolean drawAgain) {
        Graphics gr = this.getGraphics();
        
        gr.setXORMode(Color.GREEN);
        
        stateShape.translate(lastPos.x-initialPoint.x, lastPos.y-initialPoint.y);
        gr.drawPolygon(stateShape);
        stateShape.translate(-(lastPos.x-initialPoint.x), -(lastPos.y-initialPoint.y));
        
        if (drawAgain) {
            lastPos = newPos;
            stateShape.translate((lastPos.x-initialPoint.x), (lastPos.y-initialPoint.y));
            gr.drawPolygon(stateShape);
            stateShape.translate(-(lastPos.x-initialPoint.x), -(lastPos.y-initialPoint.y));
        }
        
    }
    
    
    public boolean isStatic() {
        return staticWindow;
    }
    
    
    // jedem Zustand wird seine Transitionsliste entnommen und die entspr.
    // Transitionen dann gezeichnet
    public void paintComponent(Graphics gr) {
        int numstate;
        JState current, endState;
        Object[] states;
        Point startLoc, endLoc;
        
        Graphics2D g = (Graphics2D)gr.create();
        
        if (grid) {
            
            int wi = this.getWidth();
            int he = this.getHeight();
            
            g.setColor(Color.LIGHT_GRAY);
            
            for (int i = (STATE_HALFSIZE) ; i <= wi ; i+=GRID_SIZE) {
                g.drawLine(i,0,i,he);
            }
            
            for (int j = (STATE_HALFSIZE) ; j <= he ; j+=GRID_SIZE) {
                g.drawLine(0,j,wi,j);
            }
        }
        
        g.setStroke(linePen);
        
        numstate = getComponentCountInLayer(STATE_LAYER);
        states = getComponentsInLayer(STATE_LAYER);
        
        for (int i = 0 ; i < numstate ; i++ ) {
            current = (JState)states[i];
            current.setTransDrawn(false);
        }
        
        if (topLevel.options.getBoolValueForKey("ANTIALIAS_OPTION", true)) {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        }
        
        // wir holen uns bereits hier die Farben aus dem Optionsobjekt
        // um unnötigen Methodenaufruf-Overhead in drawTransitions zu vermeiden
        charColour = topLevel.options.getColorValueForKey("CHAR_COLOR", Color.RED);
        lineColour = topLevel.options.getColorValueForKey("LINE_COLOR", Color.BLACK);
        
        for (int i = 0 ; i < numstate ; i++ ) {
            current = (JState)states[i];
            drawTransitions(current, g);
            current.setTransDrawn(true);
        }
        
        g.dispose();
        
    }
    
    public void showPopup(JState who) {
        if (drawingEdge) return;
        if (!statePopup.isVisible()) {
            statePopup.handlePopupEvent(who);
        }
    }
    
    public boolean isShowingPopup() {
        return statePopup.isVisible();
    }
    
    protected void drawTransitions(JState startState, Graphics2D g) {
        Point startLoc, endLoc;
        LinkedHashMap<JState, TransitionData> transList;
        ListIterator<TransitionData> current;
        TransitionData currTrans;
        Shape finalVertex;
        AffineTransform affineTrans;
        
        Point mp = new Point(), endPoint;
        int xsize,ysize;
        
        // dreiecksseiten
        double ankath, hypo, gegenkath;
        double winkel, cosine, sine;
        int schnittx, schnitty;
        
        transList = startState.getTransList();
        
        Point a = new Point();
        
        affineTrans = new AffineTransform();
        
        
        for ( JState endState : transList.keySet() ) {
            
            currTrans = transList.get(endState);
            
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
            } else {
                startLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
                endLoc.translate(STATE_HALFSIZE,STATE_HALFSIZE);
            }
            
            
            // für die Beschriftung mit den Transitionszeichen die Mitte der Linie bestimmen
            ankath = xsize = Math.abs(endLoc.x-startLoc.x);
            gegenkath = ysize = Math.abs(endLoc.y-startLoc.y);
            
            xsize = xsize / 2;
            ysize = ysize / 2;
            
            // mit der Transition als Hypotenuse bildet
            // das ganze ein rechtwinkliges Dreieck, berechne die fehlenden Seiten
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
            
            double rotAngel = 0.0;
            
            if (endLoc.x <= startLoc.x && endLoc.y <= startLoc.y) {
                rotAngel = winkel - PIHALF;
                endPoint.x += schnittx; mp.x = endLoc.x + xsize;
                mp.y = endLoc.y + ysize; endPoint.y += schnitty;
            }
            
            if (endLoc.x > startLoc.x && endLoc.y <= startLoc.y) {
                rotAngel = PIHALF - winkel;
                mp.x = startLoc.x + xsize; endPoint.x -= schnittx;
                mp.y = endLoc.y + ysize; endPoint.y += schnitty;
            }
            
            if (endLoc.x >= startLoc.x && endLoc.y > startLoc.y) {
                rotAngel = winkel + PIHALF;
                mp.x = startLoc.x + xsize; endPoint.x -= schnittx;
                mp.y = startLoc.y + ysize; endPoint.y -= schnitty;
            }
            
            if (endLoc.x < startLoc.x && endLoc.y > startLoc.y) {
                rotAngel = -PIHALF - winkel;
                endPoint.x += schnittx; mp.x = endLoc.x + xsize;
                mp.y = startLoc.y + ysize; endPoint.y -= schnitty;
            }
            
            g.setColor(charColour);
            g.drawString(currTrans.getChars().toString(), mp.x, mp.y-4);
            g.setColor(lineColour);
            g.drawLine(startLoc.x,startLoc.y,endPoint.x,endPoint.y);
            
            // Pfeilspitze
            affineTrans.setToRotation(rotAngel,  endPoint.x, endPoint.y);
            affineTrans.translate(endPoint.x, endPoint.y);
            
            finalVertex = affineTrans.createTransformedShape(vertex);
            
            g.fill(finalVertex);
            
        }
        
    }
    
    
    public void setGridState(boolean gs) {
        grid = gs;
    }
    
    public boolean isGrid() {
        return grid;
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
    public Vector<Character> editTransChars(String initial) {
        String result;
        
        result = JOptionPane.showInputDialog(this, "Characters (seperated by commas)", initial);
        
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
        FSA result = new FSA();
        JState current;
        TransitionData cTrans;
        LinkedHashMap<JState,TransitionData> transitions;
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
            
            transitions = current.getTransList();
            
            for (JState endState : transitions.keySet() ) {
                cTrans = transitions.get(endState);
                toState = endState.getNumber();
                
                for (Character c : cTrans.getChars() ) {
                    result.addTransition(fromState, toState, c );
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
