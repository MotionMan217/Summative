/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 * Monk Tang class that player protects.
 * 
 * @version 2.0 (June 15, 2025)
 * @author Thierry
 */
package sunwukonggame;

import processing.core.PApplet;

public class MonkTang extends GameObject {
    private int health = 100; 
    
    /**
     * Creates new Monk Tang character.
     * @param app The PApplet drawing surface
     * @param x Initial x position
     * @param y Initial y position
     * @param imagePath Path to monk image
     */
    public MonkTang(PApplet app, int x, int y, String imagePath) {
        super(x, y, 60, 60);
        this.image = app.loadImage(imagePath);
    }
    
    /** 
     * Get health of monk
     * @return Current health value
     */
    public int getHealth() { return health; }
    
    /**
     * Apply damage to monk
     * @param damage Amount of damage to take
     */
    public void takeDamage(int damage) {
        health -= damage;
        if (health < 0) health = 0;
    }
    
    /**
     * Draw the monk character
     * @param app The PApplet drawing surface
     */
    public void draw(PApplet app) {
        if (image != null) {
            app.image(image, x, y, width, height);
        } else {
            app.fill(173, 216, 230);
            app.rect(x, y, width, height);
        }
    }
}