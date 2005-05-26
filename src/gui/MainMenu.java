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
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.ImageIcon;

public class MainMenu extends JMenuBar {

     public static enum MenuID 
    { FILE_NEW, FILE_OPEN, FILE_SAVE, FILE_SAVEAS, FILE_QUIT }

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

	JMenu file = new JMenu("Datei");
	file.setMnemonic(KeyEvent.VK_D);

	/* Men� - Datei - */
	JMenuItem newFile = new JMenuItem("Neu",
					  new ImageIcon("src/images/filenew.png"));

	JMenuItem openFile = new JMenuItem("�ffnen...",
					   new ImageIcon("src/images/fileopen.png"));
	JMenuItem saveFile = new JMenuItem("Speichern",
					   new ImageIcon("src/images/filesave.png"));
	JMenuItem saveasFile = new JMenuItem("Speichern als...",
					     new ImageIcon("src/images/filesaveas.png"));
	JMenuItem quit = new JMenuItem("Beenden",
				       new ImageIcon("src/images/exit.png"));

	newFile.setMnemonic(KeyEvent.VK_N);
	openFile.setMnemonic(KeyEvent.VK_F);
 	saveFile.setMnemonic(KeyEvent.VK_S);
 	saveasFile.setMnemonic(KeyEvent.VK_A);
	quit.setMnemonic(KeyEvent.VK_B);

	entries.put(newFile, MenuID.FILE_NEW);
	entries.put(openFile, MenuID.FILE_OPEN);
	entries.put(saveFile, MenuID.FILE_SAVE);
	entries.put(saveasFile, MenuID.FILE_SAVEAS);
	entries.put(quit, MenuID.FILE_QUIT);

	file.add(newFile);
	file.add(openFile);
	file.add(saveFile);
	file.add(saveasFile);
	file.addSeparator();
	file.add(quit);
		
	add(file);

	genericHandler = new MenuHandler(topLevel, guiSide, entries);
	
	// registriere f�r alle Men�eintr�ge den EventHandler
	Enumeration<JMenuItem> menuEnum = entries.keys();
	
	while (menuEnum.hasMoreElements()) {
	    menuEnum.nextElement().addActionListener(genericHandler);
	}

    }

}
