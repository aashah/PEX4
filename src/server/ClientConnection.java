package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Arrays;

import server.Server.MessageTypes;
import util.StringUtil;

public class ClientConnection {
	
	private String currentRoom;
	
	private Server server;
	private String username;
	private Socket connection;

	public ClientConnection(Server server, String username, Socket connection) throws Exception {
		this.currentRoom = "#lobby";
		this.server = server;
		this.username = username;
		this.connection = connection;
		
		// spawn 2 threads: read/write
		new Thread(new ConnectionReader(connection)).start();
	}
	
	private class ConnectionReader implements Runnable {
		private Socket connection;
		private BufferedReader in;
		
		public ConnectionReader(Socket connection) throws IOException {
			this.connection = connection;
			
			in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
		}

		@Override
		public void run() {
			while (connection.isConnected()) {
				try {
					
					String message = in.readLine();
					if (message == null) {
						connection.close();
						break;
					}
					
					String[] tokens = message.split(" ");
					System.out.println("Server got message: " + message);
					MessageTypes opcode = MessageTypes.fromValue(tokens[0]);
					switch(opcode) {
						case QUIT: {
							server.disconnectUser(username);
							new Thread(server.getNewBroadcast(MessageTypes.USEREXIT, username, currentRoom)).start();
							break;
						}
						case MESSAGE: {
							String[] messageArr = Arrays.copyOfRange(tokens, 1, tokens.length);
							String result = StringUtil.join(messageArr, " ");
							
							new Thread(server.getNewBroadcast(MessageTypes.MESSAGE, result, currentRoom)).start();
							break;
						}
						case CREATE: {
							// validate room name
							String roomname = tokens[1];
							if (server.gameExists(roomname)) {
								new Thread(server.getRawMessage(MessageTypes.CREATE, "name_conflict", connection));
								break;
							}
							// let everyone in this person's room know he's leaving
							new Thread(server.getNewBroadcast(MessageTypes.USEREXIT, username, currentRoom)).start();
							// create new game
							GameRoom newRoom = new GameRoom(roomname);
							// add him into it
							server.createRoom(newRoom, username);
							
							new Thread(server.getRawMessage(MessageTypes.USERLIST, server.getUserList(roomname), connection)).start();
							new Thread(server.getRawMessage(MessageTypes.CREATE, "success", connection)).start();
							break;
						}
						default: {
							break;
						}
					}
					
					// new Thread(Server.MessageBroadcast(messageToSend, currentRoom)).start();
				} catch (IOException e) {
					break;
				}
			}
			
			server.disconnectUser(username);
		}
	}

	public Object getCurrentRoom() {
		return this.currentRoom;
	}

	public Socket getSocket() {
		return this.connection;
	}

	public String getUsername() {
		return this.username;
	}

	public void setCurrentRoom(String name) {
		this.currentRoom = name;
	}
}
