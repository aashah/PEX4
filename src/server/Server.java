package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	public static final int PORT = 1134;
	
	private ServerSocket serverSocket;
	private Map<String, ClientConnection> connections;
	
	public Server() throws Exception {
		connections = new ConcurrentHashMap<>();
		
		initServer();		
	}	
	
	public void initServer() throws Exception {
		serverSocket = new ServerSocket(PORT);
		System.out.println("Started server....");
		while (true) {
			Socket connection = serverSocket.accept();
			
			BufferedReader in = new BufferedReader(new InputStreamReader(
					connection.getInputStream()));
			
			String username = in.readLine();
			
			// validate username
			
			// add to our map
			ClientConnection newConnection = new ClientConnection(this, username, connection);
			connections.put(username, newConnection);
			
			// start new connection thread
			// new Thread(new ClientConnection(username, connection)).start();
		}
	}
	
	public MessageBroadcast getNewBroadcast(String message, String room) {
		return new MessageBroadcast(message, room);
	}
	
	private class MessageBroadcast implements Runnable {
		private String message;
		private String room;
		
		public MessageBroadcast(String message, String room) {
			this.message = message;
			this.room = room;
		}
		
		public void run() {
			for (Map.Entry<String, ClientConnection> entry : connections.entrySet()) {
				ClientConnection connection = entry.getValue();
				
				if (connection.getCurrentRoom().equals(this.room)) {
					// send message
					try {
						PrintWriter out = new PrintWriter(connection.getSocket().getOutputStream(), true);
						out.println(this.message);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void disconnectUser(String username) {
		// check if in there
		// disconnect socket?
		connections.remove(username);
	}
	
	public static void main(String args[]) {
		try {
			new Server();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
