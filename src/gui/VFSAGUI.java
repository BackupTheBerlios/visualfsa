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

import java.awt.Color;
import java.awt.Point;
import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JSplitPane;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.util.LinkedList;
import java.io.IOException;

import io.FileIO;
import gui.dialogs.*;
import datastructs.AppOptions;
import datastructs.FSA;
import algo.FSAAlgo;
import algo.GenericAlgorithm;

public class VFSAGUI extends JFrame {
    
    private BottomBar bottom;
    private Sidebar side;
    private AutWindow autPane;
    private MainMenu menubar;
    
    private String filename;
    
    public static AppOptions options;
    
    public static final String verString = "visualFSA 0.2";
    
    public VFSAGUI() {
        super();
    }
    
    public void showGUI() {
        JSplitPane centerSplitter;
        
        getContentPane().setLayout(new BorderLayout());
        
        bottom = new BottomBar(this);
        getContentPane().add(bottom, BorderLayout.SOUTH);
        
        
        autPane = new AutWindow(this, false);
        
        side = new Sidebar(autPane);
        
        side.insertNewAut();
        
        centerSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, autPane, side);
        centerSplitter.setResizeWeight(0.8f);
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
        
        
        options = new AppOptions(true);
        
        try {
            FileIO.readOptions(options);
        } catch (Exception e) {
            System.err.println("could not read options");
        }
        
        
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
        boolean status = autPane.toFSA().accepts(w, false, false);
        
        // für das leere Wort wähle eine spezielle Darstellung
        if (w.length()==0)
            w = "\\epsilon";
        
        if (status) {
            side.insertResults(w+" in L("+autPane.getCurrentName()+")");
        } else {
            side.insertResults(w+" "+"is not recognized");
        }
    }
    
    
    // checkSave, fragt ob der user die aktuelle Datei speichern will
    // Rückgabewert gibt an ob die Aktion weiterhin ausgeführt werden soll (also im Fall != cancel)
    // mit justSave fungiert diese Methode als 'Speichern als'
    // needFileName gibt an ob ein Dialog zur Auswahl eines Dateinamen geöffnet werden soll
    public boolean checkSave(boolean justSave, boolean needFileName) {
        int res;
        String newFilename;
        
        if (justSave) {
            res = JOptionPane.YES_OPTION;
        } else {
            res = JOptionPane.showConfirmDialog(this,
                "Changes to this file will be lost, save it now?",
                "Notice", JOptionPane.YES_NO_CANCEL_OPTION);
        }
        
        switch(res) {
            case JOptionPane.YES_OPTION:
                // speichern (als), dann $aktion
                if (!needFileName && filename.length()==0) needFileName = true;
                if (needFileName) {
                    newFilename = FileIO.getSaveFilename(this, filename);
                    if (newFilename!=null)
                        filename = newFilename;
                    else
                        return true;
                }
                try {
                    FileIO.fsaListToFile(side.getList(), filename);
                    setTitle(verString+" - "+filename);
                    side.insertResults(filename+" saved");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error while writing, check permissions?!",
                        "Fehler", JOptionPane.ERROR_MESSAGE);
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
        
        optDlg = new OptionDlg(this, "Options", options);
        options = optDlg.run();
        
        autPane.setBackground(options.getColorValueForKey("BACKGROUND_COLOR", Color.WHITE));
        autPane.repaint();
        autPane.paintComponent(autPane.getGraphics());
        
        saveOptions();
    }
    
    
    /* speichert die aktuellen programmeinstellungen  */
    public void saveOptions() {
        try {
            FileIO.writeOptions(options);
        } catch (Exception e) {
            System.err.println("error while writing options!");
        }
    }
    
    /**
     * Öffnet einen Dateidialog in dem der Benutzer eine Datei auswählen kann.
     * Mögliche Fehler werden intern durch Exceptions abgefangen und über
     * ein JOptionPane ausgegeben
     */
    public void openFile() {
        String newFilename;
        LinkedList inData;
        
        FileIO ioObj = new FileIO();
        
        // user wählt datei aus...
        newFilename = FileIO.getOpenFilename(this);
        
        // wenn dieser in Ordnung ist..
        if (newFilename!=null) {
            try {
                // Daten einlesen, eventuell hier Exceptions
                inData = ioObj.fileToFsaList(newFilename);
                filename = newFilename;
                setTitle(verString+" - "+filename);
                // die eingelesene Liste mit Automaten einfügen, bei fehlerhaften
                // Daten in der Liste -> Exception
                side.insertList(inData);
            } catch(IOException ioEx) {
                JOptionPane.showMessageDialog(this,"(IO-Error "+ioEx.getMessage()+")",
                    "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception generalEx) {
                JOptionPane.showMessageDialog(this,
                    "(Read-Error "+
                    generalEx.getMessage()+")","Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
    /*
        (Versuch der) Spracherkennung
     */
    public void guessLang() {
        FSA currAut;
        GenericAlgorithm guessLangAlgo;
        BusyDialog waitDlg;
        
        waitDlg = new BusyDialog(this, "Patience", true);
        
        // den aktuellen Automaten bestimmen
        currAut = side.getCurrentAut();
        // guessLang selbst stösst nun einen Thread an der für alles weitere sorgt
        guessLangAlgo = FSAAlgo.guessLang(currAut);
        waitDlg.run(guessLangAlgo);
        side.insertResults(guessLangAlgo.getResult().toString());
        
    }
    
    
    /* automat determinisieren */
    public void determ() {
        FSA myAut;
        FSA result;
        BusyDialog waitDlg;
        GenericAlgorithm determAlgo;
        int oldAut;
        
        /* gui infos synchr. */
        myAut = side.getCurrentAut();
        oldAut = side.getCurrentSelection();
        
        /* zunächst ein paar unschöne Fälle abfangen, gegen die wir (momentan)
           noch keine Handhabe haben -.-
         */
        
        if (myAut.getStates().size()>=17) {
            JOptionPane.showMessageDialog(this, "This automaton has more than 16 states", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // nichts zu tun
        if (myAut.isDeterministic()) {
            JOptionPane.showMessageDialog(this, "This automaton is already a DFA");
            return;
        }
        
        determAlgo = FSAAlgo.determ(myAut);
        waitDlg = new BusyDialog(this, "Patience", true);
        waitDlg.run(determAlgo);
        result = ((FSA)determAlgo.getResult());
        result.setName(myAut.getName()+"_dfa");
        side.insertAut(result, options.getBoolValueForKey("REPLACE_AUT", false));
    }
    
    public void fitWindow() {
        Utilities.fitToWindow(side.getCurrentAut(), autPane);
        if (autPane.isGrid()) {
            Utilities.processGrid(autPane);
        }
    }
    
    public void alignToGrid() {
        Utilities.processGrid(autPane);
        autPane.setGridState(!autPane.isGrid());
    }
    
    public void newFile() {
        filename = "noname.fsa";
        setTitle(verString+" - "+filename);
        side.reset();
    }
    
}

