package client.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import server.Server.MessageTypes;

import client.model.Client;
import client.view.LobbyPanel;

public class LobbyController {
	
	private Client model;

	public LobbyController(Client model, final LobbyPanel view) {
		this.model = model;
		view.addSendMessageButtonListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// get input string
				String message = view.getInputMessage();
				System.out.println("Sending " + message);
				sendMessage(message);
			}			
		});
		
		view.addSendMessageListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					String message = view.getInputMessage();
					sendMessage(message);
				}				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				
			}
		});
	}
	
	private void sendMessage(String message) {
		if (message != null || message.length() > 0) {
			// send it to server as public message	
			new Thread(new Client.MessageWriter(MessageTypes.MESSAGE, model.getUsername() + " " + message, model.getConnection())).start();
		}
	}
}
