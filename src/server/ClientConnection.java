package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
					
					MessageTypes opcode = MessageTypes.fromValue(tokens[0]);
					switch(opcode) {
						case QUIT: {
							server.disconnectUser(username);
							new Thread(server.getNewBroadcast(MessageTypes.USEREXIT, username, currentRoom)).start();
							break;
						}
						case MESSAGE: {
							String[] messageArr = StringUtil.splice(tokens, 1, tokens.length);
							String result = StringUtil.join(messageArr, " ");
							
							new Thread(server.getNewBroadcast(MessageTypes.MESSAGE, result, currentRoom)).start();
							break;
						}
					}
					
					// parse message
					System.out.println("Server got message: " + message);
					
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
}
