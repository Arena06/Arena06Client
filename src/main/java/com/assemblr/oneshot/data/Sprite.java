package com.assemblr.oneshot.data;

import com.assemblr.oneshot.utils.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;


public abstract class Sprite implements Renderable {
    
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    
    public double getX() {
        return x;
    }
    
    public void setX(double x) {
        this.x = x;
    }
    
    public double getY() {
        return y;
    }
    
    public void setY(double y) {
        this.y = y;
    }
    
    public double getWidth() {
        return width;
    }
    
    public void setWidth(double width) {
        this.width = width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setHeight(double height) {
        this.height = height;
    }
    
    public Point2D.Double getPosition() {
        return new Point2D.Double(x, y);
    }
    
    public void setPosition(Point2D position) {
        x = position.getX();
        y = position.getY();
    }
    
    public Point2D.Double getCenter() {
        return new Point2D.Double(x + width/2.0, y + height/2.0);
    }
    
    public void setCenter(Point2D position) {
        x = position.getX() - width/2.0;
        y = position.getY() - height/2.0;
    }
    
    public Dimension2D.Double getSize() {
        return new Dimension2D.Double(width, height);
    }
    
    public void setSize(java.awt.geom.Dimension2D size) {
        width = size.getWidth();
        height = size.getHeight();
    }
    
    public Rectangle2D.Double getBounds() {
        return new Rectangle2D.Double(x, y, width, height);
    }
    
    public void setBounds(Rectangle2D bounds) {
        x = bounds.getX();
        y = bounds.getY();
        width = bounds.getWidth();
        height = bounds.getHeight();
    }
    
}
