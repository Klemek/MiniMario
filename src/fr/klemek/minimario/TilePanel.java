package fr.klemek.minimario;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;
import javax.swing.JWindow;

public class TilePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final int tileWidth, tileHeight, imageWidth, imageHeight, columns, rows;
	private int id, factor;
	private BufferedImage image;
	private boolean reversed = false;

	
	
	public TilePanel(JWindow parent, BufferedImage image, int tileWidth, int tileHeight, int factor) {
		super();
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.image = image;
		this.imageWidth = image.getWidth();
		this.imageHeight = image.getHeight();
		this.columns = this.imageWidth / this.tileWidth;
		this.rows = this.imageHeight / this.tileHeight;
		System.out.println("Tileset : "+this.columns+"x"+this.rows);
		this.setFactor(factor);
		this.setBackground(new Color(0, 0, 0, 0));
		this.setOpaque(false);
	}
	
	public void setFactor(int factor){
		this.factor = factor;
		Dimension size = new Dimension(tileWidth * factor, tileHeight * factor);
		this.setPreferredSize(size);
		this.setMinimumSize(size);
		this.setMaximumSize(size);
	}

	@Override
	public void paintComponent(Graphics g) {
		g.clearRect(0, 0, tileWidth * factor, tileHeight * factor);
		if (id >= 0 && id < rows * columns) {
			if (reversed) {
				g.drawImage(image, 0, 0, tileWidth * factor, tileHeight * factor, (1 + id % columns) * tileWidth,
						(id / columns) * tileHeight, (id % columns) * tileWidth, (1 + id / columns) * tileHeight, this);
			} else {
				g.drawImage(image, 0, 0, tileWidth * factor, tileHeight * factor, (id % columns) * tileWidth,
						(id / columns) * tileHeight, (1 + id % columns) * tileWidth, (1 + id / columns) * tileHeight, this);
			}
		}
		g.dispose();
	}

	public void setTile(int id, boolean reversed) {
		this.id = id;
		this.reversed = reversed;
		this.repaint();
	}

}
