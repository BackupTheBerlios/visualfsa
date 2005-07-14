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
import gui.runvis.RunVisual;

public class VFSAGUI extends JFrame {
    
    private BottomBar bottom;
    private Sidebar side;
    private AutWindow autPane;
    private TopBar top;
    private MainMenu menubar;
    
    private String filename;
    
    public static AppOptions options;
    
    public static final String verString = "visualFSA 0.1b";
    
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
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                VFSAGUI visualFSA;
                visualFSA = new VFSAGUI();
                visualFSA.showGUI();
            }
        });
    }
    
    // eigentlich h�sslich, jede Menge Dispatchmethoden :/
    
    public String getAutoTransition() {
        return bottom.getAutoTransition();
    }
    
    // der User hat in der BottomBar den Infobutton gedr�ckt
    // es wird in das Ausgabefenster eine textuelle Zusammenfassung
    // �ber den Automaten ausgegeben
    public void autInfo() {
        side.insertResults(autPane.toFSA().infoString());
    }
    
    // der User hat den Worterkennungs-button gedrpckt
    // das Wort wird dem Automaten �bergeben, das Ergebnis
    // im Resultfenster angezeigt
    public void wInL(String w) {
        boolean status = autPane.toFSA().accepts(w, false, false);
        
        // f�r das leere Wort w�hle eine spezielle Darstellung
        if (w.length()==0)
            w = "\\epsilon";
        
        if (status) {
            side.insertResults(w+" in L("+autPane.getCurrentName()+")");
        } else {
            side.insertResults(w+" "+java.util.ResourceBundle.getBundle("global").getString("notAccept"));
        }
    }
    
    
    // checkSave, fragt ob der user die aktuelle Datei speichern will
    // R�ckgabewert gibt an ob die Aktion weiterhin ausgef�hrt werden soll (also im Fall != cancel)
    // mit justSave fungiert diese Methode als 'Speichern als'
    // needFileName gibt an ob ein Dialog zur Auswahl eines Dateinamen ge�ffnet werden soll
    public boolean checkSave(boolean justSave, boolean needFileName) {
        int res;
        String newFilename;
        
        if (justSave) {
            res = JOptionPane.YES_OPTION;
        } else {
            res = JOptionPane.showConfirmDialog(this,
                    java.util.ResourceBundle.getBundle("global").getString("discardWarn"),
                    java.util.ResourceBundle.getBundle("global").getString("Hint"), JOptionPane.YES_NO_CANCEL_OPTION);
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
                    setTitle(verString+"-"+filename);
                    side.insertResults(filename+" gesichert");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Fehler beim Schreiben, pr�fen Sie Dateinamen/rechte",
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
     * �ffnet den Optionsdialog, �bergibt dem Dialog ein Optionen Objekt
     * mit den aktuellen durch den Benutzer gesetzten Optionen
     */
    public void showOptions() {
        OptionDlg optDlg;
        
        optDlg = new OptionDlg(this, java.util.ResourceBundle.getBundle("global").getString("options"), options);
        options = optDlg.run();
        autPane.setBackground(options.getBackCol());
        autPane.repaint();
        autPane.paintComponents(autPane.getGraphics());
        options.saveOptions();
    }
    
    
    /**
     * �ffnet einen Dateidialog in dem der Benutzer eine Datei ausw�hlen kann.
     * M�gliche Fehler werden intern durch Exceptions abgefangen und �ber
     * ein JOptionPane ausgegeben
     */
    public void openFile() {
        String newFilename;
        LinkedList inData;
        
        FileIO ioObj = new FileIO();
        
        // user w�hlt datei aus...
        newFilename = FileIO.getOpenFilename(this);
        
        // wenn dieser in Ordnung ist..
        if (newFilename!=null) {
            try {
                // Daten einlesen, eventuell hier Exceptions
                inData = ioObj.fileToFsaList(newFilename);
                filename = newFilename;
                setTitle(verString+"-"+filename);
                // die eingelesene Liste mit Automaten einf�gen, bei fehlerhaften
                // Daten in der Liste -> Exception
                side.insertList(inData);
            } catch(IOException ioEx) {
                JOptionPane.showMessageDialog(this,java.util.ResourceBundle.getBundle("global").getString("ioErr")+ioEx.getMessage()+")",
                        java.util.ResourceBundle.getBundle("global").getString("Error"), JOptionPane.ERROR_MESSAGE);
            } catch (Exception generalEx) {
                JOptionPane.showMessageDialog(this,
                        java.util.ResourceBundle.getBundle("global").getString("readErr")+
                        generalEx.getMessage()+")",java.util.ResourceBundle.getBundle("global").getString("Error"),
                        JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
    /*
        �ffnet einen modalen Dialgo in dem der User die Wortl�nge bestimmen kann,
        bis zu der der Test vollzogen wird
     
        viel mieses Code-Gefrickel hier
     */
    public void guessLang() {
        final FSA currAut;
        final int wL;
        LanguageThread langThread;
        
        // den aktuellen Automaten bestimmen
        currAut = side.getCurrentAut();
        
        LangDialog langDlg = new LangDialog(this, java.util.ResourceBundle.getBundle("global").getString("maxWordLen"), true);
        
        langDlg.setAlphaSize(currAut.getAlphabet().size());
        langDlg.run();
        wL = langDlg.getWordLength();
        
        // User hat cancel gedr�ckt
        if (wL==-1) return;
        
        final BusyDialog busyDlg = new BusyDialog(this, "", true);
        
        langThread = new LanguageThread(currAut, wL, busyDlg, side);
        
        busyDlg.setWork(langThread);
        busyDlg.run();
    }
    
    
    // Laufvisualisierung starten
    public void runvis() {
        RunVisual runvisDialog;
    
        runvisDialog = new RunVisual(this);
        runvisDialog.run(side.getCurrentAut());
    }
    
    
    /* automat determinisieren */
    public void determ() {
        FSA myAut;
        FSA result;
        
        /* gui infos synchr. */
        myAut = side.getCurrentAut();
        
        // nichts zu tun
        if (myAut.isDeterministic()) {
            JOptionPane.showMessageDialog(this, java.util.ResourceBundle.getBundle("global").getString("isDFA"));
            return;
        }
        
        result = FSAAlgo.determ(myAut);
        
        result.setName(myAut.getName()+"_dfa");
        
        side.insertAut( result );
    }
    
    public void fitWindow() {
        Utilities.fitToWindow(side.getCurrentAut(), autPane);
    }
    
    public void newFile() {
        filename = "noname.fsa";
        setTitle(verString+" - "+filename);
        side.reset();
    }
    
}

