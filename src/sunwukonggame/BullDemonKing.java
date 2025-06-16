/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Boss enemy class representing the Bull Demon King.
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;

//Used: https://www.geeksforgeeks.org/java/java-util-timer-class-java/
import java.util.Timer;

//Used: https://www.geeksforgeeks.org/java/java-util-timertask-class-java/
import java.util.TimerTask;

public class BullDemonKing extends GameObject {
    private int health = 300;           
    private int speed = 3;               
    private boolean isFireBreathing = false;
    
    /**
     * Creates Bull Demon King boss.
     * @param app Processing game
     * @param x Where boss spawns at x position
     * @param y Where boss spawns at y position
     * @param imagePath Path to boss image
     */
    public BullDemonKing(PApplet app, int x, int y, String imagePath) {
        super(x, y, 80, 80);
        this.image = app.loadImage(imagePath);
    }
    
    /**
     * Check if boss is using fire breath
     * @return True if fire breathing
     */
    public boolean isFireBreathing() { return isFireBreathing; }
    
    /**
     * Get current health.
     * @return Current health value
     */
    public int getHealth() { return health; }

    /**
     * Apply damage to the boss.
     * @param damage Amount of damage to take
     */
    public void takeDamage(int damage) {
        health -= damage;
    }
    
    /**
     * Activate the fire breath ability
     */
    public void startFireBreath() {
        isFireBreathing = true;
        
        // Turn off fire breath after 1 second
        new Timer().schedule( 
            new TimerTask() {
                public void run() {
                    isFireBreathing = false;
                }
            }, 
            1000 
        );
    }

    /**
     * Move toward a target position.
     * @param targetX Target x coordinate
     * @param targetY Target y coordinate
     */
    public void moveToward(int targetX, int targetY) {
        int dx = targetX - this.x;
        int dy = targetY - this.y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            this.x += (int)(speed * dx / dist);
            this.y += (int)(speed * dy / dist);
        }
    }

    /**
     * Draw the boss with fire breath effect when active
     * @param app The PApplet drawing surface
     */
    public void draw(PApplet app) {
        if (isFireBreathing) {
            app.fill(255, 0, 0, 100);
            app.rect(x, y, width, height);
        }
        
        if (image != null) {
            app.image(image, x, y, width, height);
        } else {
            app.fill(255, 0, 0);
            app.rect(x, y, width, height);
        }
    }
}