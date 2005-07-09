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

package datastructs;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.Serializable;
import java.io.FileNotFoundException;
import java.awt.Color;


public class AppOptions implements Serializable {
    
    // Farboptionen
    private Color lineCol, charCol, backCol, transCol, markCol;
    
    // Immer nach Speichern fragen?
    private boolean askSave;
    
    private final String optFilename = "vfsa_opt.dat";
    
    /* set und get braucht die Welt */
    
    public void setLineCol(Color c) {
        lineCol = c;
    }
    
    public void setCharCol(Color c) {
        charCol = c;
    }
    
    public void setBackCol(Color c) {
        backCol = c;
    }
    
    public void setTransCol(Color c) {
        transCol = c;
    }
            
    public void setMarkCol(Color c) {
        markCol = c;
    }
    
    public Color getLineCol() { return lineCol; }
    public Color getCharCol() { return charCol; }
    public Color getBackCol() { return backCol; }
    public Color getTransCol() { return transCol; }
    public Color getMarkCol() { return markCol; }
    
    
    public void setAskSave(boolean ask) {
        askSave = ask;
    }
    
    public boolean getAskSave() {
        return askSave;
    }
    
    
    /* setzt das Objekt auf die Standardeinstellungen für vfsa */
    public void setDefault() {
        lineCol = Color.BLACK;
        charCol = Color.RED;
        backCol = Color.WHITE;
        markCol = Color.YELLOW;
        transCol = Color.GREEN;
        askSave = true;
    }
    
    // erzeugt ein Optionsobjekt mit default einstellungen
    public AppOptions getDefaultOptions() {
        AppOptions def;
        def = new AppOptions();
        def.setDefault();
        return def;
    }

    
    public void setOptionsFrom(AppOptions opt) {
        askSave = opt.getAskSave();
        lineCol = opt.getLineCol();
        charCol = opt.getCharCol();
        transCol = opt.getTransCol();
        backCol = opt.getBackCol();
        markCol = opt.getMarkCol();
    }
    
    
    public void saveOptions() {
        ObjectOutputStream outStream;
        
        try {
	    outStream = new ObjectOutputStream(new FileOutputStream(optFilename));
	    outStream.writeObject(this);
	    outStream.close();
	}
	catch (IOException ioEx) {
	    System.err.println(java.util.ResourceBundle.getBundle("global").getString("optionsWriteError"));
            System.err.println(ioEx.getMessage());
	}
	catch (Exception generalEx) {
	    // möglich sind hier, NotSerializable und InvalidClass, beide
	    // Ausnahmen sind im Endbenutzerbetrieb praktisch nicht möglich
            System.err.println(java.util.ResourceBundle.getBundle("global").getString("optionsWrongObject"));
	}
    }
    
    public void loadOptions() {
        ObjectInputStream objectStream;
	AppOptions readObject;
        
        try {
            objectStream = new ObjectInputStream(new FileInputStream(optFilename));
            readObject = ((AppOptions)(objectStream.readObject()));

            this.setOptionsFrom(readObject);
            
            objectStream.close();
        }
        catch (FileNotFoundException nfEx) {
            // wird keine Optionsdatei gefunden, wird eine mit default-werten
            // angelegt
            this.setDefault();
            this.saveOptions();
        }
        catch (Exception genEx) {
            System.err.println(genEx.getMessage());
            System.err.println(java.util.ResourceBundle.getBundle("global").getString("optionsReadError"));
        }
    }
    
}
