package com.assemblr.arena06.client.scene;

import com.assemblr.arena06.client.gui.View;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;


public class ViewScene extends Scene implements MouseListener, KeyListener, KeyEventDispatcher {
    
    private View view = new View();
    
    public ViewScene() {
        addMouseListener(this);
        addKeyListener(this);
        setFocusTraversalKeysEnabled(false);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        view.setWidth(getWidth());
        view.setHeight(getHeight());
        view.layout();
        view.paint((Graphics2D) g);
    }
    
    @Override
    public void sceneDidAppear() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
    }
    
    @Override
    public void sceneWillDisappear() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(this);
    }
    
    public void  mouseClicked(MouseEvent e) { view.mouseClicked (e); }
    public void  mousePressed(MouseEvent e) { view.mousePressed (e); }
    public void mouseReleased(MouseEvent e) { view.mouseReleased(e); }
    public void      keyTyped(KeyEvent   e) { view.keyTyped     (e); }
    public void    keyPressed(KeyEvent   e) { view.keyPressed   (e); }
    public void   keyReleased(KeyEvent   e) { view.keyReleased  (e); }
    
    public void  mouseEntered(MouseEvent e) {}
    public void   mouseExited(MouseEvent e) {}
    
    public boolean dispatchKeyEvent(KeyEvent e) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, e);
        return true;
    }
    
    public View getView() {
        return view;
    }
    
    public void setView(View view) {
        this.view = view;
    }
    
}
