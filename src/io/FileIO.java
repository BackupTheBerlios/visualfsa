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

package io;

import datastructs.FSA;
import java.awt.Component;
import java.util.LinkedList;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import javax.swing.JFileChooser;

public class FileIO {

 
    /* eine Liste von Automatenobjekten wird in die Datei 'filename'
       geschrieben
    */
    public static String fsaListToFile(LinkedList<FSA> fsaList, String filename) {
	ObjectOutputStream objectStream;

	try {
	    objectStream = new ObjectOutputStream(new FileOutputStream(filename));
	    objectStream.writeObject(fsaList);
	    objectStream.close();
	    return null; // kein fehler
	}
	catch (IOException ioEx) {
	    return "IO Fehler";
	}
	catch (Exception generalEx) {
	    // möglich sind hier, NotSerializable und InvalidClass, beide
	    // Ausnahmen sind im Endbenutzerbetrieb praktisch nicht möglich
	    return "Interner Fehler -> "+generalEx.getMessage();
	}
    }

    public static LinkedList fileToFsaList(String filename)
    throws Exception {
	ObjectInputStream objectStream;
	LinkedList content;
	
	objectStream = new ObjectInputStream(new FileInputStream(filename));
	content = ((LinkedList)(objectStream.readObject()));
	objectStream.close();
	return content; // kein Fehler

    }


    public static String getSaveFilename(Component owner, String initial) {
	JFileChooser fileDlg;

	fileDlg = new JFileChooser();
	fileDlg.setDialogTitle("Datei speichern");
	fileDlg.setSelectedFile(new File(initial));

	if (fileDlg.showSaveDialog(owner)==JFileChooser.APPROVE_OPTION) {
	    return fileDlg.getSelectedFile().toString();
	}
	else {
	    return null;
	}
    }

    public static String getOpenFilename(Component owner) {
	JFileChooser fileDlg;

	fileDlg = new JFileChooser();
	fileDlg.setDialogTitle("Datei öffnen");
	fileDlg.setMultiSelectionEnabled(false);
	if (fileDlg.showOpenDialog(owner)==JFileChooser.APPROVE_OPTION) {
	    return fileDlg.getSelectedFile().toString();
	}
	else {
	    return null;
	}
    }

}
