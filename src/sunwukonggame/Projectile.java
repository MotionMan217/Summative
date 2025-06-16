/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Projectile class with simplified movement
 * @version 2.1 (Simplified June 2025)
 * @author
 */
package sunwukonggame;

import processing.core.PApplet;

public class Projectile extends GameObject {
    private float dx, dy;
    
    /**
     * Fire projectile
     * @param app PApplet reference
     * @param startX Starting x position
     * @param startY Starting y position
     * @param targetX Target x position 
     * @param targetY Target y position
     * @param imagePath Image path
     */
    public Projectile(PApplet app, int startX, int startY, int targetX, int targetY, String imagePath) {
        super(startX, startY, 20, 20);
        
        //return direction to target (AI helped)
        float totalDistance = dist(startX, startY, targetX, targetY);
        if (totalDistance > 0) {
            this.dx = (targetX - startX) / totalDistance * 8; // 8 is speed
            this.dy = (targetY - startY) / totalDistance * 8;
        } else {
            this.dx = 0;
            this.dy = 0;
        }
        
        this.image = app.loadImage(imagePath);
    }

    //Helper method to calculate distance (AI helped)
    private float dist(float x1, float y1, float x2, float y2) {
        return (float)Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
    }

    public void move() {
        x += dx;
        y += dy;
    }
    
    public void draw(PApplet app) {
        if (image != null) {
            app.image(image, x, y, width, height);
        } else {
            app.fill(255, 255, 0);
            app.ellipse(x + width/2, y + height/2, width, height);
        }
    }
}