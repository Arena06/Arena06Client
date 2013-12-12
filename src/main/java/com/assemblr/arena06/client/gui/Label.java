package com.assemblr.arena06.client.gui;

import com.assemblr.arena06.common.utils.Fonts;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;


public class Label extends View {
    
    public static enum Alignment {
        LEFT,
        CENTER,
        RIGHT;
    }
    
    private Font font = Fonts.FONT_PRIMARY.deriveFont(18f);
    private String text;
    private Alignment alignment;
    
    public Label() {
        this("");
    }
    
    public Label(String text) {
        this(text, Alignment.LEFT);
    }
    
    public Label(String text, Alignment alignment) {
        this.text = text;
        this.alignment = alignment;
    }
    
    @Override
    public void layout() {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        setHeight(metrics.getHeight());
    }
    
    @Override
    public void paint(Graphics2D g) {
        g.setColor(Color.WHITE);
        g.setFont(font);
        
        FontMetrics metrics = g.getFontMetrics(font);
        int offset = 0;
        switch (alignment) {
        case LEFT:
            offset = 0;
            break;
        case RIGHT:
            offset = getWidth() - metrics.stringWidth(text);
            break;
        case CENTER:
            offset = (getWidth() - metrics.stringWidth(text)) / 2;
            break;
        }
        g.drawString(text, offset, getHeight());
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Alignment getAlignment() {
        return alignment;
    }
    
    public void setAlignment(Alignment alignment) {
        this.alignment = alignment;
    }
    
}
