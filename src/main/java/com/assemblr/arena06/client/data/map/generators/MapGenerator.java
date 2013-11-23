package com.assemblr.arena06.client.data.map.generators;

import com.assemblr.arena06.client.data.map.TileType;


public interface MapGenerator {
    
    public static final double TILE_SIZE = 40;
    
    public TileType[][] generateMap();
    
}
