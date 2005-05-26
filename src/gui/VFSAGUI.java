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

import java.awt.Point;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.io.IOException;

import io.FileIO;

public class VFSAGUI extends JFrame {

    private BottomBar bottom;
    private Sidebar side;
    private AutWindow autPane;
    private TopBar top;
    private MainMenu menubar;

    private String filename;

    public static final String verString = "visualFSA 0.1a";

    public VFSAGUI() {
	super();
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

	// TODO, cmd-argumente
	newFile();
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

    // eigentlich h�sslich, jede Menge Dispatchmethoden :/

    public String getAutoTransition() {
	return bottom.getAutoTransition();
    }

    // der User hat in der BottomBar den Infobutton gedr�ckt
    // es wird in das Ausgabefenster eine textuelle Zusammenfassung
    // �ber den Automaten ausgegeben
    public void autInfo() {
	side.insertResults(autPane.toFSA().infoString());
    }

    // der User hat den Worterkennungs-button gedrpckt
    // das Wort wird dem Automaten �bergeben, das Ergebnis
    // im Resultfenster angezeigt
    public void wInL(String w) {
	if (autPane.toFSA().accepts(w)) {
	    side.insertResults(w+" ist in L("+autPane.getCurrentName()+")");
	}
	else {
	    side.insertResults(w+" wird nicht erkannt");
	}
    }


    // checkSave, fragt ob der user die aktuelle Datei speichern will
    // R�ckgabewert gibt an ob die Aktion weiterhin ausgef�hrt werden soll (also im Fall != cancel)
    // mit justSave fungiert diese Methode als 'Speichern als'
    public boolean checkSave(boolean justSave) {
	int res;
	String newFilename;

	if (justSave) {
	    res = JOptionPane.YES_OPTION;
	}
	else {
	    res = JOptionPane.showConfirmDialog(this, 
						"Diese Aktion verwirft die aktuelle Datei, vorher speichern?",
						"Hinweis", JOptionPane.YES_NO_CANCEL_OPTION);
	}

	switch(res) {
	case JOptionPane.YES_OPTION:
	    // speichern (als), dann $aktion
	    newFilename = FileIO.getSaveFilename(this, filename);

	    if (newFilename!=null) {
		filename = newFilename;
		FileIO.fsaListToFile(side.getList(), filename);
		setTitle(verString+" - "+filename);
	    }
	    return true;
	case JOptionPane.NO_OPTION:
	    // nicht speichern, dann $aktion
	    return true;
	}
	return false; // implizit der cancel fall
    }

    /**
     * �ffnet einen Dateidialog in dem der Benutzer eine Datei ausw�hlen kann.
     * M�gliche Fehler werden intern durch Exceptions abgefangen und �ber
     * ein JOptionPane ausgegeben
     */
    public void openFile() {
	String newFilename;
	LinkedList inData;

	// user w�hlt datei aus...
	newFilename = FileIO.getOpenFilename(this);

	// wenn dieser in Ordnung ist..
	if (newFilename!=null) {
	    try {
		// Daten einlesen, eventuell hier Exceptions
		inData = FileIO.fileToFsaList(newFilename);
		filename = newFilename;
		setTitle(verString+" - "+filename);
		// die eingelesene Liste mit Automaten einf�gen, bei fehlerhaften
		// Daten in der Liste -> Exception
		side.insertList(inData);
	    }
	    catch(IOException ioEx) {
		JOptionPane.showMessageDialog(this,"IO-Fehler: ("+ioEx.getMessage()+")",
					      "Fehler", JOptionPane.ERROR_MESSAGE);
	    }
	    catch (Exception generalEx) {
		generalEx.printStackTrace();
		JOptionPane.showMessageDialog(this,
					      "Lesefehler, meist falsches Dateiformat ("+
					      generalEx.getMessage()+")","Fehler",
					      JOptionPane.ERROR_MESSAGE);
	    }
	    
	}
    }

    public void newFile() {
	filename = "unbenannt.fsa";
	setTitle(verString+" - "+filename);
	side.reset();
    }

}

