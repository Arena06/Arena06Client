package com.assemblr.arena06.client.scene;

import javax.swing.JPanel;


public abstract class Scene extends JPanel {
    
    private NavigationController navigationController;
    
    public void sceneWillAppear() {}
    public void sceneDidAppear() {}
    public void sceneWillDisappear() {}
    public void sceneDidDisappear() {}
    
    public void dispose() {}
    
    public NavigationController getNavigationController() {
        return navigationController;
    }
    
    public void setNavigationController(NavigationController navigationController) {
        this.navigationController = navigationController;
    }
    
}
