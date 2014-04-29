package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	public enum MessageTypes {
		ERROR("error"),
		NEW_USER("newuser"),
		USER_EXIT("userexit"),
		USER_LIST("userlist"),
		MESSAGE("message"),
		CREATE("create"),
		JOIN("join"),
		LEAVE("leave"),
		QUIT("quit"),
		
		GAME_READY("gameready"),
		PICK_WORD("pickword"),
		ROUND_OVER("roundover"), 
		/*
		 * roundover [time/correct] [username] [word]
		 */
		
		DRAW("draw"),
		COLOR("color"),
		CLEAR("clear")
		// game opcodes
		/*
		 * GAME_READY
		 * CORRECT_GUESS
		 * DRAW
		 * GAME_OVER
		 * CHANGE_TURN - not needed, just send server message
		 */
		;
		
		private String type;
		MessageTypes(String type) {
			this.type = type;
		}
		
		public String toString() {
			return this.type;
		}
		
		public static MessageTypes fromValue(String value) {
			for (MessageTypes type : MessageTypes.values()) {
				if (value.equals(type.toString())) {
					return type;
				}
			}
			return null;
		}
	}
	
	public static final int PORT = 1134;
	
	private ServerSocket serverSocket;
	private Map<String, ClientConnection> connections;
	private Map<String, GameRoom> games;
	
	public Server() throws Exception {
		connections = new ConcurrentHashMap<>();
		games = new ConcurrentHashMap<>();
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
			System.out.println("User: " + username);
			
			// validate username			
			if (connections.containsKey(username)) {
				Thread t = new Thread(getRawMessage(MessageTypes.ERROR, "name_conflict", connection));
				t.start();
				t.join();
				connection.close();
				continue;
			}
			
			// add to our map
			ClientConnection newConnection = new ClientConnection(this, username, connection);
			
			// send this one guy the userlist for his room
			// build list into string
			if (connections.size() > 0) {
				String userList = getUserList("#lobby");
				new Thread(getRawMessage(MessageTypes.USER_LIST, userList, connection)).start();
			}
			
			connections.put(username, newConnection);
			// let everyone else know someone has joined
			new Thread(getNewBroadcast(MessageTypes.NEW_USER, username, "#lobby")).start();
		}
	}

	public String getUserList(String room) {
		String userList = "";
		for (ClientConnection entry : connections.values()) {
			if (entry.getCurrentRoom().equals(room)) {
				userList += entry.getUsername() + ",";
			}
		}
		if (userList.length() > 0) {
			userList = userList.substring(0, userList.length() - 1);
		}
		return userList;
	}
	
	public boolean gameExists(String roomname) {
		return games.containsKey(roomname);
	}
	
	public GameRoom getGame(String roomname) {
		return games.get(roomname);
	}
	
	public void createRoom(GameRoom newRoom, String username) {
		games.put(newRoom.getName(), newRoom);
		connections.get(username).setCurrentRoom(newRoom.getName());
	}
	
	public void disconnectUser(String username) {
		if (connections.containsKey(username)) {
			try {
				connections.get(username).getSocket().close();
			} catch (IOException unusedException) {
				// should not happen
			}
			connections.remove(username);
		}
	}
	
	public Socket getUserSocket(String username) {
		if (connections.containsKey(username)) {
			return connections.get(username).getSocket();
		}
		return null;
	}
	
	public MessageBroadcast getRawMessage(MessageTypes opcode, String message, Socket connection) {
		Vector<Socket> socket = new Vector<Socket>();
		socket.add(connection);
		return new MessageBroadcast(opcode, message, socket);
	}

	public MessageBroadcast getNewBroadcast(MessageTypes opcode, String message, String room) {
		Vector<Socket> socketList = new Vector<Socket>();
		for (Map.Entry<String, ClientConnection> entry : connections.entrySet()) {
			ClientConnection connection = entry.getValue();
			
			if (connection.getCurrentRoom().equals(room)) {
				socketList.add(connection.getSocket());
			}
		}
		
		return new MessageBroadcast(opcode, message, socketList);
	}
	
	private class MessageBroadcast implements Runnable {
		private MessageTypes opcode;
		private String message;
		private Vector<Socket> sockets;
		
		public MessageBroadcast(MessageTypes opcode, String message, Vector<Socket> sockets) {
			this.opcode = opcode;
			this.message = message;
			this.sockets = sockets;
		}
		
		public void run() {
			for (Socket s : sockets) {
				try {
					PrintWriter out = new PrintWriter(s.getOutputStream(), true);
					out.println(this.opcode + " " + this.message);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void main(String args[]) {
		try {
			new Server();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
