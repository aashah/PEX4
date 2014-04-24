package client.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
	public static final String SERVER_IP = "138.67.187.61";
	public static final int SERVER_PORT = 1134;
	
	private Socket connection;
	
	public Client(String username) throws Exception {
		this.connection = new Socket(SERVER_IP, SERVER_PORT);
		
		// send username
		PrintWriter out = new PrintWriter(this.connection.getOutputStream(), true);
		System.out.println("Sending username: " + username);
		out.println(username);
		
		
		// start read thread
		new Thread(new ConnectionReader(this.connection)).start();
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
