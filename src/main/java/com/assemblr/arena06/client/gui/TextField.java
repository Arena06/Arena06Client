package com.assemblr.arena06.client.gui;

import com.assemblr.arena06.common.utils.Fonts;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;


public class TextField extends View implements ClipboardOwner  {
    
    private static final int TEXT_PADDING = 10;
    
    private Font font = Fonts.FONT_PRIMARY.deriveFont(18f);
    private String text = "";
    
    @Override
    public void layout() {
        FontMetrics metrics = Toolkit.getDefaultToolkit().getFontMetrics(font);
        setHeight(metrics.getHeight() + TEXT_PADDING*2);
    }
    
    @Override
    public void paint(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, getWidth(), getHeight());
        
        int stroke = isFocused() ? 3 : 2;
        g.setColor(Color.WHITE);
        g.setStroke(new BasicStroke(stroke));
        g.draw(new Rectangle2D.Double(stroke / 2.0, stroke / 2.0, getWidth() - stroke, getHeight() - stroke));
        g.setStroke(new BasicStroke());
        
        g.setFont(font);
        g.drawString(text + (isFocused() ? "_" : ""), TEXT_PADDING, getHeight() - TEXT_PADDING);
    }
    
    @Override
    public void mouseClicked(MouseEvent me) {
        requestFocus();
    }
    
    @Override
    public void keyTyped(KeyEvent ke) {
        char c = ke.getKeyChar();
        if (c != KeyEvent.CHAR_UNDEFINED && Character.getType(c) != Character.CONTROL) {
            setText(getText() + c);
        } else {
            if (c == '\u0008' /* BACKSPACE */) {
                if (getText().length() > 0) {
                    setText(getText().substring(0, getText().length() - 1));
                }
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyCode() == KeyEvent.VK_V && ke.isControlDown()) {
            setText(getText() + Toolkit.getDefaultToolkit().getSystemClipboard());
        }
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

    public void lostOwnership(Clipboard clipboard, Transferable contents) {}
    
}
