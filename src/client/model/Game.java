package client.model;

import server.GameRoom.GameState;

public class Game {
	private boolean myTurn;
	private GameState currentState;
	private int time;
	
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

}
