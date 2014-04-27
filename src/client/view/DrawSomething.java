package client.view;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import server.Server.MessageTypes;

import client.controller.ClientController;
import client.controller.LobbyController;
import client.model.Client;

@SuppressWarnings("serial")
public class DrawSomething extends JFrame {
	
	public DrawSomething(final Client model) throws IOException {
		LobbyPanel view = new LobbyPanel(model, this);			
		ClientController clientController = new ClientController(model, view);
		LobbyController controller = new LobbyController(model, view);
		
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
		
		create.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String roomname = JOptionPane.showInputDialog(null,
						"Enter a roomname:", "Draw Something",
						JOptionPane.QUESTION_MESSAGE);
				new Thread(new Client.MessageWriter(MessageTypes.CREATE, roomname, model.getConnection())).start();
			}
		});
		
		JMenuItem join = new JMenuItem("Join Game");
		menu.setMnemonic(KeyEvent.VK_J);
		menu.add(join);
		
		join.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String roomname = JOptionPane.showInputDialog(null,
						"Enter a roomname:", "Draw Something",
						JOptionPane.QUESTION_MESSAGE);
				new Thread(new Client.MessageWriter(MessageTypes.JOIN, roomname, model.getConnection())).start();
			}
		});
		
		JMenuItem leave = new JMenuItem("Leave Game");
		menu.setMnemonic(KeyEvent.VK_L);
		menu.add(leave);
		
		leave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Client.MessageWriter(MessageTypes.LEAVE, "", model.getConnection())).start();
			}
		});
		
		JMenuItem exit = new JMenuItem("Exit Program");
		menu.setMnemonic(KeyEvent.VK_Q);
		menu.add(exit);
		
		exit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(new Client.MessageWriter(MessageTypes.QUIT, "", model.getConnection())).start();
			}
		});
		
		setJMenuBar(menuBar);
		add(view);
		
		setResizable(false);
		setSize(new Dimension(1024, 800));
	}

	public static void main(String args[]) {
		// prompt for username
		String username = JOptionPane.showInputDialog(null,
				"Enter a username:", "Draw Something",
				JOptionPane.QUESTION_MESSAGE);

		// start client
		try {
			final Client model = new Client(username);	
			DrawSomething game = new DrawSomething(model);
			
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
