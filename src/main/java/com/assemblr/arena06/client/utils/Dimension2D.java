package com.assemblr.arena06.client.utils;


public abstract class Dimension2D extends java.awt.geom.Dimension2D {
    
    public static class Double extends Dimension2D {
        
        public double width;
        public double height;
        
        public Double() {
            this(0, 0);
        }
        
        public Double(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
        @Override
        public double getWidth() {
            return width;
        }
        
        @Override
        public double getHeight() {
            return height;
        }
        
        @Override
        public void setSize(double width, double height) {
            this.width = width;
            this.height = height;
        }
        
    }
    
}
