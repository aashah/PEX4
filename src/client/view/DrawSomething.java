package client.view;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.controller.LobbyController;
import client.model.Client;

@SuppressWarnings("serial")
public class DrawSomething extends JFrame {
	
	// menu
	// gui
	// changing the "main" panel
	private JPanel currentPanel;	
	public DrawSomething(JPanel view) {
		JMenuBar menuBar = new JMenuBar();
		//Build the first menu.
		JMenu menu = new JMenu("A Menu");
		menu.setMnemonic(KeyEvent.VK_A);
		menu.getAccessibleContext().setAccessibleDescription(
				"The only menu in this program that has menu items");
		menuBar.add(menu);
		setJMenuBar(menuBar);
		
		
		this.currentPanel = view;
		add(this.currentPanel);
		
		setResizable(false);
		setSize(new Dimension(800, 650));
	}
	
	public void changePanel(JPanel newView) {
		this.currentPanel = newView;
	}

	public static void main(String args[]) {
		// prompt for username
		String username = JOptionPane.showInputDialog(null,
				"Enter a username:", "Draw Something",
				JOptionPane.QUESTION_MESSAGE);

		// start client
		try {
			Client model = new Client(username);			
			LobbyPanel view = new LobbyPanel(model);			
			LobbyController controller = new LobbyController(model, view);
			
			DrawSomething game = new DrawSomething(view);
			game.setVisible(true);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
