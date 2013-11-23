package com.assemblr.arena06.client.data.map;


public enum TileType {
    NONE  (true),
    WALL  (true),
    FLOOR (false),
    DOOR  (false),
    DEBUG (false);
    
    private final boolean solid;
    
    private TileType(boolean solid) {
        this.solid = solid;
    }
    
    public boolean isSolid() {
        return solid;
    }
}
