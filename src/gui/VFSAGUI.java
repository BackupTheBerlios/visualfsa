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

package gui;

import java.awt.Point;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import java.util.LinkedList;
import java.io.IOException;

import io.FileIO;
import gui.dialogs.*;
import datastructs.AppOptions;
import datastructs.FSA;
import algo.FSAAlgo;
import threads.LanguageThread;

public class VFSAGUI extends JFrame {
    
    private BottomBar bottom;
    private Sidebar side;
    private AutWindow autPane;
    private TopBar top;
    private MainMenu menubar;
    
    private String filename;
    
    public static AppOptions options;
    
    public static final String verString = "visualFSA";
    
    public VFSAGUI() {
        super();
    }
    
    public void showGUI() {
        JSplitPane centerSplitter;
        
        options = new AppOptions();
        options.loadOptions();
        
        getContentPane().setLayout(new BorderLayout());
        
        bottom = new BottomBar(this);
        getContentPane().add(bottom, BorderLayout.SOUTH);
        
        autPane = new AutWindow(this);
        
        side = new Sidebar(autPane);
        
        side.insertNewAut();
        
        top = new TopBar();
        getContentPane().add(top, BorderLayout.NORTH);
        
        centerSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, autPane, side);
        centerSplitter.setResizeWeight(0.95);
        centerSplitter.setOneTouchExpandable(true); 
        getContentPane().add(centerSplitter, BorderLayout.CENTER);
        
        menubar = new MainMenu(this, side);
        this.setJMenuBar(menubar);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        pack();
        setLocation(30,30);
        setVisible(true);
        
        // TODO, cmd-argumente
        newFile();
    }
    
    
    // starte die GUI in einem neuen Thread
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VFSAGUI visualFSA;
                visualFSA = new VFSAGUI();
                visualFSA.showGUI();
            }
        });
    }
    
    // eigentlich hässlich, jede Menge Dispatchmethoden :/
    
    public String getAutoTransition() {
        return bottom.getAutoTransition();
    }
    
    // der User hat in der BottomBar den Infobutton gedrückt
    // es wird in das Ausgabefenster eine textuelle Zusammenfassung
    // über den Automaten ausgegeben
    public void autInfo() {
        side.insertResults(autPane.toFSA().infoString());
    }
    
    // der User hat den Worterkennungs-button gedrpckt
    // das Wort wird dem Automaten übergeben, das Ergebnis
    // im Resultfenster angezeigt
    public void wInL(String w) {
        if (autPane.toFSA().accepts(w, false, false)) {
            side.insertResults(w+" ist in L("+autPane.getCurrentName()+")");
        } else {
            side.insertResults(w+" wird nicht erkannt");
        }
    }
    
    
    // checkSave, fragt ob der user die aktuelle Datei speichern will
    // Rückgabewert gibt an ob die Aktion weiterhin ausgeführt werden soll (also im Fall != cancel)
    // mit justSave fungiert diese Methode als 'Speichern als'
    public boolean checkSave(boolean justSave) {
        int res;
        String newFilename;

        if (justSave) {
            res = JOptionPane.YES_OPTION;
        } else {
            res = JOptionPane.showConfirmDialog(this,
                    "Diese Aktion verwirft die aktuelle Datei, vorher speichern?",
                    "Hinweis", JOptionPane.YES_NO_CANCEL_OPTION);
        }
        
        switch(res) {
            case JOptionPane.YES_OPTION:
                // speichern (als), dann $aktion
                newFilename = FileIO.getSaveFilename(this, filename);
                
                if (newFilename!=null) {
                    filename = newFilename;
                    FileIO.fsaListToFile(side.getList(), filename);
                    setTitle(verString+" - "+filename);
                }
                return true;
            case JOptionPane.NO_OPTION:
                // nicht speichern, dann $aktion
                return true;
        }
        return false; // implizit der cancel fall
    }
    
    
    /*
     * Öffnet den Optionsdialog, übergibt dem Dialog ein Optionen Objekt
     * mit den aktuellen durch den Benutzer gesetzten Optionen
     */
    public void showOptions() {
        OptionDlg optDlg;
        
        optDlg = new OptionDlg(this, "Optionen", options);
        options = optDlg.run();
        autPane.setBackground(options.getBackCol());
        autPane.repaint();
        autPane.paintComponents(autPane.getGraphics());
        options.saveOptions();
    }
    
    
    /**
     * Öffnet einen Dateidialog in dem der Benutzer eine Datei auswählen kann.
     * Mögliche Fehler werden intern durch Exceptions abgefangen und über
     * ein JOptionPane ausgegeben
     */
    public void openFile() {
        String newFilename;
        LinkedList inData;
        
        // user wählt datei aus...
        newFilename = FileIO.getOpenFilename(this);
        
        // wenn dieser in Ordnung ist..
        if (newFilename!=null) {
            try {
                // Daten einlesen, eventuell hier Exceptions
                inData = FileIO.fileToFsaList(newFilename);
                filename = newFilename;
                setTitle(verString+" - "+filename);
                // die eingelesene Liste mit Automaten einfügen, bei fehlerhaften
                // Daten in der Liste -> Exception
                side.insertList(inData);
            } catch(IOException ioEx) {
                JOptionPane.showMessageDialog(this,"IO-Fehler: ("+ioEx.getMessage()+")",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
            } catch (Exception generalEx) {
                generalEx.printStackTrace();
                JOptionPane.showMessageDialog(this,
                        "Lesefehler, meist falsches Dateiformat ("+
                        generalEx.getMessage()+")","Fehler",
                        JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
    /*
        öffnet einen modalen Dialgo in dem der User die Wortlänge bestimmen kann,
        bis zu der der Test vollzogen wird
     
        viel mieses Code-Gefrickel hier
     */
    public void guessLang() {
        final FSA currAut;
        final int wL;
        LanguageThread langThread;
        
        LangDialog langDlg = new LangDialog(this, "Maximale Wortlänge", true);
        
        // den aktuellen Automaten bestimmen
        currAut = side.getCurrentAut();
        langDlg.setAlphaSize(currAut.getAlphabet().size());
        langDlg.run();
        wL = langDlg.getWordLength();
        
        final BusyDialog busyDlg = new BusyDialog(this, "", true);

        langThread = new LanguageThread(currAut, wL, busyDlg, side);
        
        busyDlg.setWork(langThread);
        busyDlg.run();
    }
    
    
    /* automat determinisieren */
    public void determ() {
        FSA myAut;
        
        /* gui infos synchr. */
        myAut = side.getCurrentAut();
        
        // nichts zu tun
        if (myAut.isDeterministic()) {
            JOptionPane.showMessageDialog(this, "Automat ist bereits vom Typ DFA!");
            return;
        }
        
        autPane.insertAut(FSAAlgo.determ(myAut));
    }
    
    
    public void newFile() {
        filename = "unbenannt.fsa";
        setTitle(verString+" - "+filename);
        side.reset();
    }
    
}

