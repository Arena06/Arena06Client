/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.menu;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 *
 * @author Henry
 */
public class TextField extends MenuObject {
    private String text = "";
    private int maxCharCount = 0;
    public TextField(int width, int height, int maxCharCount) {
        setWidth(width);
        setHeight(height);
        needsKeyInput = true;
        needsMouseInput = true;
        this.maxCharCount = maxCharCount;
        
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (isInFocus())
        switch (e.getKeyChar()) {
            case 8:
                if (text.length() > 1) {
                    setText(getText().substring(0, getText().length() - 1));
                } else {
                    setText("");
                }
            break;
            default:
                if (text.length() < maxCharCount)
                setText(getText() + e.getKeyChar());
                break;
        }
    }
    
    private boolean inFocus = false;

    @Override
    public void mouseClicked(MouseEvent e) {
        if (getDimensions().contains(e.getPoint())) {
            inFocus = true;
        }
        else {
            inFocus = false;
        }
    }
    
    
    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return the inFocus
     */
    public boolean isInFocus() {
        return inFocus;
    }
    
    
    
}
