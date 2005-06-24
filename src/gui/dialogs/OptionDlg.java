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

/* hardcoded trash here */

package gui.dialogs;


import javax.swing.JPanel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.border.TitledBorder;
import java.awt.Color;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import datastructs.AppOptions;

public class OptionDlg extends JDialog {
    
    private AppOptions opt, old_opt;
    
    private JPanel currLineCol, currMarkCol, currCharCol, currBackCol, currTransCol;
    private JCheckBox askSave;
    
    public OptionDlg(Frame owner, String title, AppOptions _opt) {
        super(owner, title, true);
        opt = _opt;
        
        // Kopie für den Cancel-Fall
        old_opt = new AppOptions();
        old_opt.setOptionsFrom(opt);
    }
    
    
    
    public AppOptions run() {
        Container content;
        JPanel genOpts, buttons, colours, options;
        
        content = getContentPane();
        
        content.setLayout(new BorderLayout());
        
        askSave = new JCheckBox(java.util.ResourceBundle.getBundle("global").getString("askSave"));
        
        askSave.setToolTipText(java.util.ResourceBundle.getBundle("global").getString("askSaveTT"));

        askSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                opt.setAskSave(askSave.isSelected());
            }
        });
        
        // Panel erzeugen, Border hinzufügen, allgemeine Optionen
        genOpts = new JPanel();
        genOpts.setBorder(new TitledBorder(java.util.ResourceBundle.getBundle("global").getString("common")));
        genOpts.setLayout(new GridLayout(1,1));
        
        genOpts.add(askSave);
        
        // Panel für die Farboptionen
        
        colours = new JPanel();
        colours.setBorder(new TitledBorder(java.util.ResourceBundle.getBundle("global").getString("colors")));
        colours.setLayout(new GridLayout(5,2));
        
        JButton lineCol, backCol, charCol, markCol, transCol;

        lineCol = new JButton(java.util.ResourceBundle.getBundle("global").getString("lineColor"));
        lineCol.setToolTipText(java.util.ResourceBundle.getBundle("global").getString("lineColorTT"));
        currLineCol = new JPanel();
        currLineCol.setBackground(opt.getLineCol());
        
        backCol = new JButton(java.util.ResourceBundle.getBundle("global").getString("bg"));
        backCol.setToolTipText(java.util.ResourceBundle.getBundle("global").getString("bgTT"));
        currBackCol = new JPanel();
        
        charCol = new JButton(java.util.ResourceBundle.getBundle("global").getString("transChars"));
        charCol.setToolTipText(java.util.ResourceBundle.getBundle("global").getString("transCharsTT"));
        currCharCol = new JPanel();
        
        markCol = new JButton(java.util.ResourceBundle.getBundle("global").getString("markedState"));
        markCol.setToolTipText(java.util.ResourceBundle.getBundle("global").getString("markedStateTT"));
        currMarkCol = new JPanel();
        
        transCol = new JButton(java.util.ResourceBundle.getBundle("global").getString("specMark"));
        transCol.setToolTipText(java.util.ResourceBundle.getBundle("global").getString("specMarkTT"));
        currTransCol = new JPanel();
        
        colours.add(lineCol); colours.add(currLineCol);
        colours.add(backCol); colours.add(currBackCol);
        colours.add(charCol); colours.add(currCharCol);
        colours.add(markCol); colours.add(currMarkCol);
        colours.add(transCol); colours.add(currTransCol);
        
        lineCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getLineCol();
                c = getColor(old);
                if (c!=null) {
                    opt.setLineCol(c);
                    update();
                }
            }
        });
        
        charCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getCharCol();
                c = getColor(old);
                if (c!=null) {
                    opt.setCharCol(c);
                    update();
                }
            }
        });
        
        markCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getMarkCol();
                c = getColor(old);
                if (c!=null) {
                    opt.setMarkCol(c);
                    update();
                }
            }
        });
        
        transCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getTransCol();
                c = getColor(old);
                if (c!=null) {
                    opt.setTransCol(c);
                    update();
                }
            }
        });
        
        backCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getBackCol();
                c = getColor(old);
                if (c!=null) {
                    opt.setBackCol(c);
                    update();
                }
            }
        });
        
        // ok und cancel butten
        
        buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,3));
        
        JButton okButton, defaultButton, cancelButton;

        
        // Optionen anzeigen
        update();
        
        okButton = new JButton(java.util.ResourceBundle.getBundle("global").getString("OK"));
        defaultButton = new JButton(java.util.ResourceBundle.getBundle("global").getString("default"));
        cancelButton = new JButton(java.util.ResourceBundle.getBundle("global").getString("cancel"));

        
        defaultButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                opt.setDefault();
                update();
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                opt.setOptionsFrom(old_opt);
                dispose();
            }
        });
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        
        buttons.add(okButton);
        buttons.add(defaultButton);
        buttons.add(cancelButton);
        
        options = new JPanel();
        options.setLayout(new GridLayout(2,1));
        
        options.add(genOpts);
        options.add(colours);
        
        content.add(options, BorderLayout.CENTER);
        content.add(buttons, BorderLayout.SOUTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        pack();
        setLocation(70,70);
        setVisible(true);

        return opt;
    }
    
    private Color getColor(Color init) {
        return ( JColorChooser.showDialog(this, java.util.ResourceBundle.getBundle("global").getString("chooseColor"), init) );
    }
    
    // aktualisiert den Dialog mit den aktuellen Wert aus opt
    private void update() {
        currLineCol.setBackground(opt.getLineCol());
        currCharCol.setBackground(opt.getCharCol());
        currMarkCol.setBackground(opt.getMarkCol());
        currBackCol.setBackground(opt.getBackCol());
        currTransCol.setBackground(opt.getTransCol());
        askSave.setSelected(opt.getAskSave());
    }
    
}
