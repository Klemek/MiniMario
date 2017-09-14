package fr.klemek.minimario;

import java.awt.AWTException;
import java.awt.Dialog.ModalExclusionType;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

import fr.klemek.minimario.LocalServer.ConnectionListener;

public abstract class Launch {

	private static final String VERSION = "1.7.5";
	
	private static TrayIcon trayIcon;
	private static PopupMenu popup;
	private static Menu sizeMenu;
	private static MenuItem add, split, kill, exit;

	private static int currentFactor = 2;
	
	public static void main(String[] args) {
		
		SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
            	LocalServer.startServer(new ConnectionListener(){
					@Override
					public void onConnection() {
						new MarioWindow(null, currentFactor, null);
						refreshTray();
					}
            	});
            	new MarioWindow(null, currentFactor, null);
            	addTrayIcon(MarioWindow.getAll().get(0));
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
				new MarioWindow(null, currentFactor, null);
				refreshTray();
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
					MarioWindow child1 = new MarioWindow(mw.getCenter(), currentFactor, mw.getTilesetName());
					MarioWindow child2 = new MarioWindow(mw.getCenter(), currentFactor, mw.getTilesetName());
					child1.getAi().setInvicible(true);
					child2.getAi().setInvicible(true);
					child1.getAi().jump(false);
					child2.getAi().jump(true);
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

        
        final Frame frame = new Frame("MiniMario");
        frame.setResizable(false);
        frame.setUndecorated(true);
        frame.setType(Frame.Type.UTILITY);
        frame.setAlwaysOnTop(true);
        frame.setAutoRequestFocus(true);
        frame.add(popup);
    	frame.setVisible(false);
    	frame.addFocusListener(new FocusListener(){

			@Override
			public void focusGained(FocusEvent e) {
				frame.setVisible(false);
			}

			@Override
			public void focusLost(FocusEvent e) {
				frame.setVisible(false);
			}
    	});
    	
        trayIcon.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				switch(e.getButton()){
				case MouseEvent.BUTTON1:
					if(e.getClickCount() == 2){
						new MarioWindow(null, currentFactor, null);
						refreshTray();
					}
					break;
				case MouseEvent.BUTTON3:
					EventQueue.invokeLater(new Runnable(){
						@Override
						public void run() {
				        	frame.setVisible(true);
				        	popup.show(frame, e.getXOnScreen(), e.getYOnScreen());
				        	frame.setVisible(false);
						}
					});
					break;
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
        });
        
        //trayIcon.setPopupMenu(popup);
        
        refreshTray();
        
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
