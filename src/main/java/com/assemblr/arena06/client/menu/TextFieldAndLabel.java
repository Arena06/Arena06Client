/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.assemblr.arena06.client.menu;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public class TextFieldAndLabel extends MenuObject {
    private boolean inFocus;
    private final String labelText;
    private String text = "";
    private int maxCharCount;
    private int textFieldWidth, textFieldHeight;
    private int textFieldX, textFieldY;
    public TextFieldAndLabel(String labelText, int textFieldWidth, int textFieldHeight, int maxCharCount) {
        this.setHeight(textFieldHeight);
        this.textFieldHeight = textFieldHeight;
        this.textFieldWidth = textFieldWidth;
        this.labelText = labelText;
        this.textFieldWidth = textFieldWidth;
        this.needsKeyInput = true;
        this.needsMouseInput = true;
        this.maxCharCount = maxCharCount;
    }
    @Override
    public void keyTyped(KeyEvent e) {
        if (isInFocus()) {
            switch (e.getKeyChar()) {
                case 8:
                    if (getText().length() > 1) {
                        setText(getText().substring(0, getText().length() - 1));
                    } else {
                        setText("");
                    }
                    break;
                default:
                    if (getText().length() < maxCharCount) {
                        setText(getText() + e.getKeyChar());
                    }
                    break;
            }
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (getTextFieldDimensions().contains(e.getPoint())) {
            inFocus = true;
        }
        else {
            inFocus = false;
        }
    }
    public Rectangle getTextFieldDimensions() {
        return new Rectangle(textFieldX, textFieldY, textFieldWidth, getTextFieldHeight());
    }
    /**
     * @return the labelText
     */
    public String getLabelText() {
        return labelText;
    }

    /**
     * @return the textFieldWidth
     */
    public int getTextFieldWidth() {
        return textFieldWidth;
    }

    /**
     * @return the textFieldX
     */
    public int getTextFieldX() {
        return textFieldX;
    }

    /**
     * @param textFieldX the textFieldX to set
     */
    public void setTextFieldX(int textFieldX) {
        this.textFieldX = textFieldX;
    }

    /**
     * @return the textFieldY
     */
    public int getTextFieldY() {
        return textFieldY;
    }

    /**
     * @param textFieldY the textFieldY to set
     */
    public void setTextFieldY(int textFieldY) {
        this.textFieldY = textFieldY;
    }

    /**
     * @return the inFocus
     */
    public boolean isInFocus() {
        return inFocus;
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

    @Override
    public void setX(int x) {
        super.setX(x);
        setTextFieldX(getX() + getWidth() - getTextFieldWidth());
    }

    @Override
    public void setY(int y) {
        super.setY(y);
        setTextFieldY(y);
    }

    /**
     * @return the textFieldHeight
     */
    public int getTextFieldHeight() {
        return textFieldHeight;
    }
    
    
    
}
