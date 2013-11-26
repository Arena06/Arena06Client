/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.menu;

import java.awt.Rectangle;


public class Button {
    private Rectangle dimensions = new Rectangle(0, 0, 0, 0);
    private boolean initialized = false;
    public final String text;
    private final ButtonAction action;
    public Button(String text, ButtonAction action) {
        this.text = text;
        this.action = action;
        
    }
    /**
     * @return the initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return dimensions.height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return  dimensions.width;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
         dimensions.height = height;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
         dimensions.width = width;
    }

    /**
     * @return the action
     */
    public ButtonAction getAction() {
        return action;
    }

    /**
     * @return the x
     */
    public int getX() {
        return  dimensions.x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
         dimensions.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return  dimensions.y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
         dimensions.y = y;
    }
    
    public Rectangle getDimensions() {
        return dimensions;
    }
    
   
    
}
