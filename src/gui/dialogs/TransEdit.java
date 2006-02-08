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
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Vector;

import gui.TransitionData;
import gui.AutWindow;
import gui.JState;

public class TransEdit extends JDialog {
    
    private JList transList;
    private DefaultListModel listMod;
    private JButton delButton, updateButton, okButton;
    private JTextField transChars;
    
    private LinkedHashMap<JState,TransitionData> data;
    private LinkedList<JState> backend;
    
    public TransEdit(Frame owner) {
        super(owner, "Edit Transitions", true);
    }
    
    public void run() {
        
        // listpanel enth�lt die Liste mit allen Transitionen, darunter ein
        // Textfeld zur Bearbeitung der Transitionszeichen
        transList = new JList();
        transChars = new JTextField();
        
        listMod = new DefaultListModel();
        
        backend = new LinkedList<JState>();
        
        for ( JState state : data.keySet() ) {
            listMod.addElement( data.get(state) );
            backend.addLast( state );
        }
        
        transList.setModel(listMod);
        transList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JPanel listPanel = new JPanel();
        
        listPanel.setLayout(new BorderLayout());
        
        listPanel.add(transList, BorderLayout.CENTER);
        listPanel.add(transChars, BorderLayout.SOUTH);
        
        JPanel buttonPanel = new JPanel();
        
        buttonPanel.setLayout(new GridLayout(1,3));
        
        delButton = new JButton("Remove");
        updateButton = new JButton("Refresh");
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
                    // tats�chlich hin :)
                    data.remove(backend.get(sel));
                    backend.remove(sel);
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
                        data.get(backend.get(sel)).setChars(newChars);
                        listMod.setElementAt(data.get(backend.get(sel)).toString(), sel);
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
                // es gerne h�tten, m�ssen auch wir wohl oder �bel den Char-Vec etwas anh�bschen
                if (sel!=-1) {
                    td = data.get(backend.get(sel));
                    for ( Character c : td.getChars() ) {
                        res.append(c);
                        res.append(",");
                    }
                    if (res.charAt(res.length()-1)==',')
                        res.setCharAt(res.length()-1,' ');
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
    
    
    public void setData(LinkedHashMap<JState,TransitionData> ld) {
        data = ld;
    }
    
}
