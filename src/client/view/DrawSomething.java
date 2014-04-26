package client.view;

import java.awt.Dimension;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import client.controller.ClientController;
import client.controller.LobbyController;
import client.model.Client;

@SuppressWarnings("serial")
public class DrawSomething extends JFrame {
	
	// TODO menu
	
	// changing the "main" panel
	private JPanel currentPanel;	
	public DrawSomething(JPanel view) {
		JMenuBar menuBar = new JMenuBar();
		//Build the first menu.
		JMenu menu = new JMenu("FILE");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription(
				"Create or join games.");
		menuBar.add(menu);
		
		JMenuItem create = new JMenuItem("Create Game");
		menu.setMnemonic(KeyEvent.VK_C);
		menu.add(create);
		
		JMenuItem join = new JMenuItem("Join Game");
		menu.setMnemonic(KeyEvent.VK_J);
		menu.add(join);
		
		JMenuItem leave = new JMenuItem("Leave Game");
		menu.setMnemonic(KeyEvent.VK_L);
		menu.add(leave);
		
		JMenuItem exit = new JMenuItem("Exit Program");
		menu.setMnemonic(KeyEvent.VK_Q);
		menu.add(exit);		
		
		setJMenuBar(menuBar);
		
		
		this.currentPanel = view;
		add(this.currentPanel);
		
		setResizable(false);
		setSize(new Dimension(1024, 800));
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
			final Client model = new Client(username);	
			LobbyPanel view = new LobbyPanel(model);			
			ClientController clientController = new ClientController(model, view);
			LobbyController controller = new LobbyController(model, view);
			
			DrawSomething game = new DrawSomething(view);
			game.setVisible(true);
			
			// TODO close socket when window closes
			game.addWindowListener(new java.awt.event.WindowAdapter() {
			    @Override
			    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
			    	model.closeConnection();
			    }
			});
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
