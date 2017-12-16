package home.ishtiaq.hangman.clienthandler.support;
/**
 *
 * @author ishtiaq
 */
import home.ishtiaq.hangman.clienthandler.HangmanClientHandler;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Scanner;
/**
 * This is used to randomly choose a word from a file, if the client wan to play.
 */
public class ChooseWord implements Runnable {
				
    @Override
    public void run() {
        System.out.println(System.getProperty("user.dir"));
        System.out.println(new File("sal.html").getAbsolutePath());
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
                    HangmanClientHandler.word = guess.nextLine();
                } else {
                        tem = guess.nextLine();
                }
            }
            guess.close();
        } catch (Exception e) {
                System.out.println("error: " + e);
        }        
     }
}