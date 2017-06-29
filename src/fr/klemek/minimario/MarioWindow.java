package fr.klemek.minimario;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
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

	private static final String VERSION = "1.6.1";
	
	private static final int REFRESH_MS = 20;
	private static final int TILE_W = 20;
	private static final int TILE_H = 24;
	
	private TilePanel p;
	private final Timer refresh;

	private int factor = 2;
	
	private MarioAI ai;

	private Point initialClick;
	
	public MarioWindow() throws IOException {
		
		this.setBackground(new Color(0, 0, 0, 0));
		this.setLocationRelativeTo(null);

		this.ai = new MarioAI(TILE_W*factor,TILE_H*factor, factor);
		
		Random r = new Random();
		String tileset_name = "mario";
		if(r.nextInt(100)>=90){ //90-99 - 10%
			tileset_name = "luigi";
		}
		
		if(r.nextInt(100)>=99){ //99 - 1%
			tileset_name += "_fire";
		}
		
		
		BufferedImage tileset = ImageIO.read(this.getClass().getResource("/"+tileset_name+".png"));
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
		
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            return;
        }

        TrayIcon trayIcon = new TrayIcon(Utils.createImage("/icon_"+tileset_name+".png", "icon"));
        trayIcon.setImageAutoSize(true);

        final PopupMenu popup = new PopupMenu();
        
        
        
        Menu sizeMenu = new Menu("Change size");
        popup.add(sizeMenu);
        
        for(int i = 0; i < 5; i++){
        	final int f = (int)Math.pow(2, i);
        	MenuItem size = new MenuItem(f+"x");
            size.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				setFactor(f);
    			}
            });
            sizeMenu.add(size);
        }
        
        MenuItem exit = new MenuItem("Kill it !");
        exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
        });
        popup.add(exit);
        
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("MiniMario ! (version "+VERSION+")\nBy Klemek");
        
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
        }
		
		
		this.setVisible(true);

		this.ai.setPos(this.getLocation());
		
		this.refresh.start();
	}

	private void setFactor(int factor){
		this.setVisible(false);
		this.factor = factor;
		this.ai.setSize(TILE_W*factor,TILE_H*factor, factor);
		this.p.setFactor(factor);
		this.pack();
		this.setVisible(true);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(this.refresh)) {
			this.setLocation(this.ai.refresh(REFRESH_MS));
			this.p.setTile(this.ai.getTile(), this.ai.isReversed());
		}
	}
}
