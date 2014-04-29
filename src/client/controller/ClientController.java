package client.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;

import javax.swing.JOptionPane;

import server.Server;
import server.Server.MessageTypes;
import util.StringUtil;
import client.model.Client;
import client.view.LobbyPanel;
import client.view.LobbyPanel.ProgramState;

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
						case USER_LIST: {
							if (tokens.length > 1) {
								String[] users = tokens[1].split(",");
								for (int i = 0; i < users.length; ++i) {
									view.addUser(users[i]);
								}
							}
							break;
						}
						case NEW_USER: {
							view.addUser(tokens[1]);
							break;
						}
						case USER_EXIT: {
							if (model.getUsername().equals(tokens[1])) {
								view.clearUserList();
							}
							view.removeUser(tokens[1]);
							break;
						}
						case MESSAGE: {
							// TODO concat tokens array
							String[] messageArr = Arrays.copyOfRange(tokens, 2, tokens.length);
							String result = StringUtil.join(messageArr, " ");
							view.newMessage(tokens[1], result);
							break;
						}
						case CREATE: {
							String response = tokens[1];
							if ("name_conflict".equals(response)) {
								view.newMessage("Server", "Error: Room already exists with that name");
							} else if ("success".equals(response)) {
								// TODO switch GUI panel
								view.switchTo(ProgramState.GAME);
							}
							break;
						}
						case JOIN: {
							String response = tokens[1];
							if ("success".equals(response)) {
								view.switchTo(ProgramState.GAME);
							}
							break;
						}
						case LEAVE: {
							String response = tokens[1];
							if ("success".equals(response)) {
								view.switchTo(ProgramState.LOBBY);
							}
							break;
						}
						case PICK_WORD: {
							if (tokens.length == 4) {
								int choice;
								do {
									String whichWord = JOptionPane.showInputDialog(null,
											"Pick a word from the list below, enter 1-3 for your choice.\n" +
											"1. " + tokens[1] + "\n" +
											"2. " + tokens[2] + "\n" +
											"3. " + tokens[3] + "\n", "Draw Something",
											JOptionPane.QUESTION_MESSAGE);
									try {									
										choice = Integer.parseInt(whichWord);
									} catch (NumberFormatException e) {
										choice = -1;
									}
								} while (choice < 1 || choice > 3);
								
								new Thread(new Client.MessageWriter(MessageTypes.PICK_WORD, tokens[choice], model.getConnection())).start();
								// view.getGame().changeTurn(me);
								view.getGameModel().isMyTurn(true);
							}
							break;
						}
						case GAME_READY: {
							int time = Integer.parseInt(tokens[1]);
							view.getGameModel().startGame(time);
							view.getGameView().update();
							break;
						}
						case DRAW: {
							view.getGameController().processClicks(Arrays.copyOfRange(tokens, 1, tokens.length));
							break;
						}
						case CLEAR: {
							view.getGameView().clear();
							break;
						}
						case COLOR: {
							view.getGameView().setCurrentColor(Integer.parseInt(tokens[1]));
							break;
						}
						case ROUND_OVER: {
							String hasWinner = tokens[1];
							if ("correct".equals(hasWinner)) {
								String username = tokens[2];
								String correctWord = tokens[3];
								view.newMessage("@Server:", username + " got the word correct, the word was: " + correctWord);
							} else if ("incorrect".equals(hasWinner)) {
								String subcode = tokens[2];
								if ("player_count".equals(subcode)) {
									view.newMessage("@Server:", "Round over. Not enough people are present to play.");
								} else if ("current_player_left".equals(subcode)) {
									view.newMessage("@Server:", "Round over. Current player left...changing current drawing player.");
								}
							}
							
							view.getGameModel().roundOver();
							view.getGameView().update();
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
