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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import javax.swing.JMenuItem;

import gui.dialogs.LangDialog;

import static gui.MainMenu.MenuID;
import datastructs.FSA;

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
        
        switch (val) {
            case FILE_NEW:
                /* Datei -> Neu, prüfen ob die aktuelle Datei gespeichert werden soll */
                if (guiMain.options.getAskSave()) {
                    if (guiMain.checkSave(false, true))
                        guiMain.newFile();    
                }
                else {
                    guiMain.newFile();
                }
                
                break;
            case FILE_OPEN:
                /* Datei -> Öffnen, prüfen ob aktuelle Datei gespeichert werden soll */
                if (guiMain.options.getAskSave()) {
                    if (guiMain.checkSave(false, true))
                        guiMain.openFile();
                }
                else {
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
                if (guiMain.options.getAskSave()) {
                    if (guiMain.checkSave(false, true))
                      guiMain.dispose();
                }
                break;
            case ALGO_LANG:
                guiMain.guessLang();
                break;
            case ALGO_DETERM:
                guiMain.determ();
                break;
            case VIEW_FITWINDOW:
                guiMain.fitWindow();
                break;
            default:
                System.out.println("not implemented yet");
        }
    }
    
}
