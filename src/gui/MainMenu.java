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

import java.util.Hashtable;
import java.util.Enumeration;
import java.awt.event.KeyEvent;
import java.awt.event.InputEvent;
import javax.swing.KeyStroke;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;

public class MainMenu extends JMenuBar {
    
    public static enum MenuID {
        FILE_NEW, FILE_OPEN, FILE_SAVE, FILE_SAVEAS, FILE_QUIT, FILE_OPTIONS,
        ALGO_LANG, ALGO_DETERM, ALGO_RUNVIS, ALGO_REMISO,
        VIEW_FITWINDOW, VIEW_ALIGNGRID,
        HELP_HELP, HELP_ABOUT
    }
    
    private MenuHandler genericHandler;
    
    private Hashtable<JMenuItem, MenuID> entries;
    
    /*
      Erzeuge das Hauptmen� der Anwendung
     
      Die einzelnen JMenuItem Objekte werden mit einer benannten Konstante
      (aus der Aufz�hlung MenuID) als Schl�ssel in eine Hashtable getan.
      F�r das gesamte Anwendungsmen� wird dann ein einziger Eventhandler bereit-
      gestellt, welcher anhand der dieser ID und der Referenz (die man �ber ActionEvent.getSource erh�lt)
      dann die n�tigen Aktionen veranlasst.
     */
    public MainMenu(VFSAGUI topLevel, Sidebar guiSide) {
        super();
        
        entries = new Hashtable<JMenuItem, MenuID>();
        
        JMenu file = new JMenu("File");
        
        file.setMnemonic('F');
        
        /* Men� - Datei - */
        JMenuItem newFile = new JMenuItem("New");
        
        newFile.setMnemonic('N');        
        newFile.setAccelerator(KeyStroke.getKeyStroke('N',InputEvent.CTRL_MASK));
        
        JMenuItem openFile = new JMenuItem("Open");
        
        openFile.setMnemonic('O');
        openFile.setAccelerator(KeyStroke.getKeyStroke('O',InputEvent.CTRL_MASK));
        
        JMenuItem saveFile = new JMenuItem("Save");
        
        saveFile.setMnemonic('S');
        saveFile.setAccelerator(KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK));
        
        JMenuItem saveasFile = new JMenuItem("Save as...");
        
        saveasFile.setMnemonic('e');
        
        JMenuItem options = new JMenuItem("Options");
        
        options.setMnemonic('P');
        
        JMenuItem quit = new JMenuItem("Quit");
        
        quit.setMnemonic('Q');
        quit.setAccelerator(KeyStroke.getKeyStroke('Q',InputEvent.CTRL_MASK));
        
        entries.put(newFile, MenuID.FILE_NEW);
        entries.put(openFile, MenuID.FILE_OPEN);
        entries.put(saveFile, MenuID.FILE_SAVE);
        entries.put(saveasFile, MenuID.FILE_SAVEAS);
        entries.put(options, MenuID.FILE_OPTIONS);
        entries.put(quit, MenuID.FILE_QUIT);
        
        file.add(newFile);
        file.add(openFile);
        file.add(saveFile);
        file.add(saveasFile);
        file.addSeparator();
        file.add(options);
        file.addSeparator();
        file.add(quit);
        
        add(file);
        
        JMenu algo = new JMenu("Algorithms");
        
        algo.setMnemonic('A');
        
        JMenuItem algo_lang = new JMenuItem("Accepted Language");
        algo_lang.setMnemonic('L');
        
        JMenuItem algo_determ = new JMenuItem("NFA -> DFA");
        algo_determ.setMnemonic('D');
        
        JMenuItem algo_runvis = new JMenuItem("Run Visualization");
        algo_runvis.setMnemonic('R');
        
        JMenuItem algo_remiso = new JMenuItem("Remove Isolated States");
        algo_remiso.setMnemonic('I');
        
        entries.put(algo_remiso, MenuID.ALGO_REMISO);
        entries.put(algo_runvis, MenuID.ALGO_RUNVIS);
        entries.put(algo_lang, MenuID.ALGO_LANG);
        entries.put(algo_determ, MenuID.ALGO_DETERM);
        
        algo.add(algo_lang);
        algo.add(algo_determ);
        algo.add(algo_runvis);
        algo.add(algo_remiso);
        
        add(algo);
        
        JMenu view_menu = new JMenu("View");
        
        view_menu.setMnemonic('V');
        
        JMenuItem view_fitwin = new JMenuItem("Fit in Window");
        
        view_fitwin.setMnemonic('W');
        view_fitwin.setAccelerator(KeyStroke.getKeyStroke('F',InputEvent.CTRL_MASK));
        
        JMenuItem view_grid = new JMenuItem("Align to Grid");
        
        view_grid.setMnemonic('G');
        view_grid.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.CTRL_MASK));
        
        view_menu.add(view_fitwin);
        view_menu.add(view_grid);
        
        add(view_menu);
        
        entries.put(view_fitwin, MenuID.VIEW_FITWINDOW);
        entries.put(view_grid, MenuID.VIEW_ALIGNGRID);
        
        
        JMenu help = new JMenu("Help");
        
        help.setMnemonic('H');
        
        JMenuItem help_help = new JMenuItem("Documentation");
        
        help_help.setMnemonic('D');
        help_help.setAccelerator(KeyStroke.getKeyStroke("F1"));
        
        
        JMenuItem help_about = new JMenuItem("About");
        help_about.setMnemonic('t');
        
        help.add(help_help);
        help.addSeparator();
        help.add(help_about);
        
        add(help);
        
        entries.put(help_help, MenuID.HELP_HELP);
        entries.put(help_about, MenuID.HELP_ABOUT);
        
        genericHandler = new MenuHandler(topLevel, guiSide, entries);
        
        // registriere f�r alle Men�eintr�ge den EventHandler
        Enumeration<JMenuItem> menuEnum = entries.keys();
        
        while (menuEnum.hasMoreElements()) {
            menuEnum.nextElement().addActionListener(genericHandler);
        }
        
    }
    
}
