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

import java.io.IOException;
import java.awt.Frame;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;

public class HelpFrame extends JDialog {
    

    public HelpFrame(Frame owner, String title, boolean modal) {
        super(owner,title,modal);
    }
    
    
    public void run() {
        JScrollPane manualScroller;
        JEditorPane htmlView;
        
        try {
            htmlView = new JEditorPane("file:docs/index.html");
            manualScroller = new JScrollPane(htmlView);
            htmlView.setEditable(false);
            getContentPane().add(manualScroller);
            setSize(500,300);
            setLocation(40,40);
            setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            setVisible(true);
        }
        catch (IOException ioEx) {
            JOptionPane.showMessageDialog(this.getParent(), "Could not read manual", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }
    
}
