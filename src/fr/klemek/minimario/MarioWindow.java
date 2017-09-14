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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JWindow;
import javax.swing.Timer;

public class MarioWindow extends JWindow{

	private static final long serialVersionUID = 797825180779341089L;

	private static List<MarioWindow> windows = new ArrayList<>();
	
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
	
	public MarioWindow(Point start, int factor, String tilesetName){
		
		this.setBackground(new Color(0, 0, 0, 0));
		
		this.refresh = new Timer(REFRESH_MS, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				setLocation(ai.refresh(REFRESH_MS));
				p.setTile(ai.getTile(), ai.isReversed());
				setAlwaysOnTop(true);
			}
		});
		
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
		
		BufferedImage tileset;
		try {
			tileset = ImageIO.read(this.getClass().getResource("/"+tilesetName+".png"));
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		//this.setLocationRelativeTo(null);
		if(start == null)
			this.setLocation(Utils.randomScreenLocation(TILE_W*factor, TILE_H*factor));
		else
			this.setLocation((int)(start.x + TILE_W*factor/2f), (int)( start.y + TILE_H*factor/2f));
		
		this.factor = factor;
		
		this.ai = new MarioAI(TILE_W*factor,TILE_H*factor, factor);
		
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

		this.pack();
		this.setAlwaysOnTop(true);

		this.setVisible(true);
		
		this.ai.setPos(this.getLocation());
		
		this.refresh.start();
		
		windows.add(this);

		System.out.println("Spawned "+tilesetName+" at ("+this.getX()+","+this.getY()+")");
	}

	//functions
	
	public void kill(){
		this.setVisible(false);
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
	
	//static functions

	public static List<MarioWindow> getAll(){
		return windows;
	}
}
