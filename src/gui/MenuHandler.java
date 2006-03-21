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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import gui.dialogs.AboutDialog;
import gui.dialogs.SelectAutDialog;

import static gui.MainMenu.MenuID;
import datastructs.FSA;

import algo.FSAAlgo;

/* Eventhandler für das Anwendungsmenü */

public class MenuHandler implements ActionListener {
    
    private VFSAGUI guiMain;
    private Sidebar guiSide;
    private Hashtable<JMenuItem, MainMenu.MenuID> entries;
    
    public MenuHandler(VFSAGUI _guiMain, Sidebar _guiSide,
        Hashtable<JMenuItem, MainMenu.MenuID> _entries) {
        guiMain = _guiMain;
        guiSide = _guiSide;
        entries = _entries;
    }
    
    
    public void actionPerformed(ActionEvent event) {
        JMenuItem eventSource = (JMenuItem)event.getSource();
        MainMenu.MenuID val = entries.get(eventSource);
        
        FSA currAut;
        int selectedAut;
        
        SelectAutDialog selAutDlg;
        
        switch (val) {
            case FILE_NEW:
                /* Datei -> Neu, prüfen ob die aktuelle Datei gespeichert werden soll */
                if (guiMain.options.getBoolValueForKey("ASKSAVE_OPTION", false)) {
                    if (guiMain.checkSave(false, true))
                        guiMain.newFile();
                } else {
                    guiMain.newFile();
                }
                
                break;
            case FILE_OPEN:
                /* Datei -> Öffnen, prüfen ob aktuelle Datei gespeichert werden soll */
                if (guiMain.options.getBoolValueForKey("ASKSAVE_OPTION", false)) {
                    if (guiMain.checkSave(false, true))
                        guiMain.openFile();
                } else {
                    guiMain.openFile();
                }
                break;
            case FILE_SAVE:
                guiMain.checkSave(true,false);
                break;
            case FILE_SAVEAS:
                guiMain.checkSave(true,true);
                break;
            case FILE_OPTIONS:
                guiMain.showOptions();
                break;
            case FILE_QUIT:
                guiMain.saveOptions();
                if (guiMain.options.getBoolValueForKey("ASKSAVE_OPTION", false)) {
                    if (guiMain.checkSave(false, true))
                        guiMain.dispose();
                } else {
                    guiMain.dispose();
                }
                break;
            case ALGO_REPLACE:
                guiMain.options.setBoolValueForKey("REPLACE_AUT", eventSource.isSelected());
                guiMain.saveOptions();
                break;
            case ALGO_UNION:
                selAutDlg = new SelectAutDialog(guiMain);
                selAutDlg.run(guiSide.getList());
                if (selAutDlg.getSelection()!=-1) {
                    currAut = guiSide.getCurrentAut();
                    FSA otherAut = guiSide.getList().get(selAutDlg.getSelection());
                    
                    if (!currAut.hasTransitions() || !otherAut.hasTransitions()) {
                        JOptionPane.showMessageDialog(guiMain, "At least one automaton does not have any transitions.", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    guiSide.insertAut(FSAAlgo.autUnion(currAut, otherAut), guiMain.options.getBoolValueForKey("REPLACE_AUT", false));
                }
                break;
            case ALGO_INTERSECT:
                selAutDlg = new SelectAutDialog(guiMain);
                selAutDlg.run(guiSide.getList());
                if (selAutDlg.getSelection()!=-1) {
                    currAut = guiSide.getCurrentAut();
                    FSA otherAut = guiSide.getList().get(selAutDlg.getSelection());
                    
                    guiSide.insertAut(FSAAlgo.autIntersect(currAut, otherAut), 
                        guiMain.options.getBoolValueForKey("REPLACE_AUT", false));
                }
                break;
            case ALGO_LANG:
                guiMain.guessLang();
                break;
            case ALGO_DETERM:
                guiMain.determ();
                break;
            case ALGO_REMISO:
                currAut = guiSide.getCurrentAut();
                 if (!currAut.isDeterministic()) {
                    JOptionPane.showMessageDialog(guiMain, "Only DFA can be minimized.", "Warning", JOptionPane.WARNING_MESSAGE);
                    break;
                }
                guiSide.insertAut(FSAAlgo.removeIsolatedStates(currAut), guiMain.options.getBoolValueForKey("REPLACE_AUT", false));
                break;
            case ALGO_COMPLEMENT:
                // muhahaha, easy ;)
                currAut = guiSide.getCurrentAut();
                currAut.setName(currAut.getName()+"_compl");
                currAut.invertStates();
                guiSide.insertAut(currAut, guiMain.options.getBoolValueForKey("REPLACE_AUT", false));
                break;
            case VIEW_FITWINDOW:
                guiMain.fitWindow();
                break;
            case VIEW_ALIGNGRID:
                guiMain.alignToGrid();
                break;
            case HELP_ABOUT:
                AboutDialog aboutDlg = new AboutDialog(guiMain,"About",true);
                aboutDlg.run();
                break;
            case HELP_HELP:
                HelpFrame myHelpFrame;
                myHelpFrame = new HelpFrame(guiMain,"Help", true);
                myHelpFrame.run();
                break;
            default:
                System.err.println("menu event not handled");
        }
    }
    
}
