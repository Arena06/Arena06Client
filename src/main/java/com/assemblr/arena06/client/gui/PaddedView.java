package com.assemblr.arena06.client.gui;


public class PaddedView extends View {
    
    private final View view;
    
    public PaddedView(View view) {
        this.view = view;
        addChild(view);
    }
    
    @Override
    public void layout() {
        super.layout();
        view.setX((getWidth() - view.getWidth()) / 2);
        view.setY((getHeight() - view.getHeight()) / 2);
    }
    
}
