/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Fire projectile class used by the Bull Demon King.
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;

public class FireProjectile extends GameObject {
    public int targetX, targetY;  
    private int speed = 4;        
    
    /**
     * Creates new fire projectile.
     * @param startX Starting x position
     * @param startY Starting y position
     * @param targetX Target x position
     * @param targetY Target y position
     */
    public FireProjectile(int startX, int startY, int targetX, int targetY) {
        super(startX, startY, 50, 50);  // Big size for fireball
        this.targetX = targetX;
        this.targetY = targetY;
    }
    
    /**
     * Make projectile move towards target.
     */
    public void move() {
        // Simple movement toward target
        if (x < targetX) x += speed;
        else if (x > targetX) x -= speed;
        
        if (y < targetY) y += speed;
        else if (y > targetY) y -= speed;
    }
    
    /**
     * Draw the fire projectile
     * @param app The PApplet drawing surface
     */
    @Override
    public void draw(PApplet app) {
        // Draw big red fireball
        app.fill(255, 0, 0);  // Red
        app.ellipse(x + width/2, y + height/2, width, height);

    }
}