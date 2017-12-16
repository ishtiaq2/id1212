package home.ishtiaq.hangman.hangmanserver;

import home.ishtiaq.hangman.clienthandler.HangmanClientHandler;
import java.net.*;

/**
 * This is used to open server socket and start listening for incoming connection.
 * Once a new client is connected, this is handled to a new thread and the server
 * start listening to a new client.
 */
public class HangmanServer {

    public static void main(String[] args) {
		
        final int PORT = 1024;
        final String host = "localhost";
        boolean run = true;

        try {
                ServerSocket server = new ServerSocket(PORT);
                System.out.println("Serever Listening on Port: " + PORT + " and Host: " +host);
                while (run) {
                        new HangmanClientHandler(server.accept()).start();
                        System.out.println("New Client connected");
                        System.out.println("Server Listening on Port: " + PORT);
                }
        } catch (Exception e) {
                System.out.println("Unable to connect " + e);
        }
    }
}