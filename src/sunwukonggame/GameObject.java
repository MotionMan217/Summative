/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 * Class for all game objects with position, size, and collision detection.
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;
import processing.core.PImage;

public abstract class GameObject {
    protected int x, y;          
    protected int width, height; 
    protected PImage image;
    
    /**
     * Creates a new game object
     * @param x Initial x position
     * @param y Initial y position
     * @param width Object width
     * @param height Object height
     */
    public GameObject(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Get the x position
     * @return Current x coordinate
     */
    public int getX() { return x; }
    
    /**
     * Get the y position
     * @return Current y coordinate
     */
    public int getY() { return y; }
    
    /**
     * Get the object width
     * @return Width in pixels
     */
    public int getWidth() { return width; }
    
    /**
     * Get the object height
     * @return Height in pixels
     */
    public int getHeight() { return height; }
    
    /**
     * Set the x position
     * @param x New x coordinate
     */
    public void setX(int x) { this.x = x; }
    
    /**
     * Set the y position
     * @param y New y coordinate
     */
    public void setY(int y) { this.y = y; }
    
    /**
     * Set the object's image
     * @param img The PImage to use
     */
    public void setImage(PImage img) {
        this.image = img;
    }
    
    /**
     * Draw the game object
     * @param app The PApplet drawing surface
     */
    public void draw(PApplet app) {
        if (image != null) {
            app.image(image, x, y, width, height);
        } else {
            //If image is missing
            app.fill(255, 0, 255);
            app.rect(x, y, width, height);
        }
    }
    
    /**
     * Check collision with another game object
     * @param other The other game object
     * @return True if objects are colliding
     */
    public boolean isColliding(GameObject other) {
        return x < other.x + other.width &&
               x + width > other.x &&
               y < other.y + other.height &&
               y + height > other.y;
    }
}