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

import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.border.BevelBorder;
import javax.swing.JTextArea;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class AboutDialog extends JDialog {
    
    public AboutDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    
    public void run() {
        final JDialog mySelf = this;
        
        JPanel desc = new JPanel();
        JPanel buttonPan = new JPanel();
        
        JTextArea about_stuff = new JTextArea();
        JButton okButton, someFun;
        
        about_stuff.setEditable(false);
        about_stuff.setText("This is visualFSA\n\n" +
                "" +
                "Actually, this tool is just some kind of a 'proof of concept'.\n" +
                "Its neither some high-scientific program, nor is it aiming to be such a tool.\n" +
                "Beyond this, it might become handy for some students solving their tasks in some\n" +
                "theory courses (probably even for some prof preparing such a course) :)\n"+
                "It is not intended to solve the NP/P problem or calculating\n" +
                "the influences of a nuclear strike on cockroaches ;-)\n\n"+
                "This program is covered by the GPL, you should have received a copy\n" +
                "of the complete license along with it.\n\n" +
                "visit http://visualfsa.berlios.de for more infos/contact");
        
        desc.add(about_stuff);
        desc.setBorder(new BevelBorder(BevelBorder.RAISED));
        
        getContentPane().setLayout(new BorderLayout());
        
        getContentPane().add(desc, BorderLayout.CENTER);
        
        okButton = new JButton("Ok");
        
        someFun = new JButton("I'm bored...");
        
        buttonPan.setLayout(new GridLayout(1,2));
        
        buttonPan.add(okButton);
        buttonPan.add(someFun);
        
        buttonPan.setBorder(new BevelBorder(BevelBorder.RAISED));
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        someFun.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
                JOptionPane.showMessageDialog(mySelf,"So, lets draw some Pythagoras Trees :)","More theory!", JOptionPane.INFORMATION_MESSAGE);
                PyTreeDialog pyTreeDlg = new PyTreeDialog();
                pyTreeDlg.run();
            }
        });
        
        getContentPane().add(buttonPan, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        setLocation(60,70);
        
        pack();
        setVisible(true);
        
        
    }
    
}
