package client.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import server.GameRoom.GameState;
import client.model.Game;

@SuppressWarnings("serial")
public class GamePanel extends JPanel implements MouseListener,
		MouseMotionListener {
	private Game model;
	private Color[] colors = { Color.BLACK, Color.WHITE, Color.BLUE,
			Color.GREEN, Color.RED, Color.YELLOW, Color.MAGENTA, Color.ORANGE,
			Color.PINK };

	private int currentColor;

	public GamePanel(Game g) {
		this.model = g;
		setBorder(BorderFactory.createLineBorder(Color.black));
		this.setBackground(Color.WHITE);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawUI(g);

	}

	private void drawUI(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		int width = getWidth();
		int height = getHeight();
		// bottom 30 pixels = controls bars

		int mainWidth = getWidth();
		int mainHeight = getHeight() - 50;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, mainWidth, mainHeight);

		g.setColor(Color.BLACK);
		g.drawRect(0, mainHeight, getWidth(), getHeight() - mainHeight);

		// draw colors
		for (int i = 0; i < colors.length; ++i) {
			if (i == currentColor) {
				g.setColor(Color.BLACK);
				g.drawRect(i * 50 + 5, mainHeight + 5, 40, 40);
			}

			g.setColor(Color.BLACK);
			g.drawRect(i * 50 + 10, mainHeight + 10, 30, 30);

			g.setColor(colors[i]);
			g.fillRect(i * 50 + 10, mainHeight + 10, 30, 30);
		}

		// clear button
		g.setColor(Color.WHITE);
		g.fillRect(width - 53, height - 53, 50, 50);
		g.setColor(Color.BLACK);
		g.drawRect(width - 53, height - 53, 49, 49);
		g.drawString("CLEAR", width - 48, height - 23);
	}

	public void update() {
		
	}

	@Override
	public void mouseDragged(MouseEvent e) {

	}

	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
