package client.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import server.Server;
import server.Server.MessageTypes;
import util.StringUtil;
import client.model.Client;
import client.view.LobbyPanel;

public class ClientController {
	private Client model;
	private LobbyPanel view;
	
	public ClientController(Client model, LobbyPanel view) throws IOException {
		this.model = model;
		this.view = view;
		
		// send username
		PrintWriter out = new PrintWriter(model.getConnection().getOutputStream(), true);
		System.out.println("Sending username: " + model.getUsername());
		out.println(model.getUsername());
		
		// start read thread
		new Thread(new ConnectionReader(model.getConnection())).start();
	}
	
	private class ConnectionReader implements Runnable {
		private Socket connection;
		private BufferedReader in;
		
		public ConnectionReader(Socket connection) throws IOException {
			this.connection = connection;
			
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		}
		
		public void run() {
			while (connection.isConnected()) {
				try {
					String message = in.readLine();
					if (message == null) {
						System.out.println("Got null message");
						connection.close();
						break;
					}
					
					String[] tokens = message.split(" ");
					System.out.println("Got message: " + message);
					Server.MessageTypes opcode = MessageTypes.fromValue(tokens[0]);
					System.out.println("Opcode: " + opcode);
					switch (opcode) {
						case ERROR: {
							System.out.println("Error: " + message);
							connection.close();
							break;
						}
						case USERLIST: {
							String[] users = tokens[1].split(",");
							for (int i = 0; i < users.length; ++i) {
								view.addUser(users[i]);
							}
							break;
						}
						case NEWUSER: {
							view.addUser(tokens[1]);
							break;
						}
						case USEREXIT: {
							view.removeUser(tokens[1]);
							break;
						}
						case MESSAGE: {
							// TODO concat tokens array
							String[] messageArr = StringUtil.splice(tokens, 2, tokens.length);
							String result = StringUtil.join(messageArr, " ");
							view.newMessage(tokens[1], result);
							break;
						}
						default: {
							System.out.println("Opcode not found: " + tokens[0]);
							break;
						}
					}
					
					// parse message
					System.out.println("Client got message: " + message);
				
					// got public message?
					//	dispatchEvent(new ClientMessage(message))
					
					// new Thread(Server.MessageBroadcast(messageToSend, currentRoom)).start();
				} catch (IOException e) {
					break;
				}
			}
		}
	}
}
