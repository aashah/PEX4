package client.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class GamePanel extends JPanel {
	public GamePanel() {
		setBorder(BorderFactory.createLineBorder(Color.black));
		this.setBackground(Color.YELLOW);
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawString("This is my custom Panel!", 10, 20);
	}
}
