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
import javax.swing.JProgressBar;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Frame;
import java.awt.BorderLayout;
import java.awt.GridLayout;

public class LangDialog extends JDialog {
    
    // größe des Eingabealphabets des Automaten, beschränkt
    // die maximale Wortlänge
    private int alphaSize;
    
    // ausgewählte Wortlänge
    private int wLen = 0;
    
    private JSpinner spin;
    
    public LangDialog(Frame parent, String title, boolean modal) {
        super(parent,title,modal);
    }
    
    public void setAlphaSize(int n) {
        alphaSize = n;
    }

    public int getWordLength() {
        return wLen;
    }
    
    public void run() {
        int max = 0;

        
        SpinnerNumberModel spinModel;

        getContentPane().setLayout(new BorderLayout());
        
        JPanel lowerPan = new JPanel();
        lowerPan.setLayout(new GridLayout(1,3));
        
        add(new JLabel("Maximale Wortlänge angeben:", SwingConstants.CENTER), BorderLayout.CENTER);
        
        // hier wird die Wortlänge begrenzt, bei mehr als 500k Wörter wird es kritisch mit dem Heap :/
        for (int k = 0 ; k < 50 ; k++) {
            ++max;
            if (Math.pow((double)alphaSize, (double)max) >= 400000) {
                --max;
                break;
            }
              
        }
        
        spinModel = new SpinnerNumberModel(1,1,max,1);
        spin = new JSpinner(spinModel);
                
        JButton start = new JButton("Ok");
        JButton cancel = new JButton("Abbruch");
        
        lowerPan.add(spin);
        lowerPan.add(start);
        lowerPan.add(cancel);
        
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                dispose();
            }
        });
        
        cancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent ev) {
                wLen = -1;
                dispose();
            }
        });
        
        spin.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent ev) {
                wLen = (int)((Integer)spin.getValue());
            }
        });
        
        getContentPane().add(lowerPan, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setLocation(90,90);

        pack();
        setVisible(true);
    }
    
    
}
