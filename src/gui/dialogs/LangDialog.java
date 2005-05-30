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

import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JTextArea;
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class LangDialog extends JDialog {

    private JProgressBar pBar;
    private JSpinner spin;
    private SpinnerNumberModel spinModel;
    
    public LangDialog(Frame parent, String title, boolean modal) {
        super(parent,title,modal);
    }
    
    
    public void run() {
        getContentPane().setLayout(new BorderLayout());
        
        JPanel lowerPan = new JPanel();
        lowerPan.setLayout(new GridLayout(2,3));
        
        JTextArea caution = new JTextArea("Hinweis: Der (Versuch) die erkannte Sprache des Automaten anzugeben arbeitet " +
                "in visualFSA wie folgt: Das Programm erzeugt alle Wörter über dem Eingabealpha bis zur einer festen Länge n. " +
                "Da dabei extrem viele Wörter entstehen und auch gespeichert werden müssen (das Programm erstellt aus denn " +
                "Wörter der Länge k-1 die Wörter der Länge k) wird die maximale Wörtlänge limitiert. (Ein Alpabet mit k Zeichen " +
                "erzeugt bei Wortlänge m k^m viele Wörter.)"
          );
        
        caution.setLineWrap(true);
        caution.setWrapStyleWord(true);
        caution.setEditable(false);
        
        getContentPane().add(caution, BorderLayout.CENTER);
        
        JLabel current = new JLabel(" ");
        
        pBar = new JProgressBar(JProgressBar.HORIZONTAL);
        pBar.setMinimum(0); pBar.setMaximum(100); pBar.setValue(0);
        
        lowerPan.add(new JLabel("Fortschritt:"));
        lowerPan.add(current);
        lowerPan.add(pBar, BorderLayout.WEST);

        spinModel = new SpinnerNumberModel(3,1,10,1);
        spin = new JSpinner(spinModel);
                
        JButton start = new JButton("Start");
        JButton cancel = new JButton("Abbruch");
        
        lowerPan.add(spin);
        lowerPan.add(start, BorderLayout.SOUTH);
        lowerPan.add(cancel, BorderLayout.SOUTH);
        
        getContentPane().add(lowerPan, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLocation(40,40);
        setSize(400,200);
        setVisible(true);
    }
    
    
}
