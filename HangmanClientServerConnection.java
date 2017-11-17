import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This is used to connect to the server, and exchange information between the 
 * client and the server.
 */

public class HangmanClientServerConnection {
	
	//private static final int TIMEOUT_HALF_HOUR = 1800000;
	private static final int TIMEOUT_HALF_MINUTE = 50000;
	public Socket socket;
	private PrintWriter out;
	private BufferedReader in; 
	public boolean connected = true;
	ShowInputFromServer outputHandler;
	
	public HangmanClientServerConnection(ShowInputFromServer outputHandler) {
		this.outputHandler = outputHandler;
	}
	
	/**
	 * Connection to the server is initiated, input and outputstreams are also 
	 * created (incase of successful connection), and a listener thread is started 
	 * to continuously listen to the server.
	 */
	 
	public void connectionTask(String cmd, String host, String p) throws Exception {
		
		socket = new Socket();
		int port = Integer.parseInt(p);
		socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
		//socket.setSoTimeout(TIMEOUT_HALF_HOUR);
		connected = true;
		boolean autoFlush = true;
		out = new PrintWriter(socket.getOutputStream(), autoFlush);
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		
		new ListenToServer(outputHandler).start();
	}
	
	public void sendUserInfoTask(String cmd, String userName) {
		out.println(cmd + ":" + userName);
	}
	
	public void sendPlayHangmanTask(String cmd, String game) {
	
		try {
			out.println(cmd +":" + game);
			for (int i=0; i<10; i++) {
				/*System.out.println(i);
				Thread.sleep(1000);*/
			}
		} catch (Exception e) {
			outputHandler.show("Check connection");
		}
	}
	
	public void disconnect(String cmd) throws Exception {
		out.println(cmd +":" +"temp");
		outputHandler.show(cmd);
		connected = false;
		socket.close();
	}
	
	/**
	 * The thread responsible to handle the incoming data and display it to the user.
	 */
	 
	private class ListenToServer extends Thread {
		ShowInputFromServer outputHandler;
		
		public ListenToServer(ShowInputFromServer output) {
			this.outputHandler = output;
		}
		
		public void run() {
			try {
				while (connected) {
					outputHandler.show(in.readLine());
				}
			} catch (Exception e) {
				outputHandler.show("Connection lost");
			}
		}
		
		
	}
	
}