/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Player class with movement and abilities.
 * Now includes an overloaded constructor for default image usage.
 * 
 * @version 2.1 (June 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;
import processing.core.PImage;

public class Player extends GameObject {
    private boolean isInvulnerable = false;
    private long invulnerableEndTime = 0;
    
    /**
     * Creates new player character with custom image
     * @param app PApplet reference
     * @param x Initial x position
     * @param y Initial y position
     * @param imagePath Path to player image
     */
    public Player(PApplet app, int x, int y, String imagePath) {
        super(x, y, 60, 60);
        this.image = app.loadImage(imagePath);
    }
    
    /**
     * Creates new player character with default image
     * @param app PApplet reference
     * @param x Initial x position
     * @param y Initial y position
     */
    public Player(PApplet app, int x, int y) {
        this(app, x, y, "default_player.png");
    }
    
    /**
     * Check if player has shield on
     * @return True if invulnerable
     */
    public boolean isInvulnerable() {
        return isInvulnerable && System.currentTimeMillis() < invulnerableEndTime;
    }
    
    /**
     * Make player invulnerable for specified duration
     * @param durationMillis Duration in milliseconds
     */
    public void setInvulnerable(long durationMillis) {
        this.isInvulnerable = true;
        this.invulnerableEndTime = System.currentTimeMillis() + durationMillis;
    }
    
    @Override
    public void draw(PApplet app) {
        if (isInvulnerable()) {
            // Flash effect during invulnerability
            if ((System.currentTimeMillis() / 100) % 2 == 0) {
                drawCharacter(app);
            }
        } else {
            drawCharacter(app);
            isInvulnerable = false;
        }
    }
    
    /**
     * Draw the player character
     * @param app The PApplet drawing surface
     */
    private void drawCharacter(PApplet app) {
        if (image != null) {
            app.image(image, x, y, width, height);
        } else {
            app.fill(255, 215, 0);
            app.rect(x, y, width, height);
        }
    }
}