/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.assemblr.arena06.client.scenes;

import com.assemblr.arena06.client.menu.Button;
import com.assemblr.arena06.client.menu.ButtonAction;
import com.assemblr.arena06.client.menu.MenuConstants;
import com.assemblr.arena06.client.menu.MenuObject;
import com.assemblr.arena06.client.menu.TextField;
import com.assemblr.arena06.client.navigation.NavigationControler;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;

/**
 *
 * @author Henry
 */
public class MenuPanel extends Panel implements MouseListener, KeyEventDispatcher, KeyListener {

    private final List<MenuObject> menuObjects;
    
    private NavigationControler navigationControler;
    public MenuPanel(NavigationControler navigationControler1) {
        this.navigationControler = navigationControler1;
        this.menuObjects = new ArrayList<MenuObject>();
        addMenuObject(new Button("Go to game", new ButtonAction() {

            public void buttonPressed(MouseEvent me) {
               navigationControler.pushPanel(new GamePanel("localhost", 30155, "bob", navigationControler));
            }
        }));
        addMenuObject(new TextField(300, 100));
        this.addMouseListener(this);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
        this.addKeyListener(this);
    }

    public boolean dispatchKeyEvent(KeyEvent ke) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().redispatchEvent(this, ke);
        return true;
    }

    private boolean firstPaint = true;
    private Point buttonPaneLocation;
    @Override
    protected void paintComponent(Graphics gr) {
        if (firstPaint) {
            layoutButtons(gr);
            firstPaint = false;
        }
        refreshButtonPane();
        ((Graphics2D)gr).setBackground(Color.black);
        gr.clearRect(0, 0, this.getWidth(), this.getHeight());
        gr.drawImage(buttonPane, buttonPaneLocation.x, buttonPaneLocation.y, null);
        buttonPaneLocation = new Point((this.getWidth() - buttonPane.getWidth()) / 2, (this.getHeight() - buttonPane.getHeight()) / 2);
    }
    
    private BufferedImage buttonPane;
    private void layoutButtons(Graphics gr) {
        //Init buttons and button pane
        int totalHeight = 0, maxWidth = 0;
        for (MenuObject menuObject : menuObjects) {
            if (menuObject instanceof Button) {
                Button button = (Button) menuObject;
                int stringWidth = gr.getFontMetrics(MenuConstants.BUTTON_TEXT_FONT).stringWidth(button.text);
                int stringHeight = (int) gr.getFontMetrics(MenuConstants.BUTTON_TEXT_FONT).getAscent();
                int buttonWidth = 2 * MenuConstants.BUTTON_BOARDER_SIDE + stringWidth;
                button.setWidth(buttonWidth);
                button.setHeight((2 * MenuConstants.BUTTON_BOARDER_TOP) + stringHeight);
            }
            if (menuObject instanceof TextField) {
                TextField textField = (TextField) menuObject;
                
                
            }
            if (menuObject.getWidth() > maxWidth) {
                    maxWidth = menuObject.getWidth();
            }
            totalHeight += menuObject.getHeight();
            totalHeight += MenuConstants.BUTTON_SPACE;
        }
        if (menuObjects.size() > 0)
        totalHeight -= MenuConstants.BUTTON_SPACE;
        buttonPane = new BufferedImage(maxWidth, totalHeight, BufferedImage.BITMASK);
        int buttonStart = 0;
        for (MenuObject menuObject : menuObjects) {
            menuObject.setX((buttonPane.getWidth() - menuObject.getWidth()) / 2);
            menuObject.setY(buttonStart);
            buttonStart += menuObject.getHeight() + MenuConstants.BUTTON_SPACE;
        }
        //Draw buttons to pane
        refreshButtonPane();
        buttonPaneLocation = new Point((this.getWidth() - buttonPane.getWidth()) / 2, (this.getHeight() - buttonPane.getHeight()) / 2);

    }
    
    public void refreshButtonPane() {
        Graphics graphics = buttonPane.createGraphics();
        graphics.clearRect(0, 0, buttonPane.getWidth(), buttonPane.getHeight());
        
        
        for (MenuObject m : menuObjects) {
            if (m instanceof Button) {
                Button b= (Button) m;
                graphics.setColor(Color.green);
                graphics.fillRect(m.getX(), m.getY(), m.getWidth(), m.getHeight());
                graphics.setColor(Color.red);
                graphics.setFont(MenuConstants.BUTTON_TEXT_FONT);
                graphics.drawString(b.text, b.getX() + MenuConstants.BUTTON_BOARDER_SIDE, b.getY() - MenuConstants.BUTTON_BOARDER_TOP + b.getHeight());
            }
            if (m instanceof TextField) {
                TextField tf = (TextField)m;
                graphics.setColor(Color.red);
                graphics.drawRect(m.getX(), m.getY(), m.getWidth() - 1 , m.getHeight() - 1);
                graphics.setFont(MenuConstants.TEXTFIELD_TEXT_FONT);
                graphics.drawString(tf.getText(), tf.getX(), tf.getY() + tf.getHeight());
                
            }
        }
    }

    public void keyTyped(KeyEvent e) {

    }
    
    private void addMenuObject(MenuObject mo) {
        menuObjects.add(mo);
        if (mo.needsKeyInput()) {
            this.addKeyListener(mo);
        }
        if (mo.needsMouseInput()) {
            this.addMouseListener(mo);
        }
    }
    public void keyPressed(KeyEvent e) {
    }

    public void keyReleased(KeyEvent e) {

    }

    public void mouseClicked(MouseEvent e) {
        for (MenuObject m : menuObjects) {
            if (m.getDimensions().contains(new Point(e.getX() - buttonPaneLocation.x, e.getY() - buttonPaneLocation.y))) {
                m.clicked(e);
            }
        }
    }

    public void mousePressed(MouseEvent e) {

    }

    public void mouseReleased(MouseEvent e) {

    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
    
    private Thread runner;
    boolean running;
    @Override
    public void enteringView() {
        
        runner = new Thread(new Runnable() {
            public void run() {
                long lastUpdate = System.currentTimeMillis();
                while (running) {
                    try {
                        SwingUtilities.invokeAndWait(new Runnable() {
                            public void run() {
                                //paintImmediately(0, 0, getWidth(), getHeight());
                                repaint();
                            }
                        });
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    } catch (InvocationTargetException ex) {
                        ex.printStackTrace();
                    }
                    
                    long elapsed = System.currentTimeMillis() - lastUpdate;
                    if (elapsed < 16) { // 60 FPS
                        try {
                            Thread.sleep(16 - elapsed);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }
                    
                    long now = System.currentTimeMillis();
                    lastUpdate = now;
                }
            }
        });
        running = true;
        runner.start();
    }

    @Override
    public void leavingView() {
        running = false;
    }

}
