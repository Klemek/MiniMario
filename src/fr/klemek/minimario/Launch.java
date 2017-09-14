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

import fr.klemek.minimario.LocalServer.ConnectionListener;

public abstract class Launch {

	private static final String VERSION = "1.7.3";
	
	private static TrayIcon trayIcon;
	private static PopupMenu popup;
	private static Menu sizeMenu;
	private static MenuItem add, split, kill, exit;

	private static int currentFactor = 2;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            try {
	            	LocalServer.startServer(new ConnectionListener(){
						@Override
						public void onConnection() {
							try {
								new MarioWindow(null, currentFactor, null);
								refreshTray();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
	            	});
	            	
	            	new MarioWindow(null, currentFactor, null);
	            	addTrayIcon(MarioWindow.getAll().get(0));
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
		
		trayIcon = new TrayIcon(Utils.createImage("/icon_"+mwRef.getTilesetName()+".png", "icon"));
        trayIcon.setImageAutoSize(true);

        popup = new PopupMenu();
        
        sizeMenu = new Menu("Change size");
        
        for(int i = 0; i < 5; i++){
        	final int f = (int)Math.pow(2, i);
        	MenuItem size = new MenuItem(f+"x");
            size.addActionListener(new ActionListener(){
    			@Override
    			public void actionPerformed(ActionEvent e) {
    				for(MarioWindow mw:MarioWindow.getAll())
    					mw.setFactor(f);
    				currentFactor = f;
    				refreshTray();
    			}
            });
            sizeMenu.add(size);
        }
  
        add = new MenuItem("Another one");
        add.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					new MarioWindow(null, currentFactor, null);
					refreshTray();
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
				save.addAll(MarioWindow.getAll());
				MarioWindow.getAll().clear();
				currentFactor /= 2;
				for(MarioWindow mw:save){
					try {
						MarioWindow child1 = new MarioWindow(mw.getCenter(), currentFactor, mw.getTilesetName());
						MarioWindow child2 = new MarioWindow(mw.getCenter(), currentFactor, mw.getTilesetName());
						child1.getAi().setInvicible(true);
						child2.getAi().setInvicible(true);
						child1.getAi().jump(false);
						child2.getAi().jump(true);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					mw.kill();
				}
				save = null;
				System.gc();
				refreshTray();
			}
        });
        
        kill = new MenuItem("Kill one randomly");
        kill.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				Random r = new Random();
				int index = r.nextInt(MarioWindow.getAll().size());
				MarioWindow.getAll().get(index).kill();
				MarioWindow.getAll().remove(index);
				System.gc();
				refreshTray();
			}
        });
        
        exit = new MenuItem("Kill it !");
        exit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
        });
        
        refreshTray();
        trayIcon.setPopupMenu(popup);
        
        final SystemTray tray = SystemTray.getSystemTray();
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            System.out.println("TrayIcon could not be added.");
            System.exit(0);
        }
	}
	
	private static void refreshTray(){
		popup.removeAll();
		popup.add(sizeMenu);
		popup.add(add);
		if(currentFactor > 1){
			popup.add(split);
		}
		if(MarioWindow.getAll().size()>1){
			exit.setLabel("Kill them all !");
			popup.add(kill);
			trayIcon.setToolTip(MarioWindow.getAll().size()+" MiniMarios ! (version "+VERSION+")\nBy Klemek");
		}else{
			exit.setLabel("Kill it !");
			trayIcon.setToolTip("MiniMario ! (version "+VERSION+")\nBy Klemek");
		}
        popup.add(exit);
	}
	
}
