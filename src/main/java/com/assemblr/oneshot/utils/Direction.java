package com.assemblr.oneshot.utils;


public enum Direction {
    NORTH,
    EAST,
    SOUTH,
    WEST;
    
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
