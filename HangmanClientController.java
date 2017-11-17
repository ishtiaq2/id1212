import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This handles all the tasks that the user want to perform. Each task is performed
 * into it's own separate thread, using Executerservice.
 */

public class HangmanClientController {
	
	private final ExecutorService tasks = Executors.newFixedThreadPool(4);
	HangmanClientServerConnection serverConnection;
	ShowInputFromServer output;
	
	public HangmanClientController(ShowInputFromServer output) {
		this.output = output;
		serverConnection = new HangmanClientServerConnection(output);
	}
	
	/**
	 * execute connection task that include connecting to the server, 
	 * and opening input and output streams to and from the server.
	 */
	 
	public void connect(String cmd, String host, String port) {
		//pool.add(new connTas().start();
		tasks.execute(new ConnectionTask(cmd, host, port));
	}
	
	/**
	 * this task is responsible to send the user information entered in the form [user:nickname]
	 */
	 
	public void sendUserInfo(String cmd, String userName) {
			tasks.execute(new SendUserInfoTask(cmd, userName));
	}
	
	/**
	 * This task is used to start playing the game and then keep continue playing,
	 * until the end or the user terminate
	 */
	 
	public void play(String cmd, String game) {
			tasks.execute(new SendPlayHangmanTask(cmd, game));
	}
	
	/**
	 * disconnect from the server
	 */
	 
	public void disconnect(String cmd) {
		
		tasks.execute(new DisconnectTask(cmd));
	}
	
	/**
	 * The following are the threads that are responsible to handle 
	 * the above task.
	 */
	 
	private class ConnectionTask extends Thread {
		
		String cmd = "";
		String host = "";
		String port = "";
				
		public ConnectionTask(String cmd, String host, String port) {
			this.cmd = cmd;
			this.host = host;
			this.port = port;
		}
		public void run() {
			try {
				serverConnection.connectionTask(cmd, host, port);	
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	private class SendUserInfoTask extends Thread {
		String cmd = "";
		String userName = "";
				
		public SendUserInfoTask(String cmd, String userName) {
			this.cmd = cmd;
			this.userName = userName;
		}
		
		public void run() {
			serverConnection.sendUserInfoTask(cmd, userName);
		}
	}
	
	private class SendPlayHangmanTask extends Thread { //SendPlayStatusTask
		String cmd = "";
		String game = "";
				
		public SendPlayHangmanTask(String cmd, String game) {
			this.cmd = cmd;
			this.game = game;
		}
		
		public void run() {
			try {
				serverConnection.sendPlayHangmanTask(cmd, game);
			} catch (Exception e) {
			}
		}
	}
	
	private class DisconnectTask extends Thread {
		String cmd = "";
				
		public DisconnectTask(String cmd) {
			this.cmd = cmd;
			
		}
		
		public void run() {
			try {
				serverConnection.disconnect(cmd);
			} catch (Exception e) {
			}
		}
	}
	
}
	

