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
import javax.swing.JCheckBoxMenuItem;
import javax.swing.ImageIcon;

public class MainMenu extends JMenuBar {
    
    public static enum MenuID {
        FILE_NEW, FILE_OPEN, FILE_SAVE, FILE_SAVEAS, FILE_QUIT, FILE_OPTIONS,
        ALGO_REPLACE, ALGO_LANG, ALGO_DETERM, ALGO_REMISO, ALGO_COMPLEMENT, ALGO_INTERSECT, ALGO_UNION, ALGO_EQUALITY, ALGO_EMPTINESS,
        VIEW_FITWINDOW, VIEW_ALIGNGRID, VIEW_REALIGN,
        HELP_HELP, HELP_ABOUT
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
        
        JMenu file = new JMenu("File");
        
        file.setMnemonic('F');
        
        /* Menü - Datei - */
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
        
        JCheckBoxMenuItem algo_replace = new JCheckBoxMenuItem("Replace automatons",
            topLevel.options.getBoolValueForKey("REPLACE_AUT", false));
        
        JMenuItem algo_lang = new JMenuItem("Accepted Language");
        algo_lang.setMnemonic('L');
        
        JMenuItem algo_determ = new JMenuItem("NFA -> DFA");
        algo_determ.setMnemonic('D');
        
        JMenuItem algo_remiso = new JMenuItem("Remove Isolated States");
        algo_remiso.setMnemonic('I');
        
        entries.put(algo_replace, MenuID.ALGO_REPLACE);
        entries.put(algo_remiso, MenuID.ALGO_REMISO);
        entries.put(algo_lang, MenuID.ALGO_LANG);
        entries.put(algo_determ, MenuID.ALGO_DETERM);
        
        algo.add(algo_replace);
        algo.addSeparator();
        algo.add(algo_lang);
        algo.add(algo_determ);
        algo.add(algo_remiso);
        
        JMenu algo_setop = new JMenu("Set Operations");
        
        algo_setop.setMnemonic('O');
        
        JMenuItem algo_setop_union = new JMenuItem("Union...");
        JMenuItem algo_setop_intersect = new JMenuItem("Intersection...");
        JMenuItem algo_setop_complement = new JMenuItem("Complement");
        
        algo_setop_union.setMnemonic('U');
        algo_setop_intersect.setMnemonic('c');
        algo_setop_intersect.setMnemonic('m');
        
        algo_setop.add(algo_setop_union);
        algo_setop.add(algo_setop_intersect);
        algo_setop.add(algo_setop_complement);
        
        entries.put(algo_setop_union, MenuID.ALGO_UNION);
        entries.put(algo_setop_intersect, MenuID.ALGO_INTERSECT);
        entries.put(algo_setop_complement, MenuID.ALGO_COMPLEMENT);

        algo.add(algo_setop);
        
        JMenuItem algo_equality = new JMenuItem("Equality Test...");
        JMenuItem algo_emptiness = new JMenuItem("Emptiness Test");
        
        entries.put(algo_emptiness, MenuID.ALGO_EMPTINESS);
        entries.put(algo_equality, MenuID.ALGO_EQUALITY);
        
        algo.add(algo_equality);
        algo.add(algo_emptiness);
        
        add(algo);
        
        JMenu view_menu = new JMenu("View");
        
        view_menu.setMnemonic('V');
        
        JMenuItem view_fitwin = new JMenuItem("Fit in Window");
        
        view_fitwin.setMnemonic('W');
        view_fitwin.setAccelerator(KeyStroke.getKeyStroke('F',InputEvent.CTRL_MASK));
        
        JMenuItem view_grid = new JMenuItem("Align to Grid");
        
        view_grid.setMnemonic('G');
        view_grid.setAccelerator(KeyStroke.getKeyStroke('G', InputEvent.CTRL_MASK));
        
        JMenuItem view_realign = new JMenuItem("Realign");
        
        view_realign.setMnemonic('R');
        
        view_menu.add(view_fitwin);
        view_menu.add(view_grid);
        view_menu.add(view_realign);
        
        add(view_menu);
        
        entries.put(view_fitwin, MenuID.VIEW_FITWINDOW);
        entries.put(view_grid, MenuID.VIEW_ALIGNGRID);
        entries.put(view_realign, MenuID.VIEW_REALIGN);
        
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
        
        // registriere für alle Menüeinträge den EventHandler
        Enumeration<JMenuItem> menuEnum = entries.keys();
        
        while (menuEnum.hasMoreElements()) {
            menuEnum.nextElement().addActionListener(genericHandler);
        }
        
    }
    
}
