package com.assemblr.arena06.client.gui;

import com.assemblr.arena06.common.utils.Fonts;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class Button extends View {
    
    private Font font = Fonts.FONT_PRIMARY.deriveFont(28f);
    private String text;
    private Action action;
    
    private boolean mouseover = false;
    
    public Button(String text, Action action) {
        this.text = text;
        this.action = action;
        setHeight(50);
    }
    
    @Override
    public void paint(Graphics2D g) {
        g.setColor(mouseover ? new Color(0x888888) : new Color(0x666666));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        g.setColor(Color.WHITE);
        g.setFont(font);
        FontMetrics metrics = g.getFontMetrics(font);
        g.drawString(text, (getWidth() - metrics.stringWidth(text)) / 2, (getHeight() + metrics.getHeight()) / 2);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        action.act();
    }
    
    @Override
    public void mouseEntered(MouseEvent me) {
        mouseover = true;
    }
    
    @Override
    public void mouseExited(MouseEvent me) {
        mouseover = false;
    }
    
    public String getText() {
        return text;
    }
    
    public void setText(String text) {
        this.text = text;
    }
    
    public Font getFont() {
        return font;
    }
    
    public void setFont(Font font) {
        this.font = font;
    }
    
    public Action getAction() {
        return action;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }
    
}
