package home.ishtiaq.hangman.clienthandler;

import home.ishtiaq.hangman.clienthandler.support.ChooseWord;
import home.ishtiaq.hangman.clienthandler.support.PlayGame;
import java.io.*;
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
    public static String word = "";
    private String userGuess = "";
    public Thread chooseWord;
    private PlayGame playGame;
    public  int totalScore = 0;

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
                cmd = parseLine(in.readLine());

                switch(cmd) {
                    case "user":
                        System.out.println(userName +" is registered now");
                        out.println("Welcome " + userName);
                        out.println("Enter [play:hangman] to play game");
                        break;
                    case "play":
                        if (!playing) {
                            System.out.println(userName +" want to play " +game);
                            chooseWord = new Thread(new ChooseWord());
                            chooseWord.start();
                            try {
                                Thread.sleep(1000);
                            } catch (Exception e) {
                            }
                            System.out.println("Word : " + word);
                            playGame = new PlayGame(word, this);
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
            closeConnection("Connection to " + userName + " Lost");
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
        } catch (Exception ee) {
        }
    }
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
        
    public void sendGuess(char[] s) {

        String str = String.valueOf(s);
        if ( (totalScore == playGame.tempScore) && (playGame.attempts > 0)) {
            try {
                out.println("*****************************");
                out.println("Game On...");
                if ( (playGame.attemptStatus) && (!playGame.sendFirst) ) { //start: !sendfirst
                   out.println("Successful Attempt!");
                } else if ((!playGame.attemptStatus) && (!playGame.sendFirst)){
                    out.println("Failed Attempt");
                }
                out.println("Remaining Failed Attempts: " + playGame.attempts);
                out.println("Total Score: " + totalScore); 
                out.print("Word offered: ");
                out.println(str);
                out.println("Enter [play:a or play:word] where 'a' or 'word' is your choice of chars");
                //out.println("*****************************");
            }
            catch(Exception e) {
                System.out.println("error" + e);
            }

        } else if (totalScore > playGame.tempScore) {
            out.println("*************************************");
            out.println(str);
            out.println("You Win!");
            out.println("Remaining Failed Attempts: " + playGame.attempts);
            out.println("Total Score: " + totalScore); 
            out.println("Enter [play:hangman] to play game");
            resetGame("");
            playGame.sendFirst = true;

        } else if (playGame.attempts == 0) {
            totalScore = totalScore - 1;
            out.println("*************************************");
            out.println("You lost!");
            out.println("Remaining Failed Attempts: " + playGame.attempts);
            out.println("Total Score: " + totalScore); 
            out.println("Enter [play:hangman] to play game");
            resetGame("");
            playGame.sendFirst = true;
        }
    }

    public void resetGame(String reset) {
        playing = false;
    }
}