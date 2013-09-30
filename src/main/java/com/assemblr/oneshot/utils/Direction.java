package com.assemblr.oneshot.utils;

import java.awt.Point;

public enum Direction {
    NORTH ( 0, -1),
    EAST  ( 1,  0),
    SOUTH ( 0,  1),
    WEST  (-1,  0);
    
    public static Point translate(Point p, Direction d) {
        return new Point(p.x + d.x, p.y + d.y);
    }
    
    private final int x;
    private final int y;
    
    private Direction(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public Direction getOpposite() {
        return values()[(ordinal() + 2) % 4];
    }
    
    public Direction getClockwise() {
        return values()[(ordinal() + 1) % 4];
    }
    
    public Direction getCounterClockwise() {
        return values()[(ordinal() + 3) % 4];
    }
}
