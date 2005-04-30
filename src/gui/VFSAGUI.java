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

package src.gui;

import java.awt.Point;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class VFSAGUI extends JFrame {

	private BottomBar bottom;
	private Sidebar side;
	private AutWindow autPane;
	private TopBar top;
	private MainMenu menubar;

	public VFSAGUI() {
		super("visualFSA");
	}

	public void showGUI() {
		JSplitPane centerSplitter;
	
		getContentPane().setLayout(new BorderLayout());
	
		bottom = new BottomBar(this);
		getContentPane().add(bottom, BorderLayout.SOUTH);

		autPane = new AutWindow(this);

		side = new Sidebar(autPane);

		side.insertNewAut();
		
		top = new TopBar();
		getContentPane().add(top, BorderLayout.NORTH);
		
		centerSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, autPane, side);
		centerSplitter.setResizeWeight(0.95);
		getContentPane().add(centerSplitter, BorderLayout.CENTER);
		
		menubar = new MainMenu(this, side);
		this.setJMenuBar(menubar);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		setLocation(30,30);
		setVisible(true);
		side.insertResults("Welcome!");
	}


	// starte die GUI in einem neuen Thread
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				VFSAGUI visualFSA;
				visualFSA = new VFSAGUI();
				visualFSA.showGUI();
			}		
		});
	}

    // eigentlich hässlich, jede Menge Dispatchmethoden :/

    public String getAutoTransition() {
	return bottom.getAutoTransition();
    }

    // der User hat in der BottomBar den Infobutton gedrückt
    // es wird in das Ausgabefenster eine textuelle Zusammenfassung
    // über den Automaten ausgegeben
    public void autInfo() {
	side.insertResults(autPane.toFSA().infoString());
    }

    // der User hat den Worterkennungs-button gedrpckt
    // das Wort wird dem Automaten übergeben, das Ergebnis
    // im Resultfenster angezeigt
    public void wInL(String w) {
	if (autPane.toFSA().accepts(w)) {
	    side.insertResults(w+" ist in L("+autPane.getCurrentName()+")");
	}
	else {
	    side.insertResults(w+" wird nicht erkannt");
	}
    }

}

