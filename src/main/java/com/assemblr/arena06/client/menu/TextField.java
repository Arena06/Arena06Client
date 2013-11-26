/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.menu;

import java.awt.event.KeyEvent;

/**
 *
 * @author Henry
 */
public class TextField extends MenuObject {
    private String text = "";
    public TextField(int width, int height) {
        setWidth(width);
        setHeight(height);
        needsKeyInput = true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        switch (e.getKeyChar()) {
            case 8:
                if (text.length() > 1) {
                    setText(getText().substring(0, getText().length() - 1));
                } else {
                    setText("");
                }
            break;
            default:
                setText(getText() + e.getKeyChar());
                break;
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
    
    
    
}
