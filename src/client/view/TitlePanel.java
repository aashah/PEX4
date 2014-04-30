package client.view;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TitlePanel extends JPanel {
	
	private BufferedImage image;
	
	public TitlePanel() {
		System.out.println("Creating title panel");
		this.setBackground(Color.CYAN);
		try {
    		image = ImageIO.read(new File("src/titlePage.png"));
    	} catch (IOException ex) {
    		System.out.println(ex.getMessage());
    	}
		
	}
	
	public void update() {
		this.repaint();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		System.out.println("Painting a picture.");
		g.drawImage(image,0,0,getWidth(),getHeight(),null);
	}
}
