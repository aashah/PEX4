package client.model;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import server.Server.MessageTypes;

public class Client {
	
	public static final String SERVER_IP = "107.170.7.128";
	public static final int SERVER_PORT = 1198;
	
	private String username;
	private Socket connection;
	
	public Client(String username) throws IOException {
		this.username = username;
		this.connection = new Socket(SERVER_IP, SERVER_PORT);
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public Socket getConnection() {
		return this.connection;
	}

	public void closeConnection() {
		try {
			// let server know we're closing
			Thread t = new Thread(new Client.MessageWriter(MessageTypes.QUIT, "", connection));
			t.start();
			t.join();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static class MessageWriter implements Runnable {
		private MessageTypes opcode;
		private String message;
		private Socket connection;
		public MessageWriter(MessageTypes opcode, String message, Socket connection) {
			this.opcode = opcode;
			this.message = message;
			this.connection = connection;
		}
		
		public void run() {
			try {
				PrintWriter out = new PrintWriter(connection.getOutputStream(), true);
				out.println(this.opcode + " " + this.message);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
