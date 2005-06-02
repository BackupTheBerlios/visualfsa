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

/*
 einfacher Dialog der den User auffordert zu warten, da ein länglicher Algorithmus
 läuft, dem Dialog wird ein Thread übergeben den dieser ausführt, der Thread
 hat die Aufgabe nach seiner Beendigung den Dialog wieder zu schließen
 */

package gui.dialogs;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import java.awt.Frame;

public class BusyDialog extends JDialog{
    
    private Runnable myWork = null;
    
    public BusyDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);
    }
    
    public void setWork(Runnable _work) {
        myWork = _work;
    }
    
    public void run() {
        
        getContentPane().add(new JLabel("      Etwas Geduld...      ", SwingConstants.CENTER));
        
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        
        // wenn wir keinen Arbeitsthread haben öffnet sich das Fenster gar nicht erst
        if (myWork!=null) {
            Thread myWorkThread = new Thread(myWork);
            myWorkThread.start();
        }
        else {
            dispose();
            return;
        }
        
        pack();
        setLocation(70,70);
        setVisible(true);
        
    }
    
    
}
