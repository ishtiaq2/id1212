import java.io.*;
import java.nio.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.*;
import java.util.*;

/**
 * This is used to communicate with the client. Send and receive data to and from
 * the client. 
 */

public class HangmanClientHandler extends Thread {
	
	BufferedReader in;
	PrintWriter out;
	final boolean autoFlush = true;
	boolean connected = true;
	Socket socket;
	
	String userName = "";
	String cmd = "";
	String game = "";
	
	private boolean playing = false;
	public String word = "";
	private String userGuess = "";
	private ChooseWord chooseWord;
	private PlayGame playGame;
	private int totalScore = 0;
		
	public HangmanClientHandler(Socket socket) throws Exception {
		this.socket = socket;
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), autoFlush);
	}
	
	/** 
	 * continuously listen for the incoming data and analyse it to get the client
	 * intention and perform the correponding action.
	 */
	 
	public void run() {
		out.println("Your are now Connected");
		out.println("Enter [user: name] to register");
		
		try {
			while (connected) {
				parseLine(in.readLine());
				
				//if not playing switch else PlayGame(choose and return world and attempts)
				
				switch(cmd) {
					
					case "user":
						System.out.println(userName +" is registered now");
						out.println("Welcome " + userName);
						out.println("Enter [play:hangman] to play game");
						break;
						
					case "play":
						if (!playing) {
							System.out.println(userName +" want to play " +game);
							chooseWord = new ChooseWord();
							chooseWord.start();
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
							}
							System.out.println("Word : " + word);
							playGame = new PlayGame(word, out);
							playGame.processWord(word);
							playing = true;
						} else {
							playGame.processWord(userGuess);
						}
						break;
					
					case "reset": 
						playing = false;
						System.out.println("Game reset");
						break;
					case "quit":
						connected = false;
						closeConnection("User Disconnedted");
						//chooseWord.interrupt();
						break;
					default:
						System.out.println("Invalid command");
						break;
				}
			}
		} catch (Exception e) {
			//String s = exceptionToString(e);
			closeConnection("Connection Lost");
		}
		
	}
	
	/**
	 * close the connection if an exception occure, or the user want to quit.
	 */
	 
	public void closeConnection(String s) {
		
		System.out.println(s);
			connected = false;
			try {
				socket.close();
			}catch (Exception ee) {
				
			}
	}
	
	/*public String exceptionToString(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String eTrace = sw.toString();
		return eTrace;
	}*/
	
	/**
	 * analyse the incoming data
	 */
	 
	public String parseLine(String line) {
		String[] temp = line.split(":");
		
		if (temp[0].equalsIgnoreCase("user")) {
			cmd = temp[0];
			userName = temp[1];
		} else if (temp[0].equalsIgnoreCase("play")) {
			if (!playing) {
				cmd = temp[0];
				game = temp[1];
			} else {
				cmd = temp[0];
				userGuess = temp[1];
			}
			
		} else if(temp[0].equalsIgnoreCase("quit")) {
			System.out.println(temp[0]);
			cmd = temp[0];
		} else if (temp[0].equalsIgnoreCase("reset")) {
			cmd = temp[0];
		} else {
			cmd = "invalid command";
		}
		return cmd;
	}
	
	/**
	 * This is used to randomly choose a word from a file, if the client wan to play.
	 */
	 
	private class ChooseWord extends Thread {
				
		public void run() {
			int r = 0;
			int lineCounter = 0;
			String[] fileArray;
			try {
				Scanner file = new Scanner(new BufferedReader(new FileReader("words.txt")));
				file.useDelimiter("\r\n");
				while(file.hasNext()) {
					lineCounter++;
					System.out.println("Line " + lineCounter + ": " +file.next());
				}
				file.close();
				r = 1 + (int)(Math.random() * lineCounter);
				
				Scanner guess = new Scanner(new BufferedReader(new FileReader("words.txt")));
				
				int temp = 0;
				String tem = "";
				while(guess.hasNext()) {
					temp++;
					
					if ( temp == r ) {
						word = guess.nextLine();
					} else {
						tem = guess.nextLine();
						
					}
				}
				guess.close();
				
							
			} catch (Exception e) {
				
			}
				//guessWord = word;
			
		}
	}
	
	/**
	 * When the user is interested to play, the controle reamin 
	 */
	 
	private class PlayGame {
		String actualWord = "";
		PrintWriter out;
		int attempts = 0;
		int tempScore = totalScore;
		boolean sendFirst = true;
		private boolean attemptStatus = false;
		char[] ch;
		char c;
		
		public PlayGame(String actualWord, PrintWriter out) {
			this.actualWord = actualWord;
			this.out = out;
			ch = new char[actualWord.length()];
			attempts = actualWord.length();
		}
		
		public void processWord(String userGuess) {
			if (sendFirst) {
				attempts = actualWord.length();			
				for (int i=0; i< attempts; i++) {
					ch[i] = '-';
				}
				
				sendGuess(ch);
				sendFirst = false;
				
			} else if (attempts > 0) {
				
				if (actualWord.indexOf(userGuess) != -1) {
					
					c = userGuess.charAt(0);
					
					//sendGuess(c);
					for (int i = 0; i< actualWord.length(); i++) {
						
						char t = actualWord.charAt(i);
						if (t == c) {
							System.out.println("Successfull Guess" + c);
							ch[i] = c;
						}
					}
					
					if ( (actualWord.equalsIgnoreCase(userGuess)) || ( String.valueOf(ch).equalsIgnoreCase(actualWord) ) ) {
						System.out.println("User wins ...........");
						
						for (int i = 0; i< actualWord.length(); i++) {
							ch[i] = actualWord.charAt(i);
						}
						totalScore++;
					}
						
					
					attemptStatus = true;
					sendGuess(ch);
					
				} else {
					attempts = attempts - 1;
					attemptStatus = false;
					sendGuess(ch);
				}
			} else {
				 char[] t= {'l', 'o', 's', 't'};
				 sendGuess(t);
			}
			
		}
			public void sendGuess(char[] s) {
				
				String str = String.valueOf(s);
				
				if ( (totalScore == tempScore) && (attempts > 0)) {
					try {
						out.println("*****************************");
						out.println("Game On...");
						if ( (attemptStatus) && (!sendFirst) ) { //start: !sendfirst
							out.println("Successfull Attempt!");
						} else if ((!attemptStatus) && (!sendFirst)){
							out.println("Failed Attempt");
						}
						out.println("Reamining Failed Attempts: " + attempts);
						out.println("Total Score: " + totalScore); 
						out.print("Word offered: ");
						out.println(str);
						out.println("Enter [play:a or play:word] where 'a' or 'word' is your choice of chars");
						//out.println("*****************************");
					}
					catch(Exception e) {
						System.out.println("error" + e);
					}
				} else if (totalScore > tempScore) {
					out.println("*************************************");
					out.println(str);
					out.println("You Win!");
					out.println("Reamining Failed Attempts: " + attempts);
					out.println("Total Score: " + totalScore); 
					out.println("Reset to play again");
					resetGame("");
					sendFirst = true;
				} else if (attempts == 0) {
					totalScore = totalScore - 1;
					out.println("*************************************");
					out.println("You lost!");
					out.println("Reamining Failed Attempts: " + attempts);
					out.println("Total Score: " + totalScore); 
					out.println("Reset to play again");
					resetGame("");
					sendFirst = true;
				}
		}
		
		public void resetGame(String reset) {
			playing = false;
			
		}
	}
	
	
}