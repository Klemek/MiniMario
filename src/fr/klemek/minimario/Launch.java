package fr.klemek.minimario;

import java.io.IOException;

import javax.swing.SwingUtilities;

public class Launch {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	            try {
					new MarioWindow();
				} catch (IOException e) {
					e.printStackTrace();
				}
	        }
	    });
	}

}
