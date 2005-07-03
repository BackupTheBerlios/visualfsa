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

package gui.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.Vector;

import gui.TransitionData;
import gui.AutWindow;

public class TransEdit extends JDialog {
    
    private JList transList;
    private DefaultListModel listMod;
    private JButton delButton, updateButton, okButton;
    private JTextField transChars;
    
    private LinkedList<TransitionData> data;
    
    public TransEdit(Frame owner) {
        super(owner, "Transitionen editieren", true);
    }
    
    public void run() {
        
        // listpanel enthält die Liste mit allen Transitionen, darunter ein
        // Textfeld zur Bearbeitung der Transitionszeichen
        transList = new JList();
        transChars = new JTextField();
        
        listMod = new DefaultListModel();
        
        for (int i = 0 ; i < data.size() ; i++ ) {
            listMod.addElement(data.get(i));
        }
        
        transList.setModel(listMod);
        transList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel listPanel = new JPanel();
        
        listPanel.setLayout(new BorderLayout());
        
        listPanel.add(transList, BorderLayout.CENTER);
        listPanel.add(transChars, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setLayout(new GridLayout(1,3));
        
        delButton = new JButton("Entfernen");
        updateButton = new JButton("Aktualisieren");
        okButton = new JButton("OK");
        
        buttonPanel.add(okButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(delButton);
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                dispose();
            }
        });
        
        delButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                int sel = transList.getSelectedIndex();
                if (sel!=-1) {
                    // !!!! wir arbeiten auf der Originalreferenz, die Transition ist dann
                    // tatsächlich hin :)
                    data.remove(sel);
                    listMod.remove(sel);
                    transList.setSelectedIndex(listMod.size()-1);
                    if (listMod.isEmpty()) {
                        delButton.setEnabled(false);
                    }
                }
            }
        });
        
        updateButton.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent event) {
                Vector<Character> newChars;
                int sel = transList.getSelectedIndex();
                
                if (sel!=-1) {
                    newChars = AutWindow.parseTransChars(transChars.getText());
                    
                    // wenn != null, war zumindest ein sinniges Zeichen dabei
                    if (newChars!=null) {
                        data.get(sel).setChars(newChars);
                        listMod.setElementAt(data.get(sel).toString(), sel);
                    }
                }
            }
        });
        
        transList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent lEvent) {
                int sel = transList.getSelectedIndex();
                TransitionData td;
                StringBuffer res = new StringBuffer();
                
                // da wir vom User erwarten das er die Zeichen so eingibt wie wir
                // es gerne hätten, müssen auch wir wohl oder übel den Char-Vec etwas anhübschen
                if (sel!=-1) {
                    td = data.get(sel);
                    for ( Iterator<Character> it = td.getChars().iterator(); it.hasNext(); ) {
                        res.append(it.next());
                        if (it.hasNext()) res.append(",");
                    }
                    
                    transChars.setText(res.toString());
                }
            }
        });
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(listPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        pack();
        setLocation(80,80);
        setSize(250,200);
        setVisible(true);
        
    }
    
    
    public void setData(LinkedList<TransitionData> ld) {
        data = ld;
    }
    
}
