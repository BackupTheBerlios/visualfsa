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

package gui;

import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import gui.dialogs.TransEdit;

public class StatePopup extends JPopupMenu {
    
    private JMenuItem edit, delete, finalst, startst;
    private JState invoker;
    private VFSAGUI myGUI;
    
    public StatePopup(VFSAGUI _myGUI) {
        edit = new JMenuItem("Edit Transitions");
        delete = new JMenuItem("Remove");
        startst = new JCheckBoxMenuItem("Start-State");
        finalst = new JCheckBoxMenuItem("Final-State");
        
        add(edit);
        add(delete);
        addSeparator();
        add(finalst);
        add(startst);
        
        myGUI = _myGUI;
        
        // Transitionen editieren
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                TransEdit myTEdlg;
                
                myTEdlg = new TransEdit(myGUI);
                
                myTEdlg.setData(invoker.getTransList());
                
                myTEdlg.run();
                invoker.getParent().repaint();
            }
        });
        
        // Zustand l�schen
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                AutWindow mainWindow;
                // l�sche den ausl�senden Zustand
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
        
        // lasse nicht zu das der letzte Zustand entfernt werden kann
        delete.setEnabled(((AutWindow)invoker.getParent()).getComponentCount()!=1);
        edit.setEnabled(invoker.getTransList().size()!=0);
        
        this.show(who, AutWindow.STATE_HALFSIZE,
                AutWindow.STATE_HALFSIZE);
    }
    
}
