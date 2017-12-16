/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package home.ishtiaq.hangman.clienthandler.support;

import home.ishtiaq.hangman.clienthandler.HangmanClientHandler;
/**
 *
 * @author ishtiaq
 */
public class PlayGame {
            
    String actualWord = "";
    HangmanClientHandler networkHandler;
    public int attempts = 0;
    public int tempScore = 0;
    public boolean sendFirst = true;
    public boolean attemptStatus = false;
    char[] ch;
    char c;

    public PlayGame(String actualWord, HangmanClientHandler networkHandler) {
        this.actualWord = actualWord;
        this.networkHandler = networkHandler;
        tempScore = networkHandler.totalScore;
        ch = new char[actualWord.length()];
        attempts = actualWord.length();
    }

    public void processWord(String userGuess) {
        if (sendFirst) {
            attempts = actualWord.length();			
            for (int i=0; i< attempts; i++) {
                ch[i] = '-';
            }
            networkHandler.sendGuess(ch);
            sendFirst = false;

        } else if (attempts > 0) {

            if (actualWord.indexOf(userGuess) != -1) {
                c = userGuess.charAt(0);
                for (int i = 0; i< actualWord.length(); i++) {
                    char t = actualWord.charAt(i);
                    if (t == c) {
                        System.out.println("Correct Guess" + c);
                        ch[i] = c;
                    }
                }
                if ( (actualWord.equalsIgnoreCase(userGuess)) || ( String.valueOf(ch).equalsIgnoreCase(actualWord) ) ) {
                    //System.out.println(networkHandler.userName + " you win!");
                    for (int i = 0; i< actualWord.length(); i++) {
                        ch[i] = actualWord.charAt(i);
                    }
                    networkHandler.totalScore++;
                }
                attemptStatus = true;
                networkHandler.sendGuess(ch);
            } else {
                    attempts = attempts - 1;
                    attemptStatus = false;
                    networkHandler.sendGuess(ch);
            }
            } else {
                char[] t= {'l', 'o', 's', 't'};
                networkHandler.sendGuess(t);
            }
        }
}