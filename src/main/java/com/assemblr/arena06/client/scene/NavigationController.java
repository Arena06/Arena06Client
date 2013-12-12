package com.assemblr.arena06.client.scene;


public interface NavigationController {
    
    public void pushScene(Scene scene);
    public Scene popScene();
    public Scene replaceScene(Scene scene);
    
}
