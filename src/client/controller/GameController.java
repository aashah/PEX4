package client.controller;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.LinkedList;
import java.util.Queue;

import server.GameRoom.GameState;
import server.Server.MessageTypes;
import client.model.Client;
import client.model.Game;
import client.view.GamePanel;

public class GameController implements MouseListener, MouseMotionListener {
	private Game model;
	private GamePanel view;
	
	private boolean dragging;
	private InputQueue queue;
	
	public GameController(Game model, GamePanel view) {
		this.model = model;
		this.view = view;
		this.dragging = false;
		this.queue = new InputQueue();
		
		view.addMouseListener(this);
		view.addMouseMotionListener(this);
	}
	
	public void processClicks(String[] drawPoints) {
		Graphics g = view.getGraphics();
		g.setColor(view.getCurrentColor());
		for (int i = 0; i < drawPoints.length; ++i) {
			String[] point = drawPoints[i].split(",");
			int x = Integer.parseInt(point[0]);
			int y = Integer.parseInt(point[1]);
			g.fillOval(x, y, GamePanel.CIRCLE_SIZE, GamePanel.CIRCLE_SIZE);			
		}
		view.update();
	}
	
	private void click(int x, int y) {
		if (model.getState() == GameState.PLAYING && model.myTurn()) {
			Graphics g = view.getGraphics();
			int width = view.getWidth();
			int mainHeight = view.getHeight() - GamePanel.CONTROLS_HEIGHT;
			// then we can change colors and clear...etc
			
			if (x >= GamePanel.CONTROLS_OFFSET && x <= (view.getNumColors() * 50 + GamePanel.CONTROLS_OFFSET) &&
				y >= (mainHeight + 5) && y <= (mainHeight + 45)) {
				// check if we're changing the color
				int currentColor = (x - GamePanel.CONTROLS_OFFSET) / 50;
				view.setCurrentColor(currentColor);
				view.repaint();
				
				new Thread(new Client.MessageWriter(MessageTypes.COLOR, "" + currentColor, model.getConnection())).start();
				
				queue.flushQueue();
			} else if (x >= (width - 53) && x <= (width - 3) &&
				y >= (mainHeight + 5) && y <= (mainHeight + 45)) {
				// clear button
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, width, mainHeight);
				
				// let server know
				new Thread(new Client.MessageWriter(MessageTypes.CLEAR, "", model.getConnection())).start();
				queue.clearQueue();
			} else if (y < (mainHeight + 5)){
				// drawing!
				g.setColor(view.getCurrentColor());
				g.fillOval(x, y, GamePanel.CIRCLE_SIZE, GamePanel.CIRCLE_SIZE);
				
				queue.addToQueue(x, y);
			}
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		if (!dragging) {
			dragging = true;
			click(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		dragging = false;
		click(e.getX(), e.getY());
		
		queue.flushQueue();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (dragging) {
			click(e.getX(), e.getY());
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		queue.flushQueue();
	}
	
	private class InputQueue {
		public static final int THRESHOLD = 10;
		private Queue<String> queue;
		
		public InputQueue() {
			queue = new LinkedList<>();
		}
		
		public void addToQueue(int x, int y) {
			queue.add(x + "," + y);
			
			if (queue.size() >= THRESHOLD) {
				flushQueue();
			}
		}
		
		public synchronized void flushQueue() {
			if (model.getState() == GameState.PLAYING && model.myTurn()) {
				String message = "";
				int size = queue.size();
				for (int i = 0; i < size; ++i) {
					message += queue.remove() + " ";
				}
				System.out.println("Sending " + message);
				new Thread(new Client.MessageWriter(MessageTypes.DRAW, message, model.getConnection())).start();
			} else {
				// queue.clear();
			}
		}
		
		public synchronized void clearQueue() {
			queue.clear();
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseMoved(MouseEvent e) {}

}
