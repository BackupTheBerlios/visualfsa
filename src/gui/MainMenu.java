

package src.gui;

import javax.swing.JMenuBar;
import javax.swing.JMenu;

public class MainMenu extends JMenuBar {

	public MainMenu() {
		super();
		JMenu file = new JMenu("Datei!");
		this.add(file);
	}


}
