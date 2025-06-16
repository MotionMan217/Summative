/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Handles saving and loading high scores to and from files
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import java.io.*;
import java.util.Scanner;

public class FileUtils {
    private static final String HIGH_SCORE_FILE = "highscore.dat";
    
    /**
     * Loads high score from file.
     * @return The loaded high score, or 0 if never played
     */
    public static int loadHighScore() {
        try {
            File file = new File(HIGH_SCORE_FILE);
            if (file.exists()) {
                Scanner scanner = null;
                try {
                    scanner = new Scanner(file);
                    if (scanner.hasNextInt()) {
                        return scanner.nextInt();
                    }
                } finally {
                    if (scanner != null) {
                        scanner.close();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading high score: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Saves high score to file.
     * @param score The score to save
     */
    public static void saveHighScore(int score) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.println(score);
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }
}