
/*

	JState.java
	
	
*/

package src.gui;



import java.awt.Point;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.AWTEvent;

import java.awt.event.MouseEvent;


import javax.swing.SwingUtilities;
import javax.swing.JComponent;

public class JState extends JComponent {

	private boolean startState;
	private boolean finalState;
	private boolean marked;
	private String name;
	private AutWindow parent;
	private Color currentColor;

	public static final int MODE_MARK = 1;
	public static final int MODE_NOMARK = 2;
	public static final int MODE_EDGE = 3;

	private Point initial;

	public JState(String _name, AutWindow _parent) {
		super();
		setDoubleBuffered(true);
		setOpaque(true);
		name = _name;
		initial = new Point();
		currentColor = Color.WHITE;
		parent = _parent;
		enableEvents(AWTEvent.MOUSE_MOTION_EVENT_MASK|AWTEvent.MOUSE_EVENT_MASK);
	}


	public void paintComponent(Graphics g) {
		int xPos;


		// Inhalt
		g.setColor(currentColor);
		g.fillRect(0,0,getWidth()-1, getHeight()-1);

		// Rand
		g.setColor(Color.BLACK);
		g.drawRect(0,0,getWidth()-1, getHeight()-1);
		
		
		g.setColor(Color.BLACK);
		xPos = SwingUtilities.computeStringWidth(g.getFontMetrics(),name);
		
		g.drawString(name,(AutWindow.STATE_SIZE/2)-(xPos/2),(AutWindow.STATE_SIZE/2)+5);		
	}

	
	protected void processMouseEvent(MouseEvent event) {
		if (event.getID()==MouseEvent.MOUSE_PRESSED) {
			
			// klickt der User doppelt auf einen Zustand
			// wird der Transitionszeichenmodus aktiviert
			// er endet solange bis der User eine Transition
			// einzeichnet oder das Zeichnen abbricht
			// während des Zeichnens ist der Ausgangszustand
			// rot markiert
			
			if (event.getClickCount()==2) {
				
				// wenn wir schon am Trans.-Zeichnen sind, beende
				// diesen Vorgang mit dem zweiten Doppelklick
				if (parent.isDrawingEdge()) {
					parent.endEdge(this);
				}
				else {
					setMode(MODE_EDGE);		
					parent.startEdge(this);
					return;
				}
			}			
			
			// Während des Kantenzeichnens ist das Markieren nicht zulässig
			if (parent.isDrawingEdge()) return;
			
			// wenn wir nicht markiert sind, schaue beim Parent
			// Window wessen Markierung wir entfernen müssen
			// setze gleichzeitig unsere Markierung
			if (!marked) {
				this.setMode(MODE_MARK);
				if (parent.getMarkedState()!=null)
					parent.getMarkedState().setMode(MODE_NOMARK);
					
				parent.setMarkedState(this);
			}
			
			initial = event.getPoint();
		}	
	}
	
	protected void processMouseMotionEvent(MouseEvent event) {
		Point newPos, currentPos;
		parent = (AutWindow)getParent();
		
		if (event.getID()==MouseEvent.MOUSE_DRAGGED) {
			currentPos = this.getLocation();
			newPos = new Point();
			newPos.x = currentPos.x + event.getPoint().x - initial.x;
			newPos.y = currentPos.y + event.getPoint().y - initial.y;
			setLocation(newPos);
		}
		else {
		}
	}
	
		
	// (de)Markiere den Knoten als ab/ausgewählt
	public void setMode(int flag) {
		switch(flag) {
		
			case MODE_MARK:
				currentColor = Color.YELLOW;
				marked = true;
				repaint();
				break;
			case MODE_NOMARK:
				currentColor = Color.WHITE;
				marked = false;
				repaint();
				break;
			case MODE_EDGE:
				currentColor= Color.RED;
				repaint();
				
		
		}
	}
		
	
	public boolean isStartState() {
		return startState;
	}
	
	public boolean isFinalState() {
		return finalState;
	}
	
	public boolean isMarked() {
		return marked;
	}

}
