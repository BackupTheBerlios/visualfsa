/*
	VFSAGUI.java
	
	Hauptfenster unserer GUI, inkl. main-Methode der Anwendung
*/

package src.gui;

import java.awt.BorderLayout;
import javax.swing.SwingUtilities;
import javax.swing.JFrame;
import javax.swing.JSplitPane;

public class VFSAGUI extends JFrame {

	
	private BottomBar bottom;
	private Sidebar side;
	private AutWindow autPane;
	private TopBar top;
	private MainMenu menubar;

	public VFSAGUI() {
		super("visualFSA pre-alpha");
	}

	public void showGUI() {
		JSplitPane centerSplitter;
	
		getContentPane().setLayout(new BorderLayout());
	
		bottom = new BottomBar();
		getContentPane().add(bottom, BorderLayout.SOUTH);

		side = new Sidebar();
		
		autPane = new AutWindow();
		
		top = new TopBar();
		getContentPane().add(top, BorderLayout.NORTH);
		
		centerSplitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, autPane, side);
		centerSplitter.setResizeWeight(0.95);
		getContentPane().add(centerSplitter, BorderLayout.CENTER);
		
		menubar = new MainMenu();
		this.setJMenuBar(menubar);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		pack();
		setLocation(30,30);
		setVisible(true);
	}


	// starte die GUI in einem neuen Thread
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				VFSAGUI visualFSA;
				visualFSA = new VFSAGUI();
				visualFSA.showGUI();
			}		
		});
	}

}
