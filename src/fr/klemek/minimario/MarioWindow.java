package fr.klemek.minimario;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JWindow;
import javax.swing.Timer;

public class MarioWindow extends JWindow implements ActionListener {

	private static final long serialVersionUID = 1L;
	
	private static final int REFRESH_MS = 20;
	private static final int TILE_W = 20;
	private static final int TILE_H = 24;
	
	private TilePanel p;
	private final Timer refresh;
	
	private int factor = 2;
	
	private MarioAI ai;

	private Point initialClick;
	
	private String tilesetName;
	
	//constructor
	
	public MarioWindow(Point start, int factor, String tilesetName) throws IOException {
		
		this.setBackground(new Color(0, 0, 0, 0));

		if(start == null)
			this.setLocationRelativeTo(null);
		else
			this.setLocation((int)(start.x + TILE_W*factor/2f), (int)( start.y + TILE_H*factor/2f));
		
		this.factor = factor;
		
		this.ai = new MarioAI(TILE_W*factor,TILE_H*factor, factor);
		
		if(tilesetName == null){
			Random r = new Random();
			tilesetName = "mario";
			if(r.nextInt(100)>=90){ //90-99 - 10%
				tilesetName = "luigi";
			}
			
			if(r.nextInt(100)>=99){ //99 - 1%
				tilesetName += "_fire";
			}
		}
		
		this.tilesetName = tilesetName;
		
		
		BufferedImage tileset = ImageIO.read(this.getClass().getResource("/"+tilesetName+".png"));
		this.p = new TilePanel(this, tileset, TILE_W, TILE_H, factor);
		
		this.p.addMouseListener(new MouseAdapter() {
	        public void mousePressed(MouseEvent e) {
	            initialClick = e.getPoint();
	            getComponentAt(initialClick);
	        }
	    });

		this.p.addMouseMotionListener(new MouseMotionAdapter() {
	        @Override
	        public void mouseDragged(MouseEvent e) {

	            // get location of Window
	            int thisX = getLocation().x;
	            int thisY = getLocation().y;

	            // Determine how much the mouse moved since the initial click
	            int xMoved = (thisX + e.getX()) - (thisX + initialClick.x);
	            int yMoved = (thisY + e.getY()) - (thisY + initialClick.y);

	            // Move window to this position
	            int X = thisX + xMoved;
	            int Y = thisY + yMoved;
	            setLocation(X, Y);
	            ai.moved(getLocation());
	        }
	    });
		
		this.add(this.p);

		this.refresh = new Timer(REFRESH_MS, this);

		this.pack();
		this.setAlwaysOnTop(true);

		this.setVisible(true);
		
		this.ai.setPos(this.getLocation());
		
		this.refresh.start();
	}

	//functions
	
	public void kill(){
		this.setVisible(false);
	}
	
	//events
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.refresh)) {
			this.setLocation(this.ai.refresh(REFRESH_MS));
			this.p.setTile(this.ai.getTile(), this.ai.isReversed());
			this.setAlwaysOnTop(true);
		}
	}
	
	//getter/setter
	
	public Point getCenter(){
		return new Point((int)this.getBounds().getCenterX(),(int)this.getBounds().getCenterY());
	}

	public String getTilesetName() {
		return tilesetName;
	}

	public MarioAI getAi() {
		return ai;
	}
	
	public void setFactor(int factor){

		int stx = this.getX();
		int sty = this.getY();
		
		this.setVisible(false);
		
		int dpx = TILE_W*(factor-this.factor);
		int dpy = TILE_H*(factor-this.factor);
		
		this.factor = factor;
		
		this.ai.setSize(TILE_W*factor,TILE_H*factor, factor);
		this.p.setFactor(factor);
		
		this.pack();
		
		this.setLocation((int)(stx-dpx/2f), (int)(sty-dpy/2f));
		this.ai.setPos(this.getLocation());
		
		this.setVisible(true);

	}
}
