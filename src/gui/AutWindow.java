
package src.gui;

import java.awt.event.MouseEvent;
import java.awt.AWTEvent;
import java.awt.Point;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JLayeredPane;


public class AutWindow extends JLayeredPane {

	public static final int STATE_LAYER = 1;
	public static final int STATE_SIZE = 30;
	
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

		if (end==edgeState) {
			System.out.println("Schlingenfoo");
		}
		else {
			System.out.println("Trans von "+edgeState+" nach "+end);
		}

		drawingEdge = false;
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
