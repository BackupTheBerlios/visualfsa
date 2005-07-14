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
        ALGO_LANG, ALGO_DETERM, ALGO_RUNVIS,
        VIEW_FITWINDOW
    }
    
    private MenuHandler genericHandler;
    
    private Hashtable<JMenuItem, MenuID> entries;
    
    /*
      Erzeuge das Hauptmenü der Anwendung
     
      Die einzelnen JMenuItem Objekte werden mit einer benannten Konstante
      (aus der Aufzählung MenuID) als Schlüssel in eine Hashtable getan.
      Für das gesamte Anwendungsmenü wird dann ein einziger Eventhandler bereit-
      gestellt, welcher anhand der dieser ID und der Referenz (die man über ActionEvent.getSource erhält)
      dann die nötigen Aktionen veranlasst.
     */
    public MainMenu(VFSAGUI topLevel, Sidebar guiSide) {
        super();
        
        entries = new Hashtable<JMenuItem, MenuID>();
        
        JMenu file = new JMenu(java.util.ResourceBundle.getBundle("global").getString("file"));
        
        /* Menü - Datei - */
        JMenuItem newFile = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("new"),
                new ImageIcon("images/filenew.png"));
        
        newFile.setAccelerator(KeyStroke.getKeyStroke('N',InputEvent.CTRL_MASK));
        
        JMenuItem openFile = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("open"),
                new ImageIcon("images/fileopen.png"));
        
        openFile.setAccelerator(KeyStroke.getKeyStroke('O',InputEvent.CTRL_MASK));
        
        JMenuItem saveFile = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("save"),
                new ImageIcon("images/filesave.png"));
        
        saveFile.setAccelerator(KeyStroke.getKeyStroke('S',InputEvent.CTRL_MASK));
        
        JMenuItem saveasFile = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("saveas"),
                new ImageIcon("images/filesaveas.png"));
        JMenuItem options = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("options"),
                new ImageIcon("images/options.png"));
        
        JMenuItem quit = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("quit"),
                new ImageIcon("images/exit.png"));
        
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
        
        JMenu algo = new JMenu(java.util.ResourceBundle.getBundle("global").getString("algo"));
        
        JMenuItem algo_lang = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("guessLang"),
                new ImageIcon("images/lang.png"));
        
        JMenuItem algo_determ = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("determ"),
                new ImageIcon("images/determ.png"));
        
        JMenuItem algo_runvis = new JMenuItem("Laufvisualisierung",
                new ImageIcon("images/runvis.png"));
        
        entries.put(algo_runvis, MenuID.ALGO_RUNVIS);
        entries.put(algo_lang, MenuID.ALGO_LANG);
        entries.put(algo_determ, MenuID.ALGO_DETERM);
        
        algo.add(algo_lang);
        algo.add(algo_determ);
        algo.add(algo_runvis);
        
        add(algo);
        
        JMenu view_menu = new JMenu(java.util.ResourceBundle.getBundle("global").getString("view"));
        
        JMenuItem view_fitwin = new JMenuItem(java.util.ResourceBundle.getBundle("global").getString("fittowindowMenu"),
                new ImageIcon("images/fitinwindow.png"));
        
        view_fitwin.setAccelerator(KeyStroke.getKeyStroke('F',InputEvent.CTRL_MASK));
        
        view_menu.add(view_fitwin);
        
        add(view_menu);
        
        entries.put(view_fitwin, MenuID.VIEW_FITWINDOW);
        
        genericHandler = new MenuHandler(topLevel, guiSide, entries);
        
        // registriere für alle Menüeinträge den EventHandler
        Enumeration<JMenuItem> menuEnum = entries.keys();
        
        while (menuEnum.hasMoreElements()) {
            menuEnum.nextElement().addActionListener(genericHandler);
        }
        
    }
    
}
