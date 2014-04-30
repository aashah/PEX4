package client.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import client.model.Game;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {	
	
	public static final int CONTROLS_HEIGHT = 50;
	public static final int COLOR_SIZE = 30;
	public static final int CURRENT_COLOR_SIZE = 40;
	public static final int CONTROLS_OFFSET = 10;
	
	public static final int CIRCLE_SIZE = 10;
	
	private Game model;
	private Color[] colors = { Color.BLACK, Color.WHITE, Color.BLUE,
			Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA, Color.ORANGE,
			Color.PINK };

	private int currentColor;
	
	public GamePanel(Game g) {
		this.model = g;
		this.setBackground(Color.CYAN);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		refreshControlsUI();
	}
	
	public void update() {
		repaint();
	}
	
	public void refreshControlsUI() {
		if (model.myTurn()) {
			System.out.println("Refreshing the controls UI");
			Graphics g = getGraphics();
			int width = getWidth();
			int height = getHeight();
			// bottom 30 pixels = controls bars

			int mainWidth = getWidth();
			int mainHeight = getHeight() - CONTROLS_HEIGHT;
			
			// clear controls bar
			g.setColor(Color.WHITE);
			g.fillRect(0, mainHeight, mainWidth, getHeight() - mainHeight);
			
			// border around controls
			g.setColor(Color.BLACK);
			g.drawRect(0, mainHeight, getWidth(), getHeight() - mainHeight);
	
			// draw colors
			for (int i = 0; i < colors.length; ++i) {
				if (i == currentColor) {
					g.setColor(Color.BLACK);
					g.drawRect(i * 50 + 5, mainHeight + 5, 40, 40);
				}
	
				g.setColor(Color.BLACK);
				g.drawRect(i * 50 + CONTROLS_OFFSET, mainHeight + 10, 30, 30);
	
				g.setColor(colors[i]);
				g.fillRect(i * 50 + CONTROLS_OFFSET, mainHeight + 10, 30, 30);
			}
	
			// clear button
			g.setColor(Color.WHITE);
			g.fillRect(width - 53, mainHeight + 5, 40, 40);
			g.setColor(Color.BLACK);
			g.drawRect(width - 53, mainHeight + 5, 50, 40);
			g.drawString("CLEAR", width - 48, height - 23);
		} 
	}

	public void clear() {
		System.out.println("Clearing");
		Graphics g = getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, getWidth(), getHeight());
		refreshControlsUI();
	}
	
	public int getNumColors() {
		return colors.length;
	}
	
	public void setCurrentColor(int newColor) {
		if (newColor >= 0 && newColor < this.colors.length) {
			this.currentColor = newColor;
		}
	}
	
	public Color getCurrentColor() {
		return this.colors[this.currentColor];
	}
}
