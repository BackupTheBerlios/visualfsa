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

/*
 einfacher Dialog der den User auffordert zu warten, da ein länglicher Algorithmus
 läuft, dem Dialog wird ein Thread übergeben den dieser ausführt, der Thread
 hat die Aufgabe nach seiner Beendigung den Dialog wieder zu schließen
 */

package gui.dialogs;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import java.awt.Frame;

import algo.GenericAlgorithm;
import threads.GenericAlgoThread;

public class BusyDialog extends JDialog{
    
    public BusyDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    
    public void run(GenericAlgorithm genAlgo) {
        
        getContentPane().add(new JLabel("Please wait...", SwingConstants.CENTER));
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        setSize(300, 70);
        setLocation(70,70);
        
        // starte den eigentlichen Arbeitsthread
        GenericAlgoThread myThread = new GenericAlgoThread(genAlgo);
        
        myThread.runAlgo(this);
        
        setVisible(true);
    }
    
    
}
