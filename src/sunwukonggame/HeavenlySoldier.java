/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Heavenly soldier class
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;

public class HeavenlySoldier extends GameObject {
    /**
     * Creates new heavenly soldier
     * @param app The PApplet drawing surface
     * @param x Initial x position
     * @param y Initial y position
     * @param imagePath Path to soldier image
     */
    public HeavenlySoldier(PApplet app, int x, int y, String imagePath) {
        super(x, y, 40, 40);
        this.image = app.loadImage(imagePath);
    }
    
    /**
     * Make soldier move toward target
     * @param targetX Target x position
     * @param targetY Target y position
     */
    public void moveToward(int targetX, int targetY) {
        int dx = targetX - x;
        int dy = targetY - y;
        double dist = Math.sqrt(dx * dx + dy * dy);
        if (dist > 0) {
            x += (int)(4 * dx / dist);
            y += (int)(4 * dy / dist);
        }
    }
    
    /**
     * Draw the soldier character.
     * @param app The PApplet drawing surface
     */
    public void draw(PApplet app) {
        if (image != null) {
            app.image(image, x, y, width, height);
        } else {
            app.fill(0, 0, 255); // Blue fallback
            app.rect(x, y, width, height);
        }
    }
}