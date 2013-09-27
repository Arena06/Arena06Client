package com.assemblr.oneshot.data.map;


public class Map {
    
    public static final double TILE_SIZE = 20;
    
    public static Map generate() {
        return new Map();
    }
    
    private TileType[][] data;
    
    private Map() {
    }
    
    public int getWidth() {
        return data.length;
    }
    
    public int getHeight() {
        return getWidth() == 0 ? 0 : data[0].length;
    }
    
    public TileType getTile(int x, int y) {
        return data[x][y];
    }
    
    public boolean isBlocked(int x, int y) {
        return data[x][y] == TileType.WALL;
    }
    
}
