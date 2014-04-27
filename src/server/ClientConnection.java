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
							new Thread(server.getNewBroadcast(MessageTypes.USER_EXIT, username, currentRoom)).start();
							// TODO was it his turn?
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
							Thread t = new Thread(server.getNewBroadcast(MessageTypes.USER_EXIT, username, currentRoom));
							try {
								t.start();
								t.join();
							} catch (Exception e) {}
							
							// create new game
							GameRoom newRoom = new GameRoom(server, roomname);
							// add him into it
							server.createRoom(newRoom, username);
							
							
							new Thread(server.getRawMessage(MessageTypes.USER_LIST, server.getUserList(roomname), connection)).start();
							t = new Thread(server.getRawMessage(MessageTypes.CREATE, "success", connection));
							try {
								t.start();
								t.join();
							} catch (Exception e) {}
							
							newRoom.add(username);
							break;
						}
						case JOIN: {
							String roomname = tokens[1];
							if (!server.gameExists(roomname)) {
								new Thread(server.getRawMessage(MessageTypes.JOIN, "no_room_found", connection));
								break;
							}
							GameRoom room = server.getGame(roomname);
							if (room.isFull()) {
								new Thread(server.getRawMessage(MessageTypes.JOIN, "room_full", connection));
								break;
							}
							try {
								// tell everyone he left
								Thread t = new Thread(server.getNewBroadcast(MessageTypes.USER_EXIT, username, currentRoom));
								t.start();
								t.join();
								
								// give him new userlist
								String userList = server.getUserList(roomname);
								t = new Thread(server.getRawMessage(MessageTypes.USER_LIST, userList, connection));
								t.start();
								t.join();
								
								// add em
								room.add(username);
								currentRoom = roomname;
								
								// tell everyone he's joined
								t = new Thread(server.getNewBroadcast(MessageTypes.NEW_USER, username, roomname));
								t.start();
								t.join();
								
								// send success
								t= new Thread(server.getRawMessage(MessageTypes.JOIN, "success", connection));
								t.start();
								t.join();
							} catch (Exception unusedException) {}
							break;
						}
						case LEAVE: {
							// TODO if in lobby, treat as QUIT
							if (!server.gameExists(currentRoom)) {
								// TODO close connection
								break;
							}
							GameRoom room = server.getGame(currentRoom);
							
							// remove player
							room.remove(username);
							
							// let everyone know
							Thread t = new Thread(server.getNewBroadcast(MessageTypes.USER_EXIT, username, currentRoom));
							try {
								t.start();
								t.join();
							} catch (Exception e) {}
														
							// give new userlist
							new Thread(server.getRawMessage(MessageTypes.USER_LIST, server.getUserList("#lobby"), connection)).start();
							
							// change to lobby
							currentRoom = "#lobby";
							new Thread(server.getNewBroadcast(MessageTypes.NEW_USER, username, "#lobby")).start();
							
							new Thread(server.getRawMessage(MessageTypes.LEAVE, "success", connection)).start();
							break;
						}
						case PICK_WORD: {
							server.getGame(currentRoom).setWord(tokens[1], username);
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
