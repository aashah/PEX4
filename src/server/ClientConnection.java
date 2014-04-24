package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
			new Thread(server.getNewBroadcast("Hey", currentRoom)).start();
			while (connection.isConnected()) {
				try {
					
					String message = in.readLine();
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
}
