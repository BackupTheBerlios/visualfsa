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

import datastructs.FSA;

import java.util.ListIterator;
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

public class Sidebar extends JPanel {
    
    private JList autList;
    private JButton newAut, renAut, delAut, resetResults;
    private DefaultListModel listModel;
    private LinkedList<FSA> listData;
    private int lastSel;
    private JScrollPane resultScroller;
    private AutWindow autWin;
    private JTextArea results;
    
    // pro Datei und oder Liste MAX_AUT Automaten
    public static final int MAX_AUT = 50;
    // die Ausgabe enthält maximal MAX_RESULT Zeilen
    public static final int MAX_RESULT = 1000;
    
    public Sidebar(AutWindow _autWin) {
        super();
        
        JSplitPane splitter;
        JScrollPane listScroller;
        JPanel upperPanel, buttonPanel, lowerPanel;
        
        autWin = _autWin;
        
        upperPanel = new JPanel(new BorderLayout());
        
        newAut = new JButton(new ImageIcon("images/stock_new.png"));
        renAut = new JButton(new ImageIcon("images/stock_edit.png"));
        delAut = new JButton(new ImageIcon("images/edit_remove.png"));
        
        newAut.setToolTipText("insert a new, empty automaton");
        renAut.setToolTipText("rename the currently selected automaton");
        delAut.setToolTipText("remove the currently selected automaton");
        
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
        
        // initial ist nur ein Automat in der Liste, dieser kann nicht gelöscht werden
        delAut.setEnabled(false);
        
        upperPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        results = new JTextArea();
        results.setEditable(false);
        resultScroller = new JScrollPane(results);
        
        lowerPanel = new JPanel(new BorderLayout());
        
        resetResults = new JButton("Clear Output");
        
        lowerPanel.add(resultScroller, BorderLayout.CENTER);
        lowerPanel.add(resetResults, BorderLayout.SOUTH);
        
        splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
        splitter.setResizeWeight(0.55);
        
        this.setLayout(new BorderLayout());
        this.add(splitter, BorderLayout.CENTER);
        
        // das Ausgabefenster wird zurückgesetzt
        resetResults.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                results.setText("");
                insertResults("Welcome!");
            }
        });
        
        // entfernen den gewählten Automaten
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
        
        // zeigt ein Dialogfenster mit dem der Automatenname geändert werden kann
        renAut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String newName;
                
                if (lastSel!=-1) {
                    newName = JOptionPane.showInputDialog(autWin, "New Name: ",
                            listModel.elementAt(lastSel));
                    if (newName==null) return; // cancel gedrückt
                    newName = newName.trim();
                    for (int i = 0 ; i <  newName.length()  ; i++ ) {
                        if (!Character.isJavaIdentifierPart(newName.charAt(i))) {
                            JOptionPane.showMessageDialog(autWin,
                                    "Invalid Name, only characters and digits are allowed.",
                                    "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    listModel.setElementAt(newName, lastSel);
                    autWin.setCurrentName(newName);
                }
            }
        });
        
        
        // füge einen neuen Automaten in die Liste ein, bis zur Obergrenze
        newAut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (listModel.getSize()<MAX_AUT) {
                    insertNewAut();
                } else {
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
    
    /* gebe die Liste mit allen Automaten zurück, vorher wird der aktuell
       angezeigte Automat nochmal in der Liste aktualisiert */
    public LinkedList<FSA> getList() {
        if (lastSel!=-1) {
            listData.set(lastSel, autWin.toFSA());
        }
        return listData;
    }
    
    // gibt den aktuellen angezeigten Automaten zurück
    public FSA getCurrentAut() {
        FSA result = null;
        if (lastSel!=-1) {
            // aktualisiere gleichzeitig die Liste
            result = autWin.toFSA();
            listData.set(lastSel, result);
        }
        return result;
    }
    
    // füge einen neuen (nicht leeren, vgl. insertNewAut) in die Liste ein
    public void insertAut(FSA aut) {
        
        if (listModel.contains(aut.getName())) {
            JOptionPane.showMessageDialog(autWin, "The list already contains a automaton with this name.");
            return;
        }
        
        listData.add(aut);
        listModel.addElement(aut.getName()); // das ein Name vorhanden ist nehmen wir an
        if (listModel.getSize()>1)
            delAut.setEnabled(true);
        autList.setSelectedIndex(listModel.getSize()-1);
    }
    
    // füge (aus einer geöffneten Datei) eine neue Liste mit Automaten ein
    // Eingabe ist die eingelesene Liste aus einer Datei
    public void insertList(LinkedList list) throws Exception {
        ListIterator it;
        FSA currentFSA;
        
        it = list.listIterator();
        
        lastSel = -1;
        listModel.clear();
        listData.clear();
        
        while (it.hasNext()) {
            // auf FSA casten, den Namen entnehmen und in die Liste schreiben
            // insertList kann eine Exception werfen, da die Datei, obwohl richtig eingelesen, immer
            // noch fehlerhafte Daten (zb LinkedList<Objekt!=FSA>) enthalten kann
            currentFSA = (FSA)it.next();
            listModel.addElement(currentFSA.getName());
            listData.add(currentFSA);
        }
        autList.setSelectedIndex(listModel.getSize()-1);
        
        if (listData.size()>1)
            delAut.setEnabled(true);
    }
    
    // füge neuen Text in das Ausgabefenster ein
    public void insertResults(String resText) {
        if (results.getLineCount()>MAX_RESULT) results.setText("");
        results.append("\n---------------------------------------\n");
        results.append(resText);
    }
    
    /*
      Fügt einen leeren Automaten in die Liste ein
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
    
    
    // aufgerufen bei neuer Datei
    // Automatenliste wird gelöscht, ein Dummyautomat eingefügt
    // das Ausgabefenster wird zurückgesetzt
    public void reset() {
        lastSel = -1; // verhindert excep. im SelectionListener
        listModel.clear();
        listData.clear();
        results.setText("");
        insertResults("Welcome!");
        insertNewAut();
    }
    
    
    private String generateName() {
        String autName = new String("Aut");
        boolean okflag = false;
        
        do {
            autName = autName + (int)(Math.random()*100);
            if (!listModel.contains(autName)) {
                listModel.addElement(autName);
                okflag = true;
            } else {
                autName = "Aut";
            }
        } while (!okflag);
        return autName;
    }
    
    
}
