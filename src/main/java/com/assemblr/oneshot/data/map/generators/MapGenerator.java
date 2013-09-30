package com.assemblr.oneshot.data.map.generators;

import com.assemblr.oneshot.data.map.TileType;


public interface MapGenerator {
    
    public static final double TILE_SIZE = 40;
    
    public TileType[][] generateMap();
    
}
