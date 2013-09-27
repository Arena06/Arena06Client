package com.assemblr.oneshot.data.map.generators;

import com.assemblr.oneshot.data.map.TileType;


public interface MapGenerator {
    
    public TileType[][] generateMap();
    
}
