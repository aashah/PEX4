package client.model;

import java.net.Socket;

import server.GameRoom.GameState;

public class Game {
	private Socket connection;
	private boolean myTurn;
	private GameState currentState;
	private int time;
	
	public Game(Socket connection) {
		this.connection = connection;
	}
	
	public void isMyTurn(boolean b) {
		this.myTurn = b;
		this.currentState = GameState.WAITING;
	}
	
	public void startGame(int time) {
		this.currentState = GameState.PLAYING;
		this.time = time;	
	}
	
	public int getTime() {
		return this.time;
	}
	
	public boolean myTurn() {
		return this.myTurn;
	}
	
	public GameState getState() {
		return this.currentState;
	}

	public Socket getConnection() {
		return this.connection;
	}

	public void roundOver() {
		this.myTurn = false;
		this.currentState = GameState.WAITING;
	}

}
