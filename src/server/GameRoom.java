package server;

import java.util.ArrayList;
import java.util.List;

import server.Server.MessageTypes;

public class GameRoom {
	public static final int MAX_PLAYERS = 10;
	public static final int MIN_PLAYERS = 2;

	public enum GameState {
		WAITING, PLAYING;
	};

	private GameState currentState;

	private String currentWord;
	private Server server;
	private String roomname;
	private int currentNumberOfPlayers;

	private List<String> order;
	private int currentPlayer;

	public GameRoom(Server server, String roomname) {
		this.server = server;
		this.currentState = GameState.WAITING;
		this.roomname = roomname;
		this.currentNumberOfPlayers = 0;

		this.order = new ArrayList<String>();
	}

	public String getName() {
		return roomname;
	}

	public void startGame() {
		this.currentState = GameState.PLAYING;
		this.currentPlayer = 0;
		startTurn();
	}
	
	public void changeTurn() {
		this.currentPlayer = (this.currentPlayer + 1) % this.currentNumberOfPlayers;
	}
	
	public void startTurn() {
		// give person a word
		System.out.println("Starting game");
		String username = order.get(currentPlayer);
		new Thread(server.getRawMessage(MessageTypes.PICK_WORD,
				server.getRandomWord() + " " +
				server.getRandomWord() + " " +
				server.getRandomWord(), server.getUserSocket(username))).start();
	}

	public void add(String username) {
		if (this.currentNumberOfPlayers < MAX_PLAYERS) {
			this.currentNumberOfPlayers++;

			order.add(username);
			if (this.currentNumberOfPlayers == MIN_PLAYERS
					&& this.currentState == GameState.WAITING) {
				startGame();
			}
		}
	}

	public void remove(String username) {
		if (this.currentNumberOfPlayers > 0) {
			this.currentNumberOfPlayers--;
			
			if (this.currentNumberOfPlayers < MIN_PLAYERS) {				
				roundOver(false, "player_count");
				this.currentState = GameState.WAITING;
			} else if (username.equals(order.get(currentPlayer))) {
				roundOver(false, "current_player_left");
				changeTurn();
				startTurn();				
			}
		}
	}
	
	public String getCurrentPlayer() {
		return order.get(currentPlayer);
	}

	public boolean isFull() {
		return (this.currentNumberOfPlayers >= MAX_PLAYERS);
	}

	public void setWord(String word, String username) {
		if (order.get(currentPlayer).equals(username)) {
			currentWord = word.toLowerCase();
			currentState = GameState.PLAYING;
			// TODO Timer?
			new Thread(server.getNewBroadcast(MessageTypes.GAME_READY, "60", roomname)).start();
		}
		
	}

	public boolean processGuess(String username, String[] messageArr) {
		for (int i = 0; i < messageArr.length; ++i) {
			if (currentWord.equals(messageArr[i].toLowerCase())) {
				roundOver(true, username);				
				changeTurn();
				startTurn();
				return true;
			}
		}
		return false;
	}

	private void roundOver(boolean correctGuess, String subcode) {
		String message = "";
		if (correctGuess) {
			// subcode = username
			message += "correct " + subcode + " " + currentWord;
		} else {
			message += "incorrect " + subcode;
		}
		
		// send round over
		Thread t = new Thread(server.getNewBroadcast(MessageTypes.ROUND_OVER, message, roomname));
		t.start();
		try {
			t.join();
		} catch (InterruptedException e) {}
	}

	public int getNumberOfPlayers() {
		return this.currentNumberOfPlayers;
	}
}
