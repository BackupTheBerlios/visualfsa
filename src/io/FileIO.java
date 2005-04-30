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

package src.io;

import src.datastructs.FSA;
import java.awt.Component;
import java.util.LinkedList;
import java.io.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileIO {

 
    /* eine Liste von Automatenobjekten wird in die Datei 'filename'
       geschrieben
    */
    public static void fsaListToFile(LinkedList<FSA> fsaList, String filename) {
	ObjectOutputStream objectStream;

	try {
	    objectStream = new ObjectOutputStream(new FileOutputStream(filename));
	    objectStream.writeObject(fsaList);
	    objectStream.close();
	}
	catch(InvalidClassException icEx) {
	    System.out.println("ICEX: "+icEx.getMessage());
	}
	catch(NotSerializableException nsEx) {
	    System.out.println("NSEX: "+nsEx.getMessage());
	}
	catch(IOException ioEx) {
	    System.out.println("IOEX: "+ioEx.getMessage());
	}
	
    }

    public static LinkedList fileToFsaList(String filename) throws Exception {
	ObjectInputStream objectStream;
	LinkedList content;

	objectStream = new ObjectInputStream(new FileInputStream(filename));
	content = ((LinkedList)(objectStream.readObject()));
	objectStream.close();
	return content;
    }


    public static String getOpenFilename(Component owner) {
	JFileChooser fileDlg;

	fileDlg = new JFileChooser();
	fileDlg.setDialogTitle("Datei öffnen");
	if (fileDlg.showOpenDialog(owner)==JFileChooser.APPROVE_OPTION) {
	    return fileDlg.getName(fileDlg.getSelectedFile());
	}
	else {
	    return null;
	}
    }

}
