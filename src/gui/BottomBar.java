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
  
  You should have received a copy of the GNU General Public License along with Foobar;
  if not, write to the Free Software Foundation, Inc., 
  59 Temple Place, Suite 330, Boston, MA 02111-1307 USA 
*/

package src.gui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class BottomBar extends JPanel {

    
    private JTextField testWord, autoCharacters;
    private JButton doTest;
    private JCheckBox autoTransition;
    private VFSAGUI topLevel;

    public BottomBar(VFSAGUI _topLevel) {
	super();
		
	JPanel leftPanel, rightPanel;
		
	topLevel = _topLevel;

	this.setLayout(new GridLayout(1,2));
		
	leftPanel = new JPanel(new BorderLayout());
	rightPanel = new JPanel(new BorderLayout());
		
	testWord = new JTextField(30);
	doTest = new JButton("w in L(/A) ?");
		
	leftPanel.add(testWord, BorderLayout.CENTER);
	leftPanel.add(doTest, BorderLayout.EAST);
		
	autoTransition = new JCheckBox("Auto-Transition");
	autoCharacters = new JTextField(30);
		
	rightPanel.add(autoTransition, BorderLayout.WEST);
	rightPanel.add(autoCharacters, BorderLayout.CENTER);
		
	this.add(leftPanel);
	this.add(rightPanel);
    }
		
		
		

    public String getAutoTransition() {
	if (autoTransition.isSelected()) {
	    return autoCharacters.getText();
	}
	else {
	    return "";
	}
    }



}
