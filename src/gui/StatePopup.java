

package src.gui;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ImageIcon;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class StatePopup extends JPopupMenu {

    private JMenuItem edit, delete, finalst, startst;
    private JState invoker;

    public StatePopup() {
	edit = new JMenuItem("Transitionen editieren", new ImageIcon("src/images/edit.png"));
	delete = new JMenuItem("Entfernen", new ImageIcon("src/images/edit_remove.png"));
	startst = new JCheckBoxMenuItem("Startzustand", new ImageIcon("src/images/start.png"));
	finalst = new JCheckBoxMenuItem("Endzustand", new ImageIcon("src/images/final.png"));

	add(edit);
	add(delete);
	addSeparator();
	add(finalst);
	add(startst);


	// Zustand löschen
	delete.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    AutWindow mainWindow;
		    // lösche den auslösenden Zustand
		    mainWindow = (AutWindow)invoker.getParent();
		    mainWindow.removeState(invoker);
		}
	    });

	startst.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    invoker.setStartState(!invoker.isStartState());
		}
	    });

	finalst.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent event) {
		    invoker.setFinalState(!invoker.isFinalState());
		}
	    });

    }

    public void handlePopupEvent(JState who) {
	invoker = who;
	
	startst.setSelected(who.isStartState());
	finalst.setSelected(who.isFinalState());

	this.show(who, AutWindow.STATE_HALFSIZE, 
		  AutWindow.STATE_HALFSIZE);
    }

}
