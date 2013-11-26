/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.menu;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public abstract class MenuObject implements KeyListener, MouseListener {
    private int x,y,height,width;
    protected boolean needsKeyInput = false, needsMouseInput = false;
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    

    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e) {}
    
    
     
    
    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * @return the y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y the y to set
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * @return the height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @param height the height to set
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the needsKeyInput
     */
    public boolean needsKeyInput() {
        return needsKeyInput;
    }

    /**
     * @return the needsMouseInput
     */
    public boolean needsMouseInput() {
        return needsMouseInput;
    }
    public Rectangle getDimensions() {
        return new Rectangle(x, y, width, height);
    }
    public void clicked(MouseEvent me) {}
}
