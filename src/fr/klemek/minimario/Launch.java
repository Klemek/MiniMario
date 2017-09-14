package fr.klemek.minimario;

import java.awt.AWTException;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

public abstract class Launch {

	private static final String VERSION = "1.7.1";
	
	private static List<MarioWindow> windows;
	
	private static PopupMenu popup;
	private static Menu sizeMenu;
	private static MenuItem add, split, kill, exit;

	private static int currentFactor = 2;
	
	public static void main(String[] args) {

		windows = new ArrayList<>();
		
		SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            try {
	            	windows.add(new MarioWindow(null, currentFactor, null));
	            	addTrayIcon(windows.get(0));
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    });
	}
	
	public static void addTrayIcon(MarioWindow mwRef){
		if (!SystemTray.isSupported()) {
            System.out.println("SystemTray is not supported");
            System.exit(0);
        }
		
		TrayIcon trayIcon = new TrayIcon(Utils.createImage("/icon_"+mwRef.getTilesetName()+".png", "icon"));
        trayIcon.setImageAutoSize(true);

        popup = new PopupMenu();
        
        sizeMenu = new Menu("Change size");
        
        for(int i = 0; i < 5; i++){
        	final int f = (int)Math.pow(2, i);
        	MenuItem size = new MenuItem(f+"x");
            size.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				for(MarioWindow mw:windows)
    					mw.setFactor(f);
    				currentFactor = f;
    				refreshPopupMenu();
    			}
            });
            sizeMenu.add(size);
        }
  
        add = new MenuItem("Another one");
        add.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					windows.add(new MarioWindow(null, currentFactor, null));
					refreshPopupMenu();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
        });
        
        split = new MenuItem("Split");
        split.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				List<MarioWindow> save = new ArrayList<>();
				save.addAll(windows);
				windows.clear();
				currentFactor /= 2;
				for(MarioWindow mw:save){
					try {
						MarioWindow child1 = new MarioWindow(mw.getCenter(), currentFactor, mw.getTilesetName());
						MarioWindow child2 = new MarioWindow(mw.getCenter(), currentFactor, mw.getTilesetName());
						child1.getAi().run(false);
						child2.getAi().run(true);
						windows.add(child1);
						windows.add(child2);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					mw.kill();
				}
				save = null;
				System.gc();
				refreshPopupMenu();
			}
        });
        
        kill = new MenuItem("Kill one randomly");
        kill.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Random r = new Random();
				int index = r.nextInt(windows.size());
				windows.get(index).kill();
				windows.remove(index);
				System.gc();
				refreshPopupMenu();
			}
        });
        
        exit = new MenuItem("Kill it !");
        exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
        });
        
       

        refreshPopupMenu();
        trayIcon.setPopupMenu(popup);
        trayIcon.setToolTip("MiniMario ! (version "+VERSION+")\nBy Klemek");
        
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            System.exit(0);
        }
	}
	
	private static void refreshPopupMenu(){
		popup.removeAll();
		popup.add(sizeMenu);
		popup.add(add);
		if(currentFactor > 1){
			popup.add(split);
		}
		if(windows.size()>1){
			exit.setLabel("Kill them all !");
			popup.add(kill);
		}else{
			exit.setLabel("Kill it !");
		}
        popup.add(exit);
	}
	
}
