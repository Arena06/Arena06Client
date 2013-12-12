package com.assemblr.arena06.client.gui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;


public class View {
    
    private int x;
    private int y;
    
    private int width;
    private int height;
    
    private Color background = new Color(0x00000000, true);
    
    private View parent = null;
    private final List<View> children = new LinkedList<View>();
    private View focus = this;
    
    public void layout() {
        for (View child : getChildren()) {
            child.layout();
        }
    }
    
    public void paint(Graphics2D g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        for (View child : getChildren()) {
            int cx = child.getX();
            int cy = child.getY();
            g.translate(cx, cy);
            child.paint(g);
            g.translate(-cx, -cy);
        }
    }
    
    public void mousePressed(MouseEvent me) {
        for (View child : getChildren()) {
            int cx = child.getX();
            int cy = child.getY();
            if (new Rectangle(cx, cy, child.getWidth(), child.getHeight()).contains(me.getPoint())) {
                me.translatePoint(-cx, -cy);
                child.mousePressed(me);
                me.translatePoint(cx, cy);
            }
        }
    }
    
    public void mouseClicked(MouseEvent me) {
        for (View child : getChildren()) {
            int cx = child.getX();
            int cy = child.getY();
            if (new Rectangle(cx, cy, child.getWidth(), child.getHeight()).contains(me.getPoint())) {
                me.translatePoint(-cx, -cy);
                child.mouseClicked(me);
                me.translatePoint(cx, cy);
            }
        }
    }
    
    public void mouseReleased(MouseEvent me) {
        for (View child : getChildren()) {
            int cx = child.getX();
            int cy = child.getY();
            if (new Rectangle(cx, cy, child.getWidth(), child.getHeight()).contains(me.getPoint())) {
                me.translatePoint(-cx, -cy);
                child.mouseReleased(me);
                me.translatePoint(cx, cy);
            }
        }
    }
    
    public void mouseEntered(MouseEvent me) {
        for (View child : getChildren()) {
            int cx = child.getX();
            int cy = child.getY();
            if (new Rectangle(cx, cy, child.getWidth(), child.getHeight()).contains(me.getPoint())) {
                me.translatePoint(-cx, -cy);
                child.mouseEntered(me);
                me.translatePoint(cx, cy);
            }
        }
    }
    
    public void mouseExited(MouseEvent me) {
        for (View child : getChildren()) {
            int cx = child.getX();
            int cy = child.getY();
            if (new Rectangle(cx, cy, child.getWidth(), child.getHeight()).contains(me.getPoint())) {
                me.translatePoint(-cx, -cy);
                child.mouseExited(me);
                me.translatePoint(cx, cy);
            }
        }
    }
    
    public void keyPressed(KeyEvent ke) {
        if (focus != this)
            focus.keyPressed(ke);
    }
    
    public void keyReleased(KeyEvent ke) {
        if (focus != this)
            focus.keyReleased(ke);
    }
    
    public void keyTyped(KeyEvent ke) {
        if (focus != this)
            focus.keyTyped(ke);
    }
    
    protected void focus(View view) {
        focus = view;
    }
    
    public void requestFocus() {
        if (parent != null) {
            parent.requestFocus();
            parent.focus(this);
        }
    }
    
    public void addChild(View child) {
        if (child.parent != null)
            throw new IllegalArgumentException("attempted to add object to multiple views");
        getChildren().add(child);
        child.parent = this;
    }
    
    public void removeChild(View child) {
        if (getChildren().remove(child))
            child.parent = null;
    }
    
    public void addChildAtIndex(View child, int i) {
        if (child.parent != null)
            throw new IllegalArgumentException("attempted to add object to multiple views");
        getChildren().add(i, child);
        child.parent = this;
    }
    
    public void removeChildAtIndex(int i) {
        children.remove(i).parent = null;
    }
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public View getParent() {
        return parent;
    }
    
    public boolean isFocused() {
        return parent == null || (parent.focus == this && parent.isFocused());
    }
    
    public View getFocusedChild() {
        if (focus == this) return this;
        return focus.getFocusedChild();
    }
    
    public Color getBackground() {
        return background;
    }
    
    public void setBackground(Color background) {
        this.background = background;
    }
    
    protected List<View> getChildren() {
        return children;
    }
    
}
