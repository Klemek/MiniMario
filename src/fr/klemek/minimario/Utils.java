package fr.klemek.minimario;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.net.URL;

import javax.swing.ImageIcon;

public abstract class Utils {

	private static Rectangle[] bounds = new Rectangle[0];
	
	public static Point2D.Float add(Point2D.Float p1, Point2D.Float p2) {
		return new Point2D.Float(p1.x + p2.x, p1.y + p2.y);
	}

	public static int getMaxX() {
		int maxx = 0;
		
		GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if(bounds.length == 0){
			bounds = new Rectangle[gds.length];
		}
		for (int i = 0; i < gds.length; i++) {
			GraphicsDevice gd = gds[i];
			bounds[i] = gd.getDefaultConfiguration().getBounds();
			if(bounds[i].getMaxX()>maxx){
				maxx=(int) bounds[i].getMaxX();
			}
		}
		return maxx;
	}
	
	public static int getMinX(){
		int minx = 0;
		GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if(bounds.length == 0){
			bounds = new Rectangle[gds.length];
		}
		for (int i = 0; i < gds.length; i++) {
			GraphicsDevice gd = gds[i];
			bounds[i] = gd.getDefaultConfiguration().getBounds();
			if(bounds[i].getMinX()<minx){
				minx=(int) bounds[i].getMinX();
			}
		}
		return minx;
	}
	
	public static int[] getYBounds(int x){
		if(bounds.length == 0){
			GraphicsDevice[] gds = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
			bounds = new Rectangle[gds.length];
			for (int i = 0; i < gds.length; i++) {
				GraphicsDevice gd = gds[i];
				Utils.bounds[i] = gd.getDefaultConfiguration().getBounds();
			}
		}
		
		int[] out = null;
		
		for(Rectangle b:bounds){
			if(x>=b.getMinX()&&x<=b.getMaxX()){
				if(out == null){
					out = new int[]{(int)b.getMinY(),(int)b.getMaxY()};
				}else{
					if(b.getMinY()<out[0])
						out[0] = (int) b.getMinY();
					if(b.getMaxY()>out[1])
						out[1] = (int) b.getMaxY();
				}
			}
		}
		if(out == null)
			out = new int[]{0,0};
		return out;
	}
	
	//Obtain the image URL
    public static Image createImage(String path, String description) {
        URL imageURL = Utils.class.getResource(path);
         
        if (imageURL == null) {
            System.err.println("Resource not found: " + path);
            return null;
        } else {
            return (new ImageIcon(imageURL, description)).getImage();
        }
    }
}