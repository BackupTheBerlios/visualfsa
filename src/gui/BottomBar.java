/*
  BottomBar.java
	
  Untere Toolbar unserer GUI, enthält ein Textfeld inkl. Button
  für Worterkennung
  
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
