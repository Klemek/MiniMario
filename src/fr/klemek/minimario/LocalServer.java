package fr.klemek.minimario;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class LocalServer extends Thread {

	public static final int[] PORTS = {34881, 41834, 16118, 24326}; //4 random, one should be available

	public static final String CONTACT = "MiniMario !";
	
	private ServerSocket serverSocket = null;
	private Socket clientSocket = null;
	
	private ConnectionListener listener;
	
	public LocalServer(ConnectionListener listener){
		this.listener = listener;
	}
	
	@Override
	public void run() {
		int i = 0;
		while(i<PORTS.length){
			try {
				System.out.println("Starting MiniMario local server on port "+PORTS[i]+" ...");
				serverSocket = new ServerSocket(PORTS[i], 1);
				System.out.println("Started MiniMario local server on port "+PORTS[i]);
				while (true) {
					clientSocket = serverSocket.accept();
					PrintWriter pw = new PrintWriter(clientSocket.getOutputStream());
					pw.println(CONTACT);
					pw.flush();
					if(listener!=null)
						listener.onConnection();
					clientSocket.close();
				}
			} catch (IOException ioe) {
				System.out.println("Error in LocalServer: " + ioe);
			}
		}
		System.out.println("Couldn't start MiniMario local server on given ports");
	}
	
	@SuppressWarnings("resource")
	public static void startServer(ConnectionListener listener){
		for(int port:PORTS){
			try {
				Socket socket = new Socket("localhost", port);
				BufferedReader br = new BufferedReader (new InputStreamReader (socket.getInputStream()));
				String serverResponse = br.readLine();
				if(serverResponse.equals(CONTACT)){
					System.out.println("Already running MiniMario local server on port : "+port);
					System.exit(0);
				}
			} catch (Exception e) {}
		}
		new LocalServer(listener).start();
	}

	protected interface ConnectionListener{
		public void onConnection();
	}
}
