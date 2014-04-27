package server;

import java.util.ArrayList;
import java.util.List;

import server.Server.MessageTypes;

public class GameRoom {
	public static final int MAX_PLAYERS = 10;
	public static final int MIN_PLAYERS = 1;

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
		this.currentPlayer = -1;
	}

	public String getName() {
		return roomname;
	}

	public void startGame() {
		this.currentPlayer = 0;
		// give first person a word
		String username = order.get(currentPlayer);
		new Thread(server.getRawMessage(MessageTypes.PICK_WORD,
				"lion teapot tree", server.getUserSocket(username))).start();

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
		}
	}

	public boolean isFull() {
		return (this.currentNumberOfPlayers >= MAX_PLAYERS);
	}

	public void setWord(String word, String username) {
		if (order.get(currentPlayer).equals(username)) {
			currentWord = word;
			currentState = GameState.PLAYING;
			new Thread(server.getNewBroadcast(MessageTypes.GAME_READY, "60", roomname)).start();
		}
		
	}
}
