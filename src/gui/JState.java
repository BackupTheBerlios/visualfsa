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

import java.util.Vector;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.awt.Polygon;
import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.AWTEvent;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class JState extends JComponent implements Comparable {
    
    private boolean startState;
    private boolean finalState;
    private boolean marked;
    private int num;
    private AutWindow parent;
    private Color currentColor;
    private LinkedHashMap<JState,TransitionData> outgoingTransList;
    
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
        outgoingTransList = new LinkedHashMap<JState,TransitionData>();
        parent = _parent;
        
        currentColor = VFSAGUI.options.getColorValueForKey("BACKGROUND_COLOR", Color.WHITE);
        
        if (!parent.isStatic()) {
            enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK);
        }
        
        
    }
    
    
    public void paintComponent(Graphics g) {
        int xPos;
        String name = ""+num;
        
        // Inhalt
        g.setColor(currentColor);
        g.fillRect(0,0,AutWindow.STATE_SIZE, AutWindow.STATE_SIZE);
        
        // Rand
        if (!finalState) {
            g.setColor(VFSAGUI.options.getColorValueForKey("LINE_COLOR", Color.BLACK));
        } else {
            g.setColor(Color.RED);
        }
        g.drawRect(0,0,AutWindow.STATE_SIZE-1, AutWindow.STATE_SIZE-1);
        
        // Startzustände werden mit einem kleinen schwarzen dreieck in der oberen Ecke kenntlich gemacht
        if (startState) {
            g.fillPolygon(parent.getStartTriangle());
        }
        
        xPos = SwingUtilities.computeStringWidth(g.getFontMetrics(),name);
        
        g.setColor(VFSAGUI.options.getColorValueForKey("LINE_COLOR", Color.BLACK));
        
        g.drawString(name,(AutWindow.STATE_HALFSIZE)-(xPos/2),AutWindow.STATE_HALFSIZE+5);
    }
    
    
    protected void processMouseEvent(MouseEvent event) {
        
        // solange das StatePopup gezeigt wird sind alle
        // Mausereignisse deaktiviert
        
        if (parent.isShowingPopup()) return;
        
        if (event.getClickCount()<2 && event.getButton()==MouseEvent.BUTTON1)
            parent.dispatchEvent(event);
        
        if (parent.isDragging()) return;
        
        // state dragging
        if (event.getButton()==MouseEvent.BUTTON1 &&
                event.getID()==MouseEvent.MOUSE_PRESSED) {
            parent.setStatePoint(event.getPoint(), getLocation());
            
        }
        
        // popup menü
        if (event.getButton()==MouseEvent.BUTTON3
                && event.getID()==MouseEvent.MOUSE_PRESSED) {
            parent.showPopup(this);
            return;
        }
        
        if (event.getID()==MouseEvent.MOUSE_PRESSED && event.getButton()==MouseEvent.BUTTON1) {
            
            // klickt der User doppelt auf einen Zustand
            // wird der Transitionszeichenmodus aktiviert
            // er endet solange bis der User eine Transition
            // einzeichnet oder das Zeichnen abbricht
            // während des Zeichnens ist der Ausgangszustand
            // rot markiert
            
            if (parent.isDrawingEdge() && event.getClickCount()==2) {
                parent.endEdge(this);
                return;
            } else if (!(parent.isDrawingEdge()) && event.getClickCount()==2) {
                setMode(MODE_EDGE);
                parent.startEdge(this);
                return;
            }
            
            
            
            
            // Während des Kantenzeichnens ist das Markieren nicht zulässig
            if (parent.isDrawingEdge()) return;
            
            markMe();
        }
    }
    
    
    // das Zeichenfenster veranlasst die Entfernung eines Zustands, deshalb
    // entfernen alle Zustände ihre Transitionen die zu diesem zustand führten
    public void removeTransTo(JState removedState) {
        outgoingTransList.remove(removedState);
    }
    
    
    
// wenn wir nicht markiert sind, schaue beim Parent
// Window wessen Markierung wir entfernen müssen
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
    
    // die MouseMotion Events werden an das Zeichenfenster durchgereicht
    protected void processMouseMotionEvent(MouseEvent event) {
        
        if (parent.isShowingPopup()) return;
        
        if (event.getID()!=MouseEvent.MOUSE_DRAGGED) return;
        
        Point pos = event.getPoint();
        
        pos.translate(getLocation().x, getLocation().y);
        
        MouseEvent ev = new MouseEvent(parent, MouseEvent.MOUSE_DRAGGED,0,0,
                pos.x, pos.y,
                0, false);
        
        parent.setDragging(true, this);
        parent.dispatchEvent(ev);
        
    }
    
    
    
    // Rückgabewert gibt an ob die Transition wirklich neu war
    public boolean insertTransition(JState endState, Vector<Character> autoTrans) {
        Character currChar;
        Vector<Character> chars, existing;
        TransitionData trans;
        String init="";
        
        // prüfe ob wir bereits eine Transition zu diesem Zustand besitzen
        
        if (outgoingTransList.containsKey(endState)) {
            trans = outgoingTransList.get(endState);
            
            existing = trans.getChars();
            
            // benutze autoTransitions Feature
            if (autoTrans!=null) {
                chars = autoTrans;
            } else {
                
                // erstelle aus den bestehenden Zeichen den Initalstring...
                for ( Character c : existing ) {
                    init = init + c + ",";
                }
                
                chars = parent.editTransChars(init);
                // falls der User cancel gedrückt hat
                if (chars!=null) existing.clear();
            }
            
            if (chars!=null) {
                
                for ( Character c : chars ) {
                    if (!existing.contains(c))
                        existing.add(c);
                }
                
            }
            
            parent.repaint();
            return false;
        }
        
        
        // hier angekommen war der Zustand noch nicht enthalten
        if (autoTrans==null) {
            chars = parent.editTransChars("");
        } else {
            chars = autoTrans;
        }
        
        if (chars!=null) {
            trans = new TransitionData(endState, chars);
            outgoingTransList.put(endState, trans);
        }
        
        parent.repaint(); // besser einzelnd neu zeichnen?
        return true;
    }
    
    protected LinkedHashMap<JState, TransitionData> getTransList() {
        return outgoingTransList;
    }
    
    // prüft ob der Zustand eine Transition zu dest hat
    public boolean hasTransTo(JState dest) {
        return outgoingTransList.containsKey(dest);
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
    
    
// (de)Markiere den Knoten als ab/ausgewählt
    public void setMode(int flag) {
        switch(flag) {
            
            case MODE_MARK:
                currentColor = VFSAGUI.options.getColorValueForKey("MARK_COLOR", Color.YELLOW);
                marked = true;
                repaint();
                break;
            case MODE_NOMARK:
                currentColor = VFSAGUI.options.getColorValueForKey("BACKGROUND_COLOR", Color.WHITE);
                marked = false;
                repaint();
                break;
            case MODE_EDGE:
                currentColor= VFSAGUI.options.getColorValueForKey("SPECIAL_COLOR", Color.GREEN);
                repaint();
                
                
        }
    }
    
    public int compareTo(Object other) {
        if (other instanceof JState) {
            int otherNum = ((JState)other).getNumber();
            return (num-otherNum);
        }
        return 0;
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
