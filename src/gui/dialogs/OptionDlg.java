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
    private JCheckBox askSave, useAnti;
    
    public OptionDlg(Frame owner, String title, AppOptions _opt) {
        super(owner, title, true);
        
        opt = _opt;
        
        // deep-copy für den Cancel-Fall
        old_opt = _opt.copy();
    }
    
    public AppOptions run() {
        Container content;
        JPanel genOpts, buttons, colours, options;
        
        content = getContentPane();
        
        content.setLayout(new BorderLayout());
        
        askSave = new JCheckBox("ask user to save file before file operations");
        
        askSave.setToolTipText("if checked, the program asks if the current file should be save, before actions like 'new file' take place");

        useAnti = new JCheckBox("use anti aliasing");
        
        useAnti.setToolTipText("if checked, drawing surface looks nicer, but drawing is a bit slower");
        
        askSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                opt.setBoolValueForKey("ASKSAVE_OPTION", askSave.isSelected());
            }
        });
        
        useAnti.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                opt.setBoolValueForKey("ANTIALIAS_OPTION", useAnti.isSelected());
            }
        });
        
        // Panel erzeugen, Border hinzufügen, allgemeine Optionen
        genOpts = new JPanel();
        genOpts.setBorder(new TitledBorder("General Options"));
        genOpts.setLayout(new GridLayout(2,1));
        
        genOpts.add(askSave);
        genOpts.add(useAnti);
        
        // Panel für die Farboptionen
        
        colours = new JPanel();
        colours.setBorder(new TitledBorder("Colors"));
        colours.setLayout(new GridLayout(5,2));
        
        JButton lineCol, backCol, charCol, markCol, transCol;

        lineCol = new JButton("Line Color");
        lineCol.setToolTipText("color in which the lines and state boxes are drawn");
        currLineCol = new JPanel();
        
        backCol = new JButton("Background");
        backCol.setToolTipText("Background Color for the drawing surface");
        currBackCol = new JPanel();
        
        charCol = new JButton("Character Color");
        charCol.setToolTipText("Color used for the characters of a transition");
        currCharCol = new JPanel();
        
        markCol = new JButton("Marking Color");
        markCol.setToolTipText("marked states appear in this color");
        currMarkCol = new JPanel();
        
        transCol = new JButton("Special Mark:");
        transCol.setToolTipText("e.g states that currently marked for a new transitions are colored in this way");
        currTransCol = new JPanel();
        
        colours.add(lineCol); colours.add(currLineCol);
        colours.add(backCol); colours.add(currBackCol);
        colours.add(charCol); colours.add(currCharCol);
        colours.add(markCol); colours.add(currMarkCol);
        colours.add(transCol); colours.add(currTransCol);
        
        lineCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getColorValueForKey("LINE_COLOR", Color.BLACK);
                c = getColor(old);
                if (c!=null) {
                    opt.setColorValueForKey("LINE_COLOR", c);
                    update();
                }
            }
        });
        
        charCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getColorValueForKey("CHAR_COLOR", Color.RED);
                c = getColor(old);
                if (c!=null) {
                    opt.setColorValueForKey("CHAR_COLOR", c);
                    update();
                }
            }
        });
        
        markCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getColorValueForKey("MARK_COLOR", Color.YELLOW);
                c = getColor(old);
                if (c!=null) {
                    opt.setColorValueForKey("MARK_COLOR", c);
                    update();
                }
            }
        });
        
        transCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getColorValueForKey("SPECIAL_COLOR", Color.GREEN);
                c = getColor(old);
                if (c!=null) {
                    opt.setColorValueForKey("SPECIAL_COLOR", c);
                    update();
                }
            }
        });
        
        backCol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Color c, old;
                old = opt.getColorValueForKey("BACKGROUND_COLOR", Color.WHITE);
                c = getColor(old);
                if (c!=null) {
                    opt.setColorValueForKey("BACKGROUND_COLOR", c);
                    update();
                }
            }
        });
        
        // ok und cancel butten
        
        buttons = new JPanel();
        buttons.setLayout(new GridLayout(1,3));
        
        JButton okButton, cancelButton;

        
        // Optionen anzeigen
        update();
        
        okButton = new JButton("OK");
        cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                opt = old_opt;
                dispose();
            }
        });
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        
        buttons.add(okButton);
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
        return ( JColorChooser.showDialog(this,"Choose a Color" , init) );
    }
    
    // aktualisiert den Dialog mit den aktuellen Wert aus opt
    private void update() {
        currLineCol.setBackground(opt.getColorValueForKey("LINE_COLOR", Color.BLACK));
        currCharCol.setBackground(opt.getColorValueForKey("CHAR_COLOR", Color.RED));
        currMarkCol.setBackground(opt.getColorValueForKey("MARK_COLOR", Color.YELLOW));
        currBackCol.setBackground(opt.getColorValueForKey("BACKGROUND_COLOR", Color.WHITE));
        currTransCol.setBackground(opt.getColorValueForKey("SPECIAL_COLOR", Color.GREEN));
        askSave.setSelected(opt.getBoolValueForKey("ASKSAVE_OPTION", false));
        useAnti.setSelected(opt.getBoolValueForKey("ANTIALIAS_OPTION", true));
    }
    
}
