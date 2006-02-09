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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.awt.BorderLayout;
import javax.swing.JScrollPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.DefaultListModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.util.LinkedList;

import datastructs.FSA;

public class SelectAutDialog extends JDialog {
    
    private int selection;
    private JList autList;
    
    public SelectAutDialog(Frame owner) {
        super(owner, "Select another automaton", true);
        selection = -1;
    }
    
    public void run(LinkedList<FSA> auts) {
        JButton cancelButton, okButton;
        JPanel buttonPanel;
        JScrollPane listScroller;
        
        DefaultListModel listModel = new DefaultListModel();
        
        // liste vorbereiten
        for ( FSA aut : auts ) {
            listModel.addElement(aut.getName());
        }
        
        autList = new JList();
        autList.setModel(listModel);
        listScroller = new JScrollPane(autList);
        
        okButton = new JButton("Ok");
        cancelButton = new JButton("Cancel");
        buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        
        /*autList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                selection = autList.getSelectedIndex();
            }
        });*/
        
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selection = -1;
            }
        });
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                selection = autList.getSelectedIndex();
                dispose();
            }
        });
        
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(listScroller, BorderLayout.CENTER);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        setSize(300,300);
        setLocation(80,80);
        setVisible(true);
    }
    
    public int getSelection() {
        return selection;
    }
    
}
