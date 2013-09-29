package com.assemblr.oneshot.utils;


public class Vector2D {
    
    public static Vector2D add(Vector2D a, Vector2D b) {
        return new Vector2D(a.x + b.x, a.y + b.y);
    }
    
    public static Vector2D subtract(Vector2D a, Vector2D b) {
        return new Vector2D(a.x - b.x, a.y - b.y);
    }
    
    public static Vector2D multiply(Vector2D vector, double scalar) {
        return new Vector2D(vector.x * scalar, vector.y * scalar);
    }
    
    public static Vector2D divide(Vector2D vector, double scalar) {
        return new Vector2D(vector.x / scalar, vector.y / scalar);
    }
    
    public static double dot(Vector2D a, Vector2D b) {
        return a.x*b.x + a.y*b.y;
    }
    
    public static double lengthSquared(Vector2D vector) {
        return Math.pow(vector.x, 2) + Math.pow(vector.y, 2);
    }
    
    public static double length(Vector2D vector) {
        return Math.sqrt(lengthSquared(vector));
    }
    
    public static Vector2D normalize(Vector2D vector) {
        Vector2D value = new Vector2D(vector);
        value.divide(length(value));
        return value;
    }
    
    public static Vector2D scale(Vector2D vector, double length) {
        return new Vector2D(vector).normalize().multiply(length);
    }
    
    public static Vector2D clamp(Vector2D vector, double length) {
        if (length(vector) > length) {
            return scale(vector, length);
        }
        return new Vector2D(vector);
    }
    
    public double x;
    public double y;
    
    public Vector2D() {
        this(0, 0);
    }
    
    public Vector2D(Vector2D o) {
        this(o.x, o.y);
    }
    
    public Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vector2D add(Vector2D o) {
        x += o.x;
        y += o.y;
        return this;
    }
    
    public Vector2D subtract(Vector2D o) {
        x -= o.x;
        y -= o.y;
        return this;
    }
    
    public Vector2D multiply(double scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }
    
    public Vector2D divide(double scalar) {
        x /= scalar;
        y /= scalar;
        return this;
    }
    
    public Vector2D normalize() {
        divide(length(this));
        return this;
    }
    
    public Vector2D scale(double length) {
        normalize();
        multiply(length);
        return this;
    }
    
    public Vector2D clamp(double length) {
        if (length(this) > length) {
            normalize();
            multiply(length);
        }
        return this;
    }
    
    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
    
}
