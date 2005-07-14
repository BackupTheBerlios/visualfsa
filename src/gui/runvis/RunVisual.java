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


package gui.runvis;

import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.Container;
import java.awt.BorderLayout;

import gui.AutWindowAnimator;
import datastructs.FSA;

import threads.RunVisualThread;

public class RunVisual extends JDialog {
    
    private JSplitPane splitter;
    private AutWindowAnimator surface;
    private JTextArea textLog;
    private JScrollPane textLogScroll;
    private WordAnimation wordAni;
    private JTextField testWord;
    private JButton quit, start;
    
    public RunVisual(Frame owner) {
        super(owner, "Laufvisualisierung", true);
    }
    
    public void run(final FSA aut) {
        
        final RunVisual mySelf = this;
        
        // textfeld für die textuelle Laufanzeige
        textLog = new JTextArea();
        textLogScroll = new JScrollPane(textLog);
        
        this.add(textLogScroll, BorderLayout.WEST);
        
        // ein autwindow mit static = true, hat zwar alle
        // Methoden wie das Zeichenfenster, sind aber deaktiviert
        surface = new AutWindowAnimator();
        
        splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, surface, textLogScroll);
        splitter.setResizeWeight(0.8f);
        
        this.add(splitter, BorderLayout.CENTER);
        
        surface.insertAut(aut);
        
        wordAni = new WordAnimation("");
        
        this.add(wordAni, BorderLayout.SOUTH);
        
        // top panel mit den Bedienelementen
        JPanel topPan = new JPanel();
        
        start = new JButton("Start");
        quit = new JButton("Beenden");
        testWord = new JTextField("");
        
        topPan.setLayout(new BorderLayout());
        topPan.add(start, BorderLayout.EAST);
        topPan.add(testWord, BorderLayout.CENTER);
        topPan.add(quit, BorderLayout.WEST);
        
        this.add(topPan, BorderLayout.NORTH);
        
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        
        // actionhandler registieren
        start.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // starte den Anithread
                RunVisualThread myThread;
                
                // protokoll verhalten muss explizit gesetzt werden
                aut.setLog(true);
                aut.accepts(testWord.getText(), false, false);
                aut.setLog(false);
                
                myThread = new RunVisualThread(wordAni, mySelf, surface, aut,
                                                    aut.getLastLog(), testWord.getText());
                
                try {
                    myThread.start();
                } catch(Exception ex) {
                    
                }
                
            }
        });
        
        pack();
        
        Dimension aniDim =
                new Dimension(getWidth(), 40);

        // Dimension des WordAni Objekts festlegen
        wordAni.setPreferredSize(aniDim);
        
        setSize(500,300);
        setVisible(true);
        
    }
    
    
    
    public void insertLog(String text) {
        textLog.append(text+"\n");
    }
    
    
}
