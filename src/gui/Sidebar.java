
package src.gui;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JList;
import javax.swing.JButton;
import javax.swing.ImageIcon;

public class Sidebar extends JPanel {

	private JList autList, results;
	private JButton newAut, renAut, delAut;

	public Sidebar() {
		super();

		JSplitPane splitter;
		JScrollPane listScroller, resultScroller;
		JPanel upperPanel, buttonPanel, lowerPanel;
		
		upperPanel = new JPanel(new BorderLayout());
		
		newAut = new JButton(new ImageIcon("src/images/stock_new.png"));
		renAut = new JButton(new ImageIcon("src/images/stock_edit.png"));
		delAut = new JButton(new ImageIcon("src/images/stock_cancel.png"));
		
		autList = new JList();
		
		listScroller = new JScrollPane(autList);
		
		upperPanel.add(listScroller, BorderLayout.CENTER);
		
		buttonPanel = new JPanel();
		
		buttonPanel.add(newAut);
		buttonPanel.add(renAut);
		buttonPanel.add(delAut);
		
		upperPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		results = new JList();
		resultScroller = new JScrollPane(results);
		
		lowerPanel = new JPanel(new BorderLayout());
		
		lowerPanel.add(resultScroller, BorderLayout.CENTER);
		lowerPanel.add(new JButton("reset"), BorderLayout.SOUTH);
		
		splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT, upperPanel, lowerPanel);
		splitter.setResizeWeight(0.55);
		
		this.setLayout(new BorderLayout());
		this.add(splitter, BorderLayout.CENTER);
	}

}
