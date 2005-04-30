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

import src.datastructs.FSA;

import java.util.LinkedList;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Point;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.DefaultListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import src.datastructs.FSA;

public class Sidebar extends JPanel {

    private JList autList;
    private JButton newAut, renAut, delAut;
    private DefaultListModel listModel;
    private LinkedList<FSA> listData;
    private int lastSel;
    private AutWindow autWin;
    private JTextArea results;

    // pro Datei und oder Liste MAX_AUT Automaten
    public static final int MAX_AUT = 50;
    // die Ausgabe enth�lt maximal MAX_RESULT Zeilen
    public static final int MAX_RESULT = 1000;

    public Sidebar(AutWindow _autWin) {
	super();

	JSplitPane splitter;
	JScrollPane listScroller, resultScroller;
	JPanel upperPanel, buttonPanel, lowerPanel;
		
	autWin = _autWin;

	upperPanel = new JPanel(new BorderLayout());
		
	newAut = new JButton(new ImageIcon("src/images/stock_new.png"));
	renAut = new JButton(new ImageIcon("src/images/stock_edit.png"));
	delAut = new JButton(new ImageIcon("src/images/stock_cancel.png"));

	newAut.setToolTipText("neuen Automaten einf�gen");
	renAut.setToolTipText("gew�hlten Automat umbennen");
	delAut.setToolTipText("gew�hlten Automat entfernen");
		
	autList = new JList();
	autList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	listModel = new DefaultListModel();	
	autList.setModel(listModel);
	lastSel = -1;
	listData = new LinkedList<FSA>();

	listScroller = new JScrollPane(autList);
		
	upperPanel.add(listScroller, BorderLayout.CENTER);
		
	buttonPanel = new JPanel();
		
	buttonPanel.add(newAut);
	buttonPanel.add(renAut);
	buttonPanel.add(delAut);

	// initial ist nur ein Automat in der Liste, dieser kann nicht gel�scht werden
	delAut.setEnabled(false);

	upperPanel.add(buttonPanel, BorderLayout.SOUTH);
		
	results = new JTextArea();
	results.setEditable(false);
	resultScroller = new JScrollPane(results);

	lowerPanel = new JPanel(new BorderLayout());
		
	lowerPanel.add(resultScroller, BorderLayout.CENTER);
	lowerPanel.add(new JButton("Ausgabe l�schen"), BorderLayout.SOUTH);
		
	splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
	splitter.setResizeWeight(0.55);
		
	this.setLayout(new BorderLayout());
	this.add(splitter, BorderLayout.CENTER);

	// entfernen den gew�hlten Automaten
	delAut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    int lastSelCopy = lastSel;
		    if (lastSelCopy!=-1) {
			lastSel = -1;
			listData.remove(lastSelCopy);
			listModel.removeElementAt(lastSelCopy);
			autList.setSelectedIndex(listModel.getSize()-1);
			if (listModel.getSize()<=1)
			    delAut.setEnabled(false);
		    }
		}
	    });
	
	// zeigt ein Dialogfenster mit dem der Automatenname ge�ndert werden kann
	renAut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    String newName;

		    if (lastSel!=-1) {
			newName = JOptionPane.showInputDialog(autWin, "neuer Name:", 
						    listModel.elementAt(lastSel));
			newName = newName.trim();
			for (int i = 0 ; i <  newName.length()  ; i++ ) {
			    if (!Character.isLetterOrDigit(newName.charAt(i))) {
				JOptionPane.showMessageDialog(autWin,
							      "Ung�ltige Zeichen, f�r den Automatennamen sind nur Zahlen oder Buchstaben zul�ssig.", 
							      "Fehler", JOptionPane.ERROR_MESSAGE);
				return;
			    }
			}
			listModel.setElementAt(newName, lastSel);
			autWin.setCurrentName(newName);
		    }
		}
	    });


	// f�ge einen neuen Automaten in die Liste ein, bis zur Obergrenze
	newAut.addActionListener(new ActionListener() {
		public void actionPerformed(ActionEvent e) {
		    if (listModel.getSize()<MAX_AUT) {
			insertNewAut();
		    }
		    else {
			newAut.setEnabled(false);
		    }
		}
	    });


	autList.addListSelectionListener(new ListSelectionListener() {
		public void valueChanged(ListSelectionEvent ev) {
		    int selection = autList.getSelectedIndex();
		    if (selection!=lastSel) {
			if (lastSel!=-1) {
			    listData.set(lastSel, autWin.toFSA());
			}
			lastSel = selection;
			autWin.insertAut(listData.get(selection));
		    }
		}
	    });
    }

    /* gebe die Liste mit allen Automaten zur�ck, vorher wird der aktuell
       angezeigte Automat nochmal in der Liste aktualisiert */
    public LinkedList<FSA> getList() {
	if (lastSel!=-1) {
	    listData.set(lastSel, autWin.toFSA());
	}
	return listData;
    }


    // f�ge neuen Text in das Ausgabefenster ein
    public void insertResults(String resText) {
	if (results.getLineCount()>MAX_RESULT) results.setText("");
	results.append("\n---------------------------------------------\n");
	results.append(resText);
    }

    /*
      F�gt einen leeren Automaten in die Liste ein 
    */
    public void insertNewAut() {
	FSA newAut = new FSA();
	newAut.setName(generateName());
	newAut.setPosition(0, new Point(30,30));
	listData.add(newAut);
	if (listModel.getSize()>1)
	    delAut.setEnabled(true);
	autList.setSelectedIndex(listModel.getSize()-1);
    }
    


    private String generateName() {
	String autName = new String("Aut");
	boolean okflag = false;

	do {
	    autName = autName + (int)(Math.random()*100);
	    if (!listModel.contains(autName)) {
		listModel.addElement(autName);
		okflag = true;
	    }
	    else {
		autName = "Aut";
	    }
	} while (!okflag);
	return autName;
    }


}
