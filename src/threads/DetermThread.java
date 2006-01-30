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

package threads;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import java.util.Vector;
import java.util.Iterator;

import gui.Sidebar;
import gui.dialogs.BusyDialog;
import algo.FSAAlgo;
import datastructs.FSA;

public class DetermThread implements Runnable {
    
    private FSA aut;
    private JDialog pleaseWaitDlg;
    private FSA result;
    
    public DetermThread(FSA _aut, JDialog _pleaseWaitDlg) {
        aut = _aut;
        pleaseWaitDlg = _pleaseWaitDlg;
    }
    
    public FSA getResult() {
        return result;
    }
    
    public void run() {
        int resultSize;
        int option;
        StringBuffer resultText;
        
        try {
            result = FSAAlgo.determ(aut, pleaseWaitDlg);
            
        } catch (OutOfMemoryError memErr) {
            JOptionPane.showMessageDialog(pleaseWaitDlg.getParent(),
                                "Insufficient memory",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
        }
        
        pleaseWaitDlg.dispose();

        
        
    }
    
}
